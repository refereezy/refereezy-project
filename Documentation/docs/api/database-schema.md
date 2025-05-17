# Database Schema

This document provides a comprehensive overview of the Refereezy platform's database schema, detailing the relationships between different entities and the structure of each table.

## Entity Relationship Diagram

The following diagram illustrates the relationships between the main entities in the Refereezy database:

```mermaid
erDiagram
    CLIENT ||--o{ TEAM : has
    CLIENT ||--o{ PLAYER : has
    CLIENT ||--o{ REFEREE : has
    CLIENT ||--o{ MATCH_GROUP : has
    CLIENT ||--o{ MATCH : has
    TEAM ||--o{ PLAYER : has
    MATCH_GROUP ||--o{ MATCH : contains
    REFEREE ||--o{ MATCH : officiates
    TEAM ||--o{ MATCH : plays_as_local
    TEAM ||--o{ MATCH : plays_as_visitor
    REFEREE ||--o| CLOCK : uses
    REFEREE ||--o| 2FA_TEMP : authenticates
    
    CLIENT {
        int id PK
        string name
        string plan
        date plan_expiration
        string email UK
        string password
        string phone
    }
    
    TEAM {
        int id PK
        string name
        string logo_url
        string primary_color
        string secondary_color
        int client_id FK
    }
    
    PLAYER {
        int id PK
        string name
        int dorsal_number
        string dni UK
        int team_id FK
        int client_id FK
        boolean is_goalkeeper
    }
    
    REFEREE {
        int id PK
        string name
        string password
        string dni UK
        string clock_code
        int client_id FK
        string token
    }
    
    MATCH_GROUP {
        int id PK
        string visibility
        string code UK
        int client_id FK
    }
    
    MATCH {
        int id PK
        datetime date
        int matchgroup_id FK
        int client_id FK
        int referee_id FK
        int local_team_id FK
        int visitor_team_id FK
    }
    
    CLOCK {
        string code PK
    }
    
    2FA_TEMP {
        string twofa_code PK
        string clock_code
        timestamp expiration
        boolean paired
        int referee_id FK
    }
```

## Table Descriptions

### CLIENT

The Client table represents organizations or individuals who use the Refereezy platform to manage sports matches.

**Columns:**
- `id` (PK, SERIAL): Auto-incrementing primary key
- `name` (VARCHAR, NOT NULL): Client name
- `plan` (VARCHAR): Subscription plan type (e.g., "Eazy", "Pro", "Enterprise")
- `plan_expiration` (DATE): Date when the current subscription expires
- `email` (VARCHAR, UNIQUE): Client's email address for authentication and communication
- `password` (VARCHAR): Hashed password for authentication
- `phone` (VARCHAR): Contact phone number

**Relationships:**
- One-to-many with TEAM: A client can have multiple teams
- One-to-many with PLAYER: A client can have multiple players
- One-to-many with REFEREE: A client can have multiple referees
- One-to-many with MATCH_GROUP: A client can have multiple match groups
- One-to-many with MATCH: A client can have multiple matches

### TEAM

The Team table represents sports teams that participate in matches.

**Columns:**
- `id` (PK, SERIAL): Auto-incrementing primary key
- `name` (VARCHAR, NOT NULL): Team name
- `logo_url` (VARCHAR): URL to the team's logo image
- `primary_color` (VARCHAR): Primary team color (hex code)
- `secondary_color` (VARCHAR): Secondary team color (hex code)
- `client_id` (FK, INT, NOT NULL): Reference to the client who owns this team

**Relationships:**
- Many-to-one with CLIENT: A team belongs to one client
- One-to-many with PLAYER: A team can have multiple players
- One-to-many with MATCH as local_team: A team can play as the local team in multiple matches
- One-to-many with MATCH as visitor_team: A team can play as the visiting team in multiple matches

### PLAYER

The Player table represents individual athletes who are part of teams.

**Columns:**
- `id` (PK, SERIAL): Auto-incrementing primary key
- `name` (VARCHAR, NOT NULL): Player name
- `dorsal_number` (INT, NOT NULL): Player's jersey number
- `dni` (VARCHAR, UNIQUE, NOT NULL): National ID document number
- `team_id` (FK, INT): Reference to the team this player belongs to
- `client_id` (FK, INT, NOT NULL): Reference to the client who registered this player
- `is_goalkeeper` (BOOLEAN, NOT NULL): Flag indicating if the player is a goalkeeper

**Relationships:**
- Many-to-one with TEAM: A player belongs to one team
- Many-to-one with CLIENT: A player is registered under one client

### REFEREE

The Referee table represents match officials who control and document sports matches.

**Columns:**
- `id` (PK, SERIAL): Auto-incrementing primary key
- `name` (VARCHAR, NOT NULL): Referee name
- `password` (VARCHAR, NOT NULL): Hashed password for authentication
- `dni` (VARCHAR, UNIQUE, NOT NULL): National ID document number
- `clock_code` (VARCHAR, NULL): Code used to link with a match clock
- `client_id` (FK, INT, NOT NULL): Reference to the client who registered this referee
- `token` (VARCHAR, NOT NULL): Authentication token for API access

**Relationships:**
- Many-to-one with CLIENT: A referee is registered under one client
- One-to-many with MATCH: A referee can officiate multiple matches
- One-to-one with CLOCK: A referee can be linked to a clock code
- One-to-many with 2FA_TEMP: A referee can have multiple 2FA temporary codes

### MATCH_GROUP

The Match_Group table represents collections of related matches (e.g., tournaments, leagues).

**Columns:**
- `id` (PK, SERIAL): Auto-incrementing primary key
- `visibility` (VARCHAR, NOT NULL): Access control setting ('public' or 'private')
- `code` (VARCHAR, UNIQUE, NOT NULL): Unique code for accessing this match group
- `client_id` (FK, INT, NOT NULL): Reference to the client who created this match group

**Relationships:**
- Many-to-one with CLIENT: A match group belongs to one client
- One-to-many with MATCH: A match group can contain multiple matches

### MATCH

The Match table represents individual sporting events.

**Columns:**
- `id` (PK, SERIAL): Auto-incrementing primary key
- `date` (DATETIME, NOT NULL): Date and time when the match takes place
- `matchgroup_id` (FK, INT): Reference to the match group this match belongs to
- `client_id` (FK, INT, NOT NULL): Reference to the client who created this match
- `referee_id` (FK, INT): Reference to the referee officiating this match
- `local_team_id` (FK, INT, NOT NULL): Reference to the local team
- `visitor_team_id` (FK, INT, NOT NULL): Reference to the visiting team

**Relationships:**
- Many-to-one with CLIENT: A match belongs to one client
- Many-to-one with MATCH_GROUP: A match can belong to a match group
- Many-to-one with REFEREE: A match is officiated by one referee
- Many-to-one with TEAM as local_team: A match has one local team
- Many-to-one with TEAM as visitor_team: A match has one visiting team

### CLOCK

The Clock table represents timing devices used in matches.

**Columns:**
- `code` (PK, VARCHAR): Unique code identifying the clock

**Relationships:**
- Many-to-one with REFEREE: A clock can be linked to one referee

### 2FA_TEMP

The 2FA_TEMP table handles temporary codes for two-factor authentication, especially when pairing watch devices.

**Columns:**
- `twofa_code` (PK, VARCHAR): Unique two-factor authentication code
- `clock_code` (VARCHAR, NOT NULL): Reference to the clock code being paired
- `expiration` (TIMESTAMP, NOT NULL): When this code expires
- `paired` (BOOLEAN, NOT NULL): Whether the pairing has been completed
- `referee_id` (FK, INT): Reference to the referee using this 2FA code

**Relationships:**
- Many-to-one with REFEREE: A 2FA code belongs to one referee

## Database Constraints

### Foreign Key Constraints

- `team.client_id` → `client.id` ON DELETE CASCADE
- `player.team_id` → `team.id` ON DELETE SET NULL
- `player.client_id` → `client.id` ON DELETE CASCADE
- `referee.client_id` → `client.id` ON DELETE CASCADE
- `match_group.client_id` → `client.id` ON DELETE CASCADE
- `match.matchgroup_id` → `match_group.id` ON DELETE SET NULL
- `match.client_id` → `client.id` ON DELETE CASCADE
- `match.referee_id` → `referee.id` ON DELETE SET NULL
- `match.local_team_id` → `team.id` ON DELETE CASCADE
- `match.visitor_team_id` → `team.id` ON DELETE CASCADE
- `2fa_temp.referee_id` → `referee.id` ON DELETE CASCADE

### Unique Constraints

- `client.email`: Each client must have a unique email address
- `player.dni`: Each player must have a unique DNI number
- `referee.dni`: Each referee must have a unique DNI number
- `match_group.code`: Each match group must have a unique access code

### Check Constraints

- `match_group.visibility`: Must be either 'public' or 'private'

## PostgreSQL Implementation

The database is implemented using PostgreSQL. Here is a sample of the SQL commands used to create some of the tables:

```sql
CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    plan VARCHAR,
    plan_expiration DATE,
    email VARCHAR UNIQUE,
    password VARCHAR,
    phone VARCHAR
);

CREATE TABLE team (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    logo_url VARCHAR,
    primary_color VARCHAR,
    secondary_color VARCHAR,
    client_id INTEGER NOT NULL REFERENCES client(id) ON DELETE CASCADE
);

CREATE TABLE player (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    dorsal_number INTEGER NOT NULL,
    dni VARCHAR UNIQUE NOT NULL,
    team_id INTEGER REFERENCES team(id) ON DELETE SET NULL,
    client_id INTEGER NOT NULL REFERENCES client(id) ON DELETE CASCADE,
    is_goalkeeper BOOLEAN NOT NULL
);

CREATE TABLE referee (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    password VARCHAR NOT NULL,
    dni VARCHAR UNIQUE NOT NULL,
    clock_code VARCHAR,
    client_id INTEGER NOT NULL REFERENCES client(id) ON DELETE CASCADE,
    token VARCHAR NOT NULL
);
```

## Firebase Real-time Database

In addition to the PostgreSQL database, Refereezy also uses Firebase Realtime Database for live match data and events. The structure of this database is as follows:

```
refereezy/
|-- matches/
|   |-- {match_id}/
|       |-- status: "in_progress"
|       |-- current_time: "00:15:30"
|       |-- period: 1
|       |-- score: {home: 2, away: 1}
|       |-- incidents/
|           |-- {incident_id}/
|               |-- type: "goal"
|               |-- time: "00:10:15"
|               |-- team: "home"
|               |-- player_id: "player123"
|
|-- clocks/
|   |-- {clock_code}/
|       |-- match_id: "match123"
|       |-- is_running: true
|       |-- current_time: "00:15:30"
|       |-- period: 1
|       |-- connected_devices: ["device1", "device2"]
|
|-- reports/
|   |-- {report_id}/
|       |-- match_id: "match123"
|       |-- status: "in_progress"
|       |-- referee_id: "ref123"
|       |-- incidents: [...]
```

This Firebase structure complements the PostgreSQL database by providing real-time capabilities for live match tracking and reporting across multiple devices.

## Data Migration and Backup

Regular database backups are performed using PostgreSQL's built-in tools:

```bash
pg_dump -U [username] -d refereezy -f backup_[date].sql
```

Data migrations between environments (development, staging, production) are managed using SQL scripts and version control.

## Database Access

Database access from application code is handled through SQLAlchemy ORM in the API layer, with models defined in `models.py` and schemas in `schemas.py`. The database connection is established in `database.py`.

For security reasons, direct database access is restricted to authorized personnel only, and all connection strings and credentials are stored as environment variables.
