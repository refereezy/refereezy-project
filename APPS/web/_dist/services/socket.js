"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const socket_io_1 = require("socket.io");
const io = new socket_io_1.Server({
    cors: {
        origin: "*",
        methods: ["GET", "POST"],
    },
});
const clockSockets = {};
io.on("connection", (socket) => {
    console.log(`New connection: ${socket.id}`);
    setUpClockEvents(socket);
    setUpReportsEvents(socket);
    socket.on("disconnect", () => {
        console.log(`Disconnected: ${socket.id}`);
    });
});
function setUpClockEvents(socket) {
    socket.on("register", (clockCode) => {
        console.log(`Clock code received: ${clockCode}`);
        clockSockets[clockCode] = socket.id;
    });
    socket.on("validate-clockCode", (pairingData) => {
        console.log(`Validating clock code: ${pairingData.clockCode}`);
        const socketId = clockSockets[pairingData.clockCode];
        if (socketId) {
            io.to(socketId).emit("pair", pairingData);
        }
        else {
            socket.emit("clock-not-online");
            console.log(`Clock code is not registered: ${pairingData.clockCode}`);
        }
    });
    socket.on("disonnnect", () => {
        const clockCode = Object.keys(clockSockets).find(key => clockSockets[key] === socket.id);
        if (clockCode) {
            delete clockSockets[clockCode];
            console.log(`Removed clock code: ${clockCode}`);
        }
    });
}
function setUpReportsEvents(socket) {
    socket.on("new-report", (id) => {
        console.log("New report event received");
    });
    socket.on("new-incident", (id) => {
        console.log("New incident event received");
    });
}
exports.default = io;
//# sourceMappingURL=socket.js.map