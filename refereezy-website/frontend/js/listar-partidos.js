/**
 * listar-partidos.js - Handles match listing functionality including filtering and pagination
 */

document.addEventListener('DOMContentLoaded', () => {
    // Check authentication
    checkAuth();
    
    // Get client ID from localStorage
    const clientId = getClientId();
    
    // Elements
    const matchesGrid = document.getElementById('matchesGrid');
    const noMatchesMessage = document.getElementById('noMatchesMessage');
    const teamFilter = document.getElementById('teamFilter');
    const dateFromFilter = document.getElementById('dateFromFilter');
    const dateToFilter = document.getElementById('dateToFilter');
    const refereeFilter = document.getElementById('refereeFilter');
    const sortByFilter = document.getElementById('sortByFilter');
    const applyFilterBtn = document.getElementById('applyFilterBtn');
    const clearFilterBtn = document.getElementById('clearFilterBtn');
    const showCreateModalBtn = document.getElementById('showCreateModalBtn');
    const prevPageBtn = document.getElementById('prevPageBtn');
    const nextPageBtn = document.getElementById('nextPageBtn');
    const pageIndicator = document.getElementById('pageIndicator');
    
    // Modal elements
    const assignRefereeModal = document.getElementById('assignRefereeModal');
    const matchInfoForReferee = document.getElementById('matchInfoForReferee');
    const selectReferee = document.getElementById('selectReferee');
    const cancelAssignBtn = document.getElementById('cancelAssignBtn');
    const saveAssignBtn = document.getElementById('saveAssignBtn');
    const removeRefereeBtn = document.getElementById('removeRefereeBtn');
    
    const deleteMatchModal = document.getElementById('deleteMatchModal');
    const matchInfoForDelete = document.getElementById('matchInfoForDelete');
    const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');
    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
    
    // State
    let allMatches = [];
    let filteredMatches = [];
    let teams = [];
    let referees = [];
    let currentPage = 1;
    let pageSize = 7;
    let currentMatchId = null;
    
    /**
     * Initial data loading
     */
    async function loadInitialData() {
        try {
            await Promise.all([
                loadMatches(),
                loadTeams(),
                loadReferees()
            ]);
            
            populateFilters();
            applyFilters();
        } catch (error) {
            console.error('Error loading initial data:', error);
            showNotification('Error cargando datos. Por favor, intente nuevamente.', 'error');
        }
    }
    
    /**
     * Load matches for the current client
     */
    async function loadMatches() {
        try {
            // Use the populated endpoint to get matches with team info
            const response = await fetch(`${API_URL}/matches/client/${clientId}/with_teams`);
            
            if (!response.ok) {
                throw new Error(`Error ${response.status}: ${response.statusText}`);
            }
            
            allMatches = await response.json();
            console.log('Matches loaded:', allMatches);
            
            return allMatches;
        } catch (error) {
            console.error('Error loading matches:', error);
            showNotification('Error cargando partidos', 'error');
            return [];
        }
    }
    
    /**
     * Load teams for filters
     */
    async function loadTeams() {
        try {
            const response = await fetch(`${API_URL}/teams/client/${clientId}`);
            
            if (!response.ok) {
                throw new Error(`Error ${response.status}: ${response.statusText}`);
            }
            
            teams = await response.json();
            return teams;
        } catch (error) {
            console.error('Error loading teams:', error);
            return [];
        }
    }
    
    /**
     * Load referees for assignment
     */
    async function loadReferees() {
        try {
            const response = await fetch(`${API_URL}/referee/client/${clientId}`);
            
            if (!response.ok) {
                throw new Error(`Error ${response.status}: ${response.statusText}`);
            }
            
            referees = await response.json();
            return referees;
        } catch (error) {
            console.error('Error loading referees:', error);
            return [];
        }
    }
    
    /**
     * Populate filter dropdowns
     */
    function populateFilters() {
        // Clear existing options
        teamFilter.innerHTML = '<option value="">Todos los equipos</option>';
        
        // Add teams to filter
        teams.forEach(team => {
            const option = document.createElement('option');
            option.value = team.id;
            option.textContent = team.name;
            teamFilter.appendChild(option);
        });
        
        // Set default date values for date filters
        const today = new Date();
        const oneMonthAgo = new Date(today);
        oneMonthAgo.setMonth(oneMonthAgo.getMonth() - 1);
        const oneYearAhead = new Date(today);
        oneYearAhead.setFullYear(oneYearAhead.getFullYear() + 1);
        
        // Format dates as YYYY-MM-DD for input values
        dateFromFilter.value = oneMonthAgo.toISOString().split('T')[0];
        dateToFilter.value = oneYearAhead.toISOString().split('T')[0];
    }
    
    /**
     * Apply filters to matches
     */
    function applyFilters() {
        const teamId = teamFilter.value;
        const dateFrom = dateFromFilter.value ? new Date(dateFromFilter.value) : null;
        const dateTo = dateToFilter.value ? new Date(dateToFilter.value) : null;
        const refereeStatus = refereeFilter.value;
        const sortBy = sortByFilter.value;
        
        // Apply filters
        filteredMatches = allMatches.filter(match => {
            const matchDate = new Date(match.date);
            
            // Team filter
            if (teamId && match.local_team.id != teamId && match.visitor_team.id != teamId) {
                return false;
            }
            
            // Date range filter
            if (dateFrom && matchDate < dateFrom) {
                return false;
            }
            if (dateTo && matchDate > dateTo) {
                return false;
            }
            
            // Referee filter
            if (refereeStatus === 'assigned' && !match.referee_id) {
                return false;
            }
            if (refereeStatus === 'unassigned' && match.referee_id) {
                return false;
            }
            
            return true;
        });
        
        // Sort matches
        if (sortBy === 'date-asc') {
            filteredMatches.sort((a, b) => new Date(a.date) - new Date(b.date));
        } else if (sortBy === 'date-desc') {
            filteredMatches.sort((a, b) => new Date(b.date) - new Date(a.date));
        }
        
        // Reset to page 1 after filtering
        currentPage = 1;
        renderMatches();
    }
    
    /**
     * Render matches with pagination
     */
    function renderMatches() {
        // Calculate pagination
        const totalPages = Math.max(1, Math.ceil(filteredMatches.length / pageSize));
        const startIdx = (currentPage - 1) * pageSize;
        const endIdx = Math.min(startIdx + pageSize, filteredMatches.length);
        const matchesToShow = filteredMatches.slice(startIdx, endIdx);
        
        // Update pagination controls
        pageIndicator.textContent = `Página ${currentPage} de ${totalPages}`;
        prevPageBtn.disabled = currentPage <= 1;
        nextPageBtn.disabled = currentPage >= totalPages;
        
        // Clear grid and check if there are matches to display
        matchesGrid.innerHTML = '';
        
        if (matchesToShow.length === 0) {
            noMatchesMessage.style.display = 'block';
            return;
        }
        
        // Hide no matches message when there are matches
        noMatchesMessage.style.display = 'none';
        
        // Render each match
        matchesToShow.forEach(match => {
            const matchItem = document.createElement('div');
            matchItem.className = 'match-item';
            matchItem.dataset.id = match.id;
            
            // Date info
            const matchDate = new Date(match.date);
            const formattedDate = formatDate(matchDate);
            const formattedTime = formatTime(matchDate);
            
            // Create date section
            const dateSection = document.createElement('div');
            dateSection.className = 'match-date';
            dateSection.innerHTML = `
                <div>${formattedDate} - ${formattedTime}h</div>
            `;
            
            // Create teams section
            const teamsSection = document.createElement('div');
            teamsSection.className = 'match-teams';
            
            // Local team
            const localTeamDiv = document.createElement('div');
            localTeamDiv.className = 'team-vs';
            localTeamDiv.innerHTML = `
                <img src="${match.local_team.logo_url || 'https://via.placeholder.com/32'}" alt="${match.local_team.name}" class="team-logo">
                <span class="team-name">${match.local_team.name}</span>
            `;
            
            // VS badge
            const vsDiv = document.createElement('div');
            vsDiv.className = 'team-vs';
            vsDiv.innerHTML = `<span class="vs-badge">VS</span>`;
            
            // Visitor team
            const visitorTeamDiv = document.createElement('div');
            visitorTeamDiv.className = 'team-vs';
            visitorTeamDiv.innerHTML = `
                <img src="${match.visitor_team.logo_url || 'https://via.placeholder.com/32'}" alt="${match.visitor_team.name}" class="team-logo">
                <span class="team-name">${match.visitor_team.name}</span>
            `;
            
            teamsSection.appendChild(localTeamDiv);
            teamsSection.appendChild(vsDiv);
            teamsSection.appendChild(visitorTeamDiv);
            
            // Create referee section
            const refereeSection = document.createElement('div');
            refereeSection.className = 'match-referee';
            
            // Find referee if assigned
            const refereeInfo = match.referee 
                ? `<span class="referee-badge">${match.referee.name}</span>`
                : `<span class="referee-badge no-referee">Sin árbitro</span>`;
            refereeSection.innerHTML = refereeInfo;
            
            // Create actions section
            const actionsSection = document.createElement('div');
            actionsSection.className = 'match-actions';
            
            // Assign referee button
            const assignBtn = document.createElement('button');
            assignBtn.className = 'action-btn action-assign';
            assignBtn.title = 'Asignar árbitro';
            assignBtn.innerHTML = `
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
            `;
            assignBtn.addEventListener('click', () => openAssignRefereeModal(match));
            
            // Delete button
            const deleteBtn = document.createElement('button');
            deleteBtn.className = 'action-btn action-delete';
            deleteBtn.title = 'Eliminar partido';
            deleteBtn.innerHTML = `
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
            `;
            deleteBtn.addEventListener('click', () => openDeleteMatchModal(match));
            
            actionsSection.appendChild(assignBtn);
            actionsSection.appendChild(deleteBtn);
            
            // Add all sections to match item
            matchItem.appendChild(dateSection);
            matchItem.appendChild(teamsSection);
            matchItem.appendChild(refereeSection);
            matchItem.appendChild(actionsSection);
            
            // Add match item to grid
            matchesGrid.appendChild(matchItem);
        });
    }
    
    /**
     * Open referee assignment modal
     */
    function openAssignRefereeModal(match) {
        currentMatchId = match.id;
        
        // Populate match info in modal
        const matchDate = new Date(match.date);
        matchInfoForReferee.innerHTML = `
            <div class="match-info-item">
                <span class="match-info-label">Fecha:</span>
                <span>${formatDate(matchDate)} ${formatTime(matchDate)}</span>
            </div>
            <div class="match-info-item">
                <span class="match-info-label">Local:</span>
                <span>${match.local_team.name}</span>
            </div>
            <div class="match-info-item">
                <span class="match-info-label">Visitante:</span>
                <span>${match.visitor_team.name}</span>
            </div>
        `;
        
        // Populate referees dropdown
        selectReferee.innerHTML = '<option value="">Sin asignar</option>';
        referees.forEach(referee => {
            const option = document.createElement('option');
            option.value = referee.id;
            option.textContent = referee.name;
            if (match.referee && referee.id === match.referee.id) {
                option.selected = true;
            }
            selectReferee.appendChild(option);
        });
        
        // Show or hide remove button based on if there's a referee
        removeRefereeBtn.style.display = match.referee ? 'block' : 'none';
        
        // Show modal
        assignRefereeModal.style.display = 'block';
    }
    
    /**
     * Open delete match confirmation modal
     */
    function openDeleteMatchModal(match) {
        currentMatchId = match.id;
        
        // Populate match info in modal
        const matchDate = new Date(match.date);
        matchInfoForDelete.innerHTML = `
            <div class="match-info-item">
                <span class="match-info-label">Fecha:</span>
                <span>${formatDate(matchDate)} ${formatTime(matchDate)}</span>
            </div>
            <div class="match-info-item">
                <span class="match-info-label">Equipos:</span>
                <span>${match.local_team.name} vs ${match.visitor_team.name}</span>
            </div>
        `;
        
        // Show modal
        deleteMatchModal.style.display = 'block';
    }
    
    /**
     * Save referee assignment
     */
    async function saveRefereeAssignment() {
        const refereeId = selectReferee.value || null;
        
        try {
            // Get current match data
            const response = await fetch(`${API_URL}/matches/${currentMatchId}`);
            if (!response.ok) {
                throw new Error(`Error ${response.status}: ${response.statusText}`);
            }
            
            const match = await response.json();
            
            // Update referee_id
            match.referee_id = refereeId ? parseInt(refereeId) : null;
            
            // Save changes
            const updateResponse = await fetch(`${API_URL}/matches/${currentMatchId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(match),
            });
            
            if (!updateResponse.ok) {
                throw new Error(`Error ${updateResponse.status}: ${updateResponse.statusText}`);
            }
            
            // Close modal and reload matches
            closeModal(assignRefereeModal);
            await loadMatches();
            applyFilters();
            
            showNotification('Árbitro asignado correctamente', 'success');
        } catch (error) {
            console.error('Error assigning referee:', error);
            showNotification('Error al asignar árbitro', 'error');
        }
    }
    
    /**
     * Delete a match
     */
    async function deleteMatch() {
        try {
            const response = await fetch(`${API_URL}/matches/${currentMatchId}`, {
                method: 'DELETE',
            });
            
            if (!response.ok) {
                throw new Error(`Error ${response.status}: ${response.statusText}`);
            }
            
            // Close modal, reload matches and update view
            closeModal(deleteMatchModal);
            await loadMatches();
            applyFilters();
            
            showNotification('Partido eliminado correctamente', 'success');
        } catch (error) {
            console.error('Error deleting match:', error);
            showNotification('Error al eliminar partido', 'error');
        }
    }
    
    /**
     * Close any modal
     */
    function closeModal(modal) {
        modal.style.display = 'none';
        currentMatchId = null;
    }
    
    // Event Listeners
    applyFilterBtn.addEventListener('click', applyFilters);
    
    clearFilterBtn.addEventListener('click', () => {
        teamFilter.value = '';
        
        // Reset dates to default range
        const today = new Date();
        const oneMonthAgo = new Date(today);
        oneMonthAgo.setMonth(oneMonthAgo.getMonth() - 1);
        const oneYearAhead = new Date(today);
        oneYearAhead.setFullYear(oneYearAhead.getFullYear() + 1);
        
        dateFromFilter.value = oneMonthAgo.toISOString().split('T')[0];
        dateToFilter.value = oneYearAhead.toISOString().split('T')[0];
        
        refereeFilter.value = '';
        sortByFilter.value = 'date-asc';
        
        applyFilters();
    });
    
    prevPageBtn.addEventListener('click', () => {
        if (currentPage > 1) {
            currentPage--;
            renderMatches();
        }
    });
    
    nextPageBtn.addEventListener('click', () => {
        const totalPages = Math.ceil(filteredMatches.length / pageSize);
        if (currentPage < totalPages) {
            currentPage++;
            renderMatches();
        }
    });
    
    // Modal events
    cancelAssignBtn.addEventListener('click', () => closeModal(assignRefereeModal));
    saveAssignBtn.addEventListener('click', saveRefereeAssignment);
    removeRefereeBtn.addEventListener('click', () => {
        selectReferee.value = '';
        saveRefereeAssignment();
    });
    
    cancelDeleteBtn.addEventListener('click', () => closeModal(deleteMatchModal));
    confirmDeleteBtn.addEventListener('click', deleteMatch);
    
    // Close modals when clicking outside
    window.addEventListener('click', (e) => {
        if (e.target === assignRefereeModal) {
            closeModal(assignRefereeModal);
        }
        if (e.target === deleteMatchModal) {
            closeModal(deleteMatchModal);
        }
    });
    
    // Initialize data when page loads
    loadInitialData();
});