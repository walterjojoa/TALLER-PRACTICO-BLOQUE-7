# Evidencias de pruebas con la API real (Spring Boot + MongoDB)

- **Fecha:** 2026-11-11 17:19
- **Entorno:** Windows 11, JDK Temurin 21, MongoDB Server 9.0.4 instalado como servicio de Windows (`mongodb://localhost:27017/smartlibrary`) y mongosh 2.14.1.
- **API:** `http://localhost:8080`, ejecutada con el JAR del proyecto.
- **Datos iniciales:** estudiantes `est-1` (Ana Torres) y `est-2` (Luis Gomez); libros `lib-1` (Clean Code), `lib-2` (Design Patterns) y `lib-3` (Refactoring).

**Cómo se obtuvo** (desde la carpeta `smartlibrary/`):

1. `reiniciar-datos.cmd`: borra la base para partir de cero.
2. `iniciar-api.cmd`: arranca la API, que vuelve a cargar los datos de prueba.
3. `probar-api.cmd`: ejecuta los escenarios con curl y muestra los documentos guardados.

En el escenario extra de R5, el script fuerza `disponible: true` en `lib-1` directamente en MongoDB. Aun así la API responde 409, porque el préstamo activo bloquea un segundo préstamo. Por eso `lib-1` aparece como disponible al final del listado.

## Salida de `probar-api.cmd`

```
=== 1. Prestamo de libro disponible (esperado 201 Created)
{"id":"6af4e9d07e9ba30859ab114b","estudianteNombre":"Ana Torres","libroTitulo":"Clean Code","fechaPrestamo":"2026-11-11"}  HTTP 201

=== 2. Libro ya prestado (esperado 409 Conflict)
{"error":"El libro con id lib-1 no esta disponible"}  HTTP 409

=== 3. Estudiante inexistente (esperado 404 Not Found)
{"error":"Estudiante con id est-99 no existe"}  HTTP 404

=== 4. Libro inexistente (esperado 404 Not Found)
{"error":"Libro con id lib-99 no existe"}  HTTP 404

=== 5. Consulta de prestamos (esperado 200 OK con registros)
[{"id":"6af4e9d07e9ba30859ab114b","estudianteNombre":"Ana Torres","libroTitulo":"Clean Code","fechaPrestamo":"2026-11-11"}]  HTTP 200

=== Extra. Solicitud sin libroId (esperado 400 Bad Request, R4)
{"error":"libroId es obligatorio"}  HTTP 400

=== Extra. R5: se fuerza disponible=true en lib-1 y se intenta otro prestamo (esperado 409)
{"error":"El libro con id lib-1 no esta disponible"}  HTTP 409

=== Documentos guardados en MongoDB
[
  {
    _id: ObjectId('6af4e9d07e9ba30859ab114b'),
    fechaPrestamo: ISODate('2026-11-11T05:00:00.000Z'),
    estudiante: 'est-1',
    libro: 'lib-1',
    _class: 'com.smartlibrary.model.Prestamo'
  }
]
[
  {
    _id: 'lib-1',
    titulo: 'Clean Code',
    disponible: true,
    _class: 'com.smartlibrary.model.Libro'
  },
  {
    _id: 'lib-2',
    titulo: 'Design Patterns',
    disponible: true,
    _class: 'com.smartlibrary.model.Libro'
  },
  {
    _id: 'lib-3',
    titulo: 'Refactoring',
    disponible: true,
    _class: 'com.smartlibrary.model.Libro'
  }
]
```
