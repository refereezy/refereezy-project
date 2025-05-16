#!/bin/bash

# Script para reconstruir todas las imágenes Docker y reiniciar los contenedores
# para el proyecto Refereezy

# Directorio base del proyecto (relativo al directorio del script)
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SCRIPT_DIR="$BASE_DIR/scripts"

echo "=== Deteniendo los contenedores actuales ==="
cd "$SCRIPT_DIR"
sudo docker-compose down

echo "=== Reconstruyendo las imágenes ==="

# Reconstruir imagen para mkdocs (Documentation/Dockerfile)
echo "Reconstruyendo mi-mkdocs..."
cd "$BASE_DIR/Documentation"
sudo docker build -t mi-mkdocs .

# Reconstruir imagen para sockets (APPS/web/Dockerfile)
echo "Reconstruyendo socket-server:latest..."
cd "$BASE_DIR/APPS/web"
sudo docker build -t socket-server:latest .

# Reconstruir imagen para test-db (API/test-db/Dockerfile)
echo "Reconstruyendo test-db:latest..."
cd "$BASE_DIR/API/test-db"
sudo docker build -t test-db:latest .

# Reconstruir imagen para test-api (API/Dockerfile)
echo "Reconstruyendo api-app:test..."
cd "$BASE_DIR/API"
sudo docker build -t api-app:test .

# Reconstruir imagen para api (API/Dockerfile)
echo "Reconstruyendo api-app:latest..."
cd "$BASE_DIR/API"
sudo docker build -t api-app:latest .

# Eliminar imágenes no utilizadas
echo "Limpiando imágenes no utilizadas..."
sudo docker image prune -f

# Reiniciar contenedores
echo "=== Reiniciando contenedores ==="
cd "$SCRIPT_DIR"
sudo docker-compose up -d

echo "=== Proceso completado ==="
echo "Los contenedores han sido reconstruidos y reiniciados"

# Mostrar contenedores en ejecución
docker ps
