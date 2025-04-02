-- Datos de prueba
INSERT INTO CLIENT (name, plan, plan_expiration, email, phone) VALUES
('John Doe', 'Eazy', '2025-12-31', 'john@example.com', '1234567890'),
('Alice Smith', 'Exceptional', '2025-11-30', 'alice@example.com', '0987654321');

INSERT INTO MATCH_GROUP (visibility, code, client_id) VALUES
('public', 'MG001', 1),
('private', 'MG002', 2);

INSERT INTO TEAM (name, logo_url, primary_color, secondary_color, client_id) VALUES
('Team A', 'logo_a.png', 'Red', 'White', 1),
('Team B', 'logo_b.png', 'Blue', 'Black', 1),
('Team C', 'logo_c.png', 'Green', 'Yellow', 2);

INSERT INTO CLOCK (code) VALUES
('CLK123'),
('CLK456');

INSERT INTO REFEREE (name, password, dni, clock_code, client_id) VALUES
('Mike Ref', 'password123', '12345678A', 'CLK123', 1),
('Sarah Ref', 'password456', '87654321B', 'CLK456', 2);

INSERT INTO MATCHES (date, matchgroup_id, client_id, local_team_id, visitor_team_id, referee_id) VALUES
('2025-01-15 15:00:00', 1, 1, 1, 2, 1),
('2025-02-20 18:00:00', 2, 2, 3, 1, 2);

INSERT INTO PLAYER (name, dorsal_number, dni, team_id, client_id) VALUES
-- Jugadores para el Team 1
('Player 1', 10, '11111111X', 1, 1),
('Player 4', 4, '44444444A', 1, 1),
('Player 5', 5, '55555555B', 1, 1),
('Player 6', 6, '66666666C', 1, 1),
('Player 7', 7, '77777777D', 1, 1),
('Player 8', 8, '88888888E', 1, 1),
('Player 9', 9, '99999999F', 1, 1),
('Player 10', 11, '10101010G', 1, 1),
('Player 11', 12, '12121212H', 1, 1),
('Player 12', 13, '13131313I', 1, 1),

-- Jugadores para el Team 3
('Player 3', 7, '33333333Z', 3, 2),
('Player 13', 14, '14141414J', 3, 2),
('Player 14', 15, '15151515K', 3, 2),
('Player 15', 16, '16161616L', 3, 2),
('Player 16', 17, '17171717M', 3, 2),
('Player 17', 18, '18181818N', 3, 2),
('Player 18', 19, '19191919O', 3, 2),
('Player 19', 20, '20202020P', 3, 2),
('Player 20', 21, '21212121Q', 3, 2),
('Player 21', 22, '22222222R', 3, 2);
