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

  setUpPairingEvents(socket);
  setUpReportsEvents(socket);

  socket.on("disconnect", () => {
    console.log(`Disconnected: ${socket.id}`);

    // checks if the socket id is in the clockCode dictionary and removes it
    const clockCode = Object.keys(clockSockets).find(key => clockSockets[key] === socket.id);
    if (clockCode) {
      delete clockSockets[clockCode];
      console.log(`Removed clock code: ${clockCode}`);
    }

  });


}); // end of io.on("connection")



function setUpPairingEvents(socket: Socket) {

  socket.on("register", (clockCode: string) => {
    console.log(`Clock code received: ${clockCode}`);
    clockSockets[clockCode] = socket.id; // storing clockCode and socket id
  });

  socket.on("validate-code", (code: string, pairingData: RefereePairing) => {
    console.log(`Validating clock code: ${code}`);
    const socketId = clockSockets[code];
    if (socketId) {
      // emits to that clockSocket that its being paired with the referee
      io.to(socketId).emit("pair", pairingData);
      socket.emit("pair-ok");
    } else {
      socket.emit("clock-not-online");
      console.log(`Clock code is not registered: ${code}`);
    }

  });

  socket.on("unregister", (clockCode: string) => {
    console.log(`Unregistering clock code: ${clockCode}`);
    // checks if the socket id is in the clockCode dictionary and removes it
    if (clockSockets[clockCode]) {
      delete clockSockets[clockCode];
      console.log(`Removed clock code: ${clockCode}`);
    } else {
      console.log(`Clock code not found: ${clockCode}`);
    }
  });


  


}

function setUpReportsEvents(socket: Socket) {
  // Listen for a request to get a specific report with all incident details
  socket.on("new-report", async (id: string, code: string | null = null) => {
    

    console.log(`Fetching report ${id} details`);
    try {
      const reportDetail = await getReportDetailById(id);
      if (reportDetail) {
        
        if (code) {
          console.log(`Clock code received: ${code}`);
          const socketId = clockSockets[code]; // storing clockCode and socket id
          if (socketId) {
            // emits to that clockSocket that its being paired with the referee
            
            io.to(socketId).emit("new-report", reportDetail.id);
            socket.emit("clock-notified");
          } else {
            socket.emit("clock-not-online");
            console.log(`Clock code is not registered: ${code}`);
          }
        }

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
  socket.on("new-incident", async (reportId: string, incident: Incident) => {
    console.log(`New incident for report ${reportId}`, incident);
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
      const addedIncident = await addIncidentToReport(reportId, incident);
      
      // Get the updated report with all incidents
      const updatedReport = await getReportDetailById(reportId);
      
      if (updatedReport) {
        // Broadcast to all clients that the report has been updated
        io.emit("report-updated", updatedReport);
        // Also emit the specific incident that was added
        io.emit("incident-added", {
          reportId: reportId,
          incident: addedIncident
        });
        
        console.log(`Added incident ${addedIncident.id} to report ${reportId}`);
      }
    } catch (error) {
      console.error(`Error adding incident to report ${reportId}:`, error);
    }
  });
}

export default io;
