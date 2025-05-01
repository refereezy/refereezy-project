document.addEventListener('DOMContentLoaded', () => {
    const logoUploadBtn = document.querySelector('.logo-upload-btn');
    const removeLogoBtn = document.querySelector('.remove-logo-btn');
    const logoPreview = document.querySelector('.logo-preview');
    const addTeamBtn = document.querySelector('.add-team-btn');
    const viewTeamsBtn = document.querySelector('.view-teams-btn');
    const teamNameInput = document.querySelector('.team-name-input');
    const primaryColorInput = document.querySelector('.primary-color-input');
    const secondaryColorInput = document.querySelector('.secondary-color-input');

    // Use API_URL from base.js instead of redefining it
    let currentLogo = null;
    let logoFile = null; // Store the actual file object

    logoUploadBtn.addEventListener('click', () => {
        const input = document.createElement('input');
        input.type = 'file';
        input.accept = 'image/*';

        input.onchange = (e) => {
            const file = e.target.files[0];
            if (file) {
                logoFile = file; // Store the file object for later upload
                const reader = new FileReader();
                reader.onload = (e) => {
                    currentLogo = e.target.result;
                    logoPreview.innerHTML = `
                        <img src="${currentLogo}" alt="Logo preview" 
                            style="max-width: 100%; max-height: 100%; object-fit: contain;">
                    `;
                    removeLogoBtn.style.display = 'block'; // Mostrar el botón de quitar logo
                };
                reader.readAsDataURL(file);
            }
        };

        input.click();
    });

    removeLogoBtn.addEventListener('click', () => {
        currentLogo = null;
        logoFile = null; // Clear the file object
        logoPreview.innerHTML = '<span>Vista previa del logo</span>';
        removeLogoBtn.style.display = 'none'; // Ocultar el botón de quitar logo
    });

    addTeamBtn.addEventListener('click', async () => {
        const teamName = teamNameInput.value.trim();
        const primaryColor = primaryColorInput.value.trim();
        const secondaryColor = secondaryColorInput.value.trim();
        const clientId = getClientId(); // Use getClientId from base.js

        if (!teamName || !primaryColor || !secondaryColor) {
            showNotification('Por favor completa todos los campos', 'error');
            return;
        }

        if (!clientId) {
            showNotification('No se pudo obtener el ID del cliente', 'error');
            return;
        }

        if (!logoFile) {
            showNotification('Por favor selecciona un logo para el equipo', 'error');
            return;
        }

        // Create FormData object to handle file upload and form fields
        const formData = new FormData();
        formData.append('name', teamName);
        formData.append('primary_color', primaryColor);
        formData.append('secondary_color', secondaryColor);
        formData.append('client_id', clientId);
        formData.append('logo', logoFile); // Add the file with the name "logo" as expected by the API

        console.log("Enviando equipo con logo...");

        try {
            const res = await fetch(`${API_URL}/teams`, {
                method: 'POST',
                // Don't set Content-Type header - fetch will set it automatically with the boundary for FormData
                body: formData
            });

            if (!res.ok) {
                const errorText = await res.text();
                console.error("Respuesta del servidor:", errorText);
                throw new Error(errorText || 'Error al guardar el equipo');
            }

            showNotification('¡Equipo agregado exitosamente!', 'success');

            // Limpiar formulario
            teamNameInput.value = '';
            primaryColorInput.value = '';
            secondaryColorInput.value = '';
            logoPreview.innerHTML = '<span>Vista previa del logo</span>';
            currentLogo = null;
            logoFile = null;
            removeLogoBtn.style.display = 'none'; // Ocultar el botón de quitar logo
        } catch (err) {
            console.error("Error al guardar el equipo:", err);
            showNotification('Error al guardar el equipo: ' + err.message, 'error');
        }
    });

    viewTeamsBtn.addEventListener('click', () => {
        window.location.href = '../pages/ver-equipos.html';
    });
});
