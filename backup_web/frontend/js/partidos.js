document.addEventListener('DOMContentLoaded', () => {
    // Referencias a elementos del DOM
    const localTeamSelect = document.getElementById('localTeam');
    const visitorTeamSelect = document.getElementById('visitorTeam');
    const matchDateInput = document.getElementById('matchDate');
    const matchTimeInput = document.getElementById('matchTime');
    const createMatchBtn = document.querySelector('.create-match-btn');
    const viewMatchesBtn = document.querySelector('.view-matches-btn');
    const API_URL = 'http://localhost:8080'; // URL de la API

    // Función para cargar equipos en los selects desde la API
    async function loadTeams() {
        try {
            const response = await fetch(`${API_URL}/teams`);
            const teams = await response.json();
            
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
        } catch (error) {
            console.error('Error cargando los equipos:', error);
        }
    }

    // Manejador para crear partido
    createMatchBtn.addEventListener('click', async () => {
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

        // Verificar si ya existe un partido con los mismos equipos y fecha/hora
        try {
            const response = await fetch(`${API_URL}/matches`);
            const matches = await response.json();

            const matchExists = matches.some(match => 
                match.localTeam === localTeam &&
                match.visitorTeam === visitorTeam &&
                match.date === matchDate &&
                match.time === matchTime
            );

            if (matchExists) {
                alert('Ya existe un partido con estos equipos en esta fecha y hora');
                return;
            }

            // Crear nuevo partido
            const newMatch = {
                localTeam,
                visitorTeam,
                date: matchDate,
                time: matchTime,
                referee: null // Se asignará después
            };

            // Enviar solicitud a la API para crear el nuevo partido
            const createResponse = await fetch(`${API_URL}/matches`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(newMatch),
            });

            if (!createResponse.ok) {
                alert('Error al crear el partido');
                return;
            }

            // Limpiar formulario
            localTeamSelect.value = '';
            visitorTeamSelect.value = '';
            matchDateInput.value = '';
            matchTimeInput.value = '';

            alert('Partido creado exitosamente!');
        } catch (error) {
            console.error('Error al crear el partido:', error);
            alert('Hubo un problema al crear el partido');
        }
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
