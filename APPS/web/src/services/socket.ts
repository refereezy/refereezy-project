import { Server, Socket } from "socket.io";
import { RefereePairing } from "../types/socket";


const io = new Server({
  cors: {
    origin: "*", 
    methods: ["GET", "POST"],
  },
});

// dictionary to store clockCode and socket id
const clockSockets: { [key: string]: string } = {};

io.on("connection", (socket) => {
  console.log(`New connection: ${socket.id}`);

  setUpClockEvents(socket);
  setUpReportsEvents(socket);

  socket.on("disconnect", () => {
    console.log(`Disconnected: ${socket.id}`);
  });


}); // end of io.on("connection")



function setUpClockEvents(socket: Socket) {

  socket.on("register", (clockCode: string) => {
    console.log(`Clock code received: ${clockCode}`);
    clockSockets[clockCode] = socket.id; // storing clockCode and socket id
  });

  socket.on("validate-clockCode", (pairingData: RefereePairing) => {
    console.log(`Validating clock code: ${pairingData.clockCode}`);
    const socketId = clockSockets[pairingData.clockCode];
    if (socketId) {
      // emits to that clockSocket that its being paired with the referee
      io.to(socketId).emit("pair", pairingData);
    } else {
      socket.emit("clock-not-online");
      console.log(`Clock code is not registered: ${pairingData.clockCode}`);
    }

  });

  socket.on("disonnnect", () => {

    // checks if the socket id is in the clockCode dictionary and removes it
    const clockCode = Object.keys(clockSockets).find(key => clockSockets[key] === socket.id);
    if (clockCode) {
      delete clockSockets[clockCode];
      console.log(`Removed clock code: ${clockCode}`);
    }

  })


}


function setUpReportsEvents(socket: Socket) {

  socket.on("new-report", (id: string) => {
    console.log("New report event received");
    // todo: emit the right data
  }); 

  socket.on("new-incident", (id: string) => {
    console.log("New incident event received");
    // todo: emit the right data
  });

}

export default io;
