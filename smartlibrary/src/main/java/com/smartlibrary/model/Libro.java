package com.smartlibrary.model;

import com.smartlibrary.service.LibroNoDisponibleException;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private boolean disponible;

    protected Libro() {
        // requerido por JPA
    }

    public Libro(String titulo, boolean disponible) {
        this.titulo = titulo;
        this.disponible = disponible;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public boolean isDisponible() {
        return disponible;
    }

    /**
     * Experto en informacion: el libro protege su propio estado (R1 y R3).
     * No hay setDisponible publico para que nadie pueda saltarse la regla.
     */
    public void marcarPrestado() {
        if (!disponible) {
            throw new LibroNoDisponibleException(id);
        }
        disponible = false;
    }
}
