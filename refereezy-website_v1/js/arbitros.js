document.addEventListener("DOMContentLoaded", () => {
  const arbitros = []
  const formArbitro = document.getElementById("form-arbitro")

  formArbitro.addEventListener("submit", (e) => {
    e.preventDefault()
    const nombreArbitro = document.getElementById("nombre-arbitro").value
    const edadArbitro = document.getElementById("edad-arbitro").value
    const nacionalidadArbitro = document.getElementById("nacionalidad-arbitro").value

    arbitros.push({
      nombre: nombreArbitro,
      edad: edadArbitro,
      nacionalidad: nacionalidadArbitro,
    })
    alert(`Árbitro "${nombreArbitro}" agregado con éxito.`)
    formArbitro.reset()
  })
})

