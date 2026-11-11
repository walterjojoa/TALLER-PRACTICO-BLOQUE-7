package com.smartlibrary.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.smartlibrary.model.Prestamo;

public interface PrestamoRepository extends MongoRepository<Prestamo, String> {

    /**
     * R5: indica si el libro ya tiene un prestamo activo.
     * En MongoDB un campo null no se guarda, asi que 'fechaDevolucion: null'
     * coincide con los prestamos que aun no se han devuelto.
     */
    @Query(value = "{ 'libro': ?0, 'fechaDevolucion': null }", exists = true)
    boolean existsByLibroIdAndFechaDevolucionIsNull(String libroId);
}
