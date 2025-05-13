import { Server, Socket } from "socket.io";
import { RefereePairing, ClockStatus, Incident as SocketIncident } from "../types/socket";
import { getReportDetailById, addIncidentToReport } from "./firebase";
import { Incident } from "../types/firebase";

// Enhanced configuration for socket.io server
const io = new Server({
  cors: {
    origin: "*", 
    methods: ["GET", "POST"],
  },
  // Connection rate limiting
  connectTimeout: 45000, // 45 seconds connection timeout
  pingTimeout: 30000,    // 30 seconds ping timeout
  pingInterval: 10000,   // ping every 10 seconds
  maxHttpBufferSize: 1e6, // 1MB max payload size
});

// Connection tracking
const connectionsByIP = new Map<string, number>();
const MAX_CONNECTIONS_PER_IP = 10;

// Socket middleware for monitoring connections
io.use((socket, next) => {
  const clientIp = socket.handshake.address;
  
  // Simple rate limiting by IP
  const currentConnections = connectionsByIP.get(clientIp) || 0;
  if (currentConnections >= MAX_CONNECTIONS_PER_IP) {
    console.log(`Connection rejected - too many connections from ${clientIp}`);
    return next(new Error("Too many connections"));
  }
  
  // Increment connection counter
  connectionsByIP.set(clientIp, currentConnections + 1);
  
  // Decrement on disconnect
  socket.on("disconnect", () => {
    const connections = connectionsByIP.get(clientIp) || 1;
    connectionsByIP.set(clientIp, connections - 1);
    if (connections - 1 <= 0) {
      connectionsByIP.delete(clientIp);
    }
  });
  
  console.log(`New connection from ${clientIp}, current count: ${currentConnections + 1}`);
  next();
});

// Enhanced clock registry to store more information about each clock
interface ClockRegistry {
  socketId: string;
  status: 'available' | 'working' | 'paired';
  linkedDeviceId?: string; // Socket ID of the device linked to this clock
  reportId?: string; // Current report ID the clock is working with
}

const clockSockets: { [key: string]: ClockRegistry } = {};
export { clockSockets }

io.on("connection", (socket) => {
  console.log(`New connection: ${socket.id}`);

  setUpPairingEvents(socket);
  setUpReportsEvents(socket);

  socket.on("disconnect", () => {
    console.log(`Disconnected: ${socket.id}`);

    // checks if the socket id is in the clockCode dictionary and removes it
    const clockCode = Object.keys(clockSockets).find(key => clockSockets[key].socketId === socket.id);
    if (clockCode) {
      // Notify the linked device that the clock is no longer available
      if (clockSockets[clockCode].linkedDeviceId) {
        io.to(clockSockets[clockCode].linkedDeviceId).emit("clock-disconnected", { 
          clockCode,
          reportId: clockSockets[clockCode].reportId
        });
        console.log(`Notified device ${clockSockets[clockCode].linkedDeviceId} about disconnection of clock ${clockCode}`);
      }
      
      delete clockSockets[clockCode];
      console.log(`Removed clock code: ${clockCode}`);
    }

  });


}); // end of io.on("connection")



function setUpPairingEvents(socket: Socket) {
  //* REGISTER
  socket.on("register", (clockCode: unknown) => {
    // Validate clock code
    if (!validateClockCode(clockCode)) {
      console.log(`Invalid clock code format: ${clockCode}`);
      socket.emit("register-error", "Invalid clock code format");
      return;
    }
    
    console.log(`Clock code received: ${clockCode}`);
    
    // Check if clock is already registered
    if (clockSockets[clockCode] && clockSockets[clockCode].socketId !== socket.id) {
      console.log(`Clock with code ${clockCode} is already registered with different socket`);
      socket.emit("register-error", "This code is already in use");
      return;
    }
    
    // Register or update clock
    clockSockets[clockCode] = {
      socketId: socket.id,
      status: 'available'
    };
    
    socket.emit("register-success", clockCode);
    console.log(`Clock ${clockCode} registered successfully with socket ${socket.id}`);
  });
  //* PAIR CODE (FROM MOBILE)
  socket.on("pair-code", (code: unknown, rawPairingData: unknown) => {
    // Validate clock code
    if (!validateClockCode(code)) {
      console.log(`Invalid clock code: ${code}`);
      socket.emit("pair-error", "Invalid clock code");
      return;
    }
    
    let pairingData: unknown;
    //let validPairingData: RefereePairing;

    // if raw is string, parse it as JSON
    if (typeof rawPairingData === 'string') {
      // parse json
      try {
        pairingData = JSON.parse(rawPairingData);
      } catch (error) {
        console.log(`Error parsing pairing data: ${error}`);
        socket.emit("pair-error", "Invalid pairing data");
        return;
      }
    }


    if (!validatePairingData(pairingData)) {
      console.log(`Invalid pairing data format: ${pairingData}`);
      socket.emit("pair-error", "Invalid pairing data");
      return;
    }

    /* if (typeof pairingData === 'string') {
      const match = pairingData.match(/[a-zA-Z]+\(id=(\d+), token=([^)]+)\)/);
      if (match) {
        validPairingData = {
          id: parseInt(match[1], 10),
          token: match[2]
        };
        console.log(`Parsed pairing data from string: id=${validPairingData.id}`);
      } else {
        console.log(`Invalid pairing data string format: ${pairingData}`);
        socket.emit("pair-error", "Invalid pairing data format");
        return;
      }
    } else if (pairingData && typeof pairingData === 'object' && 
          'id' in pairingData && 'token' in pairingData &&
          typeof (pairingData as RefereePairing).id === 'number' && 
          typeof (pairingData as RefereePairing).token === 'string') {
      validPairingData = pairingData as RefereePairing;
    } else {
      console.log(`Invalid pairing data format: ${pairingData}`);
      socket.emit("pair-error", "Invalid pairing data");
      return;
    } */
    
    console.log(`Validating clock code: ${code}`);
    const clockRegistry = clockSockets[code];
    
    if (!clockRegistry) {
      socket.emit("pair-error", "Clock not online");
      console.log(`Clock code is not registered: ${code}`);
      return;
    }
    
    if (clockRegistry.status !== 'available') {
      socket.emit("pair-error", `Clock is already paired`);
      console.log(`Clock ${code} is busy with status: ${clockRegistry.status}`);
      return;
    }
    
    // Update clock status to paired
    clockRegistry.status = 'paired';
    clockRegistry.linkedDeviceId = socket.id;
    
    // Emit to clock
    io.to(clockRegistry.socketId).emit("pair", pairingData);
    console.log(`Pairing data sent to clock ${code}`);
    
  });

  //* PAIR CONFIRMATION (FROM CLOCK)
  socket.on("pair-confirmed", (code: unknown) => {
    if (!validateClockCode(code)) {
      console.log(`Invalid clock code format: ${code}`);
      return;
    }
    
    console.log(`Clock ${code} confirmed pairing`);
    const clockRegistry = clockSockets[code];
    if (clockRegistry && clockRegistry.socketId === socket.id) {
      // Notify the linked device that the clock has confirmed pairing
      if (clockRegistry.linkedDeviceId) {
        io.to(clockRegistry.linkedDeviceId).emit("pair-ok");
        console.log(`Notified device ${clockRegistry.linkedDeviceId} that clock ${code} confirmed pairing`);
      }
    }
  });
  //* UNREGISTER CLOCK AND UNPAIR
  socket.on("unregister", (code: unknown) => {

    if (!validateClockCode(code)) {
      console.log(`Invalid clock code format: ${code}`);
      socket.emit("unregister-error", "Invalid clock code format");
      return;
    }
    
    console.log(`Unregistering clock code: ${code}`);
    // Check if clock exists and the request is from the registered socket
    if (clockSockets[code]) {
      if (clockSockets[code].socketId !== socket.id) {
        console.log(`Unauthorized unregister attempt for clock ${code}`);
        socket.emit("unregister-error", "Not authorized");
        return;
      }
      
      // Notify linked device if any
      if (clockSockets[code].linkedDeviceId) {
        io.to(clockSockets[code].linkedDeviceId).emit("clock-not-online");
      }
      
      delete clockSockets[code];
      socket.emit("unregister-success");
      console.log(`Removed clock code: ${code}`);

    } else {
      console.log(`Clock code not found: ${code}`);
      socket.emit("unregister-error", "Clock not found");
    }
  });

  socket.on("check-clock-status", (code: unknown) => {

    if (!validateClockCode(code)) {
      console.log(`Invalid clock code format: ${code}`);
      socket.emit("clock-status", {
        clockCode: typeof code === 'string' ? code : 'unknown',
        online: false,
        message: "Invalid clock code format"
      });
      return;
    }
    
    console.log(`Checking status for clock code: ${code}`);
    const clockRegistry = clockSockets[code];
    
    if (!clockRegistry) {
      socket.emit("clock-status", { 
        clockCode: code,
        online: false,
        message: "Clock is not registered"
      });
      return;
    }
    
    socket.emit("clock-status", { 
      clockCode: code,
      online: true,
      status: clockRegistry.status,
      reportId: clockRegistry.reportId,
      message: `Clock is ${clockRegistry.status}`
    });
  });
}

function setUpReportsEvents(socket: Socket) {

  //* REPORT CREATED (FROM MOBILE)
  socket.on("new-report", async (id: string, code: string) => {
    
    console.log(`Fetching report ${id} details`);
    try {
      const reportDetail = await getReportDetailById(id);
      if (!reportDetail) {
        console.log(`Report ${id} not found`);
        socket.emit("report-error", { message: "Report not found" });
        return;
      }
      
      const clockRegistry = clockSockets[code];
      
      if (!clockRegistry) {
        socket.emit("clock-not-online");
        console.log(`Clock code is not registered: ${code}`);
        return;
      }
        if (clockRegistry.status !== 'available' && clockRegistry.status !== 'paired') {
        socket.emit("clock-busy");
        console.log(`Clock ${code} is busy with status: ${clockRegistry.status}`);
        return;
      }
      
      // Update clock registry with working status and link to this device
      clockRegistry.linkedDeviceId = socket.id;
      
      // Emit to clock
      io.to(clockRegistry.socketId).emit("new-report", reportDetail.id);
      console.log(`Sent new report ${id} to clock ${code}`);

      // Send back the report with all incidents populated with player information
      io.emit("report-updated", reportDetail);
      console.log(`Sent report ${id} with ${reportDetail.incidents.length} incidents`);
    } catch (error) {
      console.error(`Error processing report ${id}:`, error);
      socket.emit("report-error", { message: "Error fetching report" });
    }
  }); 

  //* REPORT RECEIVED (FROM CLOCK)
  socket.on("report-received", (code: string, reportId: string) => {
        
    console.log(`Clock ${code} confirmed receipt of report ${reportId}`);
    if (clockSockets[code] && clockSockets[code].socketId === socket.id) {
      // Update status
      clockSockets[code].status = 'working';
      clockSockets[code].reportId = reportId;
      
      // Notify the linked device that the clock has received the report
      if (clockSockets[code].linkedDeviceId) {
        io.to(clockSockets[code].linkedDeviceId).emit("clock-notified");
        console.log(`Notified device ${clockSockets[code].linkedDeviceId} that clock ${code} is working on report ${reportId}`);
      }
    }
  });

  //* REPORT DONE (FROM CLOCK)
  socket.on("report-done", (code: string, reportId: string) => {
        
    console.log(`Clock ${code} completed work on report ${reportId}`);
    if (clockSockets[code] && clockSockets[code].socketId === socket.id) {
      // Update status
      clockSockets[code].status = 'paired';
      const linkedDeviceId = clockSockets[code].linkedDeviceId;
      
      // Clear report and linked device
      clockSockets[code].reportId = undefined;
      clockSockets[code].linkedDeviceId = undefined;
      
      // Notify the previously linked device that the clock has finished work
      if (linkedDeviceId) {
        io.to(linkedDeviceId).emit("clock-work-done");
        console.log(`Notified device ${linkedDeviceId} that clock ${code} has finished work on report ${reportId}`);
      }
    }
  });

  //* NEW INCIDENT (FROM DEVICE TO LIVE WEB)
  socket.on("new-incident", async (reportId: unknown, rawIncidentData: unknown) => {
    // Validate report ID
    const validReportId = validateReportId(reportId);
    if (!validReportId) {
      console.log(`Invalid report ID format: ${reportId}`);
      socket.emit("report-error", "Invalid report ID format");
      return;
    }

    let incidentData : unknown;

    // Parse raw incident data
    if (typeof rawIncidentData === 'string') {
      try {
        incidentData = JSON.parse(rawIncidentData);
      } catch (error) {
        console.error(`Error parsing incident data for report ${validReportId}:`, error);
        socket.emit("report-error", "Error parsing incident data");
        return;
      }
  }


    // Validate incident data
    if (!validateIncident(incidentData)) {
      console.log(`Invalid incident data`, incidentData);
      socket.emit("report-error", "Invalid incident data");
      return;
    }
    
    console.log(`New incident notification for report ${validReportId}`, incidentData);
    try {
      // Get the updated report with all incidents
      const updatedReport = await getReportDetailById(validReportId);
      
      if (updatedReport) {
        // Broadcast to all clients that the report has been updated
        io.emit("report-updated", updatedReport);
          // Also emit the specific incident that was added
        io.emit("incident-added", {
          reportId: validReportId,
          incident: incidentData
        });
        
        // Find any clocks working on this report and notify them
        const clocksWorkingOnThisReport = Object.entries(clockSockets)
          .filter(([_, registry]) => registry.reportId === validReportId && registry.status === 'working');
        
        for (const [clockCode, registry] of clocksWorkingOnThisReport) {
          io.to(registry.socketId).emit("report-incident-added", {
            reportId: validReportId,
            incident: incidentData
          });
          console.log(`Notified clock ${clockCode} about new incident ${incidentData.id} for report ${validReportId}`);
        }
        
        console.log(`Notified about new incident ${incidentData.id} for report ${validReportId}`);
      } else {
        socket.emit("report-error", "Report not found");
      }
    } catch (error) {
      console.error(`Error notifying about incident for report ${validReportId}:`, error);
      socket.emit("report-error", "Error processing incident");
    }
  });
}

// Validation functions to ensure data integrity
function validateClockCode(code: unknown): code is string {
  // Enforce clock code format (alphanumeric, 50 chars)
  const validCodePattern = /^[a-zA-Z0-9]{50}$/;
  return typeof code === 'string' && 
          validCodePattern.test(code);
}

function validateReportId(id: unknown): string | null {
  if (typeof id !== 'string') {
    return null;
  }
  
  // Simple validation for report ID (alphanumeric with possible special chars)
  const validIdPattern = /^.{6,}/;
  return validIdPattern.test(id) ? id : null;
}

function validateIncident(incident: unknown): incident is Incident {
  if (!incident || typeof incident !== 'object') {
    return false;
  }
  
  // Type casting to access properties
  const inc = incident as Partial<SocketIncident>; // Using the socket.ts Incident type
  
  // Basic validation for required fields
  if (
    inc.id === undefined || 
    typeof inc.description !== 'string' || 
    typeof inc.minute !== 'number' ||
    typeof inc.type !== 'string'
  ) {
    return false;
  }

  return true;
  
}

function validatePairingData(pairingData: unknown): pairingData is RefereePairing {
  return (pairingData !== null && typeof pairingData === 'object' && 
          'id' in pairingData && 'token' in pairingData &&
          typeof (pairingData as RefereePairing).id === 'number' &&
          typeof (pairingData as RefereePairing).token === 'string');
}

export default io;
