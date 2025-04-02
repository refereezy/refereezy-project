document.addEventListener('DOMContentLoaded', () => {
    // Referencias a elementos del DOM
    const playerNameInput = document.querySelector('.player-name-input');
    const playerNumberInput = document.querySelector('.player-number-input');
    const addPlayerBtn = document.querySelector('.add-player-btn');
    const viewPlayersBtn = document.querySelector('.view-players-btn');

    // Manejador para agregar jugador
    addPlayerBtn.addEventListener('click', () => {
        const playerName = playerNameInput.value.trim();
        const playerNumber = playerNumberInput.value.trim();

        if (playerName && playerNumber) {
            // Obtener jugadores existentes
            const players = JSON.parse(localStorage.getItem('players')) || [];
            
            // Verificar si el número ya está en uso
            const numberExists = players.some(player => player.number === playerNumber);
            
            if (numberExists) {
                alert('Este número de jugador ya está en uso');
                return;
            }

            // Agregar nuevo jugador
            players.push({
                name: playerName,
                number: playerNumber,
                id: Date.now() // Identificador único
            });
            
            // Guardar en localStorage
            localStorage.setItem('players', JSON.stringify(players));
            
            // Limpiar formulario
            playerNameInput.value = '';
            playerNumberInput.value = '';
            
            alert('Jugador agregado exitosamente!');
        } else {
            alert('Por favor complete todos los campos');
        }
    });

    // Manejador para ver jugadores
    viewPlayersBtn.addEventListener('click', () => {
        window.location.href = '../pages/ver-jugadores.html';
    });
});