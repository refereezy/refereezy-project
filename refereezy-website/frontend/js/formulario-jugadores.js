document.addEventListener('DOMContentLoaded', () => {
    // Referencias a elementos del DOM del formulario
    const playerNameInput = document.querySelector('.player-name-input');
    const playerNumberInput = document.querySelector('.player-number-input');
    const playerDniInput = document.querySelector('.player-dni-input');
    const teamSearchInput = document.querySelector('.form-team-search-input');
    const teamDropdown = document.querySelector('.form-team-dropdown');
    const teamList = document.querySelector('.form-team-list');
    const selectedTeamName = document.querySelector('.form-selected-team-name');
    const goalkeeperCheckbox = document.querySelector('.goalkeeper-checkbox');
    const addPlayerBtn = document.querySelector('.add-player-btn');

    const API_URL = "http://localhost:8080";
    const clientId = localStorage.getItem('client_id');
    
    let selectedTeam = null;

    // Configurar eventos del formulario
    setupFormEvents();

    function setupFormEvents() {
        // Manejo de búsqueda de equipos para el formulario
        teamSearchInput.addEventListener('focus', () => {
            // Obtener equipos del módulo principal
            const teams = window.playerManager.getTeams();
            renderTeamList(teams);
            teamDropdown.style.display = 'block';
        });

        teamSearchInput.addEventListener('input', () => {
            const teams = window.playerManager.getTeams();
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

        // Manejador para agregar jugador
        addPlayerBtn.addEventListener('click', addPlayer);
    }

    // Renderizar lista de equipos para el formulario
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
                selectTeam(team);
            });
            teamList.appendChild(teamItem);
        });
    }

    // Seleccionar un equipo para el formulario
    function selectTeam(team) {
        selectedTeam = team;
        selectedTeamName.textContent = team.name;
        teamSearchInput.value = team.name;
        teamDropdown.style.display = 'none';
    }

    // Añadir un nuevo jugador
    async function addPlayer() {
        const playerName = playerNameInput.value.trim();
        const playerNumber = playerNumberInput.value.trim();
        const playerDni = playerDniInput.value.trim();
        const isGoalkeeper = goalkeeperCheckbox.checked;

        // Validar que todos los campos requeridos estén completos
        if (!playerName || !playerNumber || !playerDni || !selectedTeam) {
            showNotification('Por favor complete todos los campos y seleccione un equipo', 'error');
            return;
        }

        try {
            // Verificar si el número ya está en uso (en el backend)
            const resCheck = await fetch(`${API_URL}/players/team/${selectedTeam.id}/number/${playerNumber}`);
            if (resCheck.ok) {
                showNotification('Este número de jugador ya está en uso', 'error');
                return;
            }

            // Agregar nuevo jugador
            const newPlayer = {
                name: playerName,
                dorsal_number: playerNumber,
                dni: playerDni,
                team_id: selectedTeam.id,
                client_id: parseInt(clientId),
                is_goalkeeper: isGoalkeeper,
            };

            const res = await fetch(`${API_URL}/players`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newPlayer)
            });

            if (!res.ok) {
                const errorText = await res.text();
                throw new Error(errorText || 'Error al agregar el jugador');
            }

            showNotification('Jugador agregado exitosamente!', 'success');

            // Limpiar formulario
            playerNameInput.value = '';
            playerNumberInput.value = '';
            playerDniInput.value = '';
            goalkeeperCheckbox.checked = false;
            selectedTeam = null;
            selectedTeamName.textContent = 'Ninguno';
            teamSearchInput.value = '';

            // Actualizar la lista de jugadores
            await window.playerManager.refreshPlayers();
        } catch (err) {
            console.error(err);
            showNotification('Error al agregar el jugador: ' + err.message, 'error');
        }
    }

    // Mostrar notificación de error o éxito
    function showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.textContent = message;
        
        document.body.appendChild(notification);
        
        setTimeout(() => {
            notification.classList.add('show');
            
            setTimeout(() => {
                notification.classList.remove('show');
                setTimeout(() => {
                    document.body.removeChild(notification);
                }, 300);
            }, 3000);
        }, 100);
    }
});