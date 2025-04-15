function initializeSocket(socket) {

  socket.on("connect", () => {
    console.log("Connected to server.");
  });

  socket.on("disconnect", () => {
    console.log("Disconnected from server.");
  });

  // todo: events for updating match reports


}