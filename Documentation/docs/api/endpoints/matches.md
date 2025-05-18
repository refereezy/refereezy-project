# Match Endpoints

## Match Management

These endpoints handle all operations related to sports matches, including creation, updates, and retrieval.

## Retrieve Matches

### List All Matches

```
GET /matches
```

Retrieves a list of all matches.

**Query Parameters:**
- `status` (string, optional): Filter by status (scheduled, in-progress, completed)
- `date_from` (string, optional): Filter matches from date (ISO format)
- `date_to` (string, optional): Filter matches to date (ISO format)
- `team_id` (integer, optional): Filter matches for a specific team
- `skip` (integer, optional): Number of records to skip for pagination
- `limit` (integer, optional): Maximum number of records to return

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
{
  "items": [
    {
      "id": 1,
      "name": "Team A vs Team B",
      "date": "2023-06-15T18:00:00Z",
      "location": "Sports Arena 1",
      "status": "scheduled",
      "home_team": {
        "id": 10,
        "name": "Team A",
        "logo": "https://example.com/teama.png"
      },
      "away_team": {
        "id": 11,
        "name": "Team B",
        "logo": "https://example.com/teamb.png"
      },
      "referee_id": 5,
      "created_at": "2023-05-01T12:00:00Z",
      "updated_at": "2023-05-01T12:00:00Z"
    },
    // More matches
  ],
  "total": 42,
  "skip": 0,
  "limit": 100
}
```

**Status Codes:**
- `200 OK`: Matches found
- `401 Unauthorized`: Not authenticated
- `403 Forbidden`: Not authorized to view matches

### Get Match by ID

```
GET /matches/{match_id}
```

Retrieves a specific match by its ID.

**Path Parameters:**
- `match_id` (integer, required): ID of the match to retrieve

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
{
  "id": 1,
  "name": "Team A vs Team B",
  "date": "2023-06-15T18:00:00Z",
  "location": "Sports Arena 1",
  "status": "scheduled",
  "home_team": {
    "id": 10,
    "name": "Team A",
    "logo": "https://example.com/teama.png",
    "players": [
      {
        "id": 101,
        "name": "Player 1",
        "number": 10
      },
      // More players
    ]
  },
  "away_team": {
    "id": 11,
    "name": "Team B",
    "logo": "https://example.com/teamb.png",
    "players": [
      {
        "id": 201,
        "name": "Player A",
        "number": 7
      },
      // More players
    ]
  },
  "referee": {
    "id": 5,
    "name": "John Referee"
  },
  "created_at": "2023-05-01T12:00:00Z",
  "updated_at": "2023-05-01T12:00:00Z"
}
```

**Status Codes:**
- `200 OK`: Match found
- `401 Unauthorized`: Not authenticated
- `403 Forbidden`: Not authorized to view this match
- `404 Not Found`: Match not found

## Create & Update Matches

### Create Match

```
POST /matches
```

Creates a new match.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body:**
```json
{
  "name": "Team A vs Team B",
  "date": "2023-06-15T18:00:00Z",
  "location": "Sports Arena 1",
  "home_team_id": 10,
  "away_team_id": 11,
  "referee_id": 5,
  "notes": "Season opener match"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Team A vs Team B",
  "date": "2023-06-15T18:00:00Z",
  "location": "Sports Arena 1",
  "status": "scheduled",
  "home_team_id": 10,
  "away_team_id": 11,
  "referee_id": 5,
  "notes": "Season opener match",
  "created_at": "2023-05-01T12:00:00Z",
  "updated_at": "2023-05-01T12:00:00Z"
}
```

**Status Codes:**
- `201 Created`: Match created successfully
- `400 Bad Request`: Invalid request
- `401 Unauthorized`: Not authenticated
- `403 Forbidden`: Not authorized to create matches
- `422 Unprocessable Entity`: Validation error

### Update Match

```
PUT /matches/{match_id}
```

Updates an existing match.

**Path Parameters:**
- `match_id` (integer, required): ID of the match to update

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body:**
```json
{
  "name": "Team A vs Team B - Updated",
  "date": "2023-06-16T19:00:00Z",
  "location": "Sports Arena 2",
  "status": "rescheduled",
  "referee_id": 6,
  "notes": "Rescheduled due to weather"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Team A vs Team B - Updated",
  "date": "2023-06-16T19:00:00Z",
  "location": "Sports Arena 2",
  "status": "rescheduled",
  "home_team_id": 10,
  "away_team_id": 11,
  "referee_id": 6,
  "notes": "Rescheduled due to weather",
  "created_at": "2023-05-01T12:00:00Z",
  "updated_at": "2023-05-02T10:30:00Z"
}
```

**Status Codes:**
- `200 OK`: Match updated successfully
- `400 Bad Request`: Invalid request
- `401 Unauthorized`: Not authenticated
- `403 Forbidden`: Not authorized to update this match
- `404 Not Found`: Match not found
- `422 Unprocessable Entity`: Validation error

### Delete Match

```
DELETE /matches/{match_id}
```

Deletes a match.

**Path Parameters:**
- `match_id` (integer, required): ID of the match to delete

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
{
  "success": true,
  "message": "Match deleted successfully"
}
```

**Status Codes:**
- `200 OK`: Match deleted successfully
- `401 Unauthorized`: Not authenticated
- `403 Forbidden`: Not authorized to delete this match
- `404 Not Found`: Match not found

## Match Reports

### Get Match Report

```
GET /matches/{match_id}/report
```

Retrieves the report for a specific match.

**Path Parameters:**
- `match_id` (integer, required): ID of the match

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
{
  "id": "report123",
  "match_id": 1,
  "status": "completed",
  "score": {
    "home": 3,
    "away": 2
  },
  "incidents": [
    {
      "id": "inc1",
      "type": "goal",
      "time": "00:15:30",
      "period": 1,
      "team": "home",
      "player_id": 101,
      "details": "Goal from outside the box"
    },
    // More incidents
  ],
  "created_at": "2023-06-15T18:00:00Z",
  "updated_at": "2023-06-15T20:00:00Z"
}
```

**Status Codes:**
- `200 OK`: Report found
- `401 Unauthorized`: Not authenticated
- `403 Forbidden`: Not authorized to view this report
- `404 Not Found`: Report not found

### Create Match Group

```
POST /match-groups
```

Creates a new match group (tournament, league, etc.).

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body:**
```json
{
  "name": "Summer Tournament 2023",
  "description": "Annual summer tournament",
  "start_date": "2023-06-01T00:00:00Z",
  "end_date": "2023-08-31T23:59:59Z"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Summer Tournament 2023",
  "description": "Annual summer tournament",
  "start_date": "2023-06-01T00:00:00Z",
  "end_date": "2023-08-31T23:59:59Z",
  "created_at": "2023-05-01T12:00:00Z",
  "updated_at": "2023-05-01T12:00:00Z"
}
```

**Status Codes:**
- `201 Created`: Match group created successfully
- `400 Bad Request`: Invalid request
- `401 Unauthorized`: Not authenticated
- `403 Forbidden`: Not authorized to create match groups
- `422 Unprocessable Entity`: Validation error

---

*Note for documentation contributors: Add examples of all possible status values, explain the match lifecycle, and include information about how matches relate to reports and other entities. Add sequence diagrams to illustrate the match creation and update flow.*
