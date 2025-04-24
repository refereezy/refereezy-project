document.addEventListener('DOMContentLoaded', () => {
    const logoUploadBtn = document.querySelector('.logo-upload-btn');
    const removeLogoBtn = document.querySelector('.remove-logo-btn');
    const logoPreview = document.querySelector('.logo-preview');
    const addTeamBtn = document.querySelector('.add-team-btn');
    const viewTeamsBtn = document.querySelector('.view-teams-btn');
    const teamNameInput = document.querySelector('.team-name-input');
    const primaryColorInput = document.querySelector('.primary-color-input');
    const secondaryColorInput = document.querySelector('.secondary-color-input');
    const clientIdInput = document.querySelector('.client-id-input');

    const API_URL = "http://localhost:8080";
    let currentLogo = null;

    logoUploadBtn.addEventListener('click', () => {
        const input = document.createElement('input');
        input.type = 'file';
        input.accept = 'image/*';

        input.onchange = (e) => {
            const file = e.target.files[0];
            if (file) {
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
        logoPreview.innerHTML = '<span>Vista previa del logo</span>';
        removeLogoBtn.style.display = 'none'; // Ocultar el botón de quitar logo
    });

    addTeamBtn.addEventListener('click', async () => {
        const teamName = teamNameInput.value.trim();
        const primaryColor = primaryColorInput.value.trim();
        const secondaryColor = secondaryColorInput.value.trim();
        const clientId = parseInt(localStorage.getItem('client_id'), 10);

        if (!teamName || !primaryColor || !secondaryColor) {
            alert('Por favor completa todos los campos');
            return;
        }

        if (isNaN(clientId) || clientId <= 0) {
            alert('El ID del cliente debe ser un número válido mayor a 0');
            return;
        }

        const newTeam = {
            // Aquí se debería de quitar el id
            id: 0,
            name: teamName,
            primary_color: primaryColor,
            secondary_color: secondaryColor,
            client_id: clientId,
            logo_url: currentLogo || ""
        };

        console.log("Enviando equipo:", newTeam);

        try {
            const res = await fetch(`${API_URL}/teams`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newTeam)
            });

            if (!res.ok) {
                const errorText = await res.text();
                console.error("Respuesta del servidor:", errorText);
                throw new Error(errorText || 'Error al guardar el equipo');
            }

            alert('✅ ¡Equipo agregado exitosamente!');

            // Limpiar formulario
            teamNameInput.value = '';
            primaryColorInput.value = '';
            secondaryColorInput.value = '';
            //clientIdInput.value = '';
            logoPreview.innerHTML = '<span>Vista previa del logo</span>';
            currentLogo = null;
            removeLogoBtn.style.display = 'none'; // Ocultar el botón de quitar logo
        } catch (err) {
            console.error("Error al guardar el equipo:", err);
            alert('❌ Error al guardar el equipo: ' + err.message);
        }
    });

    viewTeamsBtn.addEventListener('click', () => {
        window.location.href = '../pages/ver-equipos.html';
    });
});
