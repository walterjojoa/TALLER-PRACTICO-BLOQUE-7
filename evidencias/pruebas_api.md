# Evidencias de pruebas manuales - API SmartLibrary
# Fecha: 2026-11-11 16:53  |  Servidor: http://localhost:8099

## 1. Prestamo de libro disponible (esperado 201 Created)
$ curl -s -i -X POST localhost:8099/api/prestamos -H 'Content-Type: application/json' -d '{"estudianteId":1,"libroId":1}' | grep -E '^HTTP|^\{'

HTTP/1.1 201 
{"id":1,"estudianteNombre":"Ana Torres","libroTitulo":"Clean Code","fechaPrestamo":"2026-11-11"}


## 2. Libro ya prestado (esperado 409 Conflict)
$ curl -s -i -X POST localhost:8099/api/prestamos -H 'Content-Type: application/json' -d '{"estudianteId":2,"libroId":1}' | grep -E '^HTTP|^\{'

HTTP/1.1 409 
{"error":"El libro con id 1 no esta disponible"}


## 3. Estudiante inexistente (esperado 404 Not Found)
$ curl -s -i -X POST localhost:8099/api/prestamos -H 'Content-Type: application/json' -d '{"estudianteId":99,"libroId":2}' | grep -E '^HTTP|^\{'

HTTP/1.1 404 
{"error":"Estudiante con id 99 no existe"}


## 4. Libro inexistente (esperado 404 Not Found)
$ curl -s -i -X POST localhost:8099/api/prestamos -H 'Content-Type: application/json' -d '{"estudianteId":1,"libroId":99}' | grep -E '^HTTP|^\{'

HTTP/1.1 404 
{"error":"Libro con id 99 no existe"}


## 5. Consulta de prestamos (esperado 200 OK con registros)
$ curl -s -i localhost:8099/api/prestamos | grep -E '^HTTP|^\['

HTTP/1.1 200 
[{"id":1,"estudianteNombre":"Ana Torres","libroTitulo":"Clean Code","fechaPrestamo":"2026-11-11"}]


## Extra. Solicitud sin libroId (esperado 400 Bad Request, R4)
$ curl -s -i -X POST localhost:8099/api/prestamos -H 'Content-Type: application/json' -d '{"estudianteId":1}' | grep -E '^HTTP|^\{'

HTTP/1.1 400 
{"error":"libroId es obligatorio"}

