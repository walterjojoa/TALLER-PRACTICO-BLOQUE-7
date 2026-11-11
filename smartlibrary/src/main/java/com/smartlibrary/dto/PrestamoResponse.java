package com.smartlibrary.dto;

import java.time.LocalDate;

import com.smartlibrary.model.Prestamo;

public record PrestamoResponse(
        String id,
        String estudianteNombre,
        String libroTitulo,
        LocalDate fechaPrestamo) {

    public static PrestamoResponse desde(Prestamo p) {
        return new PrestamoResponse(
                p.getId(),
                p.getEstudiante().getNombre(),
                p.getLibro().getTitulo(),
                p.getFechaPrestamo());
    }
}
