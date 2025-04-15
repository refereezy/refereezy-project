document.addEventListener('DOMContentLoaded', () => {
    const API_URL = "http://localhost:8080";

    const refereeNameInput = document.querySelector('.referee-name-input');
    const matchSelect = document.getElementById('matchSelect');
    const addRefereeBtn = document.querySelector('.add-referee-btn');

    // 🔁 Obtener partidos y equipos desde la API
    async function loadAvailableMatches() {
        const matchRes = await fetch(API_URL + '/matches');
        const matches = await matchRes.json();

        const teamsRes = await fetch(API_URL + '/teams');
        const teams = await teamsRes.json();

        matchSelect.innerHTML = '<option value="">Seleccione un partido</option>';

        if (matches.length === 0) {
            matchSelect.innerHTML += '<option disabled>No hay partidos creados</option>';
            matchSelect.disabled = true;
            return;
        }

        matchSelect.disabled = false;

        let optionsAdded = false;

        matches.forEach(match => {
            const refereesCount = match.referees ? match.referees.length : 0;

            if (refereesCount < 4) {
                const localTeam = teams.find(team => Number(team.id) === Number(match.localTeam));
                const visitorTeam = teams.find(team => Number(team.id) === Number(match.visitorTeam));

                if (localTeam && visitorTeam) {
                    const option = document.createElement('option');
                    option.value = match.id;
                    option.textContent = `${localTeam.name} vs ${visitorTeam.name} - ${match.date} ${match.time} (${refereesCount}/4 árbitros)`;
                    matchSelect.appendChild(option);
                    optionsAdded = true;
                }
            }
        });

        if (!optionsAdded) {
            matchSelect.innerHTML += '<option disabled>Todos los partidos tienen 4 árbitros asignados</option>';
            matchSelect.disabled = true;
        }
    }

    // ➕ Agregar árbitro y asignarlo al partido
    addRefereeBtn.addEventListener('click', async () => {
        const refereeName = refereeNameInput.value.trim();
        const matchId = matchSelect.value;

        if (!refereeName) {
            alert('Por favor ingrese el nombre del árbitro');
            return;
        }

        // Verificar si ya existe el árbitro
        const res = await fetch(API_URL + '/referee');
        const referees = await res.json();

        const refereeExists = referees.some(ref => ref.name.toLowerCase() === refereeName.toLowerCase());

        if (refereeExists) {
            alert('Este árbitro ya está registrado');
            return;
        }

        // Crear árbitro nuevo
        const newReferee = {
            id: Date.now(),
            name: refereeName
        };

        const createRes = await fetch(API_URL + '/referee', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newReferee)
        });

        if (!createRes.ok) {
            alert('Error al crear el árbitro');
            return;
        }

        // Asignar árbitro al partido si se seleccionó uno
        if (matchId) {
            const matchRes = await fetch(`${API_URL}/matches/${matchId}`);
            const matchData = await matchRes.json();

            if (!matchData.referees) matchData.referees = [];

            if (matchData.referees.length >= 4) {
                alert('Este partido ya tiene 4 árbitros');
                return;
            }

            matchData.referees.push(newReferee.id);

            const updateRes = await fetch(`${API_URL}/matches/${matchId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(matchData)
            });

            if (!updateRes.ok) {
                alert('Error al asignar árbitro al partido');
                return;
            }
        }

        refereeNameInput.value = '';
        matchSelect.value = '';
        await loadAvailableMatches();

        alert('Árbitro agregado exitosamente!');
    });

    loadAvailableMatches();
});
