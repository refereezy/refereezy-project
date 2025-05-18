document.addEventListener('DOMContentLoaded', () => {
    checkAuth()
    // Referencias a elementos del DOM
    const teamsGrid = document.querySelector('.teams-grid');
    const teamsContainer = document.querySelector('.teams-container');
    const teamFilters = document.querySelector('.team-filters');
    const teamSearchInput = document.querySelector('.team-search-input');
    const noTeamsMessage = document.querySelector('.no-teams-message');
    const toggleFormBtn = document.querySelector('.toggle-form-btn');
    const teamFormModal = document.getElementById('teamFormModal');

    // Use API_URL from base.js
    const clientId = getClientId(); 
    
    let allTeams = [];
    let filteredTeams = [];

    // Inicializar la página
    initialize();

    // Inicializar la página
    async function initialize() {
        await fetchTeams();
        renderTeams(allTeams);
        setupEventListeners();
    }

    // Configurar event listeners
    function setupEventListeners() {
        // Filtrar equipos por nombre
        teamSearchInput.addEventListener('input', () => {
            const searchTerm = teamSearchInput.value.toLowerCase().trim();
            filteredTeams = allTeams.filter(team => 
                team.name.toLowerCase().includes(searchTerm)
            );
            renderTeams(filteredTeams);
        });

        // Abrir modal de formulario
        toggleFormBtn.addEventListener('click', () => {
            teamFormModal.style.display = 'block';
        });
        
        // Cerrar modal
        const closeTeamFormBtn = document.getElementById('closeTeamFormBtn');
        closeTeamFormBtn.addEventListener('click', () => {
            teamFormModal.style.display = 'none';
        });

        // Cerrar modal al hacer clic fuera
        window.addEventListener('click', (e) => {
            if (e.target === teamFormModal) {
                teamFormModal.style.display = 'none';
            }
        });
    }

    // Función para cargar equipos desde la API
    async function fetchTeams() {
        try {
            const response = await fetch(`${API_URL}/teams/client/${clientId}`);
            if (!response.ok) {
                throw new Error('Error al cargar los equipos');
            }
            const teams = await response.json();
            
            // Para cada equipo, obtener el número de jugadores
            allTeams = await Promise.all(teams.map(async (team) => {
                const playerCount = await fetchPlayerCount(team.id);
                return {
                    ...team,
                    playerCount
                };
            }));
            
            // Ordenar por nombre
            allTeams.sort((a, b) => a.name.localeCompare(b.name));
            
            filteredTeams = [...allTeams];
        } catch (error) {
            console.error('Error al cargar equipos:', error);
            showNotification('No se pudieron cargar los equipos', 'error');
        }
    }

    // Obtener número de jugadores por equipo
    async function fetchPlayerCount(teamId) {
        try {
            const response = await fetch(`${API_URL}/players/team/${teamId}`);
            if (!response.ok) {
                return 0;
            }
            const players = await response.json();
            return players.length;
        } catch (error) {
            console.error(`Error al obtener jugadores del equipo ${teamId}:`, error);
            return 0;
        }
    }

    // Función para eliminar un equipo
    async function deleteTeam(teamId, teamName) {
        if (!confirm(`¿Estás seguro de que deseas eliminar el equipo ${teamName}?`)) {
            return;
        }
        
        try {
            const response = await fetch(`${API_URL}/teams/${teamId}`, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                throw new Error('Error al eliminar el equipo');
            }
            
            showNotification(`Equipo ${teamName} eliminado correctamente`, 'success');
            await fetchTeams();
            renderTeams(filteredTeams.length > 0 ? filteredTeams : allTeams);
        } catch (error) {
            console.error('Error al eliminar equipo:', error);
            showNotification(`Error al eliminar el equipo: ${error.message}`, 'error');
        }
    }

    // Renderizar la grid de equipos
    function renderTeams(teams) {
        if (teams.length === 0) {
            teamsGrid.style.display = 'none';
            noTeamsMessage.style.display = 'flex';
            return;
        }
        
        teamsGrid.style.display = 'grid';
        noTeamsMessage.style.display = 'none';
        
        // Limpiar grid antes de añadir nuevos equipos
        teamsGrid.innerHTML = `
            <div class="team-header">Escudo</div>
            <div class="team-header">Nombre</div>
            <div class="team-header">Jugadores</div>
            <div class="team-header">Colores</div>
            <div class="team-header">Acciones</div>
        `;
        
        teams.forEach(team => {
            // Escudo del equipo
            const teamLogo = document.createElement('div');
            teamLogo.className = 'team-cell team-logo';
            if (team.logo_url) {
                const logo = document.createElement('img');
                logo.src = team.logo_url;
                logo.alt = `${team.name} logo`;
                teamLogo.appendChild(logo);
            } else {
                teamLogo.innerHTML = '<i class="fas fa-shield-alt"></i>';
            }
            
            // Nombre del equipo
            const teamName = document.createElement('div');
            teamName.className = 'team-cell team-name';
            teamName.textContent = team.name;
            
            // Número de jugadores
            const playerCount = document.createElement('div');
            playerCount.className = 'team-cell player-count';
            playerCount.textContent = team.playerCount || 0;
            
            // Colores del equipo
            const teamColors = document.createElement('div');
            teamColors.className = 'team-cell team-colors';
            
            const colorContainer = document.createElement('div');
            colorContainer.className = 'color-container';
            
            const primaryColor = document.createElement('div');
            primaryColor.className = 'color-sample primary-color';
            primaryColor.style.backgroundColor = team.primary_color;
            
            const secondaryColor = document.createElement('div');
            secondaryColor.className = 'color-sample secondary-color';
            secondaryColor.style.backgroundColor = team.secondary_color;
            
            colorContainer.appendChild(primaryColor);
            colorContainer.appendChild(secondaryColor);
            teamColors.appendChild(colorContainer);
            
            // Botones de acción
            const teamActions = document.createElement('div');
            teamActions.className = 'team-cell team-actions';
            
            const deleteButton = document.createElement('button');
            deleteButton.className = 'team-action-btn delete-btn';
            deleteButton.innerHTML = '<i class="fas fa-trash"></i>';
            deleteButton.title = 'Eliminar equipo';
            deleteButton.addEventListener('click', () => {
                deleteTeam(team.id, team.name);
            });
            
            teamActions.appendChild(deleteButton);
            
            // Añadir celdas a la grid
            teamsGrid.appendChild(teamLogo);
            teamsGrid.appendChild(teamName);
            teamsGrid.appendChild(playerCount);
            teamsGrid.appendChild(teamColors);
            teamsGrid.appendChild(teamActions);
        });
    }

    // Exponer funciones que necesitará el archivo de formulario
    window.teamManager = {
        refreshTeams: async () => {
            await fetchTeams();
            renderTeams(filteredTeams.length > 0 ? filteredTeams : allTeams);
        }
    };
});