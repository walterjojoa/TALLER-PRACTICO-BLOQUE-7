package com.smartlibrary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartlibrary.model.Libro;

public interface LibroRepository extends JpaRepository<Libro, Long> {
}
