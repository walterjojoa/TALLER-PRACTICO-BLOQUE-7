package com.smartlibrary.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.smartlibrary.model.Estudiante;
import com.smartlibrary.model.Libro;
import com.smartlibrary.repository.EstudianteRepository;
import com.smartlibrary.repository.LibroRepository;

/**
 * Carga estudiantes y libros de prueba la primera vez que arranca la aplicacion.
 * El taller supone que ya existen; el caso de uso solo registra prestamos.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final EstudianteRepository estudianteRepository;
    private final LibroRepository libroRepository;

    public DatosIniciales(EstudianteRepository estudianteRepository, LibroRepository libroRepository) {
        this.estudianteRepository = estudianteRepository;
        this.libroRepository = libroRepository;
    }

    @Override
    public void run(String... args) {
        if (estudianteRepository.count() == 0) {
            estudianteRepository.save(new Estudiante("est-1", "Ana Torres", "ana.torres@correo.edu.co"));
            estudianteRepository.save(new Estudiante("est-2", "Luis Gomez", "luis.gomez@correo.edu.co"));
        }
        if (libroRepository.count() == 0) {
            libroRepository.save(new Libro("lib-1", "Clean Code", true));
            libroRepository.save(new Libro("lib-2", "Design Patterns", true));
            libroRepository.save(new Libro("lib-3", "Refactoring", true));
        }
    }
}
