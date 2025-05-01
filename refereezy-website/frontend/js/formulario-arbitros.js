// Módulo para gestionar el formulario de creación de árbitros
document.addEventListener('DOMContentLoaded', () => {
    const API_URL = "http://localhost:8080";
    const clientId = localStorage.getItem('client_id');

    // Referencias a elementos del DOM del formulario
    const refereeNameInput = document.getElementById('refereeName');
    const refereeDniInput = document.getElementById('refereeDni');
    const refereePasswordInput = document.getElementById('refereePassword');
    const addRefereeBtn = document.getElementById('addRefereeBtn');

    // Configurar eventos del formulario
    setupFormEvents();

    function setupFormEvents() {
        // Manejador para agregar árbitro
        addRefereeBtn.addEventListener('click', addReferee);
    }

    // Añadir un nuevo árbitro
    async function addReferee() {
        const refereeName = refereeNameInput.value.trim();
        const refereeDni = refereeDniInput.value.trim();
        const refereePassword = refereePasswordInput.value.trim();

        // Validar que todos los campos requeridos estén completos
        if (!refereeName || !refereeDni || !refereePassword) {
            showNotification('Por favor complete todos los campos requeridos', 'error');
            return;
        }

        try {
            // Verificar si ya existe un árbitro con el mismo DNI
            const checkRes = await fetch(`${API_URL}/referee/client/${clientId}`);
            if (!checkRes.ok) {
                throw new Error('Error al verificar árbitros existentes');
            }
            
            const existingReferees = await checkRes.json();
            const refereeExists = existingReferees.some(ref => ref.dni.toLowerCase() === refereeDni.toLowerCase());

            if (refereeExists) {
                showNotification('Ya existe un árbitro con este DNI', 'error');
                return;
            }

            // Crear nuevo árbitro
            const newReferee = {
                name: refereeName,
                dni: refereeDni,
                password: refereePassword,
                client_id: parseInt(clientId),
                clock_code: null
            };

            const createRes = await fetch(`${API_URL}/referee`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newReferee)
            });

            if (!createRes.ok) {
                const errorText = await createRes.text();
                throw new Error(errorText || 'Error al crear el árbitro');
            }

            showNotification('Árbitro creado exitosamente', 'success');

            // Limpiar formulario
            refereeNameInput.value = '';
            refereeDniInput.value = '';
            refereePasswordInput.value = '';

            // Actualizar la lista de árbitros
            if (window.refereeManager) {
                await window.refereeManager.refreshReferees();
            }

            // Ocultar el formulario después de agregar
            document.getElementById('refereeFormContainer').style.display = 'none';
            
        } catch (err) {
            console.error('Error al crear árbitro:', err);
            showNotification('Error al crear el árbitro: ' + err.message, 'error');
        }
    }

    // Función para mostrar notificaciones, reutilizamos la del módulo de listado si está disponible
    function showNotification(message, type = 'info') {
        if (window.refereeManager && window.refereeManager.showNotification) {
            window.refereeManager.showNotification(message, type);
        } else {
            // Implementación de respaldo por si no está disponible la función global
            const notification = document.createElement('div');
            notification.className = `notification ${type}`;
            notification.textContent = message;
            
            document.body.appendChild(notification);
            
            setTimeout(() => {
                notification.classList.add('show');
                
                setTimeout(() => {
                    notification.classList.remove('show');
                    setTimeout(() => {
                        document.body.removeChild(notification);
                    }, 300);
                }, 3000);
            }, 100);
        }
    }
});