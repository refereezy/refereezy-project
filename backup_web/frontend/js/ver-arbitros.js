document.addEventListener('DOMContentLoaded', () => {
    const API_URL = "http://localhost:8080";

    const refereeContent = document.querySelector('.referees-content');
    const modal = document.getElementById('assignMatchModal');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const assignMatchBtn = document.getElementById('assignMatchBtn');
    const availableMatchesSelect = document.getElementById('availableMatches');
    const selectedRefereeNameSpan = document.getElementById('selectedRefereeName');
    let selectedRefereeId = null;
    let allMatches = [];
    let allTeams = [];

    async function fetchMatchesAndTeams() {
        const matchesRes = await fetch(`${API_URL}/matches`);
        allMatches = await matchesRes.json();

        const teamsRes = await fetch(`${API_URL}/teams`);
        allTeams = await teamsRes.json();
    }

    function getAssignedMatches(refereeId) {
        return allMatches.filter(match =>
            match.referee && match.referee.includes(refereeId)
        );
    }

    function createRefereeCard(referee) {
        const card = document.createElement('div');
        card.className = 'referee-card';

        const assignedMatches = getAssignedMatches(referee.id);

        const info = document.createElement('div');
        info.className = 'referee-info';

        const name = document.createElement('div');
        name.className = 'referee-name';
        name.textContent = referee.name;

        const matches = document.createElement('div');
        matches.className = 'referee-matches';
        matches.textContent = assignedMatches.length > 0
            ? `${assignedMatches.length} partidos asignados`
            : 'Sin partidos asignados';

        if (assignedMatches.length === 0) {
            const assignButton = document.createElement('button');
            assignButton.className = 'assign-match-btn';
            assignButton.textContent = 'Asignar a Partido';
            assignButton.dataset.refereeId = referee.id;
            assignButton.addEventListener('click', () => openAssignModal(referee.id));
            info.appendChild(assignButton);
        }

        info.appendChild(name);
        info.appendChild(matches);
        card.appendChild(info);

        return card;
    }

    function loadAvailableMatches() {
        const availableMatches = allMatches.filter(match =>
            !match.referee || match.referee.length < 4
        );

        availableMatchesSelect.innerHTML = '<option value="">Seleccione un partido</option>';

        if (allMatches.length === 0 || availableMatches.length === 0) {
            const option = document.createElement('option');
            option.textContent = allMatches.length === 0
                ? "No hay partidos creados"
                : "Todos los partidos tienen 4 árbitros asignados";
            option.disabled = true;
            availableMatchesSelect.appendChild(option);
            availableMatchesSelect.disabled = true;
            assignMatchBtn.disabled = true;
            assignMatchBtn.style.opacity = "0.5";
        } else {
            availableMatchesSelect.disabled = false;
            assignMatchBtn.disabled = false;
            assignMatchBtn.style.opacity = "1";

            availableMatches.forEach(match => {
                const localTeam = allTeams.find(t => t.id === match.localTeam);
                const visitorTeam = allTeams.find(t => t.id === match.visitorTeam);
                const refereeCount = match.referee ? match.referee.length : 0;

                const option = document.createElement('option');
                option.value = match.id;
                option.textContent = `${localTeam?.name} vs ${visitorTeam?.name} - ${match.date} ${match.time} (${refereeCount}/4 árbitros)`;
                availableMatchesSelect.appendChild(option);
            });
        }
    }

    async function openAssignModal(refereeId) {
        const res = await fetch(`${API_URL}/referee`);
        const referees = await res.json();
        const referee = referees.find(r => r.id === refereeId);
        selectedRefereeId = refereeId;
        selectedRefereeNameSpan.textContent = referee.name;

        await fetchMatchesAndTeams();
        loadAvailableMatches();
        modal.style.display = 'block';
    }

    function closeModal() {
        modal.style.display = 'none';
        selectedRefereeId = null;
        availableMatchesSelect.value = '';
    }

    async function assignMatch() {
        const matchId = availableMatchesSelect.value;
        if (!matchId || !selectedRefereeId) {
            alert('Por favor seleccione un partido');
            return;
        }

        const match = allMatches.find(m => String(m.id) === String(matchId));
        if (!match) {
            alert('Partido no encontrado');
            return;
        }

        if (!match.referee) {
            match.referee = [];
        }

        if (match.referee.length >= 4) {
            alert('Este partido ya tiene el máximo de 4 árbitros asignados');
            return;
        }

        if (match.referee.includes(selectedRefereeId)) {
            alert('Este árbitro ya está asignado a este partido');
            return;
        }

        match.referee.push(selectedRefereeId);

        await fetch(`${API_URL}/matches/${matchId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(match)
        });

        alert('Árbitro asignado exitosamente');
        closeModal();
        loadReferee();
    }

    function showNoRefereeMessage() {
        refereeContent.innerHTML = `
            <div class="no-referee-message">
                <p>No hay árbitros registrados</p>
                <a href="../pages/arbitros.html" class="add-referee-link">Agregar arbitro</a>
            </div>
        `;
    }

    function showRefereeGrid(referees) {
        const grid = document.createElement('div');
        grid.className = 'referee-grid';
        
        referees.sort((a, b) => a.name.localeCompare(b.name));

        referees.forEach(referee => {
            const card = createRefereeCard(referee);
            grid.appendChild(card);
        });

        refereeContent.innerHTML = '';
        refereeContent.appendChild(grid);
    }

    async function loadReferee() {
        const res = await fetch(`${API_URL}/referee`);
        const referee = await res.json();

        await fetchMatchesAndTeams();

        if (referee.length === 0) {
            showNoRefereeMessage();
        } else {
            showRefereeGrid(referee);
            console.log(referee);
        }
    }

    // Eventos
    closeModalBtn.addEventListener('click', closeModal);
    assignMatchBtn.addEventListener('click', assignMatch);

    window.addEventListener('click', (e) => {
        if (e.target === modal) {
            closeModal();
        }
    });

    loadReferee();
});
