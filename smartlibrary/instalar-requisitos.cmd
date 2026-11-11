@echo off
REM Instala lo necesario para ejecutar SmartLibrary en Windows usando winget.
REM Windows pedira permiso de administrador (UAC) para cada instalador: acepta.
REM   - MongoDB Server  (queda como servicio de Windows en el puerto 27017)
REM   - MongoDB Shell   (mongosh, para consultar la base)
REM   - JDK 21 Temurin  (Java que requiere el proyecto)

echo Instalando MongoDB Server...
winget install --id MongoDB.Server -e --accept-package-agreements --accept-source-agreements
echo Instalando MongoDB Shell (mongosh)...
winget install --id MongoDB.Shell -e --accept-package-agreements --accept-source-agreements
echo Instalando JDK 21...
winget install --id EclipseAdoptium.Temurin.21.JDK -e --accept-package-agreements --accept-source-agreements

echo.
sc query MongoDB | find "RUNNING" >nul && (echo MongoDB esta corriendo como servicio.) || (echo Revisa el servicio MongoDB: net start MongoDB)
echo Listo. Abre una terminal NUEVA para que se apliquen JAVA_HOME y PATH.
pause
