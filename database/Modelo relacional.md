# Modelo relacional

## CLIENT
**Columns:**
- id (PK, SERIAL)
- name (VARCHAR, NOT NULL)
- plan (VARCHAR)
- plan_expiration (DATE)
- email (VARCHAR, UNIQUE)
- phone (VARCHAR)

## MATCH_GROUP
**Columns:**
- id (PK, SERIAL)
- visibility (VARCHAR, CHECK ['public', 'private'], NOT NULL)
- code (VARCHAR, UNIQUE, NOT NULL)
- client_id (FK, INT, NOT NULL) → CLIENT(id) ON DELETE CASCADE

## MATCH
**Columns:**
- id (PK, SERIAL)
- date (DATE, NOT NULL)
- matchday_id (FK, INT) → MATCH_GROUP(id) ON DELETE SET NULL
- client_id (FK, INT, NOT NULL) → CLIENT(id) ON DELETE CASCADE
- local_team_id (FK, INT, NOT NULL) → TEAM(id) ON DELETE CASCADE
- visitor_team_id (FK, INT, NOT NULL) → TEAM(id) ON DELETE CASCADE

## REFEREE
**Columns:**
- id (PK, SERIAL)
- name (VARCHAR, NOT NULL)
- dni (VARCHAR, UNIQUE, NOT NULL)
- client_id (FK, INT, NOT NULL) → CLIENT(id) ON DELETE CASCADE

## TEAM
**Columns:**
- id (PK, SERIAL)
- name (VARCHAR, NOT NULL)
- logo (TEXT)
- primary_color (VARCHAR)
- secondary_color (VARCHAR)
- client_id (FK, INT, NOT NULL) → CLIENT(id) ON DELETE CASCADE

## PLAYER
**Columns:**
- id (PK, SERIAL)
- name (VARCHAR, NOT NULL)
- jersey_number (INT, NOT NULL)
- dni (VARCHAR, UNIQUE, NOT NULL)
- team_id (FK, INT) → TEAM(id) ON DELETE SET NULL
- client_id (FK, INT, NOT NULL) → CLIENT(id) ON DELETE CASCADE

## CLOCK
**Columns:**
- code (PK, VARCHAR)

## MATCH_REFEREE
**Columns:**
- match_id (PK, FK, INT) → MATCH(id) ON DELETE CASCADE
- referee_id (PK, FK, INT) → REFEREE(id) ON DELETE CASCADE

## 2FA_TEMP
**Columns:**
- twofa_code (PK, VARCHAR)
- clock_code (VARCHAR, NOT NULL)
- expiration (TIMESTAMP, NOT NULL)
- paired (BOOLEAN, NOT NULL)
- referee_id (FK, INT) → REFEREE(id) ON DELETE CASCADE

