# Referees API Endpoints

This document provides comprehensive documentation for the Referees API endpoints in the Refereezy platform.

## Base URL

```
/referee
```

## Request Format

All requests should include appropriate headers:

```
Accept: application/json
Content-Type: application/json
```

For endpoints requiring authentication, include:

```
Authorization: Bearer YOUR_JWT_TOKEN
```

## Authentication Endpoints

### Login Referee

Authenticates a referee with their credentials.

**Endpoint:** `POST /referee/login`

**Request Body:**

```json
{
  "dni": "12345678X",
  "password": "password123"
}
```

**Success Response (200 OK):**

```json
{
  "id": 1,
  "name": "Carlos Velasco",
  "dni": "12345678X",
  "client_id": 1,
  "clock_code": "ABC123",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Error Response (401 Unauthorized):**

```json
{
  "detail": "Invalid DNI or password"
}
```

### Load Referee by Token

Loads a referee using their ID and authentication token.

**Endpoint:** `POST /referee/load`

**Request Body:**

```json
{
  "id": 1,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Success Response (200 OK):**

```json
{
  "id": 1,
  "name": "Carlos Velasco",
  "dni": "12345678X",
  "client_id": 1,
  "clock_code": "ABC123",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Error Response (404 Not Found):**

```json
{
  "detail": "Referee not found"
}
```

**Error Response (401 Unauthorized):**

```json
{
  "detail": "Unauthorized: Invalid token"
}
```

## Basic CRUD Endpoints

### Get All Referees

Retrieves all referees registered in the system.

**Endpoint:** `GET /referee`

**Authentication:** Required

**Response:**

```json
[
  {
    "id": 1,
    "name": "Carlos Velasco",
    "dni": "12345678X",
    "client_id": 1,
    "clock_code": "ABC123"
  },
  {
    "id": 2,
    "name": "María López",
    "dni": "87654321Y",
    "client_id": 1,
    "clock_code": null
  }
]
```

### Get Referee by ID

Retrieves a specific referee by their ID.

**Endpoint:** `GET /referee/{referee_id}`

**Parameters:**
- `referee_id` (path): The unique ID of the referee

**Authentication:** Required

**Success Response (200 OK):**

```json
{
  "id": 1,
  "name": "Carlos Velasco",
  "dni": "12345678X",
  "client_id": 1,
  "clock_code": "ABC123"
}
```

**Error Response (404 Not Found):**

```json
{
  "detail": "Referee not found"
}
```

### Get Referees by Client

Retrieves all referees belonging to a specific client.

**Endpoint:** `GET /referee/client/{client_id}`

**Parameters:**
- `client_id` (path): The unique ID of the client

**Authentication:** Required

**Success Response (200 OK):**

```json
[
  {
    "id": 1,
    "name": "Carlos Velasco",
    "dni": "12345678X",
    "client_id": 1,
    "clock_code": "ABC123"
  },
  {
    "id": 2,
    "name": "María López",
    "dni": "87654321Y",
    "client_id": 1,
    "clock_code": null
  }
]
```

**Error Response (404 Not Found):**

```json
{
  "detail": "Client not found"
}
```

### Create Referee

Creates a new referee.

**Endpoint:** `POST /referee`

**Authentication:** Required

**Request Body:**

```json
{
  "name": "Jorge Fernández",
  "dni": "23456789Z",
  "client_id": 1,
  "password": "password123"
}
```

**Success Response (200 OK):**

```json
{
  "id": 3,
  "name": "Jorge Fernández",
  "dni": "23456789Z",
  "client_id": 1,
  "clock_code": null
}
```

**Error Response (400 Bad Request):**

```json
{
  "detail": "Invalid data format"
}
```

### Update Referee

Updates an existing referee.

**Endpoint:** `PUT /referee/{referee_id}`

**Parameters:**
- `referee_id` (path): The unique ID of the referee to update

**Authentication:** Required

**Request Body:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "new_password": "newpassword123"
}
```

**Success Response (200 OK):**

```json
{
  "id": 1,
  "name": "Carlos Velasco",
  "dni": "12345678X",
  "client_id": 1,
  "clock_code": "ABC123"
}
```

**Error Response (404 Not Found):**

```json
{
  "detail": "Referee not found"
}
```

### Update Referee Password

Updates a referee's password.

**Endpoint:** `PATCH /referee/{referee_id}/password`

**Parameters:**
- `referee_id` (path): The unique ID of the referee

**Authentication:** Required

**Request Body:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "new_password": "newpassword123"
}
```

**Success Response (200 OK):**

```json
{
  "id": 1,
  "name": "Carlos Velasco",
  "dni": "12345678X",
  "client_id": 1,
  "clock_code": "ABC123",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Error Response (404 Not Found):**

```json
{
  "detail": "Referee not found"
}
```

### Delete Referee

Deletes a referee from the system.

**Endpoint:** `DELETE /referee/{referee_id}`

**Parameters:**
- `referee_id` (path): The unique ID of the referee to delete

**Authentication:** Required

**Success Response (200 OK):**

```json
{
  "message": "Referee deleted"
}
```

**Error Response (404 Not Found):**

```json
{
  "detail": "Referee not found"
}
```

## Match-Related Endpoints

### Get Referee Matches

Retrieves all matches assigned to a specific referee.

**Endpoint:** `GET /referee/{referee_id}/matches`

**Parameters:**
- `referee_id` (path): The unique ID of the referee

**Authentication:** Required

**Success Response (200 OK):**

```json
[
  {
    "id": 1,
    "date": "2023-06-15T18:00:00",
    "matchgroup_id": 1,
    "client_id": 1,
    "referee_id": 1,
    "local_team_id": 1,
    "visitor_team_id": 2
  },
  {
    "id": 3,
    "date": "2023-06-22T20:00:00",
    "matchgroup_id": 1,
    "client_id": 1,
    "referee_id": 1,
    "local_team_id": 3,
    "visitor_team_id": 4
  }
]
```

**Error Response (404 Not Found):**

```json
{
  "detail": "Referee not found"
}
```

## Data Models

### RefereeCreate

Properties used when creating a referee:

```json
{
  "name": "string",
  "dni": "string",
  "client_id": 0,
  "password": "string"
}
```

### RefereeLogin

Properties used for referee login:

```json
{
  "dni": "string",
  "password": "string"
}
```

### RefereeLoad

Properties used to load a referee with token:

```json
{
  "id": 0,
  "token": "string"
}
```

### RefereeUpdate

Properties used when updating a referee:

```json
{
  "token": "string",
  "new_password": "string"
}
```

### RefereeResponse

Properties returned in referee responses (public information):

```json
{
  "id": 0,
  "name": "string",
  "dni": "string",
  "client_id": 0,
  "clock_code": "string"
}
```

### RefereeData

Properties returned after authentication (includes token):

```json
{
  "id": 0,
  "name": "string",
  "dni": "string",
  "client_id": 0,
  "clock_code": "string",
  "token": "string"
}
```

## Implementation Notes

- Passwords are hashed before storing in the database
- The `clock_code` is used to associate a referee with a specific match clock
- When a referee is deleted, all their associated matches are also deleted (cascade delete)
- The token is generated upon login and is used for authentication in subsequent requests

## Example Usage

### JavaScript Fetch API Example

```javascript
// Login as a referee
async function loginReferee(dni, password) {
  try {
    const response = await fetch('http://api.refereezy.com/referee/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify({
        dni: dni,
        password: password
      })
    });
    
    if (!response.ok) {
      throw new Error('Login failed');
    }
    
    const referee = await response.json();
    return referee;
  } catch (error) {
    console.error('Error during login:', error);
    throw error;
  }
}
```

### Auto-Login with Stored Token

```javascript
async function autoLoginReferee(id, token) {
  try {
    const response = await fetch('http://api.refereezy.com/referee/load', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify({
        id: id,
        token: token
      })
    });
    
    if (!response.ok) {
      throw new Error('Auto-login failed');
    }
    
    const referee = await response.json();
    return referee;
  } catch (error) {
    console.error('Error during auto-login:', error);
    throw error;
  }
}
```
