document.addEventListener('DOMContentLoaded', function() {
    const clientId = getClientId();
    if (clientId) {
        window.location.href = "./dashboard.html";
    }

    // Obtener referencias a los elementos del formulario
    const loginForm = document.querySelector('.form-container');
    const emailInput = document.querySelector('input[type="text"]');
    const passwordInput = document.querySelector('input[type="password"]');
    const loginButton = document.querySelector('.login-submit-button');
    // API_URL ahora está disponible desde base.js

    // Función para manejar el inicio de sesión
    async function handleLogin(event) {
        event.preventDefault();

        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();

        // Validación básica
        if (!email || !password) {
            showNotification('Por favor, complete todos los campos', 'warning');
            return;
        }

        // Preparar los datos de inicio de sesión
        const loginData = {
            email,
            password
        };

        try {
            // Enviar los datos al backend para autenticar al usuario
            const res = await fetch(`${API_URL}/clients/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(loginData)
            });

            if (!res.ok) {
                const errorText = await res.text();
                throw new Error(errorText || 'Error de autenticación');
            }

            // Suponiendo que la respuesta es un token JWT o algo que identifique al usuario
            const responseData = await res.json();
            
            // Guardar el token en localStorage
            localStorage.setItem('client_id', responseData.id);
            localStorage.setItem('token', responseData.token); // Si hay token
            localStorage.setItem('user_type', 'client');
            
            showNotification('Inicio de sesión exitoso', 'success');
            
            // Redirigir al usuario al dashboard
            setTimeout(() => {
                window.location.href = './dashboard.html';
            }, 1000);

        } catch (err) {
            console.error(err);
            showNotification('Error al iniciar sesión: ' + err.message, 'error');
        }
    }

    // Agregar event listener al botón de inicio de sesión
    loginButton.addEventListener('click', handleLogin);

    // También permitir envío con Enter en los inputs
    [emailInput, passwordInput].forEach(input => {
        input.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                handleLogin(e);
            }
        });
    });
});
