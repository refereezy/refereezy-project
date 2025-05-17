# Docker Containers

## Docker Configuration

Refereezy uses Docker and Docker Compose to create a consistent development and deployment environment. This document explains how to work with the Docker infrastructure in the project.

## Available Containers

The project contains the following Docker containers:

1. **API** - FastAPI backend for production
   - Image: `api-app:latest`
   - Port: 8080
   - Profile: `prod`

2. **Test API** - FastAPI backend for testing
   - Image: `api-app:test`
   - Port: 8888
   - Profile: `test`

3. **Test Database** - PostgreSQL database for testing
   - Image: `test-db:latest`
   - Port: 5433
   - Profile: `test`

4. **Socket Server** - Socket.IO server for real-time communication
   - Image: `socket-server:latest`
   - Port: 3000
   - Profile: `common`

5. **MkDocs** - Documentation server
   - Image: `mi-mkdocs`
   - Port: 8000
   - Profile: `common`

## Container Profiles

The Docker Compose configuration uses profiles to selectively run services:

- **common**: Always-on services (documentation, sockets)
- **test**: Testing environment services (test-db, test-api)
- **prod**: Production services (api)

## Running Containers

### Using Scripts

For convenience, use the provided scripts:

#### Windows
```powershell
# Run all containers
.\scripts\rebuild_all_containers.bat

# Run specific profiles
.\scripts\rebuild_all_containers.bat common test

# Clean up containers
.\scripts\clean_containers.bat
```

#### Linux/Mac
```bash
# Run all containers
./scripts/rebuild_all_containers.sh

# Run specific profiles
./scripts/rebuild_all_containers.sh common test

# Clean up containers
./scripts/clean_containers.sh
```

### Manual Docker Compose Commands

You can also use Docker Compose directly:

```bash
# Start all services
docker-compose --profile common --profile test --profile prod up -d

# Start only common and test services
docker-compose --profile common --profile test up -d

# Stop all services
docker-compose down
```

## Building Images Manually

If you need to rebuild specific images:

```bash
# MkDocs image
cd Documentation
docker build -t mi-mkdocs .

# Socket server image
cd APPS/web
docker build -t socket-server:latest .

# Test database image
cd API/test-db
docker build -t test-db:latest .

# API image (test version)
cd API
docker build -t api-app:test .

# API image (production version)
cd API
docker build -t api-app:latest .
```

## Environment Variables

Docker containers require environment variables to function properly. Create a `.env` file in the `scripts` directory based on the template in `.env.sample`.

Required variables include:

- Database configuration
- API ports and connections
- Firebase credentials
- Cloudinary credentials (if used)
- Socket.IO configuration

## Troubleshooting

### Common Issues

1. **Port Conflicts**
   - Error: "Bind for 0.0.0.0:XXXX failed: port is already allocated"
   - Solution: Check for services using the port and stop them or change the port in docker-compose.yml

2. **Container Name Conflicts**
   - Error: "The container name is already in use"
   - Solution: Run the cleanup script or manually remove containers with `docker rm`

3. **Network Issues**
   - If containers can't communicate, check the network configuration
   - Use `docker network ls` and `docker network inspect` to troubleshoot

4. **Volume Persistence**
   - Data not persisting between container restarts
   - Check volume configurations in docker-compose.yml

### Viewing Logs

To view container logs:

```bash
# View logs for a specific container
docker logs [container_name]

# Follow logs in real time
docker logs -f [container_name]
```

---

*Note: Expand this documentation with specific examples for each container, typical workflows, and detailed explanations of volume mappings and network configurations.*
