document.addEventListener("DOMContentLoaded", () => {
  const jugadores = []
  const formJugador = document.getElementById("form-jugador")

  formJugador.addEventListener("submit", (e) => {
    e.preventDefault()
    const equipoJugador = document.getElementById("equipo-jugador").value
    const nombreJugador = document.getElementById("nombre-jugador").value
    const numeroJugador = document.getElementById("numero-jugador").value
    const edadJugador = document.getElementById("edad-jugador").value
    const nacionalidadJugador = document.getElementById("nacionalidad-jugador").value

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
})

