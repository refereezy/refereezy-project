document.addEventListener("DOMContentLoaded", () => {
  const sections = ["inicio", "equipos", "jugadores", "arbitros", "partidos", "planes"]
  const navLinks = document.querySelectorAll("nav a")
  const actionButtons = document.querySelectorAll(".action-buttons button")

  const API_URL = 'http://localhost:8080'; // URL de tu API

  function showSection(sectionId) {
      sections.forEach((id) => {
          const section = document.getElementById(id)
          console.log(`${id} ${sectionId}`)
          section.classList.toggle("hidden", id !== sectionId)
      })
  }

  actionButtons.forEach((button) => {
      button.addEventListener("click", () => {
          const sectionId = button.id.split("-")[1]
          showSection(sectionId)
      })
  })

  // Función para obtener equipos desde la API
  async function loadTeams() {
      try {
          const response = await fetch(`${API_URL}/teams`);
          const teams = await response.json();

          // Actualizar los selects con los equipos obtenidos de la API
          actualizarSelectEquipos(teams);
      } catch (error) {
          console.error('Error al cargar equipos:', error);
      }
  }

  // Función para actualizar los select con los equipos
  function actualizarSelectEquipos(teams) {
      const selectEquipoJugador = document.getElementById("equipo-jugador");
      const selectEquipoLocal = document.getElementById("equipo-local");
      const selectEquipoVisitante = document.getElementById("equipo-visitante");
      [selectEquipoJugador, selectEquipoLocal, selectEquipoVisitante].forEach((select) => {
          select.innerHTML = '<option value="">Seleccione un equipo</option>';
          teams.forEach((equipo) => {
              const option = document.createElement("option");
              option.value = equipo.id; // Usamos el id del equipo, no solo el nombre
              option.textContent = equipo.name; // Usamos el nombre del equipo
              select.appendChild(option);
          });
      });
  }

  // Gestión de equipos
  const formEquipo = document.getElementById("form-equipo")
  formEquipo.addEventListener("submit", async (e) => {
      e.preventDefault()
      const nombreEquipo = document.getElementById("nombre-equipo").value
      const logoEquipo = document.getElementById("logo-equipo").files[0]

      const formData = new FormData();
      formData.append("name", nombreEquipo);
      formData.append("logo", logoEquipo);

      try {
          const response = await fetch(`${API_URL}/teams`, {
              method: 'POST',
              body: formData,
          });

          if (response.ok) {
              alert(`Equipo "${nombreEquipo}" agregado con éxito.`);
              formEquipo.reset();
              loadTeams(); // Recargar equipos después de agregar uno
          } else {
              alert("Hubo un problema al agregar el equipo.");
          }
      } catch (error) {
          console.error('Error al agregar equipo:', error);
      }
  })

  // Gestión de jugadores
  const formJugador = document.getElementById("form-jugador")
  formJugador.addEventListener("submit", async (e) => {
      e.preventDefault()
      const equipoJugador = document.getElementById("equipo-jugador").value
      const nombreJugador = document.getElementById("nombre-jugador").value
      const numeroJugador = document.getElementById("numero-jugador").value
      const edadJugador = document.getElementById("edad-jugador").value
      const nacionalidadJugador = document.getElementById("nacionalidad-jugador").value

      if (!equipoJugador || !nombreJugador || !numeroJugador || !edadJugador || !nacionalidadJugador) {
          alert("Por favor, complete todos los campos.");
          return;
      }

      const jugador = {
          equipo: equipoJugador,
          nombre: nombreJugador,
          numero: numeroJugador,
          edad: edadJugador,
          nacionalidad: nacionalidadJugador,
      };

      try {
          const response = await fetch(`${API_URL}/players`, {
              method: 'POST',
              headers: {
                  'Content-Type': 'application/json',
              },
              body: JSON.stringify(jugador),
          });

          if (response.ok) {
              alert(`Jugador "${nombreJugador}" agregado con éxito.`);
              formJugador.reset();
          } else {
              alert("Hubo un problema al agregar el jugador.");
          }
      } catch (error) {
          console.error('Error al agregar jugador:', error);
      }
  })

  // Gestión de árbitros
  const formArbitro = document.getElementById("form-arbitro")
  formArbitro.addEventListener("submit", async (e) => {
      e.preventDefault()
      const nombreArbitro = document.getElementById("nombre-arbitro").value
      const edadArbitro = document.getElementById("edad-arbitro").value
      const nacionalidadArbitro = document.getElementById("nacionalidad-arbitro").value

      if (!nombreArbitro || !edadArbitro || !nacionalidadArbitro) {
          alert("Por favor, complete todos los campos.");
          return;
      }

      const arbitro = {
          nombre: nombreArbitro,
          edad: edadArbitro,
          nacionalidad: nacionalidadArbitro,
      };

      try {
          const response = await fetch(`${API_URL}/referees`, {
              method: 'POST',
              headers: {
                  'Content-Type': 'application/json',
              },
              body: JSON.stringify(arbitro),
          });

          if (response.ok) {
              alert(`Árbitro "${nombreArbitro}" agregado con éxito.`);
              formArbitro.reset();
          } else {
              alert("Hubo un problema al agregar el árbitro.");
          }
      } catch (error) {
          console.error('Error al agregar árbitro:', error);
      }
  })

  // Gestión de partidos
  const formPartido = document.getElementById("form-partido")
  formPartido.addEventListener("submit", async (e) => {
      e.preventDefault()
      const equipoLocal = document.getElementById("equipo-local").value
      const equipoVisitante = document.getElementById("equipo-visitante").value
      const fechaPartido = document.getElementById("fecha-partido").value
      const horaPartido = document.getElementById("hora-partido").value

      if (!equipoLocal || !equipoVisitante || !fechaPartido || !horaPartido) {
          alert("Por favor, complete todos los campos.");
          return;
      }

      if (equipoLocal === equipoVisitante) {
          alert("El equipo local y visitante no pueden ser el mismo.");
          return;
      }

      const partido = {
          local: equipoLocal,
          visitante: equipoVisitante,
          fecha: fechaPartido,
          hora: horaPartido,
      };

      try {
          const response = await fetch(`${API_URL}/matches`, {
              method: 'POST',
              headers: {
                  'Content-Type': 'application/json',
              },
              body: JSON.stringify(partido),
          });

          if (response.ok) {
              alert(`Partido entre "${equipoLocal}" y "${equipoVisitante}" creado con éxito.`);
              formPartido.reset();
          } else {
              alert("Hubo un problema al crear el partido.");
          }
      } catch (error) {
          console.error('Error al crear partido:', error);
      }
  })

  // Cargar equipos al iniciar
  loadTeams();

  showSection("inicio");
})
