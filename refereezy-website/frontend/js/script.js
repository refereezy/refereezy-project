document.addEventListener("DOMContentLoaded", () => {
    const sections = ["inicio", "equipos", "jugadores", "arbitros", "partidos", "planes"]
    const navLinks = document.querySelectorAll("nav a")
    const actionButtons = document.querySelectorAll(".action-buttons button")
  
    const equipos = []
    const jugadores = []
    const arbitros = []
    const partidos = []
  
    function showSection(sectionId) {
      sections.forEach((id) => {
        const section = document.getElementById(id)
        section.classList.toggle("hidden", id !== sectionId)
      })
    }
  
    navLinks.forEach((link) => {
      link.addEventListener("click", (e) => {
        e.preventDefault()
        const sectionId = e.target.getAttribute("href").substring(1)
        showSection(sectionId)
      })
    })
  
    actionButtons.forEach((button) => {
      button.addEventListener("click", () => {
        const sectionId = button.id.split("-")[1]
        showSection(sectionId)
      })
    })
  
    // Gestión de equipos
    const formEquipo = document.getElementById("form-equipo")
    formEquipo.addEventListener("submit", (e) => {
      e.preventDefault()
      const nombreEquipo = document.getElementById("nombre-equipo").value
      const logoEquipo = document.getElementById("logo-equipo").files[0]
      equipos.push({ nombre: nombreEquipo, logo: logoEquipo })
      alert(`Equipo "${nombreEquipo}" agregado con éxito.`)
      formEquipo.reset()
      actualizarSelectEquipos()
    })
  
    // Gestión de jugadores
    const formJugador = document.getElementById("form-jugador")
    formJugador.addEventListener("submit", (e) => {
      e.preventDefault()
      const equipoJugador = document.getElementById("equipo-jugador").value
      const nombreJugador = document.getElementById("nombre-jugador").value
      const numeroJugador = document.getElementById("numero-jugador").value
      const edadJugador = document.getElementById("edad-jugador").value
      const nacionalidadJugador = document.getElementById("nacionalidad-jugador").value
  
      if (equipos.length === 0) {
        alert("Debe crear al menos un equipo antes de agregar jugadores.")
        return
      }
  
      jugadores.push({
        equipo: equipoJugador,
        nombre: nombreJugador,
        numero: numeroJugador,
        edad: edadJugador,
        nacionalidad: nacionalidadJugador,
      })
      alert(`Jugador "${nombreJugador}" agregado con éxito al equipo "${equipoJugador}".`)
      formJugador.reset()
    })
  
    // Gestión de árbitros
    const formArbitro = document.getElementById("form-arbitro")
    formArbitro.addEventListener("submit", (e) => {
      e.preventDefault()
      const nombreArbitro = document.getElementById("nombre-arbitro").value
      const edadArbitro = document.getElementById("edad-arbitro").value
      const nacionalidadArbitro = document.getElementById("nacionalidad-arbitro").value
  
      if (equipos.length === 0) {
        alert("Debe crear al menos un equipo antes de agregar árbitros.")
        return
      }
  
      arbitros.push({
        nombre: nombreArbitro,
        edad: edadArbitro,
        nacionalidad: nacionalidadArbitro,
      })
      alert(`Árbitro "${nombreArbitro}" agregado con éxito.`)
      formArbitro.reset()
    })
  
    // Gestión de partidos
    const formPartido = document.getElementById("form-partido")
    formPartido.addEventListener("submit", (e) => {
      e.preventDefault()
      const equipoLocal = document.getElementById("equipo-local").value
      const equipoVisitante = document.getElementById("equipo-visitante").value
      const fechaPartido = document.getElementById("fecha-partido").value
      const horaPartido = document.getElementById("hora-partido").value
  
      if (equipos.length < 2) {
        alert("Debe crear al menos dos equipos antes de crear un partido.")
        return
      }
  
      if (equipoLocal === equipoVisitante) {
        alert("El equipo local y visitante no pueden ser el mismo.")
        return
      }
  
      partidos.push({
        local: equipoLocal,
        visitante: equipoVisitante,
        fecha: fechaPartido,
        hora: horaPartido,
      })
      alert(`Partido entre "${equipoLocal}" y "${equipoVisitante}" creado con éxito.`)
      formPartido.reset()
    })
  
    function actualizarSelectEquipos() {
      const selectEquipoJugador = document.getElementById("equipo-jugador")
      const selectEquipoLocal = document.getElementById("equipo-local")
      const selectEquipoVisitante = document.getElementById("equipo-visitante")
      ;[selectEquipoJugador, selectEquipoLocal, selectEquipoVisitante].forEach((select) => {
        select.innerHTML = '<option value="">Seleccione un equipo</option>'
        equipos.forEach((equipo) => {
          const option = document.createElement("option")
          option.value = equipo.nombre
          option.textContent = equipo.nombre
          select.appendChild(option)
        })
      })
    }
    showSection("inicio")
  })
  
  