document.addEventListener('DOMContentLoaded', function() {
    // Verificar si el usuario ya está logueado
    const clientId = getClientId();
    if (clientId) {
        window.location.href = "./dashboard.html";
        return;
    }

    // Obtener referencias a los elementos del formulario
    const registerForm = document.getElementById('registerForm');
    const nameInput = document.getElementById('registerName');
    const emailInput = document.getElementById('registerEmail');
    const phoneInput = document.getElementById('registerPhone');
    const passwordInput = document.getElementById('registerPassword');
    const confirmPasswordInput = document.getElementById('registerConfirmPassword');

    // Función para validar email
    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    // Función para validar teléfono
    function isValidPhone(phone) {
        const phoneRegex = /^\+?[0-9]{9,15}$/;
        return phoneRegex.test(phone);
    }

    // Función para manejar el registro
    async function handleRegister(event) {
        event.preventDefault();

        // Obtener valores del formulario
        const name = nameInput.value.trim();
        const email = emailInput.value.trim();
        const phone = phoneInput.value.trim();
        const password = passwordInput.value.trim();
        const confirmPassword = confirmPasswordInput.value.trim();

        // Validación de entradas
        if (!name || !email || !phone || !password || !confirmPassword) {
            showNotification('Por favor, complete todos los campos', 'warning');
            return;
        }

        if (!isValidEmail(email)) {
            showNotification('Por favor, ingrese un correo electrónico válido', 'warning');
            return;
        }

        if (!isValidPhone(phone)) {
            showNotification('Por favor, ingrese un número de teléfono válido', 'warning');
            return;
        }

        if (password.length < 6) {
            showNotification('La contraseña debe tener al menos 6 caracteres', 'warning');
            return;
        }

        if (password !== confirmPassword) {
            showNotification('Las contraseñas no coinciden', 'warning');
            return;
        }

        try {
            // Mostrar estado de carga
            const submitButton = registerForm.querySelector('button[type="submit"]');
            const originalText = submitButton.textContent;
            submitButton.textContent = 'Registrando...';
            submitButton.disabled = true;

            // Calculamos la fecha de expiración del plan (1 año desde hoy)
            const today = new Date();
            const expirationDate = new Date();
            expirationDate.setFullYear(today.getFullYear() + 1);

            // Preparar los datos del registro
            const registerData = {
                name,
                email,
                password,
                phone,
                plan_expiration: expirationDate.toISOString()
            };

            // Enviar los datos al backend para crear el cliente
            const response = await fetch(`${API_URL}/clients/`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(registerData)
            });

            // Restaurar el botón
            submitButton.textContent = originalText;
            submitButton.disabled = false;

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.detail || 'Error al crear la cuenta');
            }

            // Procesar la respuesta
            const userData = await response.json();
            
            // Guardar información del usuario en localStorage
            localStorage.setItem('client_id', userData.id);
            localStorage.setItem('user_type', 'client');
            
            // Notificación de éxito
            showNotification('¡Registro exitoso! Bienvenido a RefereeZY', 'success');
            
            // Redirigir al usuario al dashboard
            setTimeout(() => {
                window.location.href = './dashboard.html';
            }, 1500);

        } catch (error) {
            console.error('Error de registro:', error);
            showNotification(`Error al registrar: ${error.message}`, 'error');
        }
    }

    // Agregar event listener al formulario
    registerForm.addEventListener('submit', handleRegister);
});