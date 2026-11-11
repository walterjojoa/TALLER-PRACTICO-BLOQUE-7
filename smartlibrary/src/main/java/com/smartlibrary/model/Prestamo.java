package com.smartlibrary.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * El prestamo guarda REFERENCIAS (ids) al estudiante y al libro, no copias embebidas:
 * ambos tienen ciclo de vida propio y pueden aparecer en muchos prestamos (asociacion 0..* a 1).
 */
@Document(collection = "prestamos")
public class Prestamo {

    @Id
    private String id;

    private LocalDate fechaPrestamo;

    /** Es null mientras el prestamo esta activo (R5). */
    private LocalDate fechaDevolucion;

    @DocumentReference
    private Estudiante estudiante;

    @DocumentReference
    private Libro libro;

    protected Prestamo() {
        // requerido por Spring Data
    }

    public Prestamo(Estudiante estudiante, Libro libro, LocalDate fecha) {
        this.estudiante = estudiante;
        this.libro = libro;
        this.fechaPrestamo = fecha;
    }

    public String getId() {
        return id;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Libro getLibro() {
        return libro;
    }

    public boolean estaActivo() {
        return fechaDevolucion == null;
    }
}
