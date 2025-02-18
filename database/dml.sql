-- Datos de prueba
INSERT INTO CLIENT (name, plan, plan_expiration, email, phone) VALUES
('John Doe', 'Premium', '2025-12-31', 'john@example.com', '1234567890'),
('Alice Smith', 'Basic', '2024-11-30', 'alice@example.com', '0987654321');

INSERT INTO MATCH_GROUP (visibility, code, client_id) VALUES
('public', 'MG001', 1),
('private', 'MG002', 2);

INSERT INTO TEAM (name, logo, primary_color, secondary_color, client_id) VALUES
('Team A', 'logo_a.png', 'Red', 'White', 1),
('Team B', 'logo_b.png', 'Blue', 'Black', 1),
('Team C', 'logo_c.png', 'Green', 'Yellow', 2);

INSERT INTO MATCH (date, matchday_id, client_id, local_team_id, visitor_team_id) VALUES
('2025-01-15', 1, 1, 1, 2),
('2025-02-20', 2, 2, 3, 1);

INSERT INTO REFEREE (name, dni, client_id) VALUES
('Mike Ref', '12345678A', 1),
('Sarah Ref', '87654321B', 2);

INSERT INTO MATCH_REFEREE (match_id, referee_id) VALUES
(1, 1),
(2, 2);

INSERT INTO PLAYER (name, jersey_number, dni, team_id, client_id) VALUES
('Player 1', 10, '11111111X', 1, 1),
('Player 2', 9, '22222222Y', 2, 1),
('Player 3', 7, '33333333Z', 3, 2);

INSERT INTO CLOCK (code) VALUES
('CLK123'),
('CLK456');

INSERT INTO 2FA_TEMP (twofa_code, clock_code, expiration, paired, referee_id) VALUES
('2FA001', 'CLK123', '2025-06-01 12:00:00', TRUE, 1),
('2FA002', 'CLK456', '2025-06-02 14:30:00', FALSE, 2);