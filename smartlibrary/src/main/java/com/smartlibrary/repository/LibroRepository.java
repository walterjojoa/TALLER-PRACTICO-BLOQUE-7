package com.smartlibrary.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.smartlibrary.model.Libro;

public interface LibroRepository extends MongoRepository<Libro, String> {
}
