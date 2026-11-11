@echo off
REM Ejecuta los escenarios de la Actividad 6 contra la API en http://localhost:8080.
REM Para obtener los resultados esperados, parte de datos limpios:
REM   1) reiniciar-datos.cmd   2) iniciar-api.cmd   3) probar-api.cmd
setlocal
set API=http://localhost:8080/api/prestamos
set H=-H "Content-Type: application/json"

echo === 1. Prestamo de libro disponible (esperado 201 Created)
curl -s -w "  HTTP %%{http_code}\n" -X POST %API% %H% -d "{\"estudianteId\":\"est-1\",\"libroId\":\"lib-1\"}"
echo.
echo === 2. Libro ya prestado (esperado 409 Conflict)
curl -s -w "  HTTP %%{http_code}\n" -X POST %API% %H% -d "{\"estudianteId\":\"est-2\",\"libroId\":\"lib-1\"}"
echo.
echo === 3. Estudiante inexistente (esperado 404 Not Found)
curl -s -w "  HTTP %%{http_code}\n" -X POST %API% %H% -d "{\"estudianteId\":\"est-99\",\"libroId\":\"lib-2\"}"
echo.
echo === 4. Libro inexistente (esperado 404 Not Found)
curl -s -w "  HTTP %%{http_code}\n" -X POST %API% %H% -d "{\"estudianteId\":\"est-1\",\"libroId\":\"lib-99\"}"
echo.
echo === 5. Consulta de prestamos (esperado 200 OK con registros)
curl -s -w "  HTTP %%{http_code}\n" %API%
echo.
echo === Extra. Solicitud sin libroId (esperado 400 Bad Request, R4)
curl -s -w "  HTTP %%{http_code}\n" -X POST %API% %H% -d "{\"estudianteId\":\"est-1\"}"
echo.
echo === Extra. R5: se fuerza disponible=true en lib-1 y se intenta otro prestamo (esperado 409)
mongosh --quiet "mongodb://localhost:27017/smartlibrary" --eval "db.libros.updateOne({_id:'lib-1'},{$set:{disponible:true}})" >nul
curl -s -w "  HTTP %%{http_code}\n" -X POST %API% %H% -d "{\"estudianteId\":\"est-2\",\"libroId\":\"lib-1\"}"
echo.
echo === Documentos guardados en MongoDB
mongosh --quiet "mongodb://localhost:27017/smartlibrary" --eval "printjson(db.prestamos.find().toArray()); printjson(db.libros.find().toArray())"
endlocal
