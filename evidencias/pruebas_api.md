# Evidencias de pruebas con la API real (Spring Boot + MongoDB)

Fecha: 2026-11-11 17:03. API en `http://localhost:8099`; MongoDB 8.0 local en `mongodb://localhost:27017/smartlibrary`.
Datos iniciales: estudiantes `est-1` (Ana Torres) y `est-2` (Luis Gomez); libros `lib-1` (Clean Code), `lib-2` (Design Patterns) y `lib-3` (Refactoring).

## 1. Préstamo de libro disponible (esperado 201 Created)

```
$ curl -s -w '\nHTTP %{http_code}\n' -X POST localhost:8099/api/prestamos -H "Content-Type: application/json" -d '{"estudianteId":"est-1","libroId":"lib-1"}'
{"id":"6af4e61e49f6313fc4b099fc","estudianteNombre":"Ana Torres","libroTitulo":"Clean Code","fechaPrestamo":"2026-11-11"}
HTTP 201

```

## 2. Libro ya prestado (esperado 409 Conflict)

```
$ curl -s -w '\nHTTP %{http_code}\n' -X POST localhost:8099/api/prestamos -H "Content-Type: application/json" -d '{"estudianteId":"est-2","libroId":"lib-1"}'
{"error":"El libro con id lib-1 no esta disponible"}
HTTP 409

```

## 3. Estudiante inexistente (esperado 404 Not Found)

```
$ curl -s -w '\nHTTP %{http_code}\n' -X POST localhost:8099/api/prestamos -H "Content-Type: application/json" -d '{"estudianteId":"est-99","libroId":"lib-2"}'
{"error":"Estudiante con id est-99 no existe"}
HTTP 404

```

## 4. Libro inexistente (esperado 404 Not Found)

```
$ curl -s -w '\nHTTP %{http_code}\n' -X POST localhost:8099/api/prestamos -H "Content-Type: application/json" -d '{"estudianteId":"est-1","libroId":"lib-99"}'
{"error":"Libro con id lib-99 no existe"}
HTTP 404

```

## 5. Consulta de préstamos (esperado 200 OK con registros)

```
$ curl -s -w '\nHTTP %{http_code}\n' localhost:8099/api/prestamos
[{"id":"6af4e61e49f6313fc4b099fc","estudianteNombre":"Ana Torres","libroTitulo":"Clean Code","fechaPrestamo":"2026-11-11"}]
HTTP 200

```

## Extra. Solicitud sin libroId (esperado 400 Bad Request, R4)

```
$ curl -s -w '\nHTTP %{http_code}\n' -X POST localhost:8099/api/prestamos -H "Content-Type: application/json" -d '{"estudianteId":"est-1"}'
{"error":"libroId es obligatorio"}
HTTP 400

```

## Extra. R5 verificada contra la base: un libro solo puede tener un préstamo activo

Se fuerza `disponible=true` en el documento `lib-1` directamente en MongoDB, para comprobar que la consulta `existsByLibroIdAndFechaDevolucionIsNull` bloquea igual el segundo préstamo (esperado 409).

Documentos guardados en MongoDB antes de forzar el cambio. El préstamo guarda solo los ids del estudiante y del libro (`@DocumentReference`):

```json
### coleccion prestamos
{"_id": "6af4e61e49f6313fc4b099fc", "fechaPrestamo": "2026-11-11 05:00:00", "estudiante": "est-1", "libro": "lib-1", "_class": "com.smartlibrary.model.Prestamo"}
### coleccion libros
{"_id": "lib-1", "titulo": "Clean Code", "disponible": false, "_class": "com.smartlibrary.model.Libro"}
{"_id": "lib-2", "titulo": "Design Patterns", "disponible": true, "_class": "com.smartlibrary.model.Libro"}
{"_id": "lib-3", "titulo": "Refactoring", "disponible": true, "_class": "com.smartlibrary.model.Libro"}
forzado lib-1 disponible=true: 1
```

```
$ curl -s -X POST localhost:8099/api/prestamos -H "Content-Type: application/json" -d '{"estudianteId":"est-2","libroId":"lib-1"}'
{"error":"El libro con id lib-1 no esta disponible"}
HTTP 409
```
