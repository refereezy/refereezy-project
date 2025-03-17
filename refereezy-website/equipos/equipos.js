document.addEventListener('DOMContentLoaded', () => {
    // Referencias a elementos del DOM
    const logoUploadBtn = document.querySelector('.logo-upload-btn');
    const logoPreview = document.querySelector('.logo-preview');
    const addTeamBtn = document.querySelector('.add-team-btn');
    const viewTeamsBtn = document.querySelector('.view-teams-btn');
    const teamNameInput = document.querySelector('.team-name-input');
    let currentLogo = null;
    
    // Manejador para subir logo
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
    
    // Manejador para agregar equipo
    addTeamBtn.addEventListener('click', () => {
        const teamName = teamNameInput.value.trim();
        if (teamName) {
            // Obtener equipos existentes
            const teams = JSON.parse(localStorage.getItem('teams')) || [];
            
            // Agregar nuevo equipo
            teams.push({
                name: teamName,
                logo: currentLogo,
                id: Date.now() // Identificador único
            });
            
            // Guardar en localStorage
            localStorage.setItem('teams', JSON.stringify(teams));
            
            // Limpiar formulario
            teamNameInput.value = '';
            logoPreview.innerHTML = '<span>Vista previa del logo</span>';
            currentLogo = null;
            
            alert('Equipo agregado exitosamente!');
        } else {
            alert('Por favor ingrese un nombre para el equipo');
        }
    });
    
    // Manejador para ver más equipos
    viewTeamsBtn.addEventListener('click', () => {
        window.location.href = 'ver-equipos.html';
    });
});