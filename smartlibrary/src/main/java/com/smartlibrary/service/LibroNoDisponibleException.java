package com.smartlibrary.service;

public class LibroNoDisponibleException extends RuntimeException {

    public LibroNoDisponibleException(String libroId) {
        super("El libro con id " + libroId + " no esta disponible");
    }
}
