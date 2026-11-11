@echo off
REM Levanta la API SmartLibrary en http://localhost:8080
REM Requiere el servicio MongoDB corriendo (se instala como servicio de Windows) y JDK 21.
cd /d "%~dp0"
sc query MongoDB | find "RUNNING" >nul || (
  echo MongoDB no esta corriendo. Inicialo con: net start MongoDB  ^(como administrador^)
  pause
  exit /b 1
)
call mvnw.cmd spring-boot:run
