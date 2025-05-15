document.addEventListener("DOMContentLoaded", () => {
  const formLogin = document.getElementById("form-login")

  formLogin.addEventListener("submit", (e) => {
    e.preventDefault()
    const email = document.getElementById("email").value
    const password = document.getElementById("password").value

    // Here you would typically send a request to your server to authenticate the user
    // For this example, we'll just show an alert
    alert(`Intento de inicio de sesión con email: ${email}`)
    formLogin.reset()
  })
})

