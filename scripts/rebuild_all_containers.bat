@echo off
setlocal enabledelayedexpansion

REM Script para reconstruir todas las imagenes Docker y reiniciar los contenedores
REM para el proyecto Refereezy en Windows

REM Verificar si se ha proporcionado un argumento para el perfil
set PROFILE=all
if not "%~1"=="" set PROFILE=%~1

echo Perfil seleccionado: %PROFILE%
echo.

echo === Deteniendo los contenedores actuales ===
cd /d "%~dp0"
docker-compose down

REM Eliminar contenedores existentes para evitar conflictos de nombres
echo === Eliminando contenedores existentes que puedan causar conflictos ===
docker rm -f docu sockets testdb testapi api 2>nul
echo Contenedores eliminados o no existentes

echo === Reconstruyendo las imagenes ===

REM Reconstruir imagen para mkdocs (Documentation/Dockerfile)
echo Reconstruyendo mi-mkdocs...
cd /d "%~dp0..\Documentation"
docker build -t mi-mkdocs .

REM Reconstruir imagen para sockets (APPS/web/Dockerfile)
echo Reconstruyendo socket-server:latest...
cd /d "%~dp0..\APPS\web"
docker build -t socket-server:latest .

if "%PROFILE%"=="all" (
    set BUILD_TEST=yes
    set BUILD_PROD=yes
) else if "%PROFILE%"=="test" (
    set BUILD_TEST=yes
    set BUILD_PROD=no
) else if "%PROFILE%"=="prod" (
    set BUILD_TEST=no
    set BUILD_PROD=yes
) else (
    echo Perfil no reconocido. Opciones validas: all, test, prod
    exit /b 1
)

if "!BUILD_TEST!"=="yes" (
    REM Reconstruir imagen para test-db (API/test-db/Dockerfile)
    echo Reconstruyendo test-db:latest...
    cd /d "%~dp0..\API\test-db"
    docker build -t test-db:latest .

    REM Reconstruir imagen para test-api (API/Dockerfile)
    echo Reconstruyendo api-app:test...
    cd /d "%~dp0..\API"
    docker build -t api-app:test .
) else (
    echo Omitiendo la construcción de imagenes de prueba...
)

if "!BUILD_PROD!"=="yes" (
    REM Reconstruir imagen para api (API/Dockerfile)
    echo Reconstruyendo api-app:latest...
    cd /d "%~dp0..\API"
    docker build -t api-app:latest .
) else (
    echo Omitiendo la construcción de imagenes de producción...
)

REM Eliminar imagenes no utilizadas
echo Limpiando imagenes no utilizadas...
docker image prune -f

REM Reiniciar contenedores
echo === Reiniciando contenedores ===
cd /d "%~dp0"

if "%PROFILE%"=="all" (
    echo Iniciando todos los contenedores...
    docker-compose --profile common --profile test --profile prod up -d
) else if "%PROFILE%"=="test" (
    echo Iniciando contenedores comunes y de prueba...
    docker-compose --profile common --profile test up -d
) else if "%PROFILE%"=="prod" (
    echo Iniciando contenedores comunes y de producción...
    docker-compose --profile common --profile prod up -d
)

echo === Proceso completado ===
echo Los contenedores han sido reconstruidos y reiniciados para el perfil: %PROFILE%

REM Mostrar contenedores en ejecución
docker ps

endlocal
pause
