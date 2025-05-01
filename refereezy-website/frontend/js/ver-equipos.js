document.addEventListener('DOMContentLoaded', () => {
    // Use API_URL from base.js instead of redefining it
    const clientId = getClientId(); // Use getClientId from base.js
    const teamsGrid = document.querySelector('.teams-grid');

    // Función para crear una tarjeta de equipo
    function createTeamCard(team) {
        const card = document.createElement('div');
        card.className = 'team-card';

        const logoContainer = document.createElement('div');
        logoContainer.className = 'team-logo';

        if (team.logo_url) {
            const img = document.createElement('img');
            img.src = team.logo_url;
            img.alt = `${team.name} logo`;
            logoContainer.appendChild(img);
        } else {
            logoContainer.innerHTML = '<span style="color: rgba(255,255,255,0.5);">Sin logo</span>';
        }

        const nameDiv = document.createElement('div');
        nameDiv.className = 'team-name';
        nameDiv.textContent = team.name;

        card.appendChild(logoContainer);
        card.appendChild(nameDiv);

        return card;
    }

    // Función para cargar los equipos
    async function loadTeams() {
        try {
            const response = await fetch(`${API_URL}/teams/client/${clientId}`);
            const teams = await response.json();

            teamsGrid.innerHTML = '';

            if (teams.length === 0) {
                teamsGrid.innerHTML = `
                    <div class="no-teams-message">
                        <p>No hay equipos registrados</p>
                        <a href="../pages/equipos.html" class="add-team-link">Agregar equipo</a>
                    </div>
                `;
                return;
            }

            teams.forEach(team => {
                const card = createTeamCard(team);
                teamsGrid.appendChild(card);
            });
        } catch (error) {
            // Use showNotification from base.js
            showNotification('Error al cargar los equipos: ' + error.message, 'error');
            teamsGrid.innerHTML = `
                <div class="no-teams-message">
                    <p>Error al cargar los equipos.</p>
                    <p style="font-size: 0.9em;">${error.message}</p>
                </div>
            `;
        }
    }

    // Cargar equipos al iniciar
    loadTeams();
});
