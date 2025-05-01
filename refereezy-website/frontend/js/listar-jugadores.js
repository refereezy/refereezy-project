document.addEventListener('DOMContentLoaded', () => {
    // Referencias a elementos del DOM
    const playersGrid = document.querySelector('.players-grid');
    const playersContainer = document.querySelector('.players-container');
    const playerFilters = document.querySelector('.player-filters');
    const teamSearchInput = document.querySelector('.team-search-input');
    const teamDropdown = document.querySelector('.team-dropdown');
    const teamList = document.querySelector('.team-list');
    const selectedTeamName = document.querySelector('.selected-team-name');
    const resetFilterBtn = document.querySelector('.reset-filter-btn');
    const noPlayersMessage = document.querySelector('.no-players-message');
    const toggleFormBtn = document.querySelector('.toggle-form-btn');
    const playerFormModal = document.getElementById('playerFormModal');

    // Use API_URL from base.js instead of redefining it
    const clientId = getClientId(); // Use getClientId from base.js
    
    let teams = [];
    let allPlayers = [];
    let filteredPlayers = [];
    let selectedTeamId = null;

    // Cargar equipos y jugadores al iniciar
    initialize();

    // Inicializar la página
    async function initialize() {
        await fetchTeams();
        await fetchAllPlayers();
        renderPlayers(allPlayers);
        setupEventListeners();
    }

    // Configurar event listeners
    function setupEventListeners() {
        // Manejo de búsqueda de equipos para filtrar
        teamSearchInput.addEventListener('focus', () => {
            renderTeamList(teams);
            teamDropdown.style.display = 'block';
        });

        teamSearchInput.addEventListener('input', () => {
            const searchTerm = teamSearchInput.value.toLowerCase().trim();
            const filteredTeams = teams.filter(team => 
                team.name.toLowerCase().includes(searchTerm)
            );
            renderTeamList(filteredTeams);
        });

        // Cerrar dropdown al hacer clic fuera
        document.addEventListener('click', (e) => {
            if (!teamSearchInput.contains(e.target) && !teamDropdown.contains(e.target)) {
                teamDropdown.style.display = 'none';
            }
        });

        // Reset filtro
        resetFilterBtn.addEventListener('click', () => {
            selectedTeamId = null;
            selectedTeamName.textContent = 'Todos';
            teamSearchInput.value = '';
            renderPlayers(allPlayers);
        });

        // Abrir modal de formulario
        toggleFormBtn.addEventListener('click', () => {
            playerFormModal.style.display = 'block';
        });
    }

    // Función para cargar equipos desde la API
    async function fetchTeams() {
        try {
            const response = await fetch(`${API_URL}/teams/client/${clientId}`);
            if (!response.ok) {
                throw new Error('Error al cargar los equipos');
            }
            teams = await response.json();
        } catch (error) {
            console.error('Error al cargar equipos:', error);
            showNotification('No se pudieron cargar los equipos', 'error');
        }
    }

    // Función para cargar todos los jugadores de todos los equipos
    async function fetchAllPlayers() {
        try {
            allPlayers = [];
            const promises = teams.map(team => fetchTeamPlayers(team));
            await Promise.all(promises);
            
            // Ordenar por equipo y número de dorsal
            allPlayers.sort((a, b) => {
                if (a.team_name !== b.team_name) {
                    return a.team_name.localeCompare(b.team_name);
                }
                return a.dorsal_number - b.dorsal_number;
            });
            
            filteredPlayers = [...allPlayers];
        } catch (error) {
            console.error('Error al cargar jugadores:', error);
            showNotification('No se pudieron cargar los jugadores', 'error');
        }
    }

    // Función para cargar jugadores de un equipo específico
    async function fetchTeamPlayers(team) {
        try {
            const response = await fetch(`${API_URL}/players/team/${team.id}`);
            if (!response.ok) {
                throw new Error(`Error al cargar jugadores del equipo ${team.name}`);
            }
            const players = await response.json();
            
            // Añadir nombre del equipo a cada jugador para mostrar en la grid
            const playersWithTeam = players.map(player => ({
                ...player,
                team_name: team.name,
                team_logo: team.logo || null
            }));
            
            allPlayers = [...allPlayers, ...playersWithTeam];
        } catch (error) {
            console.error(`Error al cargar jugadores del equipo ${team.name}:`, error);
        }
    }

    // Renderizar lista de equipos para el filtro
    function renderTeamList(teamsToRender) {
        teamList.innerHTML = '';
        
        if (teamsToRender.length === 0) {
            const noResults = document.createElement('li');
            noResults.textContent = 'No se encontraron equipos';
            teamList.appendChild(noResults);
            return;
        }
        
        teamsToRender.forEach(team => {
            const teamItem = document.createElement('li');
            teamItem.textContent = team.name;
            teamItem.addEventListener('click', () => {
                filterPlayersByTeam(team);
            });
            teamList.appendChild(teamItem);
        });
    }

    // Filtrar jugadores por equipo
    function filterPlayersByTeam(team) {
        selectedTeamId = team.id;
        selectedTeamName.textContent = team.name;
        teamSearchInput.value = team.name;
        teamDropdown.style.display = 'none';
        
        filteredPlayers = allPlayers.filter(player => player.team_id === team.id);
        renderPlayers(filteredPlayers);
    }

    // Renderizar la grid de jugadores
    function renderPlayers(players) {
        if (players.length === 0) {
            playersGrid.style.display = 'none';
            noPlayersMessage.style.display = 'flex';
            return;
        }
        
        playersGrid.style.display = 'grid';
        noPlayersMessage.style.display = 'none';
        
        // Limpiar grid antes de añadir nuevos jugadores
        playersGrid.innerHTML = `
            <div class="player-header">Nº</div>
            <div class="player-header">Nombre</div>
            <div class="player-header">DNI</div>
            <div class="player-header">Equipo</div>
            <div class="player-header">Posición</div>
        `;
        
        players.forEach(player => {
            // Número de dorsal
            const dorsalNumber = document.createElement('div');
            dorsalNumber.className = 'player-cell player-number';
            dorsalNumber.textContent = player.dorsal_number;
            
            // Nombre del jugador
            const playerName = document.createElement('div');
            playerName.className = 'player-cell player-name';
            playerName.textContent = player.name;
            
            // DNI del jugador
            const playerDni = document.createElement('div');
            playerDni.className = 'player-cell player-dni';
            playerDni.textContent = player.dni;
            
            // Equipo del jugador
            const playerTeam = document.createElement('div');
            playerTeam.className = 'player-cell player-team';
            playerTeam.textContent = player.team_name;
            
            // Posición del jugador
            const playerPosition = document.createElement('div');
            playerPosition.className = 'player-cell player-position';
            playerPosition.textContent = player.is_goalkeeper ? 'Portero' : 'Jugador';
            
            // Añadir celdas a la grid
            playersGrid.appendChild(dorsalNumber);
            playersGrid.appendChild(playerName);
            playersGrid.appendChild(playerDni);
            playersGrid.appendChild(playerTeam);
            playersGrid.appendChild(playerPosition);
        });
    }

    // Exponer funciones que necesitará el archivo de formulario
    window.playerManager = {
        refreshPlayers: async () => {
            await fetchAllPlayers();
            if (selectedTeamId) {
                filteredPlayers = allPlayers.filter(player => player.team_id === selectedTeamId);
                renderPlayers(filteredPlayers);
            } else {
                renderPlayers(allPlayers);
            }
        },
        getTeams: () => teams
    };
});