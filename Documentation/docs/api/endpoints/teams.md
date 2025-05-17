# Teams API Endpoints

This document provides comprehensive documentation for the Teams API endpoints in the Refereezy platform.

## Base URL

```
/teams
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

## Endpoints

### Get All Teams

Retrieves all teams registered in the system.

**Endpoint:** `GET /teams`

**Authentication:** Required

**Response:**

```json
[
  {
    "id": 1,
    "name": "FC Barcelona",
    "primary_color": "#0000AA",
    "secondary_color": "#AA0000",
    "logo_url": "https://example.com/logos/barcelona.png",
    "client_id": 1
  },
  {
    "id": 2,
    "name": "Real Madrid",
    "primary_color": "#FFFFFF",
    "secondary_color": "#000000",
    "logo_url": "https://example.com/logos/madrid.png",
    "client_id": 1
  }
]
```

### Get Team by ID

Retrieves a specific team by its ID.

**Endpoint:** `GET /teams/{team_id}`

**Parameters:**
- `team_id` (path): The unique ID of the team

**Authentication:** Required

**Success Response (200 OK):**

```json
{
  "id": 1,
  "name": "FC Barcelona",
  "primary_color": "#0000AA",
  "secondary_color": "#AA0000",
  "logo_url": "https://example.com/logos/barcelona.png",
  "client_id": 1
}
```

**Error Response (404 Not Found):**

```json
{
  "detail": "Team not found"
}
```

### Get Teams by Client

Retrieves all teams belonging to a specific client.

**Endpoint:** `GET /teams/client/{client_id}`

**Parameters:**
- `client_id` (path): The unique ID of the client

**Authentication:** Required

**Success Response (200 OK):**

```json
[
  {
    "id": 1,
    "name": "FC Barcelona",
    "primary_color": "#0000AA",
    "secondary_color": "#AA0000",
    "logo_url": "https://example.com/logos/barcelona.png",
    "client_id": 1
  },
  {
    "id": 2,
    "name": "Real Madrid",
    "primary_color": "#FFFFFF",
    "secondary_color": "#000000",
    "logo_url": "https://example.com/logos/madrid.png",
    "client_id": 1
  }
]
```

**Error Response (404 Not Found):**

```json
{
  "detail": "Client not found"
}
```

### Create Team

Creates a new team.

**Endpoint:** `POST /teams`

**Authentication:** Required

**Request Format:**

Form data with the following fields:
- `name`: Team name
- `primary_color`: Primary color (hex code)
- `secondary_color`: Secondary color (hex code)
- `client_id`: ID of the client who owns this team
- `logo`: Team logo image file

**Success Response (200 OK):**

```json
{
  "id": 3,
  "name": "Valencia CF",
  "primary_color": "#FFFFFF",
  "secondary_color": "#000000",
  "logo_url": "https://res.cloudinary.com/refereezy/image/upload/v1234567890/logos_equipos/valencia.png",
  "client_id": 1
}
```

**Error Response (400 Bad Request):**

```json
{
  "detail": "Invalid form data"
}
```

### Update Team

Updates an existing team.

**Endpoint:** `PUT /teams/{team_id}`

**Parameters:**
- `team_id` (path): The unique ID of the team to update

**Authentication:** Required

**Request Body:**

```json
{
  "name": "Valencia CF Updated",
  "primary_color": "#FFFFFF",
  "secondary_color": "#F0A000",
  "client_id": 1
}
```

**Success Response (200 OK):**

```json
{
  "id": 3,
  "name": "Valencia CF Updated",
  "primary_color": "#FFFFFF",
  "secondary_color": "#F0A000",
  "logo_url": "https://res.cloudinary.com/refereezy/image/upload/v1234567890/logos_equipos/valencia.png",
  "client_id": 1
}
```

**Error Response (404 Not Found):**

```json
{
  "detail": "Team not found"
}
```

### Delete Team

Deletes a team from the system.

**Endpoint:** `DELETE /teams/{team_id}`

**Parameters:**
- `team_id` (path): The unique ID of the team to delete

**Authentication:** Required

**Success Response (200 OK):**

```json
{
  "message": "Team deleted"
}
```

**Error Response (404 Not Found):**

```json
{
  "detail": "Team not found"
}
```

## Data Models

### TeamCreate

Properties used when creating a team:

```json
{
  "name": "string",
  "primary_color": "string",
  "secondary_color": "string",
  "client_id": 0
}
```

### TeamResponse

Properties returned in team responses:

```json
{
  "id": 0,
  "name": "string",
  "primary_color": "string",
  "secondary_color": "string",
  "logo_url": "string",
  "client_id": 0
}
```

## Implementation Notes

- The team logo is uploaded to Cloudinary and the URL is stored in the database
- When a team is deleted, all associated players are also deleted (cascade delete)
- Team colors are stored as hex color codes (e.g., "#FF0000" for red)

## Example Usage

### JavaScript Fetch API Example

```javascript
// Get all teams for a client
async function getClientTeams(clientId) {
  try {
    const response = await fetch(`http://api.refereezy.com/teams/client/${clientId}`, {
      method: 'GET',
      headers: {
        'Authorization': 'Bearer YOUR_JWT_TOKEN',
        'Accept': 'application/json'
      }
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch teams');
    }
    
    const teams = await response.json();
    return teams;
  } catch (error) {
    console.error('Error fetching teams:', error);
    throw error;
  }
}
```

### Creating a Team with Logo Upload

```javascript
async function createTeam(teamData, logoFile) {
  try {
    const formData = new FormData();
    formData.append('name', teamData.name);
    formData.append('primary_color', teamData.primaryColor);
    formData.append('secondary_color', teamData.secondaryColor);
    formData.append('client_id', teamData.clientId);
    formData.append('logo', logoFile);
    
    const response = await fetch('http://api.refereezy.com/teams', {
      method: 'POST',
      headers: {
        'Authorization': 'Bearer YOUR_JWT_TOKEN'
      },
      body: formData
    });
    
    if (!response.ok) {
      throw new Error('Failed to create team');
    }
    
    const newTeam = await response.json();
    return newTeam;
  } catch (error) {
    console.error('Error creating team:', error);
    throw error;
  }
}
```
