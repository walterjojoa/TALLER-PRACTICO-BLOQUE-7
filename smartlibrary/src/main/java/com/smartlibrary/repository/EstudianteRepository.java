package com.smartlibrary.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.smartlibrary.model.Estudiante;

public interface EstudianteRepository extends MongoRepository<Estudiante, String> {
}
