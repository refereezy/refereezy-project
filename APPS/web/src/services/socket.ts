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
  socket.on("register", (clockCode: unknown) => {
    // Validate clock code
    const validCode = validateClockCode(clockCode);
    if (!validCode) {
      console.log(`Invalid clock code format: ${clockCode}`);
      socket.emit("register-error", "Invalid clock code format");
      return;
    }
    
    console.log(`Clock code received: ${validCode}`);
    
    // Check if clock is already registered
    if (clockSockets[validCode] && clockSockets[validCode].socketId !== socket.id) {
      console.log(`Clock with code ${validCode} is already registered with different socket`);
      socket.emit("register-error", "This code is already in use");
      return;
    }
    
    // Register or update clock
    clockSockets[validCode] = {
      socketId: socket.id,
      status: 'available'
    };
    
    socket.emit("register-success", validCode);
    console.log(`Clock ${validCode} registered successfully with socket ${socket.id}`);
  });
  socket.on("validate-code", (code: unknown, pairingData: unknown) => {
    // Validate clock code
    const validCode = validateClockCode(code);
    if (!validCode) {
      console.log(`Invalid clock code format: ${code}`);
      socket.emit("clock-not-online");
      return;
    }
    // Parse pairing data if it's in string format like "RefereeLoad(id=1, token=xxx)"
    let validPairingData: RefereePairing;
    
    if (typeof pairingData === 'string') {
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
    }
    
    console.log(`Validating clock code: ${validCode}`);
    const clockRegistry = clockSockets[validCode];
    
    if (!clockRegistry) {
      socket.emit("clock-not-online");
      console.log(`Clock code is not registered: ${validCode}`);
      return;
    }
    
    if (clockRegistry.status !== 'available') {
      socket.emit("clock-busy");
      console.log(`Clock ${validCode} is busy with status: ${clockRegistry.status}`);
      return;
    }
    
    // Update clock status to paired
    clockRegistry.status = 'paired';
    clockRegistry.linkedDeviceId = socket.id;
    
    // Emit to clock
    io.to(clockRegistry.socketId).emit("pair", validPairingData);
    console.log(`Pairing data sent to clock ${validCode}`);
    
    socket.emit("pair-ok");
  });
    // Handle clock confirmation of pairing
  socket.on("pair-confirmed", (code: unknown) => {
    const validCode = validateClockCode(code);
    if (!validCode) {
      console.log(`Invalid clock code format: ${code}`);
      return;
    }
    
    console.log(`Clock ${validCode} confirmed pairing`);
    if (clockSockets[validCode] && clockSockets[validCode].socketId === socket.id) {
      // Notify the linked device that the clock has confirmed pairing
      if (clockSockets[validCode].linkedDeviceId) {
        io.to(clockSockets[validCode].linkedDeviceId).emit("clock-confirmed-pairing", { clockCode: validCode });
        console.log(`Notified device ${clockSockets[validCode].linkedDeviceId} that clock ${validCode} confirmed pairing`);
      }
    }
  });

  socket.on("unregister", (code: unknown) => {
    const validCode = validateClockCode(code);
    if (!validCode) {
      console.log(`Invalid clock code format: ${code}`);
      socket.emit("unregister-error", "Invalid clock code format");
      return;
    }
    
    console.log(`Unregistering clock code: ${validCode}`);
    // Check if clock exists and the request is from the registered socket
    if (clockSockets[validCode]) {
      if (clockSockets[validCode].socketId !== socket.id) {
        console.log(`Unauthorized unregister attempt for clock ${validCode}`);
        socket.emit("unregister-error", "Not authorized");
        return;
      }
      
      // Notify linked device if any
      if (clockSockets[validCode].linkedDeviceId) {
        io.to(clockSockets[validCode].linkedDeviceId).emit("clock-unregistered", { clockCode: validCode });
      }
      
      delete clockSockets[validCode];
      socket.emit("unregister-success");
      console.log(`Removed clock code: ${validCode}`);
    } else {
      console.log(`Clock code not found: ${validCode}`);
      socket.emit("unregister-error", "Clock not found");
    }
  });

  socket.on("report-received", (code: unknown, reportId: unknown) => {
    const validCode = validateClockCode(code);
    if (!validCode) {
      console.log(`Invalid clock code format: ${code}`);
      return;
    }
    
    const validReportId = validateReportId(reportId);
    if (!validReportId) {
      console.log(`Invalid report ID format: ${reportId}`);
      return;
    }
    
    console.log(`Clock ${validCode} confirmed receipt of report ${validReportId}`);
    if (clockSockets[validCode] && clockSockets[validCode].socketId === socket.id) {
      // Update status
      clockSockets[validCode].status = 'working';
      
      // Notify the linked device that the clock has received the report
      if (clockSockets[validCode].linkedDeviceId) {
        io.to(clockSockets[validCode].linkedDeviceId).emit("clock-confirmed-report", { 
          clockCode: validCode, 
          reportId: validReportId,
          status: 'working'
        });
        console.log(`Notified device ${clockSockets[validCode].linkedDeviceId} that clock ${validCode} is working on report ${validReportId}`);
      }
    }
  });
  
  socket.on("work-completed", (code: unknown, reportId: unknown) => {
    const validCode = validateClockCode(code);
    if (!validCode) {
      console.log(`Invalid clock code format: ${code}`);
      return;
    }
    
    const validReportId = validateReportId(reportId);
    if (!validReportId) {
      console.log(`Invalid report ID format: ${reportId}`);
      return;
    }
    
    console.log(`Clock ${validCode} completed work on report ${validReportId}`);
    if (clockSockets[validCode] && clockSockets[validCode].socketId === socket.id) {
      // Update status
      clockSockets[validCode].status = 'available';
      const linkedDeviceId = clockSockets[validCode].linkedDeviceId;
      
      // Clear report and linked device
      clockSockets[validCode].reportId = undefined;
      clockSockets[validCode].linkedDeviceId = undefined;
      
      // Notify the previously linked device that the clock has finished work
      if (linkedDeviceId) {
        io.to(linkedDeviceId).emit("clock-work-completed", { 
          clockCode: validCode, 
          reportId: validReportId,
          status: 'available'
        });
        console.log(`Notified device ${linkedDeviceId} that clock ${validCode} has finished work on report ${validReportId}`);
      }
    }
  });

  socket.on("check-clock-status", (code: unknown) => {
    const validCode = validateClockCode(code);
    if (!validCode) {
      console.log(`Invalid clock code format: ${code}`);
      socket.emit("clock-status", {
        clockCode: typeof code === 'string' ? code : 'unknown',
        online: false,
        message: "Invalid clock code format"
      });
      return;
    }
    
    console.log(`Checking status for clock code: ${validCode}`);
    const clockRegistry = clockSockets[validCode];
    
    if (!clockRegistry) {
      socket.emit("clock-status", { 
        clockCode: validCode,
        online: false,
        message: "Clock is not registered"
      });
      return;
    }
    
    socket.emit("clock-status", { 
      clockCode: validCode,
      online: true,
      status: clockRegistry.status,
      reportId: clockRegistry.reportId,
      message: `Clock is ${clockRegistry.status}`
    });
  });
}

function setUpReportsEvents(socket: Socket) {
  // Listen for a request to get a specific report with all incident details
  socket.on("new-report", async (id: unknown, code: unknown = null) => {
    // Validate report ID
    if (typeof id !== 'string') {
      console.log(`Invalid report ID: ${id}`);
      socket.emit("report-error", { message: "Invalid report ID" });
      return;
    }
    
    console.log(`Fetching report ${id} details`);
    try {
      const reportDetail = await getReportDetailById(id);
      if (!reportDetail) {
        console.log(`Report ${id} not found`);
        socket.emit("report-error", { message: "Report not found" });
        return;
      }
        // Handle clock notification if code is provided
      if (code !== null) {
        const validCode = validateClockCode(code);
        if (!validCode) {
          console.log(`Invalid clock code format: ${code}`);
          socket.emit("report-error", { message: "Invalid clock code format" });
          return;
        }
        
        console.log(`Clock code received: ${validCode}`);
        const clockRegistry = clockSockets[validCode];
        
        if (!clockRegistry) {
          socket.emit("clock-not-online");
          console.log(`Clock code is not registered: ${validCode}`);
          return;
        }
          if (clockRegistry.status !== 'available' && clockRegistry.status !== 'paired') {
          socket.emit("clock-busy");
          console.log(`Clock ${validCode} is busy with status: ${clockRegistry.status}`);
          return;
        }
        
        // Update clock registry with working status and link to this device
        clockRegistry.status = 'working';
        clockRegistry.linkedDeviceId = socket.id;
        clockRegistry.reportId = id;
        
        // Emit to clock
        io.to(clockRegistry.socketId).emit("new-report", reportDetail.id);
        console.log(`Sent new report ${id} to clock ${validCode}`);
          
        // Inform the requesting device that clock has been notified
        socket.emit("clock-notified", { 
          clockCode: validCode, 
          status: 'working',
          reportId: id
        });
      }

      // Send back the report with all incidents populated with player information
      io.emit("report-updated", reportDetail);
      console.log(`Sent report ${id} with ${reportDetail.incidents.length} incidents`);
    } catch (error) {
      console.error(`Error processing report ${id}:`, error);
      socket.emit("report-error", { message: "Error fetching report" });
    }
  }); 
  // Handle adding a new incident to a report
  socket.on("new-incident", async (reportId: unknown, incidentData: unknown) => {
    // Validate report ID
    const validReportId = validateReportId(reportId);
    if (!validReportId) {
      console.log(`Invalid report ID format: ${reportId}`);
      socket.emit("report-error", { message: "Invalid report ID format" });
      return;
    }
    
    // Validate incident data
    const incident = validateIncident(incidentData);
    if (!incident) {
      console.log(`Invalid incident data`, incidentData);
      socket.emit("report-error", { message: "Invalid incident data" });
      return;
    }
    
    console.log(`New incident notification for report ${validReportId}`, incident);
    try {
      // Get the updated report with all incidents
      const updatedReport = await getReportDetailById(validReportId);
      
      if (updatedReport) {
        // Broadcast to all clients that the report has been updated
        io.emit("report-updated", updatedReport);
          // Also emit the specific incident that was added
        io.emit("incident-added", {
          reportId: validReportId,
          incident: incident
        });
        
        // Find any clocks working on this report and notify them
        const clocksWorkingOnThisReport = Object.entries(clockSockets)
          .filter(([_, registry]) => registry.reportId === validReportId && registry.status === 'working');
        
        for (const [clockCode, registry] of clocksWorkingOnThisReport) {
          io.to(registry.socketId).emit("report-incident-added", {
            reportId: validReportId,
            incident
          });
          console.log(`Notified clock ${clockCode} about new incident ${incident.id} for report ${validReportId}`);
        }
        
        console.log(`Notified about new incident ${incident.id} for report ${validReportId}`);
      } else {
        socket.emit("report-error", { message: "Report not found" });
      }    } catch (error) {
      console.error(`Error notifying about incident for report ${validReportId}:`, error);
      socket.emit("report-error", { message: "Error processing incident" });
    }
  });
}

// Validation functions to ensure data integrity
function validateClockCode(code: unknown): string | null {
  if (typeof code !== 'string') {
    return null;
  }

  // Enforce clock code format (alphanumeric, 50 chars)
  const validCodePattern = /^[a-zA-Z0-9]{50}$/;
  return validCodePattern.test(code) ? code : null;
}

function validateReportId(id: unknown): string | null {
  if (typeof id !== 'string') {
    return null;
  }
  
  // Simple validation for report ID (alphanumeric with possible special chars)
  const validIdPattern = /^.{6,}/;
  return validIdPattern.test(id) ? id : null;
}

function validateIncident(incident: unknown): Incident | null {
  if (!incident || typeof incident !== 'object') {
    return null;
  }
  
  // Type casting to access properties
  const inc = incident as Partial<SocketIncident>; // Using the socket.ts Incident type
  
  // Basic validation for required fields
  if (
    inc.id === undefined || 
    typeof inc.reportId !== 'string' || 
    typeof inc.description !== 'string' || 
    typeof inc.minute !== 'number' ||
    typeof inc.type !== 'string'
  ) {
    return null;
  }
  
  // Convert from SocketIncident to Incident (firebase type)
  return {
    id: inc.id,
    description: inc.description,
    minute: inc.minute,
    type: inc.type,
    player: inc.player
  } as Incident;
}

export default io;
