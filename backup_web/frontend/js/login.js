document.addEventListener('DOMContentLoaded', function() {
    const clientId = getClientId();
    if (clientId) {
        window.location.href = "./dashboard.html";
    }

    // Obtener referencia al formulario de login
    const loginForm = document.getElementById('loginForm');
    const emailInput = document.getElementById('loginEmail');
    const passwordInput = document.getElementById('loginPassword');

    // Función para validar email
    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    // Función para manejar el inicio de sesión
    async function handleLogin(event) {
        event.preventDefault();

        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();

        // Validación mejorada
        if (!email || !password) {
            showNotification('Por favor, complete todos los campos', 'warning');
            return;
        }

        if (!isValidEmail(email)) {
            showNotification('Por favor, ingrese un correo electrónico válido', 'warning');
            return;
        }

        try {
            // Mostrar estado de carga
            const submitButton = loginForm.querySelector('button[type="submit"]');
            const originalText = submitButton.textContent;
            submitButton.textContent = 'Iniciando...';
            submitButton.disabled = true;

            // Preparar los datos de inicio de sesión
            const loginData = {
                email,
                password
            };

            // Enviar los datos al backend para autenticar al usuario
            const response = await fetch(`${API_URL}/clients/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(loginData)
            });

            // Restaurar el botón
            submitButton.textContent = originalText;
            submitButton.disabled = false;

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
            
            // Redirigir al usuario al dashboard
            setTimeout(() => {
                window.location.href = './dashboard.html';
            }, 1000);

        } catch (error) {
            console.error('Error de login:', error);
            showNotification(`Error al iniciar sesión: ${error.message}`, 'error');
        }
    }

    // Agregar event listener al formulario
    loginForm.addEventListener('submit', handleLogin);
});
