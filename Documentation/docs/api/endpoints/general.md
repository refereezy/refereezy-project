# General API Endpoints

## Base Endpoints

These endpoints provide general functionality that doesn't fit into specific resource categories.

## Authentication

### Login

```
POST /auth/login
```

Authenticates a user and returns a JWT token.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "securepassword"
}
```

**Response:**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "bearer",
  "user_id": 123,
  "role": "referee"
}
```

**Status Codes:**
- `200 OK`: Authentication successful
- `401 Unauthorized`: Invalid credentials
- `422 Unprocessable Entity`: Validation error

### Register

```
POST /auth/register
```

Registers a new user account.

**Request Body:**
```json
{
  "email": "newuser@example.com",
  "password": "securepassword",
  "name": "John Doe",
  "role": "user"
}
```

**Response:**
```json
{
  "id": 124,
  "email": "newuser@example.com",
  "name": "John Doe",
  "role": "user",
  "created_at": "2023-06-01T12:00:00Z"
}
```

**Status Codes:**
- `201 Created`: User created successfully
- `400 Bad Request`: Email already exists
- `422 Unprocessable Entity`: Validation error

### Verify Token

```
GET /auth/verify
```

Verifies if the current JWT token is valid.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
{
  "valid": true,
  "user_id": 123,
  "role": "referee"
}
```

**Status Codes:**
- `200 OK`: Token is valid
- `401 Unauthorized`: Invalid or expired token

## User Management

### Get Current User

```
GET /users/me
```

Retrieves the profile of the currently authenticated user.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
{
  "id": 123,
  "email": "user@example.com",
  "name": "John Doe",
  "role": "referee",
  "profile": {
    "phone": "+1234567890",
    "address": "123 Main St",
    "profile_picture": "https://example.com/profile.jpg"
  }
}
```

**Status Codes:**
- `200 OK`: User found
- `401 Unauthorized`: Not authenticated

### Update User Profile

```
PUT /users/me
```

Updates the profile of the currently authenticated user.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body:**
```json
{
  "name": "John Smith",
  "profile": {
    "phone": "+1987654321",
    "address": "456 Oak Ave",
    "profile_picture": "https://example.com/new_profile.jpg"
  }
}
```

**Response:**
```json
{
  "id": 123,
  "email": "user@example.com",
  "name": "John Smith",
  "role": "referee",
  "profile": {
    "phone": "+1987654321",
    "address": "456 Oak Ave",
    "profile_picture": "https://example.com/new_profile.jpg"
  }
}
```

**Status Codes:**
- `200 OK`: Profile updated successfully
- `401 Unauthorized`: Not authenticated
- `422 Unprocessable Entity`: Validation error

## Status & Health

### Health Check

```
GET /health
```

Provides system health information. This endpoint is public and does not require authentication.

**Response:**
```json
{
  "status": "healthy",
  "version": "1.2.3",
  "database": "connected",
  "uptime": 86400
}
```

**Status Codes:**
- `200 OK`: System is healthy
- `503 Service Unavailable`: System is experiencing issues

### API Version

```
GET /version
```

Returns the current API version information.

**Response:**
```json
{
  "version": "1.2.3",
  "build_date": "2023-06-01T12:00:00Z",
  "environment": "production"
}
```

**Status Codes:**
- `200 OK`: Version information retrieved

## Search

### Global Search

```
GET /search?q={query}&type={type}
```

Performs a global search across multiple resources.

**Parameters:**
- `q` (string, required): Search query
- `type` (string, optional): Limit search to specific resource type (matches, teams, players, referees)

**Response:**
```json
{
  "matches": [
    {
      "id": 45,
      "name": "Team A vs Team B",
      "date": "2023-06-15T18:00:00Z"
    }
  ],
  "teams": [
    {
      "id": 23,
      "name": "Team A",
      "logo": "https://example.com/logo.png"
    }
  ],
  "players": [
    {
      "id": 156,
      "name": "John Player",
      "team_id": 23
    }
  ]
}
```

**Status Codes:**
- `200 OK`: Search results retrieved
- `400 Bad Request`: Invalid search parameters

---

*Note for documentation contributors: Add request and response examples for all endpoints. Include details about rate limiting, pagination, error handling, and filter parameters where applicable.*
