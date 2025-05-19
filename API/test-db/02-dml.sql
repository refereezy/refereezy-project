\c refereezy

-- Datos de prueba
INSERT INTO CLIENT (name, plan, plan_expiration, email, password, phone) VALUES
('John Doe', 'Eazy', '2025-12-31', 'john@example.com', '$2b$12$QIy0hUnHLN0rsNamEB00p.1lMy7Y.UrcU0klIvI5U9l8GNGvAl9qu', '1234567890'),
('Alice Smith', 'Exceptional', '2025-11-30', 'alice@example.com', '$2b$12$B5pDqofT0YVKGuKITVb1buRl1JVbNF1B7ezsoYgvacRHB.4wDkWkm', '0987654321');

INSERT INTO MATCH_GROUP (visibility, code, client_id) VALUES
('public', 'MG001', 1),
('private', 'MG002', 2);

INSERT INTO TEAM (name, logo_url, primary_color, secondary_color, client_id) VALUES
('Barcelona', 'https://upload.wikimedia.org/wikipedia/en/thumb/4/47/FC_Barcelona_%28crest%29.svg/1200px-FC_Barcelona_%28crest%29.svg.png', '#ff0000', '#ffffff', 1),
('Madrid', 'https://upload.wikimedia.org/wikipedia/en/thumb/5/56/Real_Madrid_CF.svg/1200px-Real_Madrid_CF.svg.png', '#0000ff', '#000000', 1),
('Milan', 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/d0/Logo_of_AC_Milan.svg/1306px-Logo_of_AC_Milan.svg.png', '#00ff00', '#ffff00', 1),
('Manchester United', 'https://upload.wikimedia.org/wikipedia/en/7/7a/Manchester_United_FC_crest.svg', '#da291c', '#ffd100', 1),
('Bayern Munich', 'https://upload.wikimedia.org/wikipedia/commons/1/1f/FC_Bayern_München_logo_%282017%29.svg', '#dc052d', '#0066b2', 1);


INSERT INTO REFEREE (name, password, dni, clock_code, client_id, token) VALUES
('Mike Ref', 'password123', '12345678A', NULL, 1, '$2b$12$/88liRIt9od1TfvQDqIYj.3B3KdN0ZAv/gfNrfjXN/8aB1TPejxa.'),
('Sarah Ref', 'password456', '87654321B', NULL, 2, '$2b$12$Airxgu62eyAsXRhwqp7ejujxdkdKR6c.t8lCXBdVdqWFr2OZdkuZe');

INSERT INTO MATCHES (date, matchgroup_id, client_id, local_team_id, visitor_team_id, referee_id) VALUES
('2025-05-15 15:00:00', NULL, 1, 1, 2, 1),
('2025-05-20 18:00:00', NULL, 1, 3, 1, NULL),
('2025-05-25 20:00:00', NULL, 1, 2, 3, 1),
('2025-06-01 16:00:00', NULL, 1, 1, 2, 2),
('2025-06-01 17:00:00', NULL, 1, 3, 1, NULL),
('2025-06-01 18:00:00', NULL, 1, 2, 3, NULL),
('2025-06-01 19:00:00', NULL, 1, 3, 1, NULL),
('2025-06-01 20:00:00', NULL, 1, 1, 2, NULL),
('2025-06-01 21:00:00', NULL, 1, 1, 3, NULL),
('2025-06-01 22:00:00', NULL, 1, 2, 1, NULL),
('2025-06-01 23:00:00', NULL, 1, 3, 2, NULL),
('2025-06-01 16:00:00', NULL, 1, 1, 3, NULL),
('2025-06-01 17:00:00', NULL, 1, 1, 3, NULL);

INSERT INTO PLAYER (name, dorsal_number, dni, team_id, client_id, is_goalkeeper) VALUES
-- Jugadores para el Team 1
('Lionel Messi', 10, '22222222Y', 2, 1, false),
('Kylian Mbappé', 7, '23232323S', 2, 1, false),
('Neymar Jr', 11, '24242424T', 2, 1, false),
('Marco Verratti', 6, '25252525U', 2, 1, false),
('Achraf Hakimi', 2, '26262626V', 2, 1, false),
('Marquinhos', 5, '27272727W', 2, 1, false),
('Presnel Kimpembe', 3, '28282828X', 2, 1, false),
('Sergio Rico', 25, '29292929Y', 2, 1, true),
('Ángel Di María', 22, '30303030Z', 2, 1, false),
('Gianluigi Donnarumma', 99, '31313131A', 2, 1, true),

-- Jugadores para el Team 2
('Cristiano Ronaldo', 7, '11111111X', 1, 1, false),
('Luka Modric', 10, '44444444A', 1, 1, false),
('Karim Benzema', 9, '55555555B', 1, 1, false),
('Sergio Ramos', 4, '66666666C', 1, 1, false),
('Toni Kroos', 8, '77777777D', 1, 1, false),
('Vinicius Jr', 11, '88888888E', 1, 1, false),
('Jude Bellingham', 5, '99999999F', 1, 1, false),
('David Alaba', 22, '10101010G', 1, 1, false),
('Éder Militão', 3, '12121212H', 1, 1, false),
('Thibaut Courtois', 1, '13131313I', 1, 1, true),

-- Jugadores para el Team 3
('Mike Maignan', 16, '33333333Z', 3, 2, true),
('Cafú', 2, '14141414J', 3, 2, false),
('Alessandro Nesta', 13, '15151515K', 3, 2, false),
('Paolo Maldini', 3, '16161616L', 3, 2, false),
('Gennaro Gattuso', 8, '17171717M', 3, 2, false),
('Andrea Pirlo', 21, '18181818N', 3, 2, false),
('Clarence Seedorf', 10, '19191919O', 3, 2, false),
('Kaká', 22, '20202020P', 3, 2, false),
('Andriy Shevchenko', 7, '21212121Q', 3, 2, false),
('Zlatan Ibrahimović', 11, '22222222R', 3, 2, true),

-- Jugadores para Manchester United (Team 4)
('David de Gea', 1, '41414141A', 4, 1, true),
('Harry Maguire', 5, '42424242B', 4, 1, false),
('Raphaël Varane', 19, '43434343C', 4, 1, false),
('Luke Shaw', 23, '44444444D', 4, 1, false),
('Bruno Fernandes', 18, '45454545E', 4, 1, false),
('Marcus Rashford', 10, '46464646F', 4, 1, false),
('Casemiro', 18, '47474747G', 4, 1, false),
('Christian Eriksen', 14, '48484848H', 4, 1, false),
('Jadon Sancho', 25, '49494949I', 4, 1, false),
('Anthony Martial', 9, '50505050J', 4, 1, false);

-- Jugadores para Bayern Munich (Team 5)
INSERT INTO PLAYER (name, dorsal_number, dni, team_id, client_id, is_goalkeeper) VALUES
('Manuel Neuer', 1, '51515151K', 5, 2, true),
('Joshua Kimmich', 6, '52525252L', 5, 2, false),
('Leon Goretzka', 8, '53535353M', 5, 2, false),
('Thomas Müller', 25, '54545454N', 5, 2, false),
('Leroy Sané', 10, '55555555O', 5, 2, false),
('Serge Gnabry', 7, '56565656P', 5, 2, false),
('Alphonso Davies', 19, '57575757Q', 5, 2, false),
('Dayot Upamecano', 2, '58585858R', 5, 2, false),
('Kingsley Coman', 11, '59595959S', 5, 2, false),
('Jamal Musiala', 42, '60606060T', 5, 2, false);

