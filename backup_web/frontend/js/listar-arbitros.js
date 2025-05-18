// Módulo para gestionar la visualización de árbitros
document.addEventListener('DOMContentLoaded', () => {
    checkAuth()
    // Use API_URL from base.js
    const clientId = getClientId();

    // DOM Elements
    const refereeContent = document.querySelector('.referees-content');
    
    // Assign Match Modal
    const modal = document.getElementById('assignMatchModal');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const assignMatchBtn = document.getElementById('assignMatchBtn');
    const availableMatchesSelect = document.getElementById('availableMatches');
    const selectedRefereeNameSpan = document.getElementById('selectedRefereeName');
    
    let selectedRefereeId = null;
    let allMatches = [];
    let allTeams = [];

    // Eventos iniciales
    setupEvents();
    loadReferees();

    function setupEvents() {
        // Eventos para el modal de asignar partido
        closeModalBtn.addEventListener('click', closeModal);
        assignMatchBtn.addEventListener('click', assignMatch);

        window.addEventListener('click', (e) => {
            if (e.target === modal) {
                closeModal();
            }
        });
    }

    async function fetchMatchesAndTeams() {
        try {
            // Cargar partidos desde API
            const matchesRes = await fetch(`${API_URL}/matches/client/${clientId}`);
            if (!matchesRes.ok) throw new Error('Error al cargar partidos');
            allMatches = await matchesRes.json();

            // Cargar equipos desde API
            const teamsRes = await fetch(`${API_URL}/teams/client/${clientId}`);
            if (!teamsRes.ok) throw new Error('Error al cargar equipos');
            allTeams = await teamsRes.json();
        } catch (error) {
            console.error('Error al cargar datos:', error);
            showNotification('Error al cargar datos: ' + error.message, 'error');
        }
    }

    // Verificar si un árbitro tiene partidos asignados
    async function getAssignedMatches(refereeId) {
        try {
            const res = await fetch(`${API_URL}/referee/${refereeId}/matches`);
            if (!res.ok) {
                if (res.status === 404) {
                    return [];
                }
                throw new Error('Error al obtener partidos asignados');
            }
            return await res.json();
        } catch (error) {
            console.error('Error:', error);
            showNotification('Error al obtener partidos asignados', 'error');
            return [];
        }
    }

    // Crear tarjeta para cada árbitro
    async function createRefereeCard(referee) {
        const card = document.createElement('div');
        card.className = 'referee-card';

        const assignedMatches = await getAssignedMatches(referee.id);

        const info = document.createElement('div');
        info.className = 'referee-info';

        const name = document.createElement('div');
        name.className = 'referee-name';
        name.textContent = referee.name;

        const dniInfo = document.createElement('div');
        dniInfo.textContent = `DNI: ${referee.dni}`;
        dniInfo.style.marginBottom = '0.5rem';

        const matches = document.createElement('div');
        matches.className = 'referee-matches';
        matches.textContent = assignedMatches.length > 0
            ? `${assignedMatches.length} partidos asignados`
            : 'Sin partidos asignados';

        info.appendChild(name);
        info.appendChild(dniInfo);
        info.appendChild(matches);

        const assignButton = document.createElement('button');
        assignButton.className = 'assign-match-btn';
        assignButton.textContent = 'Asignar a Partido';
        assignButton.dataset.refereeId = referee.id;
        assignButton.addEventListener('click', () => openAssignModal(referee));
        info.appendChild(assignButton);

        card.appendChild(info);
        return card;
    }

    // Cargar opciones de partidos disponibles para asignar
    async function loadAvailableMatches() {
        availableMatchesSelect.innerHTML = '<option value="">Seleccione un partido</option>';

        try {
            // Filtrar partidos que no tienen árbitro asignado
            const availableMatches = allMatches.filter(match => 
                !match.referee_id || match.referee_id === 0);

            if (availableMatches.length === 0) {
                const option = document.createElement('option');
                option.textContent = "No hay partidos disponibles para asignar";
                option.disabled = true;
                availableMatchesSelect.appendChild(option);
                assignMatchBtn.disabled = true;
                return;
            }

            availableMatchesSelect.disabled = false;
            assignMatchBtn.disabled = false;

            for (const match of availableMatches) {
                try {
                    // Obtener detalles del partido
                    const matchInfoRes = await fetch(`${API_URL}/matches/short/${match.id}`);
                    if (!matchInfoRes.ok) continue;
                    
                    const matchInfo = await matchInfoRes.json();
                    const localTeam = matchInfo.local_team;
                    const visitorTeam = matchInfo.visitor_team;
                    
                    const option = document.createElement('option');
                    option.value = match.id;
                    
                    // Formato de fecha usando funciones de base.js
                    const matchDate = new Date(match.date);
                    const formattedDate = formatDate(match.date);
                    const formattedTime = formatTime(match.date);
                    
                    option.textContent = `${localTeam.name} vs ${visitorTeam.name} - ${formattedDate} ${formattedTime}`;
                    availableMatchesSelect.appendChild(option);
                } catch (error) {
                    console.error(`Error al cargar info del partido ${match.id}:`, error);
                }
            }
        } catch (error) {
            console.error('Error al cargar partidos disponibles:', error);
            showNotification('Error al cargar partidos disponibles', 'error');
        }
    }

    // Abrir modal para asignar un partido
    async function openAssignModal(referee) {
        selectedRefereeId = referee.id;
        selectedRefereeNameSpan.textContent = referee.name;

        await fetchMatchesAndTeams();
        await loadAvailableMatches();
        modal.style.display = 'block';
    }

    // Cerrar modal
    function closeModal() {
        modal.style.display = 'none';
        selectedRefereeId = null;
        availableMatchesSelect.value = '';
    }

    // Asignar partido a árbitro
    async function assignMatch() {
        const matchId = availableMatchesSelect.value;
        
        if (!matchId || !selectedRefereeId) {
            showNotification('Por favor seleccione un partido', 'error');
            return;
        }

        try {
            // Obtener partido actual
            const matchRes = await fetch(`${API_URL}/matches/${matchId}`);
            if (!matchRes.ok) throw new Error('Partido no encontrado');
            
            const match = await matchRes.json();
            
            // Actualizar asignación de árbitro
            match.referee_id = selectedRefereeId;
            
            // Guardar cambios
            const updateRes = await fetch(`${API_URL}/matches/${matchId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(match)
            });

            if (!updateRes.ok) throw new Error('Error al asignar árbitro');
            
            showNotification('Árbitro asignado exitosamente', 'success');
            closeModal();
            loadReferees(); // Recargar lista de árbitros
        } catch (error) {
            console.error('Error:', error);
            showNotification('Error al asignar árbitro: ' + error.message, 'error');
        }
    }

    // Mensaje cuando no hay árbitros
    function showNoRefereeMessage() {
        refereeContent.innerHTML = `
            <div class="no-referee-message">
                <p>No hay árbitros registrados</p>
                <p>Utiliza el botón "Agregar Árbitro" para crear uno nuevo</p>
            </div>
        `;
    }

    // Mostrar grid de árbitros
    async function showRefereeGrid(referees) {
        const grid = document.createElement('div');
        grid.className = 'referee-grid';
        
        // Ordenar por nombre
        referees.sort((a, b) => a.name.localeCompare(b.name));

        // Crear tarjeta para cada árbitro
        for (const referee of referees) {
            const card = await createRefereeCard(referee);
            grid.appendChild(card);
        }

        refereeContent.innerHTML = '';
        refereeContent.appendChild(grid);
    }

    // Cargar lista de árbitros
    async function loadReferees() {
        try {
            const res = await fetch(`${API_URL}/referee/client/${clientId}`);
            if (!res.ok) throw new Error('Error al cargar árbitros');
            
            const referees = await res.json();
            
            await fetchMatchesAndTeams();

            if (referees.length === 0) {
                showNoRefereeMessage();
            } else {
                await showRefereeGrid(referees);
            }
        } catch (error) {
            console.error('Error:', error);
            showNotification('Error al cargar árbitros: ' + error.message, 'error');
        }
    }

    // No need to define showNotification anymore as it's available from base.js

    // Exponer funciones para comunicación con el módulo de formulario
    window.refereeManager = {
        refreshReferees: loadReferees,
        showNotification
    };
});