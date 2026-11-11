@echo off
REM Borra la base smartlibrary de MongoDB local (estudiantes, libros y prestamos).
REM Al volver a iniciar la API se cargan otra vez los datos de prueba (DatosIniciales).
mongosh --quiet "mongodb://localhost:27017/smartlibrary" --eval "db.dropDatabase(); print('Base smartlibrary borrada')"
if errorlevel 1 echo No se encontro mongosh. Ejecuta instalar-requisitos.cmd o abre una terminal nueva.
