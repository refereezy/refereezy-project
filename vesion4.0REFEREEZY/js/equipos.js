document.addEventListener("DOMContentLoaded", () => {
  const equipos = []
  const formEquipo = document.getElementById("form-equipo")

  formEquipo.addEventListener("submit", (e) => {
    e.preventDefault()
    const nombreEquipo = document.getElementById("nombre-equipo").value
    const logoEquipo = document.getElementById("logo-equipo").files[0]
    equipos.push({ nombre: nombreEquipo, logo: logoEquipo })
    alert(`Equipo "${nombreEquipo}" agregado con éxito.`)
    formEquipo.reset()
  })
})

