document.addEventListener('DOMContentLoaded', () => {
    const matchesContent = document.querySelector('.matches-content');
    const API_URL = "http://localhost:8080";

    // Obtener detalles de los árbitros desde la API
    async function getRefereesDetails(refereeIds) {
        if (!refereeIds || !Array.isArray(refereeIds) || refereeIds.length === 0) {
            return 'Sin árbitros asignados';
        }

        try {
            const res = await fetch(`${API_URL}/referees`);
            const referees = await res.json();

            const assignedReferees = refereeIds
                .map(id => {
                    const referee = referees.find(r => Number(r.id) === Number(id));
                    return referee ? referee.name : null;
                })
                .filter(name => name !== null);

            if (assignedReferees.length === 0) {
                return 'Sin árbitros asignados';
            }

            return assignedReferees.join(', ');
        } catch (err) {
            console.error("Error al cargar árbitros:", err);
            return 'Error al cargar árbitros';
        }
    }

    // Obtener nombres de equipos
    async function getTeamDetails(localTeamId, visitorTeamId) {
        try {
            const res = await fetch(`${API_URL}/matches/client/1/with_teams?client_id=1`);
            const matches = await res.json();
    
            let localTeam = null;
            let visitorTeam = null;
    
            for (const match of matches) {
                if (Number(match.local_team?.id) === Number(localTeamId)) {
                    localTeam = match.local_team;
                }
    
                if (Number(match.visitor_team?.id) === Number(visitorTeamId)) {
                    visitorTeam = match.visitor_team;
                }
    
                if (localTeam && visitorTeam) break;
            }
    
            return {
                localName: localTeam ? localTeam.name : 'Equipo no encontrado',
                visitorName: visitorTeam ? visitorTeam.name : 'Equipo no encontrado'
            };
        } catch (err) {
            console.error("Error al cargar equipos:", err);
            return {
                localName: 'Error',
                visitorName: 'Error'
            };
        }
    }

    // Crear tarjeta de partido con datos completos
    async function createMatchCard(match) {
        const card = document.createElement('div');
        card.className = 'match-card';

        const { localName, visitorName } = await getTeamDetails(match.local_team?.id, match.visitor_team?.id);

        console.log(`Partido: ${localName} vs ${visitorName}`); // 👈 Aquí imprime en consola

        const refereesNames = await getRefereesDetails(match.referees);
        const refereesCount = match.referees ? match.referees.length : 0;

        const info = document.createElement('div');
        info.className = 'match-info';

        const teamsDiv = document.createElement('div');
        teamsDiv.className = 'match-teams';
        teamsDiv.textContent = `${localName} vs ${visitorName}`;

        const details = document.createElement('div');
        details.className = 'match-details';

        const dateTime = document.createElement('div');
        dateTime.className = 'match-date-time';
        dateTime.textContent = `${match.date} - ${match.time}`;

        const referees = document.createElement('div');
        referees.className = 'match-referee';
        referees.textContent = `Árbitros (${refereesCount}/4): ${refereesNames}`;

        const status = document.createElement('div');
        status.className = `match-status ${refereesCount === 0 ? 'pending' : ''}`;
        status.textContent = refereesCount === 4
            ? 'Cupo de árbitros completo'
            : `${4 - refereesCount} cupos disponibles`;

        details.appendChild(dateTime);
        info.appendChild(teamsDiv);
        info.appendChild(details);
        info.appendChild(referees);
        info.appendChild(status);
        card.appendChild(info);

        return card;
    }

    // Mostrar mensaje cuando no hay partidos
    function showNoMatchesMessage() {
        matchesContent.innerHTML = `
            <div class="no-matches-message">
                <p>No hay partidos registrados</p>
                <a href="../pages/partidos.html" class="add-match-link">Crear partido</a>
            </div>
        `;
    }

    // Mostrar los partidos en pantalla
    async function showMatchesGrid(matches) {
        const grid = document.createElement('div');
        grid.className = 'matches-grid';

        matches.sort((a, b) => {
            const dateA = new Date(`${a.date} ${a.time}`);
            const dateB = new Date(`${b.date} ${b.time}`);
            return dateA - dateB;
        });

        for (const match of matches) {
            const card = await createMatchCard(match);
            grid.appendChild(card);
        }

        matchesContent.innerHTML = '';
        matchesContent.appendChild(grid);
    }

    // Cargar partidos desde la API
    async function loadMatches() {
        try {
            const res = await fetch(`${API_URL}/matches/client/1/with_teams?client_id=1`);
            const matches = await res.json();
            console.log(matches);
            if (matches.length === 0) {
                showNoMatchesMessage();
            } else {
                await showMatchesGrid(matches);
            }
        } catch (err) {
            matchesContent.innerHTML = `
                <div class="no-matches-message">
                    <p>Error al cargar los partidos</p>
                    <p style="font-size: 0.9em;">${err.message}</p>
                </div>
            `;
        }
    }

    loadMatches();

    // Escuchar eventos de cambio (por si lo necesitas mantener)
    window.addEventListener('storage', (e) => {
        if (e.key === 'matches' || e.key === 'teams' || e.key === 'referees') {
            loadMatches();
        }
    });
});
