# Authentication Documentation

## API Authentication

This document outlines how to authenticate with the Refereezy API using various authentication methods.

## Available Authentication Methods

The API supports the following authentication methods:

1. **JWT Token-based Authentication** - Primary method for applications
2. **API Key Authentication** - For server-to-server communication
3. **Session-based Authentication** - For web applications

## Authentication Flow

```mermaid
sequenceDiagram
    participant Client
    participant API
    participant Database
    Client->>API: POST /auth/login (credentials)
    API->>Database: Verify credentials
    Database-->>API: Credentials valid
    API->>API: Generate JWT token
    API-->>Client: Return token and refresh token
    Note over Client,API: For subsequent requests
    Client->>API: Request with token in header
    API->>API: Validate token
    API-->>Client: Response
```

## JWT Authentication

### Obtaining a Token

To obtain a JWT token, send a POST request to the login endpoint:

```
POST /auth/login
```

**Request:**
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
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "bearer",
  "expires_in": 3600
}
```

### Using the Token

Include the JWT token in the Authorization header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Refresh

When the access token expires, use the refresh token to obtain a new one:

```
POST /auth/refresh
```

**Request:**
```json
{
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:**
```json
{
  "access_token": "neweyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "neweyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "bearer",
  "expires_in": 3600
}
```

## API Key Authentication

### Obtaining an API Key

API keys are generated through the administrative interface. Contact the system administrator to request an API key.

### Using the API Key

Include the API key in the request header:

```
X-API-Key: your-api-key-here
```

## Error Handling

### Common Authentication Errors

- **401 Unauthorized** - Invalid credentials or token
- **403 Forbidden** - Valid authentication but insufficient permissions
- **422 Unprocessable Entity** - Malformed request

### Error Response Format

```json
{
  "status": "error",
  "code": 401,
  "message": "Invalid or expired token",
  "details": "The provided token has expired. Please obtain a new token."
}
```

## Security Considerations

1. **Token Storage**
   - Store tokens securely (e.g., HttpOnly cookies)
   - Never store in localStorage or sessionStorage

2. **Token Expiration**
   - Short-lived access tokens (1 hour)
   - Longer-lived refresh tokens (2 weeks)

3. **Transport Security**
   - Always use HTTPS
   - Enforce TLS 1.2 or higher

## Code Examples

### Authentication in JavaScript

```javascript
// Login and get token
async function login(email, password) {
  const response = await fetch('https://api.refereezy.com/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ email, password })
  });
  
  if (!response.ok) {
    throw new Error('Authentication failed');
  }
  
  const data = await response.json();
  return data.access_token;
}

// Use token for API requests
async function fetchMatches(token) {
  const response = await fetch('https://api.refereezy.com/matches', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return response.json();
}
```

### Authentication in Python

```python
import requests

def login(email, password):
    response = requests.post(
        'https://api.refereezy.com/auth/login',
        json={'email': email, 'password': password}
    )
    
    if response.status_code != 200:
        raise Exception('Authentication failed')
        
    data = response.json()
    return data['access_token']

def fetch_matches(token):
    response = requests.get(
        'https://api.refereezy.com/matches',
        headers={'Authorization': f'Bearer {token}'}
    )
    
    return response.json()
```

### Authentication in Mobile Apps

```swift
// Swift example for iOS
func login(email: String, password: String, completion: @escaping (Result<String, Error>) -> Void) {
    let url = URL(string: "https://api.refereezy.com/auth/login")!
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.addValue("application/json", forHTTPHeaderField: "Content-Type")
    
    let body: [String: String] = ["email": email, "password": password]
    request.httpBody = try? JSONSerialization.data(withJSONObject: body)
    
    URLSession.shared.dataTask(with: request) { data, response, error in
        // Handle response and extract token
    }.resume()
}
```

## Role-Based Access Control

The API implements role-based access to restrict access to certain endpoints:

| Role | Description | Access Level |
|------|-------------|--------------|
| admin | System administrator | Full access to all endpoints |
| referee | Match official | Access to match management and reporting |
| user | Regular user | Limited access to own data and public information |

---

*Note for documentation contributors: Add detailed information about token payload structure, security best practices, and integration examples for various platforms.*
