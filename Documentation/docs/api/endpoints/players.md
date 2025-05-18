# Players API Endpoints

This document provides comprehensive documentation for the Players API endpoints in the Refereezy platform.

## Base URL

```
/players
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

### Get All Players

Retrieves all players registered in the system.

**Endpoint:** `GET /players`

**Authentication:** Required

**Response:**

```json
[
  {
    "id": 1,
    "name": "Lionel Messi",
    "dorsal_number": 10,
    "dni": "12345678A",
    "team_id": 1,
    "client_id": 1,
    "is_goalkeeper": false
  },
  {
    "id": 2,
    "name": "Jan Oblak",
    "dorsal_number": 1,
    "dni": "87654321B",
    "team_id": 3,
    "client_id": 1,
    "is_goalkeeper": true
  }
]
```

### Get Players by Client

Retrieves all players belonging to a specific client.

**Endpoint:** `GET /players/client/{client_id}`

**Parameters:**
- `client_id` (path): The unique ID of the client

**Authentication:** Required

**Success Response (200 OK):**

```json
[
  {
    "id": 1,
    "name": "Lionel Messi",
    "dorsal_number": 10,
    "dni": "12345678A",
    "team_id": 1,
    "client_id": 1,
    "is_goalkeeper": false
  },
  {
    "id": 2,
    "name": "Jan Oblak",
    "dorsal_number": 1,
    "dni": "87654321B",
    "team_id": 3,
    "client_id": 1,
    "is_goalkeeper": true
  }
]
```

**Error Response (404 Not Found):**

```json
{
  "detail": "Client not found"
}
```

### Get Player by ID

Retrieves a specific player by their ID.

**Endpoint:** `GET /players/{player_id}`

**Parameters:**
- `player_id` (path): The unique ID of the player

**Authentication:** Required

**Success Response (200 OK):**

```json
{
  "id": 1,
  "name": "Lionel Messi",
  "dorsal_number": 10,
  "dni": "12345678A",
  "team_id": 1,
  "client_id": 1,
  "is_goalkeeper": false
}
```

**Error Response (404 Not Found):**

```json
{
  "detail": "Player not found"
}
```

### Get Player by Team and Dorsal Number

Retrieves a player by team ID and dorsal number.

**Endpoint:** `GET /players/team/{team_id}/{dorsal_number}`

**Parameters:**
- `team_id` (path): The unique ID of the team
- `dorsal_number` (path): The dorsal number of the player

**Authentication:** Required

**Success Response (200 OK):**

```json
{
  "id": 1,
  "name": "Lionel Messi",
  "dorsal_number": 10,
  "dni": "12345678A",
  "team_id": 1,
  "client_id": 1,
  "is_goalkeeper": false
}
```

**Error Response (404 Not Found):**

```json
{
  "detail": "Player not found with the given team_id and dorsal_number"
}
```

### Get Players by Team

Retrieves all players belonging to a specific team.

**Endpoint:** `GET /players/team/{team_id}`

**Parameters:**
- `team_id` (path): The unique ID of the team

**Authentication:** Required

**Success Response (200 OK):**

```json
[
  {
    "id": 1,
    "name": "Lionel Messi",
    "dorsal_number": 10,
    "dni": "12345678A",
    "team_id": 1,
    "client_id": 1,
    "is_goalkeeper": false
  },
  {
    "id": 3,
    "name": "Marc-André ter Stegen",
    "dorsal_number": 1,
    "dni": "23456789C",
    "team_id": 1,
    "client_id": 1,
    "is_goalkeeper": true
  }
]
```

**Error Response (404 Not Found):**

```json
{
  "detail": "No players found for the given team_id"
}
```

### Create Player

Creates a new player.

**Endpoint:** `POST /players`

**Authentication:** Required

**Request Body:**

```json
{
  "name": "Antoine Griezmann",
  "dorsal_number": 7,
  "dni": "34567890D",
  "team_id": 1,
  "client_id": 1,
  "is_goalkeeper": false
}
```

**Success Response (200 OK):**

```json
{
  "id": 4,
  "name": "Antoine Griezmann",
  "dorsal_number": 7,
  "dni": "34567890D",
  "team_id": 1,
  "client_id": 1,
  "is_goalkeeper": false
}
```

**Error Response (400 Bad Request):**

```json
{
  "detail": "Invalid data format"
}
```

### Update Player

Updates an existing player.

**Endpoint:** `PUT /players/{player_id}`

**Parameters:**
- `player_id` (path): The unique ID of the player to update

**Authentication:** Required

**Request Body:**

```json
{
  "name": "Antoine Griezmann",
  "dorsal_number": 8,
  "dni": "34567890D",
  "team_id": 1,
  "client_id": 1,
  "is_goalkeeper": false
}
```

**Success Response (200 OK):**

```json
{
  "id": 4,
  "name": "Antoine Griezmann",
  "dorsal_number": 8,
  "dni": "34567890D",
  "team_id": 1,
  "client_id": 1,
  "is_goalkeeper": false
}
```

**Error Response (404 Not Found):**

```json
{
  "detail": "Player not found"
}
```

### Delete Player

Deletes a player from the system.

**Endpoint:** `DELETE /players/{player_id}`

**Parameters:**
- `player_id` (path): The unique ID of the player to delete

**Authentication:** Required

**Success Response (200 OK):**

```json
{
  "message": "Player deleted"
}
```

**Error Response (404 Not Found):**

```json
{
  "detail": "Player not found"
}
```

## Data Models

### PlayerBase / PlayerCreate

Properties used when creating or updating a player:

```json
{
  "name": "string",
  "dorsal_number": 0,
  "dni": "string",
  "team_id": 0,
  "client_id": 0,
  "is_goalkeeper": false
}
```

### PlayerResponse

Properties returned in player responses:

```json
{
  "id": 0,
  "name": "string",
  "dorsal_number": 0,
  "dni": "string",
  "team_id": 0,
  "client_id": 0,
  "is_goalkeeper": false
}
```

## Implementation Notes

- The `dni` field (National Identity Document) must be unique in the system
- When a player's team is deleted, the `team_id` is set to NULL, not cascaded
- The `is_goalkeeper` flag is used to identify players who can serve as goalkeepers

## Example Usage

### JavaScript Fetch API Example

```javascript
// Get all players for a team
async function getTeamPlayers(teamId) {
  try {
    const response = await fetch(`http://api.refereezy.com/players/team/${teamId}`, {
      method: 'GET',
      headers: {
        'Authorization': 'Bearer YOUR_JWT_TOKEN',
        'Accept': 'application/json'
      }
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch players');
    }
    
    const players = await response.json();
    return players;
  } catch (error) {
    console.error('Error fetching players:', error);
    throw error;
  }
}
```

### Creating a New Player

```javascript
async function createPlayer(playerData) {
  try {
    const response = await fetch('http://api.refereezy.com/players', {
      method: 'POST',
      headers: {
        'Authorization': 'Bearer YOUR_JWT_TOKEN',
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(playerData)
    });
    
    if (!response.ok) {
      throw new Error('Failed to create player');
    }
    
    const newPlayer = await response.json();
    return newPlayer;
  } catch (error) {
    console.error('Error creating player:', error);
    throw error;
  }
}
```
