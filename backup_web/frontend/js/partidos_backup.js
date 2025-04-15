document.addEventListener('DOMContentLoaded', () => {
    // Referencias a elementos del DOM
    const localTeamSelect = document.getElementById('localTeam');
    const visitorTeamSelect = document.getElementById('visitorTeam');
    const matchDateInput = document.getElementById('matchDate');
    const matchTimeInput = document.getElementById('matchTime');
    const createMatchBtn = document.querySelector('.create-match-btn');
    const viewMatchesBtn = document.querySelector('.view-matches-btn');

    // Función para cargar equipos en los selects
    function loadTeams() {
        const teams = JSON.parse(localStorage.getItem('teams')) || [];
        
        // Ordenar equipos por nombre
        teams.sort((a, b) => a.name.localeCompare(b.name));
        
        // Limpiar opciones existentes
        localTeamSelect.innerHTML = '<option value="">Seleccione equipo local</option>';
        visitorTeamSelect.innerHTML = '<option value="">Seleccione equipo visitante</option>';
        
        // Agregar equipos a los selects
        teams.forEach(team => {
            const localOption = document.createElement('option');
            localOption.value = team.id;
            localOption.textContent = team.name;
            localTeamSelect.appendChild(localOption);
            
            const visitorOption = document.createElement('option');
            visitorOption.value = team.id;
            visitorOption.textContent = team.name;
            visitorTeamSelect.appendChild(visitorOption);
        });
    }

    // Manejador para crear partido
    createMatchBtn.addEventListener('click', () => {
        const localTeam = localTeamSelect.value;
        const visitorTeam = visitorTeamSelect.value;
        const matchDate = matchDateInput.value;
        const matchTime = matchTimeInput.value;

        if (!localTeam || !visitorTeam || !matchDate || !matchTime) {
            alert('Por favor complete todos los campos');
            return;
        }

        if (localTeam === visitorTeam) {
            alert('El equipo local y visitante no pueden ser el mismo');
            return;
        }

        // Obtener partidos existentes
        const matches = JSON.parse(localStorage.getItem('matches')) || [];
        
        // Crear nuevo partido
        const newMatch = {
            id: Date.now(),
            localTeam,
            visitorTeam,
            date: matchDate,
            time: matchTime,
            referee: null // Se asignará después
        };
        
        // Agregar nuevo partido
        matches.push(newMatch);
        
        // Guardar en localStorage
        localStorage.setItem('matches', JSON.stringify(matches));
        
        // Limpiar formulario
        localTeamSelect.value = '';
        visitorTeamSelect.value = '';
        matchDateInput.value = '';
        matchTimeInput.value = '';
        
        alert('Partido creado exitosamente!');
    });

    // Manejador para ver partidos
    viewMatchesBtn.addEventListener('click', () => {
        window.location.href = "../pages/ver-partidos.html";
    });

    // Cargar equipos al iniciar
    loadTeams();

    // Actualizar cuando cambie el localStorage de equipos
    window.addEventListener('storage', (e) => {
        if (e.key === 'teams') {
            loadTeams();
        }
    });
});