# Taller práctico Bloque 7 · SmartLibrary

**Diseño de Software · Cuarto semestre**

Este taller rediseña el modelo de clases inicial de SmartLibrary (centrado en `GestorPrestamos`) en tres capas, **Controller → Service → Repository**, para una API REST con **Spring Boot y MongoDB**. El caso de uso que se resuelve es **Registrar préstamo** (`POST /api/prestamos`). También incluye la actividad de cierre del caso **Editorial** de la clase "De la Web a una API REST con Spring Boot y MongoDB".

## Entregables

**Documento único de entrega:** [`entregables/Entrega_Taller7_SmartLibrary.pdf`](entregables/Entrega_Taller7_SmartLibrary.pdf) (15 páginas con los 6 entregables y el anexo Editorial).

| # | Entregable pedido | Dónde está |
|---|---|---|
| 1 | Diagrama inicial anotado con observaciones | [Actividad 1](#actividad-1--modelo-inicial-anotado) · `diagramas/imagenes/01_Modelo_Inicial_Anotado.png` |
| 2 | Tabla de responsabilidades completa | [Actividad 2](#actividad-2--tabla-de-responsabilidades) |
| 3 | Diagrama de clases por capas con relaciones y multiplicidades justificadas | [Actividad 3](#actividad-3--diagrama-de-clases-por-capas) · `diagramas/imagenes/02_Diagrama_Clases_Por_Capas.png` |
| 4 | Diagrama de secuencia del préstamo | [Actividad 4](#actividad-4--diagrama-de-secuencia) · `diagramas/imagenes/03_Secuencia_Registrar_Prestamo.png` |
| 5 | Proyecto Spring Boot y evidencias de pruebas | [`smartlibrary/`](smartlibrary/) · [`evidencias/`](evidencias/) |
| 6 | Explicación de las decisiones de diseño (máximo una página) | [`entregables/Decisiones_de_diseno.docx`](entregables/Decisiones_de_diseno.docx) (también en PDF) |
| + | Actividad de cierre Editorial (diapositivas de la clase) | [`actividad-editorial/`](actividad-editorial/) |

Los diagramas 1, 3 y 4 están en el proyecto de Visual Paradigm `diagramas/visual-paradigm/SmartLibrary_Taller7.vpp` (*File → Open Project*).

## Contenido del repositorio

```
├── enunciado/                               Enunciado del taller y diapositivas de la clase (PDF)
├── diagramas/
│   ├── visual-paradigm/SmartLibrary_Taller7.vpp
│   ├── imagenes/                            Diagramas exportados en PNG
│   └── plantuml/                            Los mismos diagramas en PlantUML
├── smartlibrary/                            Proyecto Spring Boot + MongoDB
├── evidencias/
│   ├── pruebas_automaticas.txt              12 pruebas JUnit (12 de 12 pasan)
│   └── pruebas_api.md                       Salida de probar-api.cmd contra MongoDB 9.0 instalado
├── entregables/
│   ├── Entrega_Taller7_SmartLibrary.pdf     Documento único con todos los entregables
│   ├── Decisiones_de_diseno.docx            Explicación de una página
│   └── Decisiones_de_diseno.pdf
├── actividad-editorial/                     Caso Editorial: endpoints, responsabilidades, reto DELETE y diagramas
└── solucion/taller_resuelto.html            Solución completa de las actividades 1 a 7
```

## Código · Proyecto Spring Boot

Hecho con **Java 21, Maven, Spring Boot 4.1.2, Spring Web, Spring Data MongoDB y Validation**.

> **Nota sobre la persistencia.** El enunciado propone Spring Data JPA con H2 para la demostración, pero la clase ("De la Web a una API REST con Spring Boot y MongoDB") y el repositorio de referencia del profesor implementan con **MongoDB**, así que se usó MongoDB. El diseño por capas, las clases y las reglas R1 a R5 son los mismos; solo cambia cómo el Repository guarda los datos. Las diferencias (sin transacción entre documentos y referencias por id) están justificadas en [`entregables/Decisiones_de_diseno.docx`](entregables/Decisiones_de_diseno.docx).

Los paquetes siguen el diagrama de clases:

```
smartlibrary/src/main/java/com/smartlibrary/
├── controller/   PrestamoController, ManejadorExcepciones
├── service/      PrestamoService, RecursoNoEncontradoException, LibroNoDisponibleException
├── repository/   EstudianteRepository, LibroRepository, PrestamoRepository  (MongoRepository)
├── model/        Estudiante, Libro, Prestamo  (@Document)
├── dto/          PrestamoRequest, PrestamoResponse
└── config/       DatosIniciales (carga estudiantes y libros de prueba)
```

**Colecciones en MongoDB:** `estudiantes`, `libros` y `prestamos`. Cada préstamo guarda **referencias** (`@DocumentReference`) a su estudiante y su libro, no copias embebidas.

### Cómo ejecutarlo en Windows

La carpeta `smartlibrary/` trae scripts para cada paso:

| Script | Qué hace |
|---|---|
| `instalar-requisitos.cmd` | Instala con winget **MongoDB Server** (como servicio de Windows), **mongosh** y **JDK 21**. Se ejecuta una sola vez; Windows pedirá permiso de administrador |
| `iniciar-api.cmd` | Comprueba que MongoDB esté corriendo y levanta la API en `http://localhost:8080` |
| `probar-api.cmd` | Ejecuta los escenarios de la Actividad 6 con curl y muestra los documentos guardados en MongoDB |
| `reiniciar-datos.cmd` | Borra la base `smartlibrary` para empezar de cero. Al reiniciar la API se cargan otra vez los datos de prueba |
| `prestamos.http` | Las mismas peticiones, para ejecutarlas desde VS Code (REST Client) o IntelliJ |

**Primera vez:** `instalar-requisitos.cmd`, luego abrir una terminal nueva y ejecutar `iniciar-api.cmd`.

**Demostración limpia:** `reiniciar-datos.cmd`, luego `iniciar-api.cmd` y, en otra terminal, `probar-api.cmd`.

Pruebas automáticas (no necesitan MongoDB; no hace falta instalar Maven porque el proyecto trae Maven Wrapper):

```bash
cd smartlibrary
mvnw.cmd test        # en Linux/Mac: ./mvnw test
```

**Conexión a MongoDB:** por defecto la API usa `mongodb://localhost:27017/smartlibrary`. Para usar **MongoDB Atlas**, define la variable de entorno `MONGODB_URI` con tu cadena de conexión. No escribas usuarios ni contraseñas en `application.properties`.

Al arrancar se cargan los estudiantes `est-1` y `est-2` y los libros `lib-1`, `lib-2` y `lib-3`, todos disponibles. Para ver los datos: `mongosh mongodb://localhost:27017/smartlibrary` y luego `db.prestamos.find()`.

### Endpoints

| Método | Ruta | Cuerpo | Respuestas |
|---|---|---|---|
| POST | `/api/prestamos` | `{"estudianteId": "est-1", "libroId": "lib-1"}` | 201, 400, 404, 409 |
| GET | `/api/prestamos` | — | 200 |

## Reglas del caso

| Regla | Descripción |
|---|---|
| R1 | Solo un libro disponible puede prestarse |
| R2 | El préstamo registra estudiante, libro y fecha |
| R3 | Al prestarlo, el libro deja de estar disponible |
| R4 | Una solicitud inválida no debe crear préstamo |
| R5 | Un libro puede tener varios préstamos históricos, pero solo uno activo |

## Actividad 1 · Modelo inicial anotado

![Modelo inicial anotado](diagramas/imagenes/01_Modelo_Inicial_Anotado.png)

- `GestorPrestamos` tiene baja cohesión: guarda colecciones en memoria y además coordina el caso de uso. No hay capa HTTP ni persistencia.
- Libro–Prestamo y Estudiante–Prestamo son **asociaciones, no composiciones**: el libro y el estudiante existen antes y después del préstamo, y borrar un préstamo no los borra.
- Faltan un `PrestamoController` (entrada y salida HTTP) y repositorios (acceso a datos).

## Actividad 2 · Tabla de responsabilidades

| Operación o decisión | Clase responsable | Justificación |
|---|---|---|
| Recibir `POST /api/prestamos` | PrestamoController | Frontera HTTP: mapea la ruta y valida el `PrestamoRequest` con `@Valid` |
| Buscar estudiante por identificador | EstudianteRepository (lo invoca PrestamoService) | Acceso a datos de una sola colección (`findById`) |
| Buscar libro por identificador | LibroRepository (lo invoca PrestamoService) | Acceso a datos de una sola colección (`findById`) |
| Validar disponibilidad | PrestamoService + Libro | Regla de negocio (R1, R5). El dato y la verificación pertenecen a `Libro` (Experto en información) |
| Crear el préstamo | PrestamoService | Creador (GRASP): tiene estudiante, libro y fecha (R2) |
| Cambiar disponibilidad | `Libro.marcarPrestado()` | Experto en información: el libro protege su propio estado (R3) |
| Guardar cambios | PrestamoRepository y LibroRepository | `save()` en MongoDB, después de validar todo (R4). Primero el préstamo y luego el libro |
| Devolver respuesta HTTP | PrestamoController y ManejadorExcepciones | 201 Created; las excepciones se traducen a 404 y 409, y los datos inválidos a 400 |

## Actividad 3 · Diagrama de clases por capas

![Diagrama de clases por capas](diagramas/imagenes/02_Diagrama_Clases_Por_Capas.png)

- **Entre capas:** asociaciones dirigidas 1 a 1, porque cada capa guarda la siguiente como atributo inyectado. El Controller nunca usa un Repository.
- **Dominio:** `Prestamo` 0..* → 1 `Estudiante` y `Prestamo` 0..* → 1 `Libro`, navegables desde `Prestamo`. En MongoDB se guardan como referencias por id.
- **Repositorios:** interfaces que extienden `MongoRepository` (generalización). No hay realización, porque Spring Data genera la implementación.
- **Dependencias «use», «create» y «throw»:** se usan para los DTO, la creación de `Prestamo` y las excepciones, que son usos puntuales sin atributo.

## Actividad 4 · Diagrama de secuencia

![Secuencia Registrar préstamo](diagramas/imagenes/03_Secuencia_Registrar_Prestamo.png)

El Service busca al estudiante y el libro, verifica `isDisponible()` y consulta `existsByLibroIdAndFechaDevolucionIsNull` (R5). Después, un fragmento `alt` separa dos caminos: **préstamo exitoso** (201 Created) y **libro no disponible o con préstamo activo** (409 Conflict, sin escribir nada). Cada mensaje está enlazado a una operación del diagrama de clases y sigue el mismo orden que `PrestamoService.registrarPrestamo`.

## Actividades 5 y 6 · Pruebas

| Escenario | Resultado esperado | Prueba automática | API real con MongoDB |
|---|---|---|---|
| Préstamo de libro disponible | 201 Created y préstamo registrado | ✅ | ✅ 201 |
| Libro ya prestado | 409 Conflict, sin préstamo nuevo | ✅ | ✅ 409 |
| Estudiante inexistente | 404 Not Found | ✅ | ✅ 404 |
| Libro inexistente | 404 Not Found | ✅ | ✅ 404 |
| Consulta de préstamos | 200 OK con registros | ✅ | ✅ 200 |
| Solicitud sin `libroId` (extra, R4) | 400 Bad Request | ✅ | ✅ 400 |
| Segundo préstamo activo del mismo libro (extra, R5) | 409 Conflict | ✅ | ✅ 409 |

- **`PrestamoControllerTest`** comprueba los códigos HTTP.
- **`PrestamoServiceTest`** comprueba las reglas R1 a R5.
- **`evidencias/pruebas_api.md`** guarda las peticiones reales y los documentos que quedaron en MongoDB.

## Actividad 7 · Defensa

- **Patrón GRASP:** Controlador. Está en `PrestamoService`, que coordina el caso de uso. El `@RestController` solo traduce HTTP, así que no es el controlador GRASP.
- **Regla de disponibilidad:** está en la capa de negocio. El servicio la verifica antes de escribir y `Libro.marcarPrestado()` protege el estado del libro.
- **Trazabilidad:** requisito → clase → operación → prueba. Por ejemplo: R1 → `Libro` → `isDisponible()` / `marcarPrestado()` → prueba "libro ya prestado devuelve 409".

La versión completa de todas las respuestas está en [`solucion/taller_resuelto.html`](solucion/taller_resuelto.html).
