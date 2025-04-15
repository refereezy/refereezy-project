document.addEventListener('DOMContentLoaded', () => {
    const logoUploadBtn = document.querySelector('.logo-upload-btn');
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
                };
                reader.readAsDataURL(file);
            }
        };

        input.click();
    });

    addTeamBtn.addEventListener('click', async () => {
        const teamName = teamNameInput.value.trim();
        const primaryColor = primaryColorInput.value.trim();
        const secondaryColor = secondaryColorInput.value.trim();
        const clientId = clientIdInput.value.trim();

        if (!teamName || !primaryColor || !secondaryColor || !clientId) {
            alert('Por favor completa todos los campos');
            return;
        }

        const newTeam = {
            id: 0, // o puedes omitirlo si el backend lo genera
            name: teamName,
            primary_color: primaryColor,
            secondary_color: secondaryColor,
            logo_url: currentLogo || "", // puede estar vacío si no se subió uno
            client_id: Number(clientId)
        };

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
                throw new Error(errorText || 'Error al guardar el equipo');
            }

            alert('Equipo agregado exitosamente!');

            // Limpiar formulario
            teamNameInput.value = '';
            primaryColorInput.value = '';
            secondaryColorInput.value = '';
            clientIdInput.value = '';
            logoPreview.innerHTML = '<span>Vista previa del logo</span>';
            currentLogo = null;
        } catch (err) {
            console.error(err);
            alert('Error al guardar el equipo: ' + err.message);
        }
    });

    viewTeamsBtn.addEventListener('click', () => {
        window.location.href = '../pages/ver-equipos.html';
    });
});
