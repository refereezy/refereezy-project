document.addEventListener('DOMContentLoaded', () => {
    // Referencias a elementos del DOM para el formulario
    const logoUploadBtn = document.querySelector('.logo-upload-btn');
    const removeLogoBtn = document.querySelector('.remove-logo-btn');
    const logoPreview = document.querySelector('.logo-preview');
    const addTeamBtn = document.querySelector('.add-team-btn');
    const teamNameInput = document.querySelector('.team-name-input');
    const primaryColorInput = document.querySelector('.primary-color-input');
    const secondaryColorInput = document.querySelector('.secondary-color-input');
    const primaryColorCircle = document.querySelector('.primary-color-circle');
    const secondaryColorCircle = document.querySelector('.secondary-color-circle');
    const teamFormModal = document.getElementById('teamFormModal');

    // Variables para almacenar datos del equipo
    let currentLogo = null;
    let logoFile = null; // Store the actual file object
    
    // Use API_URL and clientId from base.js
    const clientId = getClientId();

    // Inicializar valores predeterminados para los colores (más típicos de equipos de fútbol)
    primaryColorInput.value = "#FF0000"; // Rojo (como Man Utd, Liverpool)
    secondaryColorInput.value = "#FFFFFF"; // Blanco (común en uniformes alternativos)
    updateColorCircle(primaryColorCircle, primaryColorInput.value);
    updateColorCircle(secondaryColorCircle, secondaryColorInput.value);

    // Setup event listeners
    setupEventListeners();

    function setupEventListeners() {
        // Manejo de subida de logo
        logoUploadBtn.addEventListener('click', handleLogoUpload);
        removeLogoBtn.addEventListener('click', handleLogoRemoval);
        
        // Cambio de colores
        primaryColorInput.addEventListener('input', () => {
            updateColorCircle(primaryColorCircle, primaryColorInput.value);
        });
        
        secondaryColorInput.addEventListener('input', () => {
            updateColorCircle(secondaryColorCircle, secondaryColorInput.value);
        });
        
        // Agregar equipo
        addTeamBtn.addEventListener('click', handleTeamSubmission);
        
        // Limpiar formulario cuando se cierra el modal
        document.getElementById('closeTeamFormBtn').addEventListener('click', resetForm);
        
        // También limpiar cuando se cierra haciendo clic fuera
        window.addEventListener('click', (e) => {
            if (e.target === teamFormModal) {
                resetForm();
            }
        });
    }
    
    // Función para manejar la subida del logo
    function handleLogoUpload() {
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
    }
    
    // Función para quitar el logo
    function handleLogoRemoval() {
        currentLogo = null;
        logoFile = null;
        logoPreview.innerHTML = '<span>Vista previa del logo</span>';
        removeLogoBtn.style.display = 'none';
    }
    
    // Función para actualizar el color del círculo
    function updateColorCircle(circle, color) {
        circle.style.backgroundColor = color;
        // Ajustar el color del número para garantizar la legibilidad
        const textElem = circle.querySelector('.circle-number');
        if (textElem) {
            // Calcular el brillo del color de fondo
            const r = parseInt(color.slice(1, 3), 16);
            const g = parseInt(color.slice(3, 5), 16);
            const b = parseInt(color.slice(5, 7), 16);
            const brightness = (r * 299 + g * 587 + b * 114) / 1000;
            
            // Usar texto oscuro o claro según el brillo
            textElem.style.color = brightness > 128 ? '#000000' : '#ffffff';
        }
    }
    
    // Función para manejar la creación del equipo
    async function handleTeamSubmission() {
        const teamName = teamNameInput.value.trim();
        const primaryColor = primaryColorInput.value.trim();
        const secondaryColor = secondaryColorInput.value.trim();

        if (!teamName) {
            showNotification('Por favor ingresa un nombre para el equipo', 'error');
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

        // Mostrar indicador de carga
        addTeamBtn.disabled = true;
        addTeamBtn.textContent = 'Guardando...';

        // Create FormData object to handle file upload and form fields
        const formData = new FormData();
        formData.append('name', teamName);
        formData.append('primary_color', primaryColor);
        formData.append('secondary_color', secondaryColor);
        formData.append('client_id', clientId);
        formData.append('logo', logoFile);

        try {
            const res = await fetch(`${API_URL}/teams`, {
                method: 'POST',
                body: formData
            });

            if (!res.ok) {
                const errorText = await res.text();
                console.error("Respuesta del servidor:", errorText);
                throw new Error(errorText || 'Error al guardar el equipo');
            }

            showNotification('¡Equipo agregado exitosamente!', 'success');
            
            // Cerrar el modal y resetear el formulario
            teamFormModal.style.display = 'none';
            resetForm();
            
            // Actualizar la lista de equipos
            if (window.teamManager && window.teamManager.refreshTeams) {
                window.teamManager.refreshTeams();
            }
        } catch (err) {
            console.error("Error al guardar el equipo:", err);
            showNotification('Error al guardar el equipo: ' + err.message, 'error');
        } finally {
            // Restaurar el botón
            addTeamBtn.disabled = false;
            addTeamBtn.textContent = 'Agregar equipo';
        }
    }
    
    // Función para resetear el formulario
    function resetForm() {
        teamNameInput.value = '';
        primaryColorInput.value = "#FF0000"; // Rojo
        secondaryColorInput.value = "#FFFFFF"; // Blanco
        updateColorCircle(primaryColorCircle, primaryColorInput.value);
        updateColorCircle(secondaryColorCircle, secondaryColorInput.value);
        handleLogoRemoval();
    }
});