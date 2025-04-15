// This file is part of the WebSocket client for the live data feed.
// It connects to the server and listens for incoming messages.

// check if the io function is defined
if (typeof io === "undefined") {
  console.error("Socket.IO client library not loaded.");
} else {

    // create a socket connection to the server
  const socket = io("http://localhost:3000", {
    autoConnect: false,
    transports: ["websocket"],
  });


  // connect to the server
  socket.connect();

  // sets up the socket event listeners
  initializeSocket(socket);

}



