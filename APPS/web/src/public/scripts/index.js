// Este archivo es parte del cliente WebSocket para el feed de datos en vivo.
// Se conecta al servidor y escucha los mensajes entrantes.

// verificar si la función io está definida
if (typeof io === "undefined") {
  console.error("Biblioteca cliente Socket.IO no cargada.");
} else {

    // crear una conexión socket al servidor
  const socket = io(`http://${window.location.hostname}:3000`, {
    autoConnect: false,
    transports: ["websocket"],
  });


  // conectar al servidor
  socket.connect();

  // configura los event listeners del socket
  // esta función debe declararse antes de ejecutar el archivo index.js, tal vez dentro de socket.js
  initializeSocket(socket);

}





