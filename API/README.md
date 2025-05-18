# Refereezy API

This directory contains the FastAPI backend for the Refereezy platform. The API handles static client data storage and integration with Firebase for real-time updates.

## Features

- RESTful API endpoints for matches, teams, players, referees and clock management
- PostgreSQL database for persistent data storage
- Firebase integration for real-time updates
- Docker containerization

## Quick Start

### Using Docker (Recommended)

The easiest way to run the API is using the provided Docker scripts:

1. Run the testing environment script:

```bash
./init-testing.sh   # Linux/Mac
# OR
init-testing.bat    # Windows
```

### Manual Setup

1. Configure environment variables by creating a `.env` file in the `/api` directory:

```env
DB_USER=test_db_user
DB_PASS=test_db_password
DB_PORT=5432
DB_NAME=refereezy
DB_HOST=test-db
```

2. Install dependencies:

```bash
cd api
pip install -r requirements.txt
```

3. Run the FastAPI application:

```bash
cd api
uvicorn main:app --reload
```

## API Documentation

Once the API is running, you can access the auto-generated Swagger documentation at:

- Testing: http://localhost:8888/docs
- Production: http://localhost:8080/docs

## Directory Structure

- `/api` - Main application code
  - `main.py` - Application entry point
  - `models.py` - Database models
  - `schemas.py` - Pydantic schemas
  - `database.py` - Database connection setup
  - `/routers` - API endpoint routers
- `/test-db` - Test database setup scripts
- `/docs` - API documentation assets

## Development

For more detailed information, see the [Development Documentation](../Documentation/docs/development/getting-started.md).
