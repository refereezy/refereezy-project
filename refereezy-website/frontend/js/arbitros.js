document.addEventListener('DOMContentLoaded', () => {
    
    const API_URL = "http://refereezy.smcardona.tech:8080";
    
    // Referencias a elementos del DOM
    const refereeNameInput = document.querySelector('.referee-name-input');
    const matchSelect = document.getElementById('matchSelect');
    const addRefereeBtn = document.querySelector('.add-referee-btn');

    // Función para cargar partidos disponibles
    async function loadAvailableMatches() {
        let res = await fetch(API_URL+'/matches');
        const matches = await res.json();

        res = await fetch(API_URL+'/teams');
        const teams = await res.json();
        
        // Limpiar select
        matchSelect.innerHTML = '<option value="">Seleccione un partido</option>';
        
        if (matches.length === 0) {
            const option = document.createElement('option');
            option.value = "";
            option.textContent = "No hay partidos creados";
            option.disabled = true;
            matchSelect.appendChild(option);
            matchSelect.disabled = true;
        } else {
            matchSelect.disabled = false;
            
            // Filtrar y mostrar solo partidos con menos de 4 árbitros
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
                    }
                }
            });

            // Si todos los partidos tienen 4 árbitros
            if (matchSelect.options.length === 1) {
                const option = document.createElement('option');
                option.value = "";
                option.textContent = "Todos los partidos tienen 4 árbitros asignados";
                option.disabled = true;
                matchSelect.appendChild(option);
                matchSelect.disabled = true;
            }
        }
    }

    // Manejador para agregar árbitro
    addRefereeBtn.addEventListener('click', () => {
        const refereeName = refereeNameInput.value.trim();
        const matchId = matchSelect.value;

        if (!refereeName) {
            alert('Por favor ingrese el nombre del árbitro');
            return;
        }

        // Obtener árbitros existentes
        const referees = JSON.parse(localStorage.getItem('referees')) || [];
        
        // Verificar si el árbitro ya existe
        const refereeExists = referees.some(referee => 
            referee.name.toLowerCase() === refereeName.toLowerCase()
        );
        
        if (refereeExists) {
            alert('Este árbitro ya está registrado');
            return;
        }

        // Crear nuevo árbitro
        const newReferee = {
            id: Date.now(),
            name: refereeName
        };
        
        // Agregar árbitro
        referees.push(newReferee);
        localStorage.setItem('referees', JSON.stringify(referees));

        // Si se seleccionó un partido, actualizarlo
        if (matchId) {
            const matches = JSON.parse(localStorage.getItem('matches')) || [];
            const matchIndex = matches.findIndex(m => String(m.id) === String(matchId));
            
            if (matchIndex !== -1) {
                if (!matches[matchIndex].referees) {
                    matches[matchIndex].referees = [];
                }
                
                // Verificar límite de árbitros
                if (matches[matchIndex].referees.length >= 4) {
                    alert('Este partido ya tiene el máximo de 4 árbitros asignados');
                } else {
                    matches[matchIndex].referees.push(newReferee.id);
                    localStorage.setItem('matches', JSON.stringify(matches));
                }
            }
        }
        
        // Limpiar formulario
        refereeNameInput.value = '';
        matchSelect.value = '';
        
        // Recargar partidos disponibles
        loadAvailableMatches();
        
        alert('Árbitro agregado exitosamente!');
    });

    // Cargar partidos disponibles al iniciar
    loadAvailableMatches();

    // Actualizar cuando cambie el localStorage
    window.addEventListener('storage', (e) => {
        if (e.key === 'matches' || e.key === 'teams') {
            loadAvailableMatches();
        }
    });
});