document.addEventListener('DOMContentLoaded', () => {
    const refereesContent = document.querySelector('.referees-content');
    const modal = document.getElementById('assignMatchModal');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const assignMatchBtn = document.getElementById('assignMatchBtn');
    const availableMatchesSelect = document.getElementById('availableMatches');
    const selectedRefereeNameSpan = document.getElementById('selectedRefereeName');
    let selectedRefereeId = null;
    
    // Función para obtener los partidos asignados a un árbitro
    function getAssignedMatches(refereeId) {
        const matches = JSON.parse(localStorage.getItem('matches')) || [];
        return matches.filter(match => 
            match.referees && match.referees.includes(Number(refereeId))
        );
    }
    
    // Función para crear una tarjeta de árbitro
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
            
        // Agregar botón de asignar si no tiene partidos
        if (assignedMatches.length === 0) {
            const assignButton = document.createElement('button');
            assignButton.className = 'assign-match-btn';
            assignButton.textContent = 'Asignar a Partido';
            assignButton.dataset.refereeId = referee.id;
            assignButton.addEventListener('click', () => openAssignModal(Number(referee.id)));
            info.appendChild(assignButton);
        }
        
        info.appendChild(name);
        info.appendChild(matches);
        card.appendChild(info);
        
        return card;
    }
    
    // Función para cargar partidos disponibles
    function loadAvailableMatches() {
        const matches = JSON.parse(localStorage.getItem('matches')) || [];
        const teams = JSON.parse(localStorage.getItem('teams')) || [];
        
        // Filtrar partidos con menos de 4 árbitros
        const availableMatches = matches.filter(match => 
            !match.referees || match.referees.length < 4
        );

        // Limpiar el select
        availableMatchesSelect.innerHTML = '<option value="">Seleccione un partido</option>';
        
        if (matches.length === 0) {
            // Si no hay partidos, mostrar mensaje en el select
            const option = document.createElement('option');
            option.value = "";
            option.textContent = "No hay partidos creados";
            option.disabled = true;
            availableMatchesSelect.appendChild(option);
            availableMatchesSelect.disabled = true;
            assignMatchBtn.disabled = true;
            assignMatchBtn.style.opacity = "0.5";
        } else if (availableMatches.length === 0) {
            // Si hay partidos pero todos tienen 4 árbitros
            const option = document.createElement('option');
            option.value = "";
            option.textContent = "Todos los partidos tienen 4 árbitros asignados";
            option.disabled = true;
            availableMatchesSelect.appendChild(option);
            availableMatchesSelect.disabled = true;
            assignMatchBtn.disabled = true;
            assignMatchBtn.style.opacity = "0.5";
        } else {
            // Si hay partidos disponibles
            availableMatchesSelect.disabled = false;
            assignMatchBtn.disabled = false;
            assignMatchBtn.style.opacity = "1";
            
            availableMatches.forEach(match => {
                const localTeam = teams.find(team => Number(team.id) === Number(match.localTeam));
                const visitorTeam = teams.find(team => Number(team.id) === Number(match.visitorTeam));
                const refereesCount = match.referees ? match.referees.length : 0;
                
                const option = document.createElement('option');
                option.value = match.id;
                option.textContent = `${localTeam.name} vs ${visitorTeam.name} - ${match.date} ${match.time} (${refereesCount}/4 árbitros)`;
                availableMatchesSelect.appendChild(option);
            });
        }
    }
    
    // Función para abrir el modal
    function openAssignModal(refereeId) {
        const referees = JSON.parse(localStorage.getItem('referees')) || [];
        const referee = referees.find(r => Number(r.id) === Number(refereeId));
        selectedRefereeId = Number(refereeId);
        selectedRefereeNameSpan.textContent = referee.name;
        loadAvailableMatches();
        modal.style.display = 'block';
    }
    
    // Función para cerrar el modal
    function closeModal() {
        modal.style.display = 'none';
        selectedRefereeId = null;
        availableMatchesSelect.value = '';
    }
    
    // Función para asignar partido
    function assignMatch() {
        const matchId = availableMatchesSelect.value;
        if (!matchId || !selectedRefereeId) {
            alert('Por favor seleccione un partido');
            return;
        }

        const matches = JSON.parse(localStorage.getItem('matches')) || [];
        const matchIndex = matches.findIndex(m => String(m.id) === String(matchId));

        if (matchIndex === -1) {
            alert('Partido no encontrado');
            return;
        }

        // Inicializar el array de árbitros si no existe
        if (!matches[matchIndex].referees) {
            matches[matchIndex].referees = [];
        }

        // Verificar límite de árbitros
        if (matches[matchIndex].referees.length >= 4) {
            alert('Este partido ya tiene el máximo de 4 árbitros asignados');
            return;
        }

        // Verificar si el árbitro ya está asignado
        if (matches[matchIndex].referees.includes(selectedRefereeId)) {
            alert('Este árbitro ya está asignado a este partido');
            return;
        }

        // Asignar el árbitro
        matches[matchIndex].referees.push(selectedRefereeId);
        localStorage.setItem('matches', JSON.stringify(matches));

        alert('Árbitro asignado exitosamente');
        closeModal();
        loadReferees();
    }
    
    // Event Listeners
    closeModalBtn.addEventListener('click', closeModal);
    assignMatchBtn.addEventListener('click', assignMatch);
    
    // Cerrar modal al hacer clic fuera
    window.addEventListener('click', (event) => {
        if (event.target === modal) {
            closeModal();
        }
    });
    
    // Función para mostrar mensaje cuando no hay árbitros
    function showNoRefereesMessage() {
        refereesContent.innerHTML = `
            <div class="no-referees-message">
                <p>No hay árbitros registrados</p>
                <a href="arbitros.html" class="add-referee-link">Agregar arbitro</a>
            </div>
        `;
    }
    
    // Función para mostrar la cuadrícula de árbitros
    function showRefereesGrid(referees) {
        const grid = document.createElement('div');
        grid.className = 'referees-grid';
        
        // Ordenar árbitros por nombre
        referees.sort((a, b) => a.name.localeCompare(b.name));
        
        referees.forEach(referee => {
            const card = createRefereeCard(referee);
            grid.appendChild(card);
        });
        
        refereesContent.innerHTML = '';
        refereesContent.appendChild(grid);
    }
    
    // Función para cargar y mostrar los árbitros
    function loadReferees() {
        const referees = JSON.parse(localStorage.getItem('referees')) || [];
        
        if (referees.length === 0) {
            showNoRefereesMessage();
        } else {
            showRefereesGrid(referees);
        }
    }

    // Inicialización
    loadReferees();
    
    // Actualizar cuando cambie el localStorage
    window.addEventListener('storage', (e) => {
        if (e.key === 'referees' || e.key === 'matches') {
            loadReferees();
        }
    });
});