package com.smartlibrary.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.smartlibrary.service.LibroNoDisponibleException;

@Document(collection = "libros")
public class Libro {

    @Id
    private String id;

    private String titulo;

    private boolean disponible;

    protected Libro() {
        // requerido por Spring Data
    }

    public Libro(String id, String titulo, boolean disponible) {
        this.id = id;
        this.titulo = titulo;
        this.disponible = disponible;
    }

    public String getId() {
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
