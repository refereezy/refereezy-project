document.addEventListener('DOMContentLoaded', () => {
    const teamsGrid = document.querySelector('.teams-grid');
    
    // Función para crear una tarjeta de equipo
    function createTeamCard(team) {
        const card = document.createElement('div');
        card.className = 'team-card';
        
        const logoContainer = document.createElement('div');
        logoContainer.className = 'team-logo';
        
        if (team.logo) {
            const img = document.createElement('img');
            img.src = team.logo;
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
    function loadTeams() {
        // Obtener equipos del localStorage
        const teams = JSON.parse(localStorage.getItem('teams')) || [];
        
        // Limpiar el grid
        teamsGrid.innerHTML = '';
        
        if (teams.length === 0) {
            // Mostrar mensaje cuando no hay equipos
            teamsGrid.innerHTML = `
                <div class="no-teams-message">
                    <p>No hay equipos registrados</p>
                    <a href="../pages/equipos.html" class="add-team-link">Agregar equipo</a>
                </div>
            `;
            return;
        }
        
        // Agregar cada equipo al grid
        teams.forEach(team => {
            const card = createTeamCard(team);
            teamsGrid.appendChild(card);
        });
    }
    
    // Cargar equipos al iniciar
    loadTeams();
});