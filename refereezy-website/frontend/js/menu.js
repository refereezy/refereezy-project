/**
 * menu.js - Script para manejar la funcionalidad del menú hamburguesa
 */

document.addEventListener('DOMContentLoaded', function() {
    // Referencias a elementos del DOM
    const hamburgerBtn = document.querySelector('.hamburger-menu');
    const mobileNav = document.querySelector('.mobile-nav');
    
    // Añadir listener para el botón de hamburguesa
    if (hamburgerBtn) {
        hamburgerBtn.addEventListener('click', function() {
            hamburgerBtn.classList.toggle('active');
            mobileNav.classList.toggle('active');
            // Prevenir scroll cuando el menú está abierto
            document.body.classList.toggle('menu-open');
        });
    }
    
    // Cerrar el menú al hacer clic en un enlace
    const mobileNavLinks = document.querySelectorAll('.mobile-nav-links a');
    if (mobileNavLinks) {
        mobileNavLinks.forEach(link => {
            link.addEventListener('click', function() {
                hamburgerBtn.classList.remove('active');
                mobileNav.classList.remove('active');
                document.body.classList.remove('menu-open');
            });
        });
    }
    
    // Cerrar el menú al hacer clic fuera de él
    document.addEventListener('click', function(event) {
        if (mobileNav && mobileNav.classList.contains('active')) {
            // Si el clic no es en el menú ni en el botón hamburguesa
            if (!mobileNav.contains(event.target) && !hamburgerBtn.contains(event.target)) {
                hamburgerBtn.classList.remove('active');
                mobileNav.classList.remove('active');
                document.body.classList.remove('menu-open');
            }
        }
    });
});
