// Función para obtener datos del cliente
    async function loadClientData() {
      try {
        const clientId = getClientId();
        if (!clientId) {
          window.location.href = "./login.html";
          return;
        }
        
        // Obtener información del cliente
        const clientResponse = await fetch(`${API_URL}/clients/${clientId}`);
        if (!clientResponse.ok) {
          throw new Error('Error al cargar datos del cliente');
        }
        const clientData = await clientResponse.json();
        
        // Mostrar información del cliente
        document.getElementById('client-name').textContent = `${clientData.name}`;
        document.getElementById('client-email').textContent = clientData.email;
        document.getElementById('client-plan').textContent = clientData.plan;
        document.getElementById('plan-expiration').textContent = formatDate(clientData.plan_expiration);
        
        // Cargar estadísticas
        await loadStats(clientId);
      } catch (error) {
        console.error('Error:', error);
        showNotification('Error al cargar datos del cliente', 'error');
      }
    }
    
    // Función para cargar estadísticas
    async function loadStats(clientId) {
      try {
        // Cargar equipos
        const teamsResponse = await fetch(`${API_URL}/teams/client/${clientId}`);
        if (!teamsResponse.ok) {
          throw new Error('Error al cargar equipos');
        }
        const teamsData = await teamsResponse.json();
        document.getElementById('total-equipos').textContent = teamsData.length;
        
        // Cargar jugadores
        const playersResponse = await fetch(`${API_URL}/players/client/${clientId}`);
        if (!playersResponse.ok) {
          throw new Error('Error al cargar jugadores');
        }
        const playersData = await playersResponse.json();
        document.getElementById('total-jugadores').textContent = playersData.length;
        
        // Cargar árbitros
        const refereesResponse = await fetch(`${API_URL}/referee/client/${clientId}`);
        if (!refereesResponse.ok) {
          throw new Error('Error al cargar árbitros');
        }
        const refereesData = await refereesResponse.json();
        document.getElementById('total-arbitros').textContent = refereesData.length;
        
        // Cargar partidos
        const matchesResponse = await fetch(`${API_URL}/matches/client/${clientId}`);
        if (!matchesResponse.ok) {
          throw new Error('Error al cargar partidos');
        }
        const matchesData = await matchesResponse.json();
        document.getElementById('total-partidos').textContent = matchesData.length;
      } catch (error) {
        console.error('Error:', error);
        showNotification('Error al cargar estadísticas', 'error');
      }
    }
    
    // Inicializar cuando se carga la página
    document.addEventListener('DOMContentLoaded', () => {
      loadClientData();
    });
