// Añadimos un poco de interactividad
document.addEventListener('DOMContentLoaded', function() {
    // Billing Toggle para la página de inicio
    const billingToggleHome = document.getElementById('billingToggleHome');
    if (billingToggleHome) {
        const billingOptions = document.querySelectorAll('.billing-option');
        const monthlyPrices = document.querySelectorAll('.monthly-price');
        const annualPrices = document.querySelectorAll('.annual-price');
        const purchaseButtons = document.querySelectorAll('.purchase-button-home');
        
        // URLs de pago simuladas
        const paymentUrls = {
            exceptional: {
                monthly: "https://ejemplo.com/pago/exceptional-mensual",
                annual: "https://ejemplo.com/pago/exceptional-anual"
            },
            enterprise: {
                monthly: "https://ejemplo.com/pago/enterprise-mensual",
                annual: "https://ejemplo.com/pago/enterprise-anual"
            }
        };
        
        // Gestionar cambio en el toggle
        billingToggleHome.addEventListener('change', function() {
            updatePriceDisplay();
        });
        
        // Manejar botones de compra en la página principal
        purchaseButtons.forEach(button => {
            button.addEventListener('click', function() {
                const plan = this.getAttribute('data-plan');
                const isAnnual = billingToggleHome.checked;
                const billingType = isAnnual ? 'annual' : 'monthly';
                
                // Redirigir a la URL correspondiente
                if (paymentUrls[plan] && paymentUrls[plan][billingType]) {
                    window.open(paymentUrls[plan][billingType], '_blank');
                }
            });
        });
        
        // Actualizar visualización de precios
        function updatePriceDisplay() {
            const isAnnual = billingToggleHome.checked;
            
            // Actualizar estilos de las opciones
            billingOptions.forEach((option, index) => {
                if ((index === 0 && !isAnnual) || (index === 1 && isAnnual)) {
                    option.style.opacity = '1';
                    option.style.color = 'var(--primary)';
                } else {
                    option.style.opacity = '0.7';
                    option.style.color = 'var(--text)';
                }
            });
            
            // Mostrar precios correspondientes
            monthlyPrices.forEach(price => {
                price.style.display = isAnnual ? 'none' : 'block';
            });
            annualPrices.forEach(price => {
                price.style.display = isAnnual ? 'block' : 'none';
            });
        }
        
        // Inicializar con el precio anual
        updatePriceDisplay();
    }

    // Ejemplo de notificación al hacer clic en algunos elementos
    const demoButtons = document.querySelectorAll('.demo-actions button');
    demoButtons.forEach(button => {
        button.addEventListener('click', function() {
            showNotification('Esto es solo una demostración. Regístrate para acceder a la funcionalidad completa.', 'info');
        });
    });

    // Añadir un poco de animación al scroll
    const animateOnScroll = () => {
        const elements = document.querySelectorAll('.feature-card, .demo-container, .plan-card');
        
        elements.forEach(element => {
            const elementPosition = element.getBoundingClientRect().top;
            const screenPosition = window.innerHeight / 1.2;
            
            if (elementPosition < screenPosition) {
                element.style.opacity = '1';
                element.style.transform = 'translateY(0)';
            }
        });
    };

    // Aplicar estilos iniciales para la animación
    const prepareAnimation = () => {
        const elements = document.querySelectorAll('.feature-card, .demo-container, .plan-card');
        
        elements.forEach(element => {
            element.style.opacity = '0';
            element.style.transform = 'translateY(20px)';
            element.style.transition = 'opacity 0.6s ease-out, transform 0.6s ease-out';
        });
    };

    prepareAnimation();
    window.addEventListener('scroll', animateOnScroll);
    animateOnScroll(); // Ejecutar una vez al cargar para animar elementos visibles
});