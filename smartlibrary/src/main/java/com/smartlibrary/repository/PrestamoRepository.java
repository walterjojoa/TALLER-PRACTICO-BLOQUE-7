package com.smartlibrary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartlibrary.model.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    /** R5: indica si el libro ya tiene un prestamo activo (sin fecha de devolucion). */
    boolean existsByLibroIdAndFechaDevolucionIsNull(Long libroId);
}
