/**
 * crear-partidos.js - Handles match creation functionality
 */

document.addEventListener('DOMContentLoaded', () => {
    // Check authentication
    checkAuth();
    
    // Get client ID from localStorage
    const clientId = getClientId();
    
    // Modal elements
    const createMatchModal = document.getElementById('createMatchModal');
    const modalLocalTeamContainer = document.getElementById('modalLocalTeamContainer');
    const modalVisitorTeamContainer = document.getElementById('modalVisitorTeamContainer');
    const modalLocalTeamSearch = document.getElementById('modalLocalTeamSearch');
    const modalVisitorTeamSearch = document.getElementById('modalVisitorTeamSearch');
    const modalLocalTeamDropdown = document.getElementById('modalLocalTeamDropdown');
    const modalVisitorTeamDropdown = document.getElementById('modalVisitorTeamDropdown');
    const modalMatchDate = document.getElementById('modalMatchDate');
    const modalMatchTime = document.getElementById('modalMatchTime');
    const modalRefereeContainer = document.getElementById('modalRefereeContainer');
    const modalRefereeSearch = document.getElementById('modalRefereeSearch'); // This is the input field we'll use
    const saveMatchBtn = document.getElementById('saveMatchBtn');
    const cancelCreateBtn = document.getElementById('cancelCreateBtn');
    const showCreateModalBtn = document.getElementById('showCreateModalBtn');
    
    // Referee search elements
    const modalRefereeDropdown = document.getElementById('modalRefereeDropdown');
    
    // State
    let teams = [];
    let referees = [];
    let selectedLocalTeam = null;
    let selectedVisitorTeam = null;
    let selectedModalRefereeId = '';
    let selectedModalRefereeName = '';
    
    /**
     * Load teams for dropdowns
     */
    async function loadTeams() {
        try {
            const response = await fetch(`${API_URL}/teams/client/${clientId}`);
            
            if (!response.ok) {
                throw new Error(`Error ${response.status}: ${response.statusText}`);
            }
            
            teams = await response.json();
            setupTeamSearchFunctionality();
            return teams;
        } catch (error) {
            console.error('Error loading teams:', error);
            showNotification('Error cargando equipos', 'error');
            return [];
        }
    }
    
    /**
     * Load referees for dropdown
     */
    async function loadReferees() {
        try {
            const response = await fetch(`${API_URL}/referee/client/${clientId}`);
            
            if (!response.ok) {
                throw new Error(`Error ${response.status}: ${response.statusText}`);
            }
            
            referees = await response.json();
            populateRefereeDropdown();
            return referees;
        } catch (error) {
            console.error('Error loading referees:', error);
            return [];
        }
    }
    
    /**
     * Set up team search functionality
     */
    function setupTeamSearchFunctionality() {
        // Local team search functionality
        modalLocalTeamSearch.addEventListener('input', () => {
            const query = modalLocalTeamSearch.value;
            // Reset selection if input changes
            if (selectedLocalTeam && modalLocalTeamSearch.value !== selectedLocalTeam.name) {
                clearSelectedTeam('local');
            }
            filterTeamsDropdown(query, modalLocalTeamDropdown, 'local');
        });
        
        // Visitor team search functionality
        modalVisitorTeamSearch.addEventListener('input', () => {
            const query = modalVisitorTeamSearch.value;
            // Reset selection if input changes
            if (selectedVisitorTeam && modalVisitorTeamSearch.value !== selectedVisitorTeam.name) {
                clearSelectedTeam('visitor');
            }
            filterTeamsDropdown(query, modalVisitorTeamDropdown, 'visitor');
        });
        
        // Handle Enter key for local team search
        modalLocalTeamSearch.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                const firstTeam = modalLocalTeamDropdown.querySelector('li');
                if (firstTeam && modalLocalTeamDropdown.style.display !== 'none') {
                    firstTeam.click();
                    e.preventDefault();
                }
            } else if (e.key === 'Escape') {
                clearSelectedTeam('local');
                modalLocalTeamDropdown.style.display = 'none';
                e.preventDefault();
            }
        });
        
        // Handle Enter key for visitor team search
        modalVisitorTeamSearch.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                const firstTeam = modalVisitorTeamDropdown.querySelector('li');
                if (firstTeam && modalVisitorTeamDropdown.style.display !== 'none') {
                    firstTeam.click();
                    e.preventDefault();
                }
            } else if (e.key === 'Escape') {
                clearSelectedTeam('visitor');
                modalVisitorTeamDropdown.style.display = 'none';
                e.preventDefault();
            }
        });
        
        // Clear functionality on blur if input is empty or different
        modalLocalTeamSearch.addEventListener('blur', () => {
            setTimeout(() => {
                if (!modalLocalTeamSearch.value.trim()) {
                    clearSelectedTeam('local');
                } else if (selectedLocalTeam && modalLocalTeamSearch.value !== selectedLocalTeam.name) {
                    // Reset to selected name or clear if doesn't match
                    modalLocalTeamSearch.value = selectedLocalTeam ? selectedLocalTeam.name : '';
                }
                modalLocalTeamDropdown.style.display = 'none';
            }, 200);
        });
        
        modalVisitorTeamSearch.addEventListener('blur', () => {
            setTimeout(() => {
                if (!modalVisitorTeamSearch.value.trim()) {
                    clearSelectedTeam('visitor');
                } else if (selectedVisitorTeam && modalVisitorTeamSearch.value !== selectedVisitorTeam.name) {
                    // Reset to selected name or clear if doesn't match
                    modalVisitorTeamSearch.value = selectedVisitorTeam ? selectedVisitorTeam.name : '';
                }
                modalVisitorTeamDropdown.style.display = 'none';
            }, 200);
        });
        
        // Close dropdowns when clicking outside
        document.addEventListener('click', (e) => {
            if (!e.target.closest('.team-search-container')) {
                modalLocalTeamDropdown.style.display = 'none';
                modalVisitorTeamDropdown.style.display = 'none';
            }
        });
    }
    
    /**
     * Filter teams in dropdown based on search query
     */
    function filterTeamsDropdown(query, dropdown, type) {
        const list = dropdown.querySelector('ul');
        list.innerHTML = '';
        
        if (!query.trim()) {
            dropdown.style.display = 'none';
            return;
        }
        
        const filteredTeams = teams.filter(team => {
            // Filter by name
            const matchesName = team.name.toLowerCase().includes(query.toLowerCase());
            
            // Don't allow selecting the same team for both positions
            let isAvailable = true;
            if (type === 'local' && selectedVisitorTeam && team.id === selectedVisitorTeam.id) {
                isAvailable = false;
            }
            
            if (type === 'visitor' && selectedLocalTeam && team.id === selectedLocalTeam.id) {
                isAvailable = false;
            }
            
            return matchesName && isAvailable;
        });
        
        if (filteredTeams.length === 0) {
            dropdown.style.display = 'none';
            return;
        }
        
        filteredTeams.forEach(team => {
            const li = document.createElement('li');
            li.textContent = team.name;
            li.dataset.id = team.id;
            li.dataset.name = team.name;
            
            li.addEventListener('click', () => {
                if (type === 'local') {
                    modalLocalTeamSearch.value = team.name;
                    selectedLocalTeam = team;
                    updateSelectedTeamDisplay('local', team);
                } else {
                    modalVisitorTeamSearch.value = team.name;
                    selectedVisitorTeam = team;
                    updateSelectedTeamDisplay('visitor', team);
                }
                dropdown.style.display = 'none';
            });
            
            list.appendChild(li);
        });
        
        dropdown.style.display = 'block';
    }
    
    /**
     * Clear selected team
     */
    function clearSelectedTeam(type) {
        if (type === 'local') {
            selectedLocalTeam = null;
            document.getElementById('selectedLocalTeamInfo').style.display = 'none';
        } else {
            selectedVisitorTeam = null;
            document.getElementById('selectedVisitorTeamInfo').style.display = 'none';
        }
    }
    
    /**
     * Update the selected team display
     */
    function updateSelectedTeamDisplay(type, team) {
        if (type === 'local') {
            document.getElementById('selectedLocalTeamName').textContent = team.name;
            document.getElementById('selectedLocalTeamInfo').style.display = 'block';
        } else {
            document.getElementById('selectedVisitorTeamName').textContent = team.name;
            document.getElementById('selectedVisitorTeamInfo').style.display = 'block';
        }
    }
    
    /**
     * Populate referee dropdown
     */
    function populateRefereeDropdown() {
        // Clear existing options in the dropdown list
        const refereeList = modalRefereeDropdown.querySelector('ul');
        refereeList.innerHTML = '<li data-id="">Sin asignar</li>';
        
        // Add event listener to "Sin asignar" option
        const noRefereeOption = refereeList.querySelector('li[data-id=""]');
        noRefereeOption.addEventListener('click', () => {
            selectedModalRefereeId = '';
            selectedModalRefereeName = 'Sin asignar';
            modalRefereeSearch.value = 'Sin asignar';
            document.getElementById('selectedModalRefereeName').textContent = 'Sin asignar';
            document.getElementById('selectedModalRefereeInfo').style.display = 'block';
            modalRefereeDropdown.style.display = 'none';
        });
        
        // Add referees to dropdown
        referees.forEach(referee => {
            const li = document.createElement('li');
            li.textContent = `${referee.name} - ${referee.dni}`;
            li.dataset.id = referee.id;
            li.dataset.name = referee.name;
            
            li.addEventListener('click', () => {
                selectedModalRefereeId = referee.id;
                selectedModalRefereeName = referee.name;
                modalRefereeSearch.value = referee.name;
                document.getElementById('selectedModalRefereeName').textContent = referee.name;
                document.getElementById('selectedModalRefereeInfo').style.display = 'block';
                modalRefereeDropdown.style.display = 'none';
            });
            
            refereeList.appendChild(li);
        });
    }
    
    /**
     * Open modal to create a match
     */
    function openCreateModal() {
        // Reset form fields and selections
        modalLocalTeamSearch.value = '';
        modalVisitorTeamSearch.value = '';
        selectedLocalTeam = null;
        selectedVisitorTeam = null;
        document.getElementById('selectedLocalTeamInfo').style.display = 'none';
        document.getElementById('selectedVisitorTeamInfo').style.display = 'none';
        
        // Set default date & time
        const now = new Date();
        const futureDate = new Date(now);
        futureDate.setDate(now.getDate() + 7); // Default to 1 week ahead
        
        modalMatchDate.value = futureDate.toISOString().split('T')[0];
        modalMatchTime.value = '18:00'; // Default to 6:00 PM
        
        // Reset referee selection
        modalRefereeSearch.value = '';
        selectedModalRefereeId = '';
        selectedModalRefereeName = '';
        document.getElementById('selectedModalRefereeInfo').style.display = 'none';
        
        // Show modal
        createMatchModal.style.display = 'block';
    }
    
    /**
     * Close create match modal
     */
    function closeCreateModal() {
        createMatchModal.style.display = 'none';
    }
    
    /**
     * Save new match
     */
    async function saveMatch() {
        // Validaciones iniciales
        let isValid = true;
        let errorMessage = '';

        // Validar equipo local
        if (!selectedLocalTeam) {
            isValid = false;
            errorMessage = 'Debe seleccionar un equipo local';
            modalLocalTeamSearch.classList.add('is-invalid');
        } else {
            modalLocalTeamSearch.classList.remove('is-invalid');
        }
        
        // Validar equipo visitante
        if (!selectedVisitorTeam) {
            isValid = false;
            if (errorMessage) errorMessage += '\n';
            errorMessage += 'Debe seleccionar un equipo visitante';
            modalVisitorTeamSearch.classList.add('is-invalid');
        } else {
            modalVisitorTeamSearch.classList.remove('is-invalid');
        }
        
        // Validar fecha y hora
        if (!modalMatchDate.value) {
            isValid = false;
            if (errorMessage) errorMessage += '\n';
            errorMessage += 'La fecha del partido es obligatoria';
            modalMatchDate.classList.add('is-invalid');
        } else {
            modalMatchDate.classList.remove('is-invalid');
        }
        
        if (!modalMatchTime.value) {
            isValid = false;
            if (errorMessage) errorMessage += '\n';
            errorMessage += 'La hora del partido es obligatoria';
            modalMatchTime.classList.add('is-invalid');
        } else {
            modalMatchTime.classList.remove('is-invalid');
        }
        
        if (!isValid) {
            showNotification(errorMessage, 'error');
            return;
        }
        
        const localTeamId = selectedLocalTeam.id;
        const visitorTeamId = selectedVisitorTeam.id;
        const matchDate = modalMatchDate.value;
        const matchTime = modalMatchTime.value;
        const refereeId = selectedModalRefereeId || null;
        
        // Validate teams are different
        if (localTeamId === visitorTeamId) {
            showNotification('El equipo local y visitante no pueden ser el mismo', 'error');
            return;
        }
        
        // Create datetime from date and time inputs
        const dateTimeStr = `${matchDate}T${matchTime}:00`;
        
        // Create match object
        const newMatch = {
            date: dateTimeStr,
            matchgroup_id: 1, // Default match group, could be made selectable
            client_id: parseInt(clientId),
            referee_id: refereeId ? parseInt(refereeId) : null,
            local_team_id: parseInt(localTeamId),
            visitor_team_id: parseInt(visitorTeamId)
        };
        
        try {
            // Send POST request to create match
            const response = await fetch(`${API_URL}/matches/`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(newMatch),
            });
            
            if (!response.ok) {
                throw new Error(`Error ${response.status}: ${response.statusText}`);
            }
            
            // Close modal
            closeCreateModal();
            
            // Show success notification
            showNotification('Partido creado exitosamente', 'success');
            
            // Reload matches if listar-partidos.js is loaded
            if (typeof loadMatches === 'function') {
                await loadMatches();
                if (typeof applyFilters === 'function') {
                    applyFilters();
                }
            }
        } catch (error) {
            console.error('Error creating match:', error);
            showNotification('Error al crear el partido', 'error');
        }
    }
    
    // Event listeners
    showCreateModalBtn.addEventListener('click', openCreateModal);
    saveMatchBtn.addEventListener('click', saveMatch);
    cancelCreateBtn.addEventListener('click', closeCreateModal);
    
    // Close modal when clicking outside
    window.addEventListener('click', (e) => {
        if (e.target === createMatchModal) {
            closeCreateModal();
        }
    });
    
    // Initialize data when page loads
    loadTeams();
    loadReferees();
    
    // Search referee
    modalRefereeSearch.addEventListener('input', () => {
        // Reset selection if input changes
        if (selectedModalRefereeName && modalRefereeSearch.value !== selectedModalRefereeName) {
            clearSelectedModalReferee();
        }
        filterReferees(modalRefereeSearch.value, modalRefereeDropdown);
    });
    
    // Handle Enter key for referee search
    modalRefereeSearch.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            const firstReferee = modalRefereeDropdown.querySelector('li');
            if (firstReferee && modalRefereeDropdown.style.display !== 'none') {
                firstReferee.click();
                e.preventDefault();
            }
        } else if (e.key === 'Escape') {
            clearSelectedModalReferee();
            modalRefereeDropdown.style.display = 'none';
            e.preventDefault();
        }
    });
    
    // Handle referee search input blur
    modalRefereeSearch.addEventListener('blur', () => {
        setTimeout(() => {
            if (!modalRefereeSearch.value.trim()) {
                clearSelectedModalReferee();
            } else if (selectedModalRefereeId && modalRefereeSearch.value !== selectedModalRefereeName) {
                // Reset to selected name or clear if doesn't match
                modalRefereeSearch.value = selectedModalRefereeName || '';
            }
            modalRefereeDropdown.style.display = 'none';
        }, 200);
    });
    
    // Handle click to open referee dropdown
    modalRefereeSearch.addEventListener('click', () => {
        if (modalRefereeDropdown.style.display !== 'block') {
            filterReferees(modalRefereeSearch.value, modalRefereeDropdown);
            modalRefereeDropdown.style.display = 'block';
        }
    });
    
    /**
     * Filter referees in dropdown based on search query (name or DNI)
     */
    function filterReferees(query, dropdown) {
        const list = dropdown.querySelector('ul');
        
        // Mantener la opción "Sin asignar" como primera opción
        const noRefereeOption = list.querySelector('li[data-id=""]');
        list.innerHTML = '';
        
        if (noRefereeOption) {
            list.appendChild(noRefereeOption);
        } else {
            const li = document.createElement('li');
            li.textContent = 'Sin asignar';
            li.dataset.id = '';
            
            li.addEventListener('click', () => {
                selectedModalRefereeId = '';
                selectedModalRefereeName = 'Sin asignar';
                modalRefereeSearch.value = 'Sin asignar';
                document.getElementById('selectedModalRefereeName').textContent = 'Sin asignar';
                document.getElementById('selectedModalRefereeInfo').style.display = 'block';
                dropdown.style.display = 'none';
            });
            
            list.appendChild(li);
        }
        
        if (!query.trim()) {
            // Si no hay consulta, mostrar todos los árbitros
            referees.forEach(referee => {
                addRefereeToList(referee, list, dropdown);
            });
            dropdown.style.display = 'block';
            return;
        }
        
        // Filtrar árbitros por nombre o DNI
        const lowerQuery = query.toLowerCase();
        const filteredReferees = referees.filter(referee => 
            referee.name.toLowerCase().includes(lowerQuery) || 
            referee.dni.toLowerCase().includes(lowerQuery)
        );
        
        if (filteredReferees.length === 0 && query.trim() !== 'Sin asignar') {
            dropdown.style.display = 'none';
            return;
        }
        
        filteredReferees.forEach(referee => {
            addRefereeToList(referee, list, dropdown);
        });
        
        dropdown.style.display = 'block';
    }
    
    /**
     * Add referee to dropdown list
     */
    function addRefereeToList(referee, list, dropdown) {
        const li = document.createElement('li');
        li.textContent = `${referee.name} - ${referee.dni}`;
        li.dataset.id = referee.id;
        li.dataset.name = referee.name;
        
        li.addEventListener('click', () => {
            selectedModalRefereeId = referee.id;
            selectedModalRefereeName = referee.name;
            modalRefereeSearch.value = referee.name;
            document.getElementById('selectedModalRefereeName').textContent = referee.name;
            document.getElementById('selectedModalRefereeInfo').style.display = 'block';
            dropdown.style.display = 'none';
        });
        
        list.appendChild(li);
    }
    
    // Clear selected referee
    function clearSelectedModalReferee() {
        selectedModalRefereeId = '';
        selectedModalRefereeName = '';
        document.getElementById('selectedModalRefereeInfo').style.display = 'none';
    }
});