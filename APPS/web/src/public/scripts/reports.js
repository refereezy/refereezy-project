// reports.js - Handles reports data fetching and display

// Main app state
const appState = {
  reports: [],
  currentReport: null,
  filters: {
    status: 'all',
    date: null
  }
};

// DOM Elements
const reportsList = document.getElementById('reportsList');
const reportDetailModal = document.getElementById('reportDetailModal');
const reportDetail = document.getElementById('reportDetail');
const closeModal = document.querySelector('.close-modal');
const filterStatus = document.getElementById('filterStatus');
const filterDate = document.getElementById('filterDate');
const clearFilters = document.getElementById('clearFilters');

// Templates
const reportItemTemplate = document.getElementById('reportItemTemplate');
const reportDetailTemplate = document.getElementById('reportDetailTemplate');
const incidentItemTemplate = document.getElementById('incidentItemTemplate');

// Socket.io connection
let socket;

// Initialize the app
document.addEventListener('DOMContentLoaded', () => {
  // Connect to socket.io
  connectSocket();
  
  // Setup event listeners
  setupEventListeners();
  
  // Load initial reports list with short form data
  fetchReportsList();
});

// Connect to the socket.io server
function connectSocket() {
  socket = io("http://localhost:3000", {
    autoConnect: false,
    transports: ["websocket"],
  });
  
  socket.connect();
  
  // Setup socket event listeners
  socket.on("connect", () => {
    console.log("Connected to server");
  });
  
  socket.on("disconnect", () => {
    console.log("Disconnected from server");
  });
  
  // Listen for report updates
  socket.on("report-updated", (updatedReport) => {
    console.log("Report updated:", updatedReport);
    updateReportInList(updatedReport);
    
    // If the updated report is currently being viewed, update the detail view
    if (appState.currentReport && appState.currentReport.id === updatedReport.id) {
      fetchReportDetail(updatedReport.id);
    }
  });
  
  // Listen for new incidents
  socket.on("incident-added", (data) => {
    console.log("New incident:", data);
    if (appState.currentReport && appState.currentReport.id === data.reportId) {
      // Refresh the current report detail
      fetchReportDetail(data.reportId);
    }
  });
}

// Setup UI event listeners
function setupEventListeners() {
  // Close modal when clicking the close button
  closeModal.addEventListener('click', () => {
    reportDetailModal.classList.remove('show');
  });
  
  // Close modal when clicking outside the content
  window.addEventListener('click', (event) => {
    if (event.target === reportDetailModal) {
      reportDetailModal.classList.remove('show');
    }
  });
  
  // Filter by status
  filterStatus.addEventListener('change', applyFilters);
  
  // Filter by date
  filterDate.addEventListener('change', applyFilters);
  
  // Clear filters
  clearFilters.addEventListener('click', clearAllFilters);
}

// SIMPLIFIED FUNCTION 1: Fetch short reports list for the main view
async function fetchReportsList() {
  try {
    showLoading();
    
    const response = await fetch('/api/reports/short');
    if (!response.ok) {
      throw new Error(`Error fetching reports: ${response.statusText}`);
    }
    
    const data = await response.json();
    appState.reports = data;
    
    renderReportsList();
  } catch (error) {
    console.error('Failed to fetch reports:', error);
    showError('Failed to load reports. Please try again later.');
  }
}

// SIMPLIFIED FUNCTION 2: Fetch a specific report with full details
async function fetchReportDetail(reportId) {
  try {
    const response = await fetch(`/api/reports/${reportId}`);
    if (!response.ok) {
      throw new Error(`Error fetching report detail: ${response.statusText}`);
    }
    
    const report = await response.json();
    appState.currentReport = report;
    
    displayReportDetail(report);
  } catch (error) {
    console.error('Failed to fetch report details:', error);
    showError('Failed to load report details. Please try again later.');
  }
}

// Render the list of reports
function renderReportsList() {
  reportsList.innerHTML = '';
  
  if (appState.reports.length === 0) {
    reportsList.innerHTML = '<div class="no-reports">No reports found. Check back later.</div>';
    return;
  }
  
  // Apply filters
  const filteredReports = filterReports();
  
  if (filteredReports.length === 0) {
    reportsList.innerHTML = '<div class="no-reports">No reports match your filters.</div>';
    return;
  }
  
  // Create a document fragment to improve performance
  const fragment = document.createDocumentFragment();
  
  filteredReports.forEach(report => {
    const reportElement = createReportElement(report);
    fragment.appendChild(reportElement);
  });
  
  reportsList.appendChild(fragment);
}

// Create a report list item element
function createReportElement(report) {
  const template = reportItemTemplate.content.cloneNode(true);
  const reportItem = template.querySelector('.report-item');
  
  // Set report ID
  reportItem.dataset.reportId = report.id;
  
  // Set team names and logos
  const homeTeamName = template.querySelector('.team-home .team-name');
  const awayTeamName = template.querySelector('.team-away .team-name');
  const homeTeamLogo = template.querySelector('.team-home .team-logo');
  const awayTeamLogo = template.querySelector('.team-away .team-logo');
  
  homeTeamName.textContent = report.match.local_team.name;
  awayTeamName.textContent = report.match.visitor_team.name;
  homeTeamLogo.src = report.match.local_team.logo_url || 'https://via.placeholder.com/40';
  awayTeamLogo.src = report.match.visitor_team.logo_url || 'https://via.placeholder.com/40';
  
  // Set score
  const scoreHome = template.querySelector('.score-home');
  const scoreAway = template.querySelector('.score-away');
    // Calculate score from incidents (goals)
  const homeGoals = report.incidents.filter(i => {
    // Verify if it's a goal incident and determine if it belongs to the local team
    const isGoal = i.type === 'goal' || i.type === 'GOAL';
    if (!isGoal) return false;
    
    // If incident has player information, check the team_local flag
    if (i.player && i.player.team_local !== undefined) {
      return i.player.team_local === true;
    }
    
    // If we have team info in the incident, use that
    if (i.team && i.team.local !== undefined) {
      return i.team.local === true;
    }
    
    // Default: assume it's not a goal for this team if we can't determine
    return false;
  }).length;
  
  const awayGoals = report.incidents.filter(i => {
    // Verify if it's a goal incident and determine if it belongs to the away team
    const isGoal = i.type === 'goal' || i.type === 'GOAL';
    if (!isGoal) return false;
    
    // If incident has player information, check the team_local flag
    if (i.player && i.player.team_local !== undefined) {
      return i.player.team_local === false;
    }
    
    // If we have team info in the incident, use that
    if (i.team && i.team.local !== undefined) {
      return i.team.local === false;
    }
    
    // Default: assume it's not a goal for this team if we can't determine
    return false;
  }).length;
  
  scoreHome.textContent = homeGoals;
  scoreAway.textContent = awayGoals;
  
  // Set date
  const dateText = template.querySelector('.date-text');
  dateText.textContent = formatDate(report.match.raw.date);
  
  // Set referee
  const refereeName = template.querySelector('.referee-name');
  refereeName.textContent = report.referee ? report.referee.name : 'Unknown';
  
  // Set status
  const statusBadge = template.querySelector('.status-badge');
  statusBadge.textContent = report.done ? 'Completed' : 'In Progress';
  // Fix: Use classList.add with a single class name to avoid whitespace issues
  statusBadge.classList.add(report.done ? 'completed' : 'pending');
  
  // Set view details action
  const viewDetailsBtn = template.querySelector('.view-details');
  viewDetailsBtn.addEventListener('click', () => openReportDetail(report.id));
  
  return reportItem;
}

// Open the report detail modal
function openReportDetail(reportId) {
  fetchReportDetail(reportId);
  reportDetailModal.classList.add('show');
}

// Display report detail in the modal
function displayReportDetail(report) {
  reportDetail.innerHTML = '';
  
  const template = reportDetailTemplate.content.cloneNode(true);
  
  // Set match title
  const matchTitle = template.querySelector('.match-title');
  matchTitle.textContent = `${report.match.local_team.name} vs ${report.match.visitor_team.name}`;
  
  // Set match date and time
  const matchDateTime = template.querySelector('.match-date-time');
  matchDateTime.textContent = formatDateTime(report.match.raw.date);
  
  // Set status
  const reportStatus = template.querySelector('.report-status-detail');
  reportStatus.textContent = report.done ? 'Match Completed' : 'Match In Progress';
  // Fix: Add each class separately instead of a space-separated string
  reportStatus.classList.add('status-badge');
  reportStatus.classList.add(report.done ? 'completed' : 'pending');
  
  // Set team details
  const homeTeamName = template.querySelector('.team-home-detail .team-name-large');
  const awayTeamName = template.querySelector('.team-away-detail .team-name-large');
  const homeTeamLogo = template.querySelector('.team-home-detail .team-logo-large');
  const awayTeamLogo = template.querySelector('.team-away-detail .team-logo-large');
  
  homeTeamName.textContent = report.match.local_team.name;
  awayTeamName.textContent = report.match.visitor_team.name;
  homeTeamLogo.src = report.match.local_team.logo_url || 'https://via.placeholder.com/80';
  awayTeamLogo.src = report.match.visitor_team.logo_url || 'https://via.placeholder.com/80';
  
  // Set score
  const scoreHomeLarge = template.querySelector('.score-home-large');
  const scoreAwayLarge = template.querySelector('.score-away-large');
    // Calculate score from incidents (goals)
  const homeGoals = report.incidents.filter(i => {
    // Verify if it's a goal incident and determine if it belongs to the local team
    const isGoal = i.type === 'goal' || i.type === 'GOAL';
    if (!isGoal) return false;
    
    // If incident has player information, check the team_local flag
    if (i.player && i.player.team_local !== undefined) {
      return i.player.team_local === true;
    }
    
    // If we have team info in the incident, use that
    if (i.team && i.team.local !== undefined) {
      return i.team.local === true;
    }
    
    // Default: assume it's not a goal for this team if we can't determine
    return false;
  }).length;
  
  const awayGoals = report.incidents.filter(i => {
    // Verify if it's a goal incident and determine if it belongs to the away team
    const isGoal = i.type === 'goal' || i.type === 'GOAL';
    if (!isGoal) return false;
    
    // If incident has player information, check the team_local flag
    if (i.player && i.player.team_local !== undefined) {
      return i.player.team_local === false;
    }
    
    // If we have team info in the incident, use that
    if (i.team && i.team.local !== undefined) {
      return i.team.local === false;
    }
    
    // Default: assume it's not a goal for this team if we can't determine
    return false;
  }).length;
  
  scoreHomeLarge.textContent = homeGoals;
  scoreAwayLarge.textContent = awayGoals;
  
  // Set referee info
  const refereeName = template.querySelector('.referee-name-detail');
  refereeName.textContent = report.referee ? report.referee.name : 'Unknown';
  
  // Set timer values
  const timerValues = template.querySelector('.timer-values');
  timerValues.textContent = `${report.timer[0]}' : ${report.timer[1]}"`;
  
  // Render incidents
  const incidentsList = template.querySelector('.incidents-list');
  
  if (report.incidents.length === 0) {
    incidentsList.innerHTML = '<div class="no-incidents">No incidents recorded in this match yet.</div>';
  } else {
    // Sort incidents by minute
    const sortedIncidents = [...report.incidents].sort((a, b) => a.minute - b.minute);
    
    sortedIncidents.forEach(incident => {
      const incidentElement = createIncidentElement(incident, report);
      incidentsList.appendChild(incidentElement);
    });
  }
  
  reportDetail.appendChild(template);
}

// Create an incident element
function createIncidentElement(incident, report) {
  const template = incidentItemTemplate.content.cloneNode(true);
  
  // Set incident time
  const incidentTime = template.querySelector('.incident-time');
  incidentTime.textContent = `${incident.minute}'`;
  
  // Set incident type with proper styling
  const incidentType = template.querySelector('.incident-type');
  incidentType.textContent = formatIncidentType(incident.type);
  incidentType.classList.add(getIncidentClass(incident.type));
  
  // Determine if this incident type requires a player
  const typeUpperCase = incident.type.toUpperCase();
  const requiresPlayer = !['FIGHT', 'LESION', 'SUSPEND', 'OTHER'].includes(typeUpperCase);
  
  // Set player information if available and required
  const incidentPlayerElement = template.querySelector('.incident-player');
  
  if (incident.player && requiresPlayer) {
    const playerName = template.querySelector('.player-name');
    const playerTeamElement = template.querySelector('.player-team');
    const playerNumber = template.querySelector('.player-number');
    
    playerName.textContent = incident.player.name || 'Unknown Player';
    playerNumber.textContent = incident.player.dorsal || '?';
    
    // Use team_local flag to determine which team the player belongs to
    const team = incident.player.team_local ? 
                report.match.local_team : 
                report.match.visitor_team;
    
    playerTeamElement.textContent = team ? team.name : 'Unknown Team';
    
    // Make sure player info is visible
    incidentPlayerElement.style.display = 'flex';
  } else {
    // Hide player info when not needed or available
    incidentPlayerElement.style.display = 'none';
  }
  
  // Set description
  const description = template.querySelector('.incident-description');
  description.textContent = incident.description || '';
  
  return template;
}

// Helper function: Format incident type for display
function formatIncidentType(type) {
  switch(type) {
    case 'yellow-card':
    case 'YELLOW_CARD':
      return 'Yellow Card';
    case 'red-card':
    case 'RED_CARD':
      return 'Red Card';
    case 'goal':
    case 'GOAL':
      return 'Goal';
    case 'substitution':
      return 'Substitution';
    case 'injury':
    case 'LESION':
      return 'Injury';
    case 'FIGHT':
      return 'Fight';
    case 'SUSPEND':
      return 'Suspension';
    case 'OTHER':
      return 'Other';
    default:
      return type.charAt(0).toUpperCase() + type.slice(1);
  }
}

// Helper function: Get CSS class for incident type
function getIncidentClass(type) {
  const typeUpper = type.toUpperCase();
  switch(typeUpper) {
    case 'YELLOW_CARD':
    case 'YELLOW-CARD':
      return 'yellow-card';
    case 'RED_CARD':
    case 'RED-CARD':
      return 'red-card';
    case 'GOAL':
      return 'goal';
    case 'LESION':
    case 'INJURY':
      return 'injury';
    case 'FIGHT':
      return 'fight';
    case 'SUSPEND':
      return 'suspend';
    case 'OTHER':
      return 'other';
    default:
      return 'other';
  }
}

// Helper function: Format date (e.g., "April 16, 2023")
function formatDate(dateString) {
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
}

// Helper function: Format date and time (e.g., "April 16, 2023 at 15:30")
function formatDateTime(dateString) {
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

// Filter reports based on current filter settings
function filterReports() {
  return appState.reports.filter(report => {
    // Filter by status
    if (appState.filters.status !== 'all') {
      const isCompleted = appState.filters.status === 'completed';
      if (report.done !== isCompleted) return false;
    }
    
    // Filter by date
    if (appState.filters.date) {
      const filterDate = new Date(appState.filters.date);
      const reportDate = new Date(report.match.raw.date);
      
      // Compare only the date part (ignore time)
      if (filterDate.toDateString() !== reportDate.toDateString()) return false;
    }
    
    return true;
  });
}

// Apply filters and update the UI
function applyFilters() {
  appState.filters.status = filterStatus.value;
  appState.filters.date = filterDate.value || null;
  
  renderReportsList();
}

// Clear all filters
function clearAllFilters() {
  filterStatus.value = 'all';
  filterDate.value = '';
  
  appState.filters.status = 'all';
  appState.filters.date = null;
  
  renderReportsList();
}

// Update a report in the list without re-fetching all reports
function updateReportInList(updatedReport) {
  const index = appState.reports.findIndex(report => report.id === updatedReport.id);
  
  if (index !== -1) {
    appState.reports[index] = updatedReport;
    renderReportsList();
  }
}

// Show loading indicator
function showLoading() {
  reportsList.innerHTML = `
    <div class="loading-indicator">
      <i class="fas fa-spinner fa-spin"></i> Loading reports...
    </div>
  `;
}

// Show error message
function showError(message) {
  reportsList.innerHTML = `
    <div class="error-message">
      <i class="fas fa-exclamation-circle"></i> ${message}
    </div>
  `;
}