const auth_ok = localStorage.getItem("auth_ok");
if (auth_ok) {
    window.location.href = "./dashboard.html";
}


document.addEventListener('DOMContentLoaded', function() {
    // Obtener referencias a los elementos del formulario
    const loginForm = document.querySelector('.form-container');
    const emailInput = document.querySelector('input[type="text"]');
    const passwordInput = document.querySelector('input[type="password"]');
    const loginButton = document.querySelector('.login-submit-button');
    const API_URL = "http://localhost:8080"; // Cambia esta URL según sea necesario

    // Función para manejar el inicio de sesión
    async function handleLogin(event) {
        event.preventDefault();

        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();

        // Validación básica
        if (!email || !password) {
            alert('Por favor, complete todos los campos');
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
            
            // Guardar el token en localStorage (si se utiliza JWT)
            //localStorage.setItem('authToken', responseData.token);
            console.log(responseData);
            localStorage.setItem('auth_ok', true)
            localStorage.setItem('client_id', responseData.id)
            // Redirigir al usuario al dashboard o página principal
            window.location.href = './dashboard.html';  // Cambia esto a la URL de tu dashboard

        } catch (err) {
            console.error(err);
            alert('Error al iniciar sesión: ' + err.message);
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
