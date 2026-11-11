package com.smartlibrary.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.smartlibrary.dto.PrestamoRequest;
import com.smartlibrary.model.Estudiante;
import com.smartlibrary.model.Libro;
import com.smartlibrary.model.Prestamo;
import com.smartlibrary.service.LibroNoDisponibleException;
import com.smartlibrary.service.PrestamoService;
import com.smartlibrary.service.RecursoNoEncontradoException;

/**
 * Escenarios de la Actividad 6 vistos desde HTTP: comprueba que el Controller
 * y el ManejadorExcepciones devuelvan el codigo correcto en cada caso.
 * El servicio se reemplaza por un doble de prueba, asi no se necesita MongoDB.
 */
@WebMvcTest(PrestamoController.class)
class PrestamoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PrestamoService prestamoService;

    private static final Estudiante ANA = new Estudiante("est-1", "Ana Torres", "ana@correo.edu.co");
    private static final Libro CLEAN_CODE = new Libro("lib-1", "Clean Code", true);

    private static String cuerpo(String estudianteId, String libroId) {
        return "{\"estudianteId\": \"" + estudianteId + "\", \"libroId\": \"" + libroId + "\"}";
    }

    @Test
    @DisplayName("Prestamo de libro disponible -> 201 Created y prestamo registrado")
    void prestamoDeLibroDisponible() throws Exception {
        when(prestamoService.registrarPrestamo(any(PrestamoRequest.class)))
                .thenReturn(new Prestamo(ANA, CLEAN_CODE, LocalDate.of(2026, 11, 11)));

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("est-1", "lib-1")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estudianteNombre").value("Ana Torres"))
                .andExpect(jsonPath("$.libroTitulo").value("Clean Code"))
                .andExpect(jsonPath("$.fechaPrestamo").value("2026-11-11"));
    }

    @Test
    @DisplayName("Libro ya prestado -> 409 Conflict")
    void libroYaPrestado() throws Exception {
        when(prestamoService.registrarPrestamo(any(PrestamoRequest.class)))
                .thenThrow(new LibroNoDisponibleException("lib-1"));

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("est-2", "lib-1")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El libro con id lib-1 no esta disponible"));
    }

    @Test
    @DisplayName("Estudiante inexistente -> 404 Not Found")
    void estudianteInexistente() throws Exception {
        when(prestamoService.registrarPrestamo(any(PrestamoRequest.class)))
                .thenThrow(new RecursoNoEncontradoException("Estudiante", "est-99"));

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("est-99", "lib-1")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Estudiante con id est-99 no existe"));
    }

    @Test
    @DisplayName("Libro inexistente -> 404 Not Found")
    void libroInexistente() throws Exception {
        when(prestamoService.registrarPrestamo(any(PrestamoRequest.class)))
                .thenThrow(new RecursoNoEncontradoException("Libro", "lib-99"));

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("est-1", "lib-99")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Libro con id lib-99 no existe"));
    }

    @Test
    @DisplayName("Consulta de prestamos -> 200 OK con registros")
    void consultaDePrestamos() throws Exception {
        when(prestamoService.listarPrestamos())
                .thenReturn(List.of(new Prestamo(ANA, CLEAN_CODE, LocalDate.of(2026, 11, 11))));

        mockMvc.perform(get("/api/prestamos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estudianteNombre").value("Ana Torres"))
                .andExpect(jsonPath("$[0].libroTitulo").value("Clean Code"));
    }

    @Test
    @DisplayName("Solicitud sin libroId -> 400 Bad Request y el servicio no se invoca (R4)")
    void solicitudInvalida() throws Exception {
        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estudianteId\": \"est-1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("libroId es obligatorio"));

        verify(prestamoService, never()).registrarPrestamo(any());
    }
}
