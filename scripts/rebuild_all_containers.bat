@echo off
REM Script para reconstruir todas las imágenes Docker y reiniciar los contenedores
REM para el proyecto Refereezy en Windows

echo === Deteniendo los contenedores actuales ===
cd /d "%~dp0"
docker-compose down

echo === Reconstruyendo las imágenes ===

REM Reconstruir imagen para mkdocs (Documentation/Dockerfile)
echo Reconstruyendo mi-mkdocs...
cd /d "%~dp0..\Documentation"
docker build -t mi-mkdocs .

REM Reconstruir imagen para sockets (APPS/web/Dockerfile)
echo Reconstruyendo socket-server:latest...
cd /d "%~dp0..\APPS\web"
docker build -t socket-server:latest .

REM Reconstruir imagen para test-db (API/test-db/Dockerfile)
echo Reconstruyendo test-db:latest...
cd /d "%~dp0..\API\test-db"
docker build -t test-db:latest .

REM Reconstruir imagen para test-api (API/Dockerfile)
echo Reconstruyendo api-app:test...
cd /d "%~dp0..\API"
docker build -t api-app:test .

REM Reconstruir imagen para api (API/Dockerfile)
echo Reconstruyendo api-app:latest...
cd /d "%~dp0..\API"
docker build -t api-app:latest .

REM Eliminar imágenes no utilizadas
echo Limpiando imágenes no utilizadas...
docker image prune -f

REM Reiniciar contenedores
echo === Reiniciando contenedores ===
cd /d "%~dp0"
docker-compose up -d

echo === Proceso completado ===
echo Los contenedores han sido reconstruidos y reiniciados

REM Mostrar contenedores en ejecución
docker ps

pause
