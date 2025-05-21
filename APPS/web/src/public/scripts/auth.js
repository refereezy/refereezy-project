
// Constants
const API_URL = `https://${window.location.hostname || "localhost"}:8888`;

// Verificar si el usuario está autenticado
function getClientId() {
    return localStorage.getItem('client_id');
}

function getUserType() {
    return localStorage.getItem('user_type');
}

// Función para validar email
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Función para mostrar notificaciones
function showNotification(message, type = 'info') {
    const notificationElement = document.createElement('div');
    notificationElement.className = `notification ${type}`;
    notificationElement.textContent = message;
    
    // Add notification to page
    document.body.appendChild(notificationElement);
    
    // Auto remove after 3 seconds
    setTimeout(() => {
        notificationElement.remove();
    }, 3000);
}

// Function to handle login
async function handleLogin(email, password, statusElement) {
    if (!email || !password) {
        showNotification('Por favor, complete todos los campos', 'warning');
        return false;
    }

    if (!isValidEmail(email)) {
        showNotification('Por favor, ingrese un correo electrónico válido', 'warning');
        return false;
    }

    try {
        // Preparar los datos de inicio de sesión
        const loginData = { email, password };

        // Enviar los datos al backend para autenticar al usuario
        const response = await fetch(`${API_URL}/clients/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.detail || 'Credenciales inválidas');
        }

        // Procesar la respuesta
        const userData = await response.json();
        
        // Guardar información del usuario en localStorage
        localStorage.setItem('client_id', userData.id);
        localStorage.setItem('user_type', 'client');
        
        // Notificación de éxito
        showNotification('Inicio de sesión exitoso', 'success');
        
        return true;
    } catch (error) {
        console.error('Error de login:', error);
        showNotification(`Error al iniciar sesión: ${error.message}`, 'error');
        if (statusElement) {
            statusElement.textContent = `Error: ${error.message}`;
            statusElement.className = 'status error';
        }
        return false;
    }
}

// Función para cerrar sesión
function logout() {
    localStorage.removeItem('client_id');
    localStorage.removeItem('user_type');
    showNotification('Sesión cerrada correctamente', 'success');
    
    // Redirigir a la página de inicio de sesión
    window.location.reload();
}

// CSS para notificaciones
const style = document.createElement('style');
style.textContent = `
.notification {
    position: fixed;
    top: 20px;
    right: 20px;
    padding: 15px 20px;
    border-radius: 4px;
    color: white;
    z-index: 1000;
    opacity: 0;
    transform: translateY(-20px);
    animation: notification-fade-in 0.3s forwards;
}

@keyframes notification-fade-in {
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.notification.success {
    background-color: #4CAF50;
}

.notification.error {
    background-color: #f44336;
}

.notification.warning {
    background-color: #ff9800;
}

.notification.info {
    background-color: #2196F3;
}
`;
document.head.appendChild(style);
