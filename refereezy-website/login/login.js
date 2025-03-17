document.addEventListener('DOMContentLoaded', function() {
    // Obtener referencias a los elementos del formulario
    const loginForm = document.querySelector('.form-container');
    const emailInput = document.querySelector('input[type="text"]');
    const passwordInput = document.querySelector('input[type="password"]');
    const loginButton = document.querySelector('.login-submit-button');

    // Función para manejar el inicio de sesión
    function handleLogin(event) {
        event.preventDefault();

        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();

        // Validación básica
        if (!email || !password) {
            alert('Por favor, complete todos los campos');
            return;
        }

        // Aquí puedes agregar la lógica de autenticación
        // Por ahora solo mostraremos un mensaje
        console.log('Intentando iniciar sesión con:', {
            email,
            password
        });

        // Simulación de inicio de sesión
        // En una implementación real, aquí irían las llamadas a la API
        alert('Función de inicio de sesión en desarrollo');
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