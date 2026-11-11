package com.smartlibrary.service;

public class LibroNoDisponibleException extends RuntimeException {

    public LibroNoDisponibleException(Long libroId) {
        super("El libro con id " + libroId + " no esta disponible");
    }
}
