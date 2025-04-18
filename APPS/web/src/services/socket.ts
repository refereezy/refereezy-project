import { Server, Socket } from "socket.io";
import { RefereePairing, Incident as SocketIncident } from "../types/socket";
import { getReportDetailById, addIncidentToReport } from "./firebase";
import { Incident } from "../types/firebase";

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
  // Listen for a request to get a specific report with all incident details
  socket.on("new-report", async (id: string) => {
    console.log(`Fetching report ${id} details`);
    try {
      const reportDetail = await getReportDetailById(id);
      if (reportDetail) {
        // Send back the report with all incidents populated with player information
        io.emit("report-updated", reportDetail);
        console.log(`Sent report ${id} with ${reportDetail.incidents.length} incidents`);
      } else {
        console.log(`Report ${id} not found`);
      }
    } catch (error) {
      console.error(`Error processing report ${id}:`, error);
    }
  }); 

  // Handle adding a new incident to a report
  socket.on("new-incident", async (data: { reportId: string, incident: Incident }) => {
    console.log(`New incident for report ${data.reportId}`, data.incident);
    try {
      // Convert from socket incident type to firebase incident type
      /* const firestoreIncident: Incident = {
        id: data.incident.id,
        description: data.incident.description,
        minute: data.incident.minute,
        type: data.incident.type,
        player: data.incident.player
      }; */
      
      // Add incident to the report's incidents subcollection
      const addedIncident = await addIncidentToReport(data.reportId, data.incident);
      
      // Get the updated report with all incidents
      const updatedReport = await getReportDetailById(data.reportId);
      
      if (updatedReport) {
        // Broadcast to all clients that the report has been updated
        io.emit("report-updated", updatedReport);
        // Also emit the specific incident that was added
        io.emit("incident-added", {
          reportId: data.reportId,
          incident: addedIncident
        });
        
        console.log(`Added incident ${addedIncident.id} to report ${data.reportId}`);
      }
    } catch (error) {
      console.error(`Error adding incident to report ${data.reportId}:`, error);
    }
  });
}

export default io;
