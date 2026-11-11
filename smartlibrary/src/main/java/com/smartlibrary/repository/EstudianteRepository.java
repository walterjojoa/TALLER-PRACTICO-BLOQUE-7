package com.smartlibrary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartlibrary.model.Estudiante;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
}
