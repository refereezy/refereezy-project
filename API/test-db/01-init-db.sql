-- Create the database if it doesn't already exist
CREATE DATABASE refereezy;

-- Connect to the newly created database
\c refereezy

-- Drop tables in the correct order to avoid foreign key conflicts
DROP TABLE IF EXISTS PLAYER;
DROP TABLE IF EXISTS MATCHES;
DROP TABLE IF EXISTS REFEREE;
DROP TABLE IF EXISTS TEAM;
DROP TABLE IF EXISTS MATCH_GROUP;
DROP TABLE IF EXISTS CLIENT;
DROP TYPE IF EXISTS Plan;
-- Tables
CREATE TYPE Plan as ENUM ('Eazy', 'Exceptional', 'Enterprise');

CREATE TABLE IF NOT EXISTS CLIENT (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    plan Plan NOT NULL DEFAULT 'Eazy',
    plan_expiration DATE,
    email VARCHAR(255) UNIQUE,
    password TEXT,
    phone VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS MATCH_GROUP (
    id SERIAL PRIMARY KEY,
    visibility VARCHAR(10) CHECK (visibility IN ('public', 'private')) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    client_id INT NOT NULL,
	
    FOREIGN KEY (client_id) REFERENCES CLIENT(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS TEAM (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    logo_url TEXT, -- URL logo (Generada por API)
    primary_color VARCHAR(50), -- # HEX
    secondary_color VARCHAR(50), -- # HEX
    client_id INT NOT NULL,
	
    FOREIGN KEY (client_id) REFERENCES CLIENT(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REFEREE (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
	password TEXT NOT NULL,
    token TEXT NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
	clock_code VARCHAR(50),
    client_id INT NOT NULL,
	
    FOREIGN KEY (client_id) REFERENCES CLIENT(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS MATCHES (
    id SERIAL PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    matchgroup_id INT,
	referee_id INT,
    client_id INT NOT NULL,
    local_team_id INT NOT NULL,
    visitor_team_id INT NOT NULL,

    FOREIGN KEY (matchgroup_id) REFERENCES MATCH_GROUP(id) ON DELETE SET NULL,
    FOREIGN KEY (client_id) REFERENCES CLIENT(id) ON DELETE CASCADE,
    FOREIGN KEY (local_team_id) REFERENCES TEAM(id) ON DELETE CASCADE,
    FOREIGN KEY (visitor_team_id) REFERENCES TEAM(id) ON DELETE CASCADE,
	FOREIGN KEY (referee_id) REFERENCES REFEREE(id) ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS PLAYER (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dorsal_number INT NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
    team_id INT,
    client_id INT NOT NULL,
    is_goalkeeper BOOLEAN,
	
    FOREIGN KEY (team_id) REFERENCES TEAM(id) ON DELETE SET NULL,
    FOREIGN KEY (client_id) REFERENCES CLIENT(id) ON DELETE CASCADE
);
