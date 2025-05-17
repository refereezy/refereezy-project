# Authentication

## Authentication System

The Refereezy API uses JSON Web Tokens (JWT) for authentication, providing a secure and stateless method for verifying user identity.

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
    API-->>Client: Return token
    Note over Client,API: For subsequent requests
    Client->>API: Request with token in header
    API->>API: Validate token
    API-->>Client: Response
```

## Registration and Login

### User Registration

New users must register before accessing the API. Registration is handled by the following endpoint:

```
POST /auth/register
```

See the [General Endpoints](general.md) documentation for detailed request and response formats.

### User Login

To obtain an authentication token, users must send their credentials to:

```
POST /auth/login
```

Upon successful authentication, the server returns a JWT token that must be included in subsequent requests.

## Token Format

The Refereezy API uses signed JWTs with the following claims:

- **sub**: User ID
- **role**: User role (admin, referee, user)
- **exp**: Token expiration timestamp
- **iat**: Token issuance timestamp

Example token payload:

```json
{
  "sub": "123",
  "role": "referee",
  "exp": 1640995200,
  "iat": 1640908800
}
```

## Authentication Methods

### Bearer Token

Include the token in the Authorization header of all protected requests:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### API Key Authentication

For server-to-server integration, API keys can be used:

```
X-API-Key: your_api_key_here
```

API keys should be requested from the Refereezy administration team and stored securely.

## Role-Based Access Control

The API implements role-based access control (RBAC) to determine user permissions:

- **admin**: Full access to all endpoints and operations
- **referee**: Access to match management, report generation, and incidents
- **user**: Limited access to public data and own user information

Each API endpoint specifies the required roles in its documentation.

## Token Expiration and Renewal

Tokens are valid for 24 hours by default. To avoid disruption of service, clients should:

1. Store the token's expiration time
2. Renew the token before it expires
3. Handle expired token errors gracefully

To renew a token without requiring the user to log in again:

```
POST /auth/refresh
```

Include the current token in the Authorization header. If valid, a new token will be issued.

## Security Considerations

### Token Storage

Clients should store tokens securely:

- Mobile applications: Use secure storage APIs
- Web applications: Use HttpOnly cookies or secure client-side storage
- Never store tokens in local storage or session storage in web browsers

### HTTPS Requirement

All API requests must use HTTPS to ensure token security during transmission.

### Token Revocation

To invalidate a token before its expiration:

```
POST /auth/logout
```

This adds the token to a blacklist until its natural expiration.

## Sample Code

### Authentication in JavaScript

```javascript
async function login(email, password) {
  const response = await fetch('https://api.refereezy.com/v1/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ email, password })
  });
  
  const data = await response.json();
  
  if (response.ok) {
    // Store token securely
    localStorage.setItem('token', data.access_token);
    return true;
  } else {
    console.error('Login failed:', data.message);
    return false;
  }
}

async function callProtectedEndpoint() {
  const token = localStorage.getItem('token');
  
  const response = await fetch('https://api.refereezy.com/v1/matches', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  if (response.status === 401) {
    // Token expired or invalid
    // Redirect to login
  }
  
  return await response.json();
}
```

### Authentication in Python

```python
import requests

def login(email, password):
    response = requests.post(
        'https://api.refereezy.com/v1/auth/login',
        json={'email': email, 'password': password}
    )
    
    if response.status_code == 200:
        data = response.json()
        return data['access_token']
    else:
        print(f"Login failed: {response.json().get('message')}")
        return None

def call_protected_endpoint(token):
    headers = {'Authorization': f'Bearer {token}'}
    response = requests.get(
        'https://api.refereezy.com/v1/matches',
        headers=headers
    )
    
    if response.status_code == 401:
        # Token expired or invalid
        # Handle accordingly
        return None
    
    return response.json()
```

---

*Note for documentation contributors: Add detailed error handling examples, token validation processes, and security best practices. Include information about how authentication integrates with the Firebase real-time features.*
