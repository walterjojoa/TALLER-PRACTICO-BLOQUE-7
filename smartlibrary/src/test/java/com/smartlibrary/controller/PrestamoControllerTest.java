package com.smartlibrary.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.smartlibrary.repository.LibroRepository;
import com.smartlibrary.repository.PrestamoRepository;

/**
 * Escenarios de la Actividad 6. Cada prueba corre en una transaccion
 * que se revierte al final, asi todas parten de los datos de data.sql.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PrestamoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private LibroRepository libroRepository;

    private static String cuerpo(Object estudianteId, Object libroId) {
        return "{\"estudianteId\": " + estudianteId + ", \"libroId\": " + libroId + "}";
    }

    @Test
    @DisplayName("Prestamo de libro disponible -> 201 Created y prestamo registrado")
    void prestamoDeLibroDisponible() throws Exception {
        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(1, 1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.estudianteNombre").value("Ana Torres"))
                .andExpect(jsonPath("$.libroTitulo").value("Clean Code"));

        assertThat(prestamoRepository.count()).isEqualTo(1);
        assertThat(libroRepository.findById(1L).orElseThrow().isDisponible()).isFalse();
    }

    @Test
    @DisplayName("Libro ya prestado -> 409 Conflict y sin nuevo prestamo")
    void libroYaPrestado() throws Exception {
        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(1, 2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(2, 2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El libro con id 2 no esta disponible"));

        assertThat(prestamoRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Estudiante inexistente -> 404 Not Found")
    void estudianteInexistente() throws Exception {
        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(99, 1)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Estudiante con id 99 no existe"));

        assertThat(prestamoRepository.count()).isZero();
    }

    @Test
    @DisplayName("Libro inexistente -> 404 Not Found")
    void libroInexistente() throws Exception {
        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(1, 99)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Libro con id 99 no existe"));

        assertThat(prestamoRepository.count()).isZero();
    }

    @Test
    @DisplayName("Consulta de prestamos -> 200 OK con registros")
    void consultaDePrestamos() throws Exception {
        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(2, 3)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/prestamos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estudianteNombre").value("Luis Gomez"))
                .andExpect(jsonPath("$[0].libroTitulo").value("Refactoring"));
    }

    @Test
    @DisplayName("Solicitud sin libroId -> 400 Bad Request (R4)")
    void solicitudInvalida() throws Exception {
        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estudianteId\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("libroId es obligatorio"));

        assertThat(prestamoRepository.count()).isZero();
    }
}
