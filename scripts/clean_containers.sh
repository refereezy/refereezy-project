#!/bin/bash

# Script para eliminar todos los contenedores del proyecto Refereezy
# y limpiar recursos Docker no utilizados

# Directorio base del proyecto (relativo al directorio del script)
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SCRIPT_DIR="$BASE_DIR/scripts"

echo "=== Deteniendo y eliminando contenedores ==="
cd "$SCRIPT_DIR"
docker-compose down

echo "=== Eliminando contenedores específicos ==="
docker rm -f docu sockets testdb testapi api 2>/dev/null || true

echo "=== Limpiando recursos Docker no utilizados ==="
docker system prune -f

echo "=== Proceso completado ==="
echo "Todos los contenedores han sido eliminados y los recursos no utilizados han sido limpiados."
