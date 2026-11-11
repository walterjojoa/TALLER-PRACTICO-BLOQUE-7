package com.smartlibrary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartlibrary.dto.PrestamoRequest;
import com.smartlibrary.model.Estudiante;
import com.smartlibrary.model.Libro;
import com.smartlibrary.model.Prestamo;
import com.smartlibrary.repository.EstudianteRepository;
import com.smartlibrary.repository.LibroRepository;
import com.smartlibrary.repository.PrestamoRepository;

/** Reglas de negocio R1 a R5 probadas en la capa de servicio, con repositorios simulados. */
@ExtendWith(MockitoExtension.class)
class PrestamoServiceTest {

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private PrestamoRepository prestamoRepository;

    @InjectMocks
    private PrestamoService prestamoService;

    private final Estudiante ana = new Estudiante("est-1", "Ana Torres", "ana@correo.edu.co");

    @Test
    @DisplayName("R1, R2, R3: libro disponible -> crea prestamo con fecha de hoy y marca el libro como prestado")
    void registraPrestamoDeLibroDisponible() {
        Libro libro = new Libro("lib-1", "Clean Code", true);
        when(estudianteRepository.findById("est-1")).thenReturn(Optional.of(ana));
        when(libroRepository.findById("lib-1")).thenReturn(Optional.of(libro));
        when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(inv -> inv.getArgument(0));

        Prestamo prestamo = prestamoService.registrarPrestamo(new PrestamoRequest("est-1", "lib-1"));

        assertThat(prestamo.getEstudiante()).isSameAs(ana);
        assertThat(prestamo.getLibro()).isSameAs(libro);
        assertThat(prestamo.getFechaPrestamo()).isEqualTo(LocalDate.now());
        assertThat(prestamo.estaActivo()).isTrue();
        assertThat(libro.isDisponible()).isFalse();
        verify(libroRepository).save(libro);
    }

    @Test
    @DisplayName("R1 y R4: libro no disponible -> LibroNoDisponibleException y no se guarda nada")
    void rechazaLibroNoDisponible() {
        Libro libro = new Libro("lib-1", "Clean Code", false);
        when(estudianteRepository.findById("est-1")).thenReturn(Optional.of(ana));
        when(libroRepository.findById("lib-1")).thenReturn(Optional.of(libro));

        assertThatThrownBy(() -> prestamoService.registrarPrestamo(new PrestamoRequest("est-1", "lib-1")))
                .isInstanceOf(LibroNoDisponibleException.class);

        verify(prestamoRepository, never()).save(any());
        verify(libroRepository, never()).save(any());
    }

    @Test
    @DisplayName("R5: el libro ya tiene un prestamo activo -> LibroNoDisponibleException")
    void rechazaSegundoPrestamoActivo() {
        Libro libro = new Libro("lib-1", "Clean Code", true);
        when(estudianteRepository.findById("est-1")).thenReturn(Optional.of(ana));
        when(libroRepository.findById("lib-1")).thenReturn(Optional.of(libro));
        when(prestamoRepository.existsByLibroIdAndFechaDevolucionIsNull("lib-1")).thenReturn(true);

        assertThatThrownBy(() -> prestamoService.registrarPrestamo(new PrestamoRequest("est-1", "lib-1")))
                .isInstanceOf(LibroNoDisponibleException.class);

        verify(prestamoRepository, never()).save(any());
    }

    @Test
    @DisplayName("R4: estudiante inexistente -> RecursoNoEncontradoException y no se guarda nada")
    void rechazaEstudianteInexistente() {
        when(estudianteRepository.findById("est-99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> prestamoService.registrarPrestamo(new PrestamoRequest("est-99", "lib-1")))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("Estudiante con id est-99 no existe");

        verify(prestamoRepository, never()).save(any());
    }

    @Test
    @DisplayName("R4: libro inexistente -> RecursoNoEncontradoException y no se guarda nada")
    void rechazaLibroInexistente() {
        when(estudianteRepository.findById("est-1")).thenReturn(Optional.of(ana));
        when(libroRepository.findById("lib-99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> prestamoService.registrarPrestamo(new PrestamoRequest("est-1", "lib-99")))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("Libro con id lib-99 no existe");

        verify(prestamoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Experto en informacion: Libro.marcarPrestado() rechaza un libro ya prestado")
    void libroProtegeSuEstado() {
        Libro libro = new Libro("lib-1", "Clean Code", true);
        libro.marcarPrestado();

        assertThat(libro.isDisponible()).isFalse();
        assertThatThrownBy(libro::marcarPrestado).isInstanceOf(LibroNoDisponibleException.class);
    }
}
