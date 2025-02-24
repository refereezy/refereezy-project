-- PostgreSQL

CREATE TABLE IF NOT EXISTS CLIENT (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    plan VARCHAR(100),
    plan_expiration DATE,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS MATCH_GROUP (
    id SERIAL PRIMARY KEY,
    visibility VARCHAR(10) CHECK (visibility IN ('public', 'private')) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    client_id INT NOT NULL,
    FOREIGN KEY (client_id) REFERENCES CLIENT(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS MATCHES (
    id SERIAL PRIMARY KEY,
    date DATE NOT NULL,
    matchday_id INT,
    client_id INT NOT NULL,
    local_team_id INT NOT NULL,
    visitor_team_id INT NOT NULL,
    FOREIGN KEY (matchday_id) REFERENCES MATCH_GROUP(id) ON DELETE SET NULL,
    FOREIGN KEY (client_id) REFERENCES CLIENT(id) ON DELETE CASCADE,
    FOREIGN KEY (local_team_id) REFERENCES TEAM(id) ON DELETE CASCADE,
    FOREIGN KEY (visitor_team_id) REFERENCES TEAM(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REFEREE (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
    client_id INT NOT NULL,
    FOREIGN KEY (client_id) REFERENCES CLIENT(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS TEAM (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    logo TEXT,
    primary_color VARCHAR(50),
    secondary_color VARCHAR(50),
    client_id INT NOT NULL,
    FOREIGN KEY (client_id) REFERENCES CLIENT(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PLAYER (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    jersey_number INT NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
    team_id INT,
    client_id INT NOT NULL,
    FOREIGN KEY (team_id) REFERENCES TEAM(id) ON DELETE SET NULL,
    FOREIGN KEY (client_id) REFERENCES CLIENT(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS CLOCK (
    code VARCHAR(50) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS MATCH_REFEREE (
    match_id INT,
    referee_id INT,
    PRIMARY KEY (match_id, referee_id),
    FOREIGN KEY (match_id) REFERENCES MATCHES(id) ON DELETE CASCADE,
    FOREIGN KEY (referee_id) REFERENCES REFEREE(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS 2FA_TEMP (
    twofa_code VARCHAR(50) PRIMARY KEY,
    clock_code VARCHAR(50) NOT NULL,
    expiration TIMESTAMP NOT NULL,
    paired BOOLEAN NOT NULL,
    referee_id INT,
    FOREIGN KEY (referee_id) REFERENCES REFEREE(id) ON DELETE CASCADE
);
