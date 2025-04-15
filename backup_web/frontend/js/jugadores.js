document.addEventListener('DOMContentLoaded', () => {
    // Referencias a elementos del DOM
    const playerNameInput = document.querySelector('.player-name-input');
    const playerNumberInput = document.querySelector('.player-number-input');
    const addPlayerBtn = document.querySelector('.add-player-btn');
    const viewPlayersBtn = document.querySelector('.view-players-btn');
    const API_URL = "http://localhost:8080"; // Cambia si es necesario

    // Manejador para agregar jugador
    addPlayerBtn.addEventListener('click', async () => {
        const playerName = playerNameInput.value.trim();
        const playerNumber = playerNumberInput.value.trim();

        if (!playerName || !playerNumber) {
            alert('Por favor complete todos los campos');
            return;
        }

        try {
            // Verificar si el número ya está en uso (en el backend)
            const resCheck = await fetch(`${API_URL}/players?number=${playerNumber}`);
            const existingPlayers = await resCheck.json();
            
            if (existingPlayers.length > 0) {
                alert('Este número de jugador ya está en uso');
                return;
            }

            // Agregar nuevo jugador
            const newPlayer = {
                name: playerName,
                number: playerNumber
            };

            const res = await fetch(`${API_URL}/players`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newPlayer)
            });

            if (!res.ok) {
                const errorText = await res.text();
                throw new Error(errorText || 'Error al agregar el jugador');
            }

            alert('Jugador agregado exitosamente!');

            // Limpiar formulario
            playerNameInput.value = '';
            playerNumberInput.value = '';
        } catch (err) {
            console.error(err);
            alert('Error al agregar el jugador: ' + err.message);
        }
    });

    // Manejador para ver jugadores
    viewPlayersBtn.addEventListener('click', () => {
        window.location.href = '../pages/ver-jugadores.html';
    });
});
