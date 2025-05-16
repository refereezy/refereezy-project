
# Scripts para Refereezy

Este directorio contiene scripts y archivos de configuración para gestionar los contenedores Docker del proyecto Refereezy.

## Requisitos previos

- [Docker](https://www.docker.com/get-started) instalado y funcionando
- [Docker Compose](https://docs.docker.com/compose/install/) (incluido con Docker Desktop en Windows y Mac)

## Estructura del proyecto

El proyecto Refereezy está compuesto por varios servicios:

- **mkdocs**: Documentación del proyecto (puerto 8000)
- **sockets**: Servidor Socket.io para la web en tiempo real (puerto 3000)
- **test-db**: Base de datos PostgreSQL para pruebas (puerto 5433)
- **test-api**: API para entorno de pruebas (puerto 8888)
- **api**: API para entorno de producción (puerto 8080)

## Archivos principales

- **docker-compose.yml**: Configuración de todos los servicios
- **rebuild_all_containers.bat**: Script para Windows que reconstruye y reinicia todos los contenedores
- **restar_all_containers.sh**: Script para Linux/Mac que reconstruye y reinicia todos los contenedores

## Pasos para ejecutar los contenedores

### Opción 1: Usando los scripts automáticos

#### En Windows:

1. Abre una terminal (PowerShell o CMD) como administrador.
2. Navega hasta este directorio:
   ```powershell
   cd path\to\refereezy-project\scripts
   ```
3. Ejecuta el script de Windows:
   ```powershell
   .\rebuild_all_containers.bat
   ```

#### En Linux/Mac:

1. Abre una terminal.
2. Navega hasta este directorio:
   ```bash
   cd path/to/refereezy-project/scripts
   ```
3. Dale permisos de ejecución al script (solo la primera vez):
   ```bash
   chmod +x restar_all_containers.sh
   ```
4. Ejecuta el script:
   ```bash
   ./restar_all_containers.sh
   ```

### Opción 2: Ejecución manual paso a paso

Si prefieres ejecutar los pasos manualmente:

1. **Construir las imágenes individualmente**:

   ```bash
   # Imagen mkdocs
   cd ../Documentation
   docker build -t mi-mkdocs .

   # Imagen socket-server
   cd ../APPS/web
   docker build -t socket-server:latest .

   # Imagen test-db
   cd ../API/test-db
   docker build -t test-db:latest .

   # Imagen api-app (versión test)
   cd ../API
   docker build -t api-app:test .

   # Imagen api-app (versión producción)
   cd ../API
   docker build -t api-app:latest .
   ```

2. **Iniciar los contenedores**:

   ```bash
   cd ../scripts
   docker-compose up -d
   ```

3. **Verificar que los contenedores estén funcionando**:

   ```bash
   docker ps
   ```

## Acceso a los servicios

Una vez que los contenedores estén en ejecución, puedes acceder a los servicios en:

- Documentación: [http://localhost:8000](http://localhost:8000)
- API de pruebas: [http://localhost:8888](http://localhost:8888)
- API de producción: [http://localhost:8080](http://localhost:8080)

## Detener los contenedores

Para detener todos los contenedores:

```bash
cd path/to/refereezy-project/scripts
docker-compose down
```

## Solución de problemas

Si encuentras problemas con los contenedores:

1. Verifica los logs:
   ```bash
   docker-compose logs
   ```

2. Para un servicio específico:
   ```bash
   docker-compose logs [nombre-servicio]
   ```

3. Reinicia un servicio específico:
   ```bash
   docker-compose restart [nombre-servicio]
   ```

## Variables de entorno

El archivo docker-compose.yml utiliza variables de entorno que deben estar definidas. Debes crear un archivo `.env` en este directorio usando el ejemplo de `.env.sample`

```sh
# api
API_PORT=<api_port>
DB_USER=<db_username>
DB_PASS=<db_password>
DB_PORT=<db_port>
DB_NAME=<db_name>
DB_HOST=<db_host>

# test-db & test-api
TEST_DB_USER=<test_db_username>
TEST_DB_PASS=<test_db_password>
TEST_DB_PORT=<test_db_port>
TEST_DB_NAME=<test_db_name>
TEST_DB_HOST=<test_db_host>

# mkdocs
MKDOCS_PORT=<mkdocs_port>

# socket.io
REFEREEZY_API_URL=<api_url>
SOCKET_IO_PORT=<socket_port>

# Cloudinary
CLOUDINARY_NAME=<cloudinary_name>
CLOUDINARY_API_KEY=<cloudinary_api_key>
CLOUDINARY_API_SECRET=<cloudinary_api_secret>

# Firebase config
FIREBASE_KEY=<firebase_key>
FIREBASE_AUTH_DOMAIN=<firebase_auth_domain>
FIREBASE_PROJECT_ID=<firebase_project_id>
FIREBASE_STORAGE_BUCKET=<firebase_storage_bucket>
FIREBASE_MESSAGING_SENDER_ID=<firebase_messaging_sender_id>
FIREBASE_APP_ID=<firebase_app_id>
FIREBASE_MEASUREMENT_ID=<firebase_measurement_id>
FIREBASE_CLIENT_EMAIL=<firebase_client_email>


```

