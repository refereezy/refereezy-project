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

INSERT INTO MATCHES (date, matchgroup_id, client_id, local_team_id, visitor_team_id, referee_id) VALUES
('2025-01-15 15:00:00', 1, 1, 1, 2, 1),
('2025-02-20 18:00:00', 2, 2, 3, 1, 2);

INSERT INTO REFEREE (name, password, dni, clock_code, client_id) VALUES
('Mike Ref', 'password123', '12345678A', 'CLK123', 1),
('Sarah Ref', 'password456', '87654321B', 'CLK456', 2);

INSERT INTO PLAYER (name, dorsal_number, dni, team_id, client_id) VALUES
('Player 1', 10, '11111111X', 1, 1),
('Player 2', 9, '22222222Y', 2, 1),
('Player 3', 7, '33333333Z', 3, 2);

INSERT INTO CLOCK (code) VALUES
('CLK123'),
('CLK456');
