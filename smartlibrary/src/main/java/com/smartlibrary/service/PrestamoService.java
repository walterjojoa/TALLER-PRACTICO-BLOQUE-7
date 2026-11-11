package com.smartlibrary.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.smartlibrary.dto.PrestamoRequest;
import com.smartlibrary.model.Estudiante;
import com.smartlibrary.model.Libro;
import com.smartlibrary.model.Prestamo;
import com.smartlibrary.repository.EstudianteRepository;
import com.smartlibrary.repository.LibroRepository;
import com.smartlibrary.repository.PrestamoRepository;

/**
 * Controlador GRASP del caso de uso "Registrar prestamo".
 * Coordina repositorios y entidades; no conoce HTTP.
 */
@Service
public class PrestamoService {

    private final EstudianteRepository estudianteRepository;
    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;

    public PrestamoService(EstudianteRepository estudianteRepository,
                           LibroRepository libroRepository,
                           PrestamoRepository prestamoRepository) {
        this.estudianteRepository = estudianteRepository;
        this.libroRepository = libroRepository;
        this.prestamoRepository = prestamoRepository;
    }

    /**
     * Aplica R1 a R5. Todas las validaciones se hacen ANTES de escribir,
     * asi una solicitud invalida nunca crea prestamo (R4).
     *
     * MongoDB garantiza atomicidad por documento. Sin un replica set no hay
     * transacciones entre documentos, por eso el prestamo se guarda primero:
     * si fallara el guardado del libro, el prestamo activo ya registrado sigue
     * bloqueando nuevos prestamos del mismo libro gracias a la consulta de R5.
     */
    public Prestamo registrarPrestamo(PrestamoRequest request) {
        Estudiante estudiante = estudianteRepository.findById(request.estudianteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante", request.estudianteId()));
        Libro libro = libroRepository.findById(request.libroId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro", request.libroId()));

        if (!libro.isDisponible() || prestamoRepository.existsByLibroIdAndFechaDevolucionIsNull(libro.getId())) {
            throw new LibroNoDisponibleException(libro.getId());
        }

        libro.marcarPrestado();
        Prestamo prestamo = prestamoRepository.save(new Prestamo(estudiante, libro, LocalDate.now()));
        libroRepository.save(libro);
        return prestamo;
    }

    public List<Prestamo> listarPrestamos() {
        return prestamoRepository.findAll();
    }
}
