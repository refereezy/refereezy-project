# Clock Endpoints

## Clock Management

These endpoints handle all operations related to match clocks, including synchronization between referee devices and timing management.

## Clock Status

### Get Clock Status

```
GET /clock/{clock_code}
```

Retrieves the current status of a match clock.

**Path Parameters:**
- `clock_code` (string, required): Unique code of the clock to check

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
{
  "code": "ABC123",
  "status": "active",
  "match_id": 45,
  "current_time": "00:15:30",
  "period": 1,
  "is_running": true,
  "last_updated": "2023-06-15T18:15:30Z",
  "connected_devices": [
    {
      "device_id": "ref-mobile-123",
      "device_type": "mobile",
      "connected_at": "2023-06-15T18:00:05Z"
    },
    {
      "device_id": "ref-watch-456",
      "device_type": "watch",
      "connected_at": "2023-06-15T18:00:15Z"
    }
  ]
}
```

**Status Codes:**
- `200 OK`: Clock status retrieved
- `401 Unauthorized`: Not authenticated
- `403 Forbidden`: Not authorized to view this clock
- `404 Not Found`: Clock not found

## Clock Creation and Management

### Generate Clock Code

```
POST /clock/generate
```

Generates a new unique clock code for a match.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body:**
```json
{
  "match_id": 45,
  "expiry_minutes": 120
}
```

**Response:**
```json
{
  "code": "ABC123",
  "match_id": 45,
  "created_at": "2023-06-15T17:30:00Z",
  "expires_at": "2023-06-15T19:30:00Z",
  "created_by": "referee_user_id"
}
```

**Status Codes:**
- `201 Created`: Clock code generated successfully
- `400 Bad Request`: Invalid request
- `401 Unauthorized`: Not authenticated
- `403 Forbidden`: Not authorized to generate clock codes
- `422 Unprocessable Entity`: Validation error

### Verify Clock Code

```
POST /clock/verify
```

Verifies if a clock code is valid and associates a device with it.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body:**
```json
{
  "code": "ABC123",
  "device_id": "ref-watch-456",
  "device_type": "watch"
}
```

**Response:**
```json
{
  "valid": true,
  "match_id": 45,
  "match_details": {
    "name": "Team A vs Team B",
    "date": "2023-06-15T18:00:00Z",
    "location": "Sports Arena 1"
  },
  "connected": true
}
```

**Status Codes:**
- `200 OK`: Clock code verified
- `400 Bad Request`: Invalid request or code
- `401 Unauthorized`: Not authenticated
- `404 Not Found`: Code not found or expired

## Clock Control

### Start Clock

```
POST /clock/{clock_code}/start
```

Starts the match clock.

**Path Parameters:**
- `clock_code` (string, required): Unique code of the clock

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body:**
```json
{
  "period": 1,
  "start_time": "00:00:00"
}
```

**Response:**
```json
{
  "code": "ABC123",
  "status": "active",
  "match_id": 45,
  "current_time": "00:00:00",
  "period": 1,
  "is_running": true,
  "started_at": "2023-06-15T18:00:00Z",
  "action_by": "referee_user_id"
}
```

**Status Codes:**
- `200 OK`: Clock started successfully
- `400 Bad Request`: Invalid request or clock already running
- `401 Unauthorized`: Not authenticated
- `403 Forbidden`: Not authorized to control this clock
- `404 Not Found`: Clock not found

### Stop Clock

```
POST /clock/{clock_code}/stop
```

Stops the match clock.

**Path Parameters:**
- `clock_code` (string, required): Unique code of the clock

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body:**
```json
{
  "stop_time": "00:15:30",
  "reason": "half_time"
}
```

**Response:**
```json
{
  "code": "ABC123",
  "status": "paused",
  "match_id": 45,
  "current_time": "00:15:30",
  "period": 1,
  "is_running": false,
  "stopped_at": "2023-06-15T18:15:30Z",
  "reason": "half_time",
  "action_by": "referee_user_id"
}
```

**Status Codes:**
- `200 OK`: Clock stopped successfully
- `400 Bad Request`: Invalid request or clock not running
- `401 Unauthorized`: Not authenticated
- `403 Forbidden`: Not authorized to control this clock
- `404 Not Found`: Clock not found

### Update Clock Time

```
PUT /clock/{clock_code}/time
```

Manually updates the clock time.

**Path Parameters:**
- `clock_code` (string, required): Unique code of the clock

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body:**
```json
{
  "current_time": "00:16:00",
  "period": 1,
  "reason": "referee_adjustment"
}
```

**Response:**
```json
{
  "code": "ABC123",
  "status": "paused",
  "match_id": 45,
  "current_time": "00:16:00",
  "period": 1,
  "is_running": false,
  "updated_at": "2023-06-15T18:16:00Z",
  "reason": "referee_adjustment",
  "action_by": "referee_user_id"
}
```

**Status Codes:**
- `200 OK`: Clock time updated successfully
- `400 Bad Request`: Invalid request
- `401 Unauthorized`: Not authenticated
- `403 Forbidden`: Not authorized to update this clock
- `404 Not Found`: Clock not found

### Change Period

```
POST /clock/{clock_code}/period
```

Changes the current period of the match.

**Path Parameters:**
- `clock_code` (string, required): Unique code of the clock

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body:**
```json
{
  "period": 2,
  "reset_time": true
}
```

**Response:**
```json
{
  "code": "ABC123",
  "status": "paused",
  "match_id": 45,
  "current_time": "00:00:00",
  "period": 2,
  "is_running": false,
  "updated_at": "2023-06-15T18:20:00Z",
  "action_by": "referee_user_id"
}
```

**Status Codes:**
- `200 OK`: Period changed successfully
- `400 Bad Request`: Invalid request
- `401 Unauthorized`: Not authenticated
- `403 Forbidden`: Not authorized to control this clock
- `404 Not Found`: Clock not found

### End Match

```
POST /clock/{clock_code}/end
```

Ends the match and finalizes the clock.

**Path Parameters:**
- `clock_code` (string, required): Unique code of the clock

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body:**
```json
{
  "final_time": "01:30:00",
  "final_period": 2
}
```

**Response:**
```json
{
  "code": "ABC123",
  "status": "completed",
  "match_id": 45,
  "final_time": "01:30:00",
  "final_period": 2,
  "is_running": false,
  "ended_at": "2023-06-15T19:30:00Z",
  "action_by": "referee_user_id",
  "match_duration": "01:30:00"
}
```

**Status Codes:**
- `200 OK`: Match ended successfully
- `400 Bad Request`: Invalid request
- `401 Unauthorized`: Not authenticated
- `403 Forbidden`: Not authorized to control this clock
- `404 Not Found`: Clock not found

---

*Note for documentation contributors: Add diagrams showing the clock synchronization process between different devices. Include information about timeout handling, error recovery, and how to handle clock discrepancies between devices.*
