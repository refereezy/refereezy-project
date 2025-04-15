document.addEventListener('DOMContentLoaded', () => {
    const API_URL = "http://localhost:8080";
    const playersContent = document.querySelector('.players-content');

    // Función para crear una tarjeta de jugador
    function createPlayerCard(player) {
        const card = document.createElement('div');
        card.className = 'player-card';

        const info = document.createElement('div');
        info.className = 'player-info';

        const number = document.createElement('div');
        number.className = 'player-number';
        number.textContent = player.number;

        const name = document.createElement('div');
        name.className = 'player-name';
        name.textContent = player.name;

        info.appendChild(number);
        info.appendChild(name);
        card.appendChild(info);

        return card;
    }

    // Función para mostrar mensaje cuando no hay jugadores
    function showNoPlayersMessage() {
        playersContent.innerHTML = `
            <div class="no-players-message">
                <p>No hay jugadores registrados</p>
                <a href="../pages/jugadores.html" class="add-player-link">Agregar jugador</a>
            </div>
        `;
    }

    // Función para mostrar la cuadrícula de jugadores
    function showPlayersGrid(players) {
        const grid = document.createElement('div');
        grid.className = 'players-grid';

        // Ordenar jugadores por número
        players.sort((a, b) => Number(a.number) - Number(b.number));

        players.forEach(player => {
            const card = createPlayerCard(player);
            grid.appendChild(card);
        });

        playersContent.innerHTML = '';
        playersContent.appendChild(grid);
    }

    // Función para cargar y mostrar los jugadores desde la API
    async function loadPlayers() {
        try {
            const response = await fetch(`${API_URL}/players`);
            const players = await response.json();

            if (players.length === 0) {
                showNoPlayersMessage();
            } else {
                showPlayersGrid(players);
            }
        } catch (error) {
            playersContent.innerHTML = `
                <div class="no-players-message">
                    <p>Error al cargar los jugadores.</p>
                    <p style="font-size: 0.9em;">${error.message}</p>
                </div>
            `;
        }
    }

    // Cargar jugadores al iniciar
    loadPlayers();
});
