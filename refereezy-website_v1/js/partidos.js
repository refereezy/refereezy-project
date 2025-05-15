document.addEventListener("DOMContentLoaded", () => {
    const partidos = []
    const formPartido = document.getElementById("form-partido")
  
    formPartido.addEventListener("submit", (e) => {
      e.preventDefault()
      const equipoLocal = document.getElementById("equipo-local").value
      const equipoVisitante = document.getElementById("equipo-visitante").value
      const fechaPartido = document.getElementById("fecha-partido").value
      const horaPartido = document.getElementById("hora-partido").value
  
      partidos.push({
        local: equipoLocal,
        visitante: equipoVisitante,
        fecha: fechaPartido,
        hora: horaPartido,
      })
      alert(`Partido entre "${equipoLocal}" y "${equipoVisitante}" creado con éxito.`)
      formPartido.reset()
    })
  })
  
  