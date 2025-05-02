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
    const dateFromFilter = document.getElementById('dateFromFilter');
    const dateToFilter = document.getElementById('dateToFilter');
    const sortByFilter = document.getElementById('sortByFilter');
    const applyFilterBtn = document.getElementById('applyFilterBtn');
    const clearFilterBtn = document.getElementById('clearFilterBtn');
    const showCreateModalBtn = document.getElementById('showCreateModalBtn');
    const prevPageBtn = document.getElementById('prevPageBtn');
    const nextPageBtn = document.getElementById('nextPageBtn');
    const pageIndicator = document.getElementById('pageIndicator');
    
    // Team search inputs
    const teamSearch1Input = document.getElementById('teamSearch1');
    const teamSearch2Input = document.getElementById('teamSearch2');
    const teamDropdown1 = document.getElementById('teamDropdown1');
    const teamDropdown2 = document.getElementById('teamDropdown2');

    // Referee search input
    const refereeSearchInput = document.getElementById('refereeSearch');
    const refereeDropdown = document.getElementById('refereeDropdown');
    
    // Modal elements
    const assignRefereeModal = document.getElementById('assignRefereeModal');
    const matchInfoForReferee = document.getElementById('matchInfoForReferee');
    const assignRefereeSearch = document.getElementById('assignRefereeSearch');
    const assignRefereeDropdown = document.getElementById('assignRefereeDropdown');
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
    let selectedTeam1Id = '';
    let selectedTeam2Id = '';
    let selectedTeam1Name = '';
    let selectedTeam2Name = '';
    let selectedRefereeId = '';
    let selectedRefereeName = '';
    let selectedAssignRefereeId = '';
    let selectedAssignRefereeName = '';
    let refereeFilterType = ''; // 'assigned', 'unassigned', 'specific' o ''
    
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
            setupTeamSearch();
            setupRefereeSearch();
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
     * Set up team search functionality
     */
    function setupTeamSearch() {
        // Search for teams in dropdown 1
        teamSearch1Input.addEventListener('input', () => {
            // Reset selection if input changes
            if (selectedTeam1Name && teamSearch1Input.value !== selectedTeam1Name) {
                clearSelectedTeam(1);
            }
            filterTeams(teamSearch1Input.value, teamDropdown1, 1);
        });
        
        // Search for teams in dropdown 2
        teamSearch2Input.addEventListener('input', () => {
            // Reset selection if input changes
            if (selectedTeam2Name && teamSearch2Input.value !== selectedTeam2Name) {
                clearSelectedTeam(2);
            }
            filterTeams(teamSearch2Input.value, teamDropdown2, 2);
        });
        
        // Handle Enter key for first dropdown
        teamSearch1Input.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                const firstTeam = teamDropdown1.querySelector('li');
                if (firstTeam && teamDropdown1.style.display !== 'none') {
                    firstTeam.click();
                    e.preventDefault();
                }
            } else if (e.key === 'Escape') {
                clearSelectedTeam(1);
                teamDropdown1.style.display = 'none';
                e.preventDefault();
            }
        });
        
        // Handle Enter key for second dropdown
        teamSearch2Input.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                const firstTeam = teamDropdown2.querySelector('li');
                if (firstTeam && teamDropdown2.style.display !== 'none') {
                    firstTeam.click();
                    e.preventDefault();
                }
            } else if (e.key === 'Escape') {
                clearSelectedTeam(2);
                teamDropdown2.style.display = 'none';
                e.preventDefault();
            }
        });
        
        // Clear functionality on blur if input is empty
        teamSearch1Input.addEventListener('blur', () => {
            setTimeout(() => {
                if (!teamSearch1Input.value.trim()) {
                    clearSelectedTeam(1);
                } else if (selectedTeam1Id && teamSearch1Input.value !== selectedTeam1Name) {
                    // Reset to selected name or clear if doesn't match
                    teamSearch1Input.value = selectedTeam1Name || '';
                }
                teamDropdown1.style.display = 'none';
            }, 200);
        });
        
        teamSearch2Input.addEventListener('blur', () => {
            setTimeout(() => {
                if (!teamSearch2Input.value.trim()) {
                    clearSelectedTeam(2);
                } else if (selectedTeam2Id && teamSearch2Input.value !== selectedTeam2Name) {
                    // Reset to selected name or clear if doesn't match
                    teamSearch2Input.value = selectedTeam2Name || '';
                }
                teamDropdown2.style.display = 'none';
            }, 200);
        });
        
        // Close dropdowns when clicking outside
        document.addEventListener('click', (e) => {
            if (!e.target.closest('.team-search-container')) {
                teamDropdown1.style.display = 'none';
                teamDropdown2.style.display = 'none';
                refereeDropdown.style.display = 'none';
                assignRefereeDropdown.style.display = 'none';
            }
        });
    }
    
    /**
     * Set up referee search and filter functionality
     */
    function setupRefereeSearch() {
        // Initial setup of referee dropdown options
        populateRefereeFilterDropdown();
        
        // Setup referee search
        refereeSearchInput.addEventListener('input', () => {
            // Reset selection if input changes
            if (selectedRefereeName && refereeSearchInput.value !== selectedRefereeName) {
                clearSelectedReferee();
            }
            filterReferees(refereeSearchInput.value);
        });
        
        // Handle Enter key for referee dropdown
        refereeSearchInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                const firstReferee = refereeDropdown.querySelector('li:not(.filter-option):not(.filter-separator)');
                if (firstReferee && refereeDropdown.style.display !== 'none') {
                    firstReferee.click();
                    e.preventDefault();
                }
            } else if (e.key === 'Escape') {
                clearSelectedReferee();
                refereeDropdown.style.display = 'none';
                e.preventDefault();
            }
        });
        
        // Clear functionality on blur if input is empty or doesn't match
        refereeSearchInput.addEventListener('blur', () => {
            setTimeout(() => {
                if (!refereeSearchInput.value.trim()) {
                    clearSelectedReferee();
                } else if (selectedRefereeId && selectedRefereeName && 
                           refereeSearchInput.value !== selectedRefereeName) {
                    // Reset to selected name
                    refereeSearchInput.value = selectedRefereeName || '';
                }
                refereeDropdown.style.display = 'none';
            }, 200);
        });
        
        // Setup assign referee search for modal
        assignRefereeSearch.addEventListener('input', () => {
            // Filtra los árbitros en el modal de asignación
            if (selectedAssignRefereeName && assignRefereeSearch.value !== selectedAssignRefereeName) {
                clearAssignSelectedReferee();
            }
            filterAssignReferees(assignRefereeSearch.value);
        });
        
        // Handle Enter key for assign referee dropdown
        assignRefereeSearch.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                const firstReferee = assignRefereeDropdown.querySelector('li');
                if (firstReferee && assignRefereeDropdown.style.display !== 'none') {
                    firstReferee.click();
                    e.preventDefault();
                }
            } else if (e.key === 'Escape') {
                clearAssignSelectedReferee();
                assignRefereeDropdown.style.display = 'none';
                e.preventDefault();
            }
        });
        
        // Clear functionality on blur for assign referee search
        assignRefereeSearch.addEventListener('blur', () => {
            setTimeout(() => {
                if (!assignRefereeSearch.value.trim()) {
                    clearAssignSelectedReferee();
                } else if (selectedAssignRefereeId && selectedAssignRefereeName && 
                           assignRefereeSearch.value !== selectedAssignRefereeName) {
                    // Reset to selected name
                    assignRefereeSearch.value = selectedAssignRefereeName || '';
                }
                assignRefereeDropdown.style.display = 'none';
            }, 200);
        });
    }
    
    /**
     * Populate referee filter dropdown with options and referees
     */
    function populateRefereeFilterDropdown() {
        const refereeList = refereeDropdown.querySelector('ul');
        
        // Mantener las opciones fijas (all, assigned, unassigned)
        const fixedOptions = Array.from(refereeList.querySelectorAll('.filter-option, .filter-separator'));
        
        // Limpiar la lista pero preservar las opciones fijas
        refereeList.innerHTML = '';
        
        // Añadir de nuevo las opciones fijas
        fixedOptions.forEach(option => {
            refereeList.appendChild(option);
        });
        
        // Añadir eventos a las opciones fijas
        refereeList.querySelectorAll('.filter-option').forEach(option => {
            option.addEventListener('click', () => {
                const value = option.dataset.value;
                refereeFilterType = value;
                
                // Actualizar el texto del input y el estado de selección
                if (value === 'assigned') {
                    refereeSearchInput.value = 'Con árbitro';
                    selectedRefereeId = '';
                    selectedRefereeName = 'Con árbitro';
                } else if (value === 'unassigned') {
                    refereeSearchInput.value = 'Sin árbitro';
                    selectedRefereeId = '';
                    selectedRefereeName = 'Sin árbitro';
                } else {
                    clearSelectedReferee();
                }
                
                document.getElementById('selectedRefereeInfo').style.display = value !== 'all' ? 'block' : 'none';
                if (value !== 'all') {
                    document.getElementById('selectedReferee').textContent = 
                        value === 'assigned' ? 'Con árbitro' : 
                        value === 'unassigned' ? 'Sin árbitro' : '';
                }
                
                refereeDropdown.style.display = 'none';
            });
        });
        
        // Añadir los árbitros a la lista
        referees.forEach(referee => {
            const li = document.createElement('li');
            li.textContent = `${referee.name} - ${referee.dni}`;
            li.dataset.id = referee.id;
            li.dataset.name = referee.name;
            li.dataset.dni = referee.dni;
            
            li.addEventListener('click', () => {
                selectedRefereeId = referee.id;
                selectedRefereeName = referee.name;
                refereeFilterType = 'specific';
                refereeSearchInput.value = referee.name;
                document.getElementById('selectedReferee').textContent = referee.name;
                document.getElementById('selectedRefereeInfo').style.display = 'block';
                refereeDropdown.style.display = 'none';
            });
            
            refereeList.appendChild(li);
        });
    }
    
    /**
     * Populate assign referee dropdown in the assignment modal
     */
    function populateAssignRefereeDropdown(matchRefereeId = null) {
        const refereeList = assignRefereeDropdown.querySelector('ul');
        refereeList.innerHTML = '<li data-id="">Sin asignar</li>'; // Opción para quitar árbitro
        
        // Evento para la opción "Sin asignar"
        const noRefereeOption = refereeList.querySelector('li[data-id=""]');
        noRefereeOption.addEventListener('click', () => {
            selectedAssignRefereeId = '';
            selectedAssignRefereeName = 'Sin asignar';
            assignRefereeSearch.value = 'Sin asignar';
            document.getElementById('selectedAssignRefereeName').textContent = 'Sin asignar';
            document.getElementById('selectedAssignRefereeInfo').style.display = 'block';
            assignRefereeDropdown.style.display = 'none';
        });
        
        // Añadir los árbitros a la lista
        referees.forEach(referee => {
            const li = document.createElement('li');
            li.textContent = `${referee.name} - ${referee.dni}`;
            li.dataset.id = referee.id;
            li.dataset.name = referee.name;
            
            li.addEventListener('click', () => {
                selectedAssignRefereeId = referee.id;
                selectedAssignRefereeName = referee.name;
                assignRefereeSearch.value = referee.name;
                document.getElementById('selectedAssignRefereeName').textContent = referee.name;
                document.getElementById('selectedAssignRefereeInfo').style.display = 'block';
                assignRefereeDropdown.style.display = 'none';
            });
            
            refereeList.appendChild(li);
        });
        
        // Seleccionar el árbitro actual si existe
        if (matchRefereeId) {
            const matchReferee = referees.find(r => r.id == matchRefereeId);
            if (matchReferee) {
                selectedAssignRefereeId = matchReferee.id;
                selectedAssignRefereeName = matchReferee.name;
                assignRefereeSearch.value = matchReferee.name;
                document.getElementById('selectedAssignRefereeName').textContent = matchReferee.name;
                document.getElementById('selectedAssignRefereeInfo').style.display = 'block';
            }
        } else {
            // Si no hay árbitro, seleccionar "Sin asignar"
            clearAssignSelectedReferee();
            assignRefereeSearch.value = '';
        }
    }
    
    /**
     * Filter referees in the dropdown based on search query
     */
    function filterReferees(query) {
        // Mostrar tanto las opciones fijas como los árbitros que coincidan
        const items = refereeDropdown.querySelectorAll('li');
        refereeDropdown.style.display = 'block';
        
        // Siempre mostrar las opciones fijas al principio (Con árbitro, Sin árbitro)
        refereeDropdown.querySelectorAll('.filter-option, .filter-separator').forEach(item => {
            item.style.display = '';
        });
        
        if (!query) {
            // Si no hay consulta, mostrar todas las opciones
            items.forEach(item => {
                if (!item.classList.contains('filter-option') && !item.classList.contains('filter-separator')) {
                    item.style.display = '';
                }
            });
            return;
        }
        
        // Filtrar árbitros por nombre o DNI
        const lowerQuery = query.toLowerCase();
        let foundMatch = false;
        
        items.forEach(item => {
            if (item.classList.contains('filter-option') || item.classList.contains('filter-separator')) {
                return; // Saltar opciones fijas ya que siempre se muestran
            }
            
            const name = item.dataset.name?.toLowerCase() || '';
            const dni = item.dataset.dni?.toLowerCase() || '';
            
            if (name.includes(lowerQuery) || dni.includes(lowerQuery)) {
                item.style.display = '';
                foundMatch = true;
            } else {
                item.style.display = 'none';
            }
        });
    }
    
    /**
     * Filter referees in the assign dropdown based on search query
     */
    function filterAssignReferees(query) {
        const items = assignRefereeDropdown.querySelectorAll('li');
        assignRefereeDropdown.style.display = 'block';
        
        // Siempre mostrar la opción "Sin asignar"
        const noRefereeOption = assignRefereeDropdown.querySelector('li[data-id=""]');
        if (noRefereeOption) {
            noRefereeOption.style.display = '';
        }
        
        if (!query) {
            // Si no hay consulta, mostrar todos los árbitros
            items.forEach(item => {
                if (!item.dataset.id || item.dataset.id !== "") {
                    item.style.display = '';
                }
            });
            return;
        }
        
        // Filtrar árbitros por nombre o DNI
        const lowerQuery = query.toLowerCase();
        let foundMatch = false;
        
        items.forEach(item => {
            if (!item.dataset.id || item.dataset.id === "") {
                return; // Saltar la opción "Sin asignar" ya que siempre se muestra
            }
            
            const name = item.dataset.name?.toLowerCase() || '';
            const dni = item.textContent?.toLowerCase().split(' - ')[1] || '';
            
            if (name.includes(lowerQuery) || dni.includes(lowerQuery)) {
                item.style.display = '';
                foundMatch = true;
            } else {
                item.style.display = 'none';
            }
        });
    }
    
    /**
     * Clear selected referee for filtering
     */
    function clearSelectedReferee() {
        selectedRefereeId = '';
        selectedRefereeName = '';
        refereeFilterType = '';
        document.getElementById('selectedRefereeInfo').style.display = 'none';
    }
    
    /**
     * Clear selected referee for assignment
     */
    function clearAssignSelectedReferee() {
        selectedAssignRefereeId = '';
        selectedAssignRefereeName = '';
        document.getElementById('selectedAssignRefereeInfo').style.display = 'none';
    }
    
    /**
     * Filter teams in dropdown based on search query
     */
    function filterTeams(query, dropdown, dropdownNum) {
        const list = dropdown.querySelector('ul');
        list.innerHTML = '';
        
        if (!query.trim()) {
            dropdown.style.display = 'none';
            return;
        }
        
        const filteredTeams = teams.filter(team => 
            team.name.toLowerCase().includes(query.toLowerCase())
        );
        
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
                if (dropdownNum === 1) {
                    selectedTeam1Id = team.id;
                    selectedTeam1Name = team.name;
                    teamSearch1Input.value = team.name;
                    document.getElementById('selectedTeam1').textContent = team.name;
                    document.getElementById('selectedTeamInfo1').style.display = 'block';
                } else {
                    selectedTeam2Id = team.id;
                    selectedTeam2Name = team.name;
                    teamSearch2Input.value = team.name;
                    document.getElementById('selectedTeam2').textContent = team.name;
                    document.getElementById('selectedTeamInfo2').style.display = 'block';
                }
                dropdown.style.display = 'none';
            });
            
            list.appendChild(li);
        });
        
        dropdown.style.display = 'block';
    }
    
    /**
     * Clear selected team by number
     */
    function clearSelectedTeam(teamNum) {
        if (teamNum === 1) {
            selectedTeam1Id = '';
            selectedTeam1Name = '';
            document.getElementById('selectedTeamInfo1').style.display = 'none';
        } else {
            selectedTeam2Id = '';
            selectedTeam2Name = '';
            document.getElementById('selectedTeamInfo2').style.display = 'none';
        }
    }
    
    /**
     * Clear all selected teams
     */
    function clearSelectedTeams() {
        // Clear first team
        teamSearch1Input.value = '';
        selectedTeam1Id = '';
        selectedTeam1Name = '';
        document.getElementById('selectedTeamInfo1').style.display = 'none';
        
        // Clear second team
        teamSearch2Input.value = '';
        selectedTeam2Id = '';
        selectedTeam2Name = '';
        document.getElementById('selectedTeamInfo2').style.display = 'none';
    }
    
    /**
     * Populate filter dropdowns
     */
    function populateFilters() {
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
        const dateFrom = dateFromFilter.value ? new Date(dateFromFilter.value) : null;
        const dateTo = dateToFilter.value ? new Date(dateToFilter.value) : null;
        const sortBy = sortByFilter.value;
        
        // Apply filters
        filteredMatches = allMatches.filter(match => {
            const matchDate = new Date(match.date);
            
            // Team filters (for both teams, no matter if local or visitor)
            if (selectedTeam1Id && selectedTeam2Id) {
                // Match has to include both specific teams (exact match for both teams)
                // Scenario 1: team1 is local and team2 is visitor
                const scenario1 = match.local_team.id == selectedTeam1Id && match.visitor_team.id == selectedTeam2Id;
                // Scenario 2: team2 is local and team1 is visitor
                const scenario2 = match.local_team.id == selectedTeam2Id && match.visitor_team.id == selectedTeam1Id;
                
                // Only return true if one of the scenarios is true
                if (!(scenario1 || scenario2)) {
                    return false;
                }
            } else if (selectedTeam1Id) {
                // Filter by just the first team
                if (match.local_team.id != selectedTeam1Id && match.visitor_team.id != selectedTeam1Id) {
                    return false;
                }
            } else if (selectedTeam2Id) {
                // Filter by just the second team
                if (match.local_team.id != selectedTeam2Id && match.visitor_team.id != selectedTeam2Id) {
                    return false;
                }
            }
            
            // Date range filter
            if (dateFrom && matchDate < dateFrom) {
                return false;
            }
            if (dateTo && matchDate > dateTo) {
                return false;
            }
            
            // Referee filter
            if (refereeFilterType === 'assigned' && !match.referee_id) {
                return false;
            }
            if (refereeFilterType === 'unassigned' && match.referee_id) {
                return false;
            }
            if (refereeFilterType === 'specific' && selectedRefereeId) {
                if (match.referee_id != selectedRefereeId) {
                    return false;
                }
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
        
        // Populate referees dropdown and set selected referee if exists
        populateAssignRefereeDropdown(match.referee_id);
        
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
        const refereeId = selectedAssignRefereeId || null;
        
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
        
        // Reset assign referee selection
        if (modal === assignRefereeModal) {
            clearAssignSelectedReferee();
            assignRefereeSearch.value = '';
        }
    }
    
    // Event Listeners
    applyFilterBtn.addEventListener('click', applyFilters);
    
    clearFilterBtn.addEventListener('click', () => {
        // Clear team filters
        clearSelectedTeams();
        
        // Clear referee filter
        refereeSearchInput.value = '';
        clearSelectedReferee();
        
        // Reset dates to default range
        const today = new Date();
        const oneMonthAgo = new Date(today);
        oneMonthAgo.setMonth(oneMonthAgo.getMonth() - 1);
        const oneYearAhead = new Date(today);
        oneYearAhead.setFullYear(oneYearAhead.getFullYear() + 1);
        
        dateFromFilter.value = oneMonthAgo.toISOString().split('T')[0];
        dateToFilter.value = oneYearAhead.toISOString().split('T')[0];
        
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
    
    // Referee search event to show dropdown on click
    refereeSearchInput.addEventListener('click', () => {
        if (refereeDropdown.style.display !== 'block') {
            filterReferees(refereeSearchInput.value);
            refereeDropdown.style.display = 'block';
        }
    });
    
    // Assign referee search event to show dropdown on click
    assignRefereeSearch.addEventListener('click', () => {
        if (assignRefereeDropdown.style.display !== 'block') {
            filterAssignReferees(assignRefereeSearch.value);
            assignRefereeDropdown.style.display = 'block';
        }
    });
    
    // Modal events
    cancelAssignBtn.addEventListener('click', () => closeModal(assignRefereeModal));
    saveAssignBtn.addEventListener('click', saveRefereeAssignment);
    removeRefereeBtn.addEventListener('click', () => {
        selectedAssignRefereeId = '';
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