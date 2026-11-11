package com.smartlibrary.dto;

import jakarta.validation.constraints.NotBlank;

public record PrestamoRequest(
        @NotBlank(message = "estudianteId es obligatorio") String estudianteId,
        @NotBlank(message = "libroId es obligatorio") String libroId) {
}
