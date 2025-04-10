\c refereezy

-- Datos de prueba
INSERT INTO CLIENT (name, plan, plan_expiration, email, phone) VALUES
('John Doe', 'Eazy', '2025-12-31', 'john@example.com', '1234567890'),
('Alice Smith', 'Exceptional', '2025-11-30', 'alice@example.com', '0987654321');

INSERT INTO MATCH_GROUP (visibility, code, client_id) VALUES
('public', 'MG001', 1),
('private', 'MG002', 2);

INSERT INTO TEAM (name, logo_url, primary_color, secondary_color, client_id) VALUES
('Barcelona', 'https://upload.wikimedia.org/wikipedia/en/thumb/4/47/FC_Barcelona_%28crest%29.svg/1200px-FC_Barcelona_%28crest%29.svg.png', '#ff0000', '#ffffff', 1),
('Madrid', 'https://upload.wikimedia.org/wikipedia/en/thumb/5/56/Real_Madrid_CF.svg/1200px-Real_Madrid_CF.svg.png', '#0000ff', '#000000', 1),
('Milan', 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/d0/Logo_of_AC_Milan.svg/1306px-Logo_of_AC_Milan.svg.png', '#00ff00', '#ffff00', 2);

INSERT INTO CLOCK (code) VALUES
('CLK123'),
('CLK456');

INSERT INTO REFEREE (name, password, dni, clock_code, client_id) VALUES
('Mike Ref', 'password123', '12345678A', 'CLK123', 1),
('Sarah Ref', 'password456', '87654321B', 'CLK456', 2);

INSERT INTO MATCHES (date, matchgroup_id, client_id, local_team_id, visitor_team_id, referee_id) VALUES
('2025-05-15 15:00:00', 1, 1, 1, 2, 1),
('2025-05-20 18:00:00', 2, 1, 3, 1, 1);

INSERT INTO PLAYER (name, dorsal_number, dni, team_id, client_id, is_goalkeeper) VALUES
-- Jugadores para el Team 1
('Player 1', 10, '11111111X', 1, 1, true),
('Player 4', 4, '44444444A', 1, 1, false),
('Player 5', 5, '55555555B', 1, 1, false),
('Player 6', 6, '66666666C', 1, 1, false),
('Player 7', 7, '77777777D', 1, 1, false),
('Player 8', 8, '88888888E', 1, 1, false),
('Player 9', 9, '99999999F', 1, 1, false),
('Player 10', 11, '10101010G', 1, 1, false),
('Player 11', 12, '12121212H', 1, 1, false),
('Player 12', 13, '13131313I', 1, 1, true),

-- Jugadores para el Team 2
('Player 2', 9, '22222222Y', 2, 1, true),
('Player 22', 14, '23232323S', 2, 1, false),
('Player 23', 15, '24242424T', 2, 1, false),
('Player 24', 16, '25252525U', 2, 1, false),
('Player 25', 17, '26262626V', 2, 1, false),
('Player 26', 18, '27272727W', 2, 1, false),
('Player 27', 19, '28282828X', 2, 1, false),
('Player 28', 20, '29292929Y', 2, 1, false),
('Player 29', 21, '30303030Z', 2, 1, false),
('Player 30', 22, '31313131A', 2, 1, true),

-- Jugadores para el Team 3
('Player 3', 7, '33333333Z', 3, 2, true),
('Player 13', 14, '14141414J', 3, 2, false),
('Player 14', 15, '15151515K', 3, 2, false),
('Player 15', 16, '16161616L', 3, 2, false),
('Player 16', 17, '17171717M', 3, 2, false),
('Player 17', 18, '18181818N', 3, 2, false),
('Player 18', 19, '19191919O', 3, 2, false),
('Player 19', 20, '20202020P', 3, 2, false),
('Player 20', 21, '21212121Q', 3, 2, false),
('Player 21', 22, '22222222R', 3, 2, true);
