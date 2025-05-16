@echo off
REM Script para eliminar todos los contenedores del proyecto Refereezy
REM y limpiar recursos Docker no utilizados

echo === Deteniendo y eliminando contenedores ===
sudo docker-compose down

echo === Eliminando contenedores específicos ===
sudo docker rm -f docu sockets testdb testapi api 2>nul

echo === Limpiando recursos Docker no utilizados ===
sudo docker system prune -f

echo === Proceso completado ===
echo Todos los contenedores han sido eliminados y los recursos no utilizados han sido limpiados.

pause
