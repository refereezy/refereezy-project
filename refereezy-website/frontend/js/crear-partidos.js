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
    const modalLocalTeam = document.getElementById('modalLocalTeam');
    const modalVisitorTeam = document.getElementById('modalVisitorTeam');
    const modalMatchDate = document.getElementById('modalMatchDate');
    const modalMatchTime = document.getElementById('modalMatchTime');
    const modalReferee = document.getElementById('modalReferee');
    const saveMatchBtn = document.getElementById('saveMatchBtn');
    const cancelCreateBtn = document.getElementById('cancelCreateBtn');
    const showCreateModalBtn = document.getElementById('showCreateModalBtn');
    
    // State
    let teams = [];
    let referees = [];
    
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
            populateTeamDropdowns();
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
     * Populate team dropdowns
     */
    function populateTeamDropdowns() {
        // Clear existing options
        modalLocalTeam.innerHTML = '<option value="">Seleccione equipo local</option>';
        modalVisitorTeam.innerHTML = '<option value="">Seleccione equipo visitante</option>';
        
        // Add teams to dropdowns
        teams.forEach(team => {
            const localOption = document.createElement('option');
            localOption.value = team.id;
            localOption.textContent = team.name;
            modalLocalTeam.appendChild(localOption);
            
            const visitorOption = document.createElement('option');
            visitorOption.value = team.id;
            visitorOption.textContent = team.name;
            modalVisitorTeam.appendChild(visitorOption);
        });
    }
    
    /**
     * Populate referee dropdown
     */
    function populateRefereeDropdown() {
        // Clear existing options
        modalReferee.innerHTML = '<option value="">Sin asignar</option>';
        
        // Add referees to dropdown
        referees.forEach(referee => {
            const option = document.createElement('option');
            option.value = referee.id;
            option.textContent = referee.name;
            modalReferee.appendChild(option);
        });
    }
    
    /**
     * Open modal to create a match
     */
    function openCreateModal() {
        // Reset form fields
        modalLocalTeam.value = '';
        modalVisitorTeam.value = '';
        
        // Set default date & time
        const now = new Date();
        const futureDate = new Date(now);
        futureDate.setDate(now.getDate() + 7); // Default to 1 week ahead
        
        modalMatchDate.value = futureDate.toISOString().split('T')[0];
        modalMatchTime.value = '18:00'; // Default to 6:00 PM
        
        modalReferee.value = '';
        
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
        // Get form values
        const localTeamId = modalLocalTeam.value;
        const visitorTeamId = modalVisitorTeam.value;
        const matchDate = modalMatchDate.value;
        const matchTime = modalMatchTime.value;
        const refereeId = modalReferee.value || null;
        
        // Validate required fields
        if (!localTeamId || !visitorTeamId || !matchDate || !matchTime) {
            showNotification('Por favor complete todos los campos requeridos', 'error');
            return;
        }
        
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
                applyFilters();
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
    
    // Validation to prevent same team selection
    modalLocalTeam.addEventListener('change', () => {
        const localId = modalLocalTeam.value;
        
        // Enable all options in visitor dropdown
        Array.from(modalVisitorTeam.options).forEach(option => {
            option.disabled = false;
        });
        
        // Disable the selected local team in visitor dropdown
        if (localId) {
            const visitorOption = modalVisitorTeam.querySelector(`option[value="${localId}"]`);
            if (visitorOption) {
                visitorOption.disabled = true;
            }
            
            // If visitor team is same as local, reset visitor
            if (modalVisitorTeam.value === localId) {
                modalVisitorTeam.value = '';
            }
        }
    });
    
    modalVisitorTeam.addEventListener('change', () => {
        const visitorId = modalVisitorTeam.value;
        
        // Enable all options in local dropdown
        Array.from(modalLocalTeam.options).forEach(option => {
            option.disabled = false;
        });
        
        // Disable the selected visitor team in local dropdown
        if (visitorId) {
            const localOption = modalLocalTeam.querySelector(`option[value="${visitorId}"]`);
            if (localOption) {
                localOption.disabled = true;
            }
            
            // If local team is same as visitor, reset local
            if (modalLocalTeam.value === visitorId) {
                modalLocalTeam.value = '';
            }
        }
    });
    
    // Initialize data when page loads
    loadTeams();
    loadReferees();
});