#!/bin/bash

# Script para reconstruir todas las imágenes Docker y reiniciar los contenedores
# para el proyecto Refereezy

# Directorio base del proyecto (relativo al directorio del script)
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SCRIPT_DIR="$BASE_DIR/scripts"

# Verificar si se ha proporcionado un argumento para el perfil
PROFILE=${1:-all}

echo "Perfil seleccionado: $PROFILE"
echo ""

echo "=== Deteniendo los contenedores actuales ==="
cd "$SCRIPT_DIR"
docker-compose down

echo "=== Reconstruyendo las imágenes ==="

# Reconstruir imagen para mkdocs (Documentation/Dockerfile)
echo "Reconstruyendo mi-mkdocs..."
cd "$BASE_DIR/Documentation"
docker build -t mi-mkdocs .

# Reconstruir imagen para sockets (APPS/web/Dockerfile)
echo "Reconstruyendo socket-server:latest..."
cd "$BASE_DIR/APPS/web"
docker build -t socket-server:latest .

# Determinar qué imágenes construir según el perfil
if [ "$PROFILE" = "all" ]; then
    BUILD_TEST=yes
    BUILD_PROD=yes
elif [ "$PROFILE" = "test" ]; then
    BUILD_TEST=yes
    BUILD_PROD=no
elif [ "$PROFILE" = "prod" ]; then
    BUILD_TEST=no
    BUILD_PROD=yes
else
    echo "Perfil no reconocido. Opciones válidas: all, test, prod"
    exit 1
fi

if [ "$BUILD_TEST" = "yes" ]; then
    # Reconstruir imagen para test-db (API/test-db/Dockerfile)
    echo "Reconstruyendo test-db:latest..."
    cd "$BASE_DIR/API/test-db"
    docker build -t test-db:latest .

    # Reconstruir imagen para test-api (API/Dockerfile)
    echo "Reconstruyendo api-app:test..."
    cd "$BASE_DIR/API"
    docker build -t api-app:test .
else
    echo "Omitiendo la construcción de imágenes de prueba..."
fi

if [ "$BUILD_PROD" = "yes" ]; then
    # Reconstruir imagen para api (API/Dockerfile)
    echo "Reconstruyendo api-app:latest..."
    cd "$BASE_DIR/API"
    docker build -t api-app:latest .
else
    echo "Omitiendo la construcción de imágenes de producción..."
fi

# Eliminar imágenes no utilizadas
echo "Limpiando imágenes no utilizadas..."
docker image prune -f

# Reiniciar contenedores
echo "=== Reiniciando contenedores ==="
cd "$SCRIPT_DIR"

if [ "$PROFILE" = "all" ]; then
    echo "Iniciando todos los contenedores..."
    docker-compose --profile common --profile test --profile prod up -d
elif [ "$PROFILE" = "test" ]; then
    echo "Iniciando contenedores comunes y de prueba..."
    docker-compose --profile common --profile test up -d
elif [ "$PROFILE" = "prod" ]; then
    echo "Iniciando contenedores comunes y de producción..."
    docker-compose --profile common --profile prod up -d
fi

echo "=== Proceso completado ==="
echo "Los contenedores han sido reconstruidos y reiniciados para el perfil: $PROFILE"

# Mostrar contenedores en ejecución
docker ps