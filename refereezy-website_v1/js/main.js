document.addEventListener("DOMContentLoaded", () => {
    // Load header
    const headerPlaceholder = document.getElementById("header-placeholder")
  
    if (headerPlaceholder) {
      fetch("components/header.html")
        .then((response) => {
          if (!response.ok) {
            throw new Error("Network response was not ok")
          }
          return response.text()
        })
        .then((data) => {
          headerPlaceholder.innerHTML = data
          // After header is loaded, update active navigation
          updateActiveNavItem()
        })
        .catch((error) => {
          console.error("Error loading header:", error)
          headerPlaceholder.innerHTML = "<p>Error loading header</p>"
        })
    }
  
    // Update active navigation item
    function updateActiveNavItem() {
      const currentPage = window.location.pathname.split("/").pop() || "index.html"
      const navLinks = document.querySelectorAll("nav a")
  
      navLinks.forEach((link) => {
        const href = link.getAttribute("href")
        if (href === currentPage) {
          link.style.textDecoration = "underline"
        }
      })
    }
  })
  
  