document.addEventListener('DOMContentLoaded', () => {
    const matchesContent = document.querySelector('.matches-content');
    
    // Función para obtener detalles de los árbitros
    function getRefereesDetails(refereeIds) {
        if (!refereeIds || !Array.isArray(refereeIds) || refereeIds.length === 0) {
            return 'Sin árbitros asignados';
        }

        const referees = JSON.parse(localStorage.getItem('referees')) || [];
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
    }
    
    // Función para obtener detalles de los equipos
    function getTeamDetails(localTeamId, visitorTeamId) {
        const teams = JSON.parse(localStorage.getItem('teams')) || [];
        const localTeam = teams.find(team => Number(team.id) === Number(localTeamId));
        const visitorTeam = teams.find(team => Number(team.id) === Number(visitorTeamId));
        
        return {
            localName: localTeam ? localTeam.name : 'Equipo no encontrado',
            visitorName: visitorTeam ? visitorTeam.name : 'Equipo no encontrado'
        };
    }
    
    // Función para crear una tarjeta de partido
    function createMatchCard(match) {
        const card = document.createElement('div');
        card.className = 'match-card';
        
        const teams = getTeamDetails(match.localTeam, match.visitorTeam);
        const refereesNames = getRefereesDetails(match.referees);
        const refereesCount = match.referees ? match.referees.length : 0;
        
        const info = document.createElement('div');
        info.className = 'match-info';
        
        // Equipos
        const teamsDiv = document.createElement('div');
        teamsDiv.className = 'match-teams';
        teamsDiv.textContent = `${teams.localName} vs ${teams.visitorName}`;
        
        // Detalles
        const details = document.createElement('div');
        details.className = 'match-details';
        
        const dateTime = document.createElement('div');
        dateTime.className = 'match-date-time';
        dateTime.textContent = `${match.date} - ${match.time}`;
        
        const referees = document.createElement('div');
        referees.className = 'match-referee';
        referees.textContent = `Árbitros (${refereesCount}/4): ${refereesNames}`;
        
        // Estado
        const status = document.createElement('div');
        status.className = `match-status ${refereesCount === 0 ? 'pending' : ''}`;
        status.textContent = refereesCount === 4 ? 
            'Cupo de árbitros completo' : 
            `${4 - refereesCount} cupos disponibles`;
        
        details.appendChild(dateTime);
        info.appendChild(teamsDiv);
        info.appendChild(details);
        info.appendChild(referees);
        info.appendChild(status);
        card.appendChild(info);
        
        return card;
    }
    
    // Función para mostrar mensaje cuando no hay partidos
    function showNoMatchesMessage() {
        matchesContent.innerHTML = `
            <div class="no-matches-message">
                <p>No hay partidos registrados</p>
                <a href="partidos.html" class="add-match-link">Crear partido</a>
            </div>
        `;
    }
    
    // Función para mostrar la cuadrícula de partidos
    function showMatchesGrid(matches) {
        const grid = document.createElement('div');
        grid.className = 'matches-grid';
        
        // Ordenar partidos por fecha y hora
        matches.sort((a, b) => {
            const dateA = new Date(`${a.date} ${a.time}`);
            const dateB = new Date(`${b.date} ${b.time}`);
            return dateA - dateB;
        });
        
        matches.forEach(match => {
            const card = createMatchCard(match);
            grid.appendChild(card);
        });
        
        matchesContent.innerHTML = '';
        matchesContent.appendChild(grid);
    }
    
    // Función para cargar y mostrar los partidos
    function loadMatches() {
        const matches = JSON.parse(localStorage.getItem('matches')) || [];
        
        if (matches.length === 0) {
            showNoMatchesMessage();
        } else {
            showMatchesGrid(matches);
        }
    }
    
    // Cargar partidos al iniciar
    loadMatches();
    
    // Actualizar cuando cambie el localStorage
    window.addEventListener('storage', (e) => {
        if (e.key === 'matches' || e.key === 'teams' || e.key === 'referees') {
            loadMatches();
        }
    });
});