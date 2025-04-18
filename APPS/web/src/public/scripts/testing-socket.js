function initializeSocket(socket) {
  const connectionStatus = document.getElementById('connectionStatus');
  const eventLog = document.getElementById('eventLog');
  const registerStatus = document.getElementById('registerStatus');
  const validateStatus = document.getElementById('validateStatus');
  
  // UI Elements
  const clockCodeInput = document.getElementById('clockCodeInput');
  const registerBtn = document.getElementById('registerBtn');
  const validateCodeInput = document.getElementById('validateCodeInput');
  const refereeIdInput = document.getElementById('refereeIdInput');
  const validateBtn = document.getElementById('validateBtn');

  // Utility function to log events
  function logEvent(message) {
    const logEntry = document.createElement('div');
    logEntry.textContent = `${new Date().toLocaleTimeString()}: ${message}`;
    eventLog.prepend(logEntry);
  }

  // Connection events
  socket.on("connect", () => {
    console.log("Connected to server.");
    connectionStatus.textContent = "Connected";
    connectionStatus.className = "status success";
    logEvent("Connected to server");
  });

  socket.on("disconnect", () => {
    console.log("Disconnected from server.");
    connectionStatus.textContent = "Disconnected";
    connectionStatus.className = "status error";
    logEvent("Disconnected from server");
  });

  // Register clock code button handler
  registerBtn.addEventListener('click', () => {
    const clockCode = clockCodeInput.value.trim();
    
    if (!clockCode) {
      registerStatus.textContent = "Please enter a clock code";
      registerStatus.className = "status error";
      return;
    }

    socket.emit("register", clockCode);
    registerStatus.textContent = "Registration sent! Waiting for confirmation...";
    registerStatus.className = "status info";
    logEvent(`Registering clock code: ${clockCode}`);
  });

  // Validate clock code button handler
  validateBtn.addEventListener('click', () => {
    const clockCode = validateCodeInput.value.trim();
    const refereeId = refereeIdInput.value.trim();
    
    if (!clockCode || !refereeId) {
      validateStatus.textContent = "Please enter both clock code and referee ID";
      validateStatus.className = "status error";
      return;
    }

    const pairingData = {
      clockCode: clockCode,
      refereeId: refereeId
    };

    socket.emit("validate-clockCode", pairingData);
    validateStatus.textContent = "Validation request sent! Waiting for response...";
    validateStatus.className = "status info";
    logEvent(`Validating clock code: ${clockCode} with referee ID: ${refereeId}`);
  });

  // Response events
  socket.on("pair", (pairingData) => {
    logEvent(`Pairing successful with referee ID: ${pairingData.refereeId}`);
    registerStatus.textContent = `Paired with referee ID: ${pairingData.refereeId}`;
    registerStatus.className = "status success";
  });

  socket.on("clock-not-online", () => {
    logEvent("Clock validation failed: Clock is not online");
    validateStatus.textContent = "Clock is not online or code is invalid";
    validateStatus.className = "status error";
  });

  // Add events for updating match reports
  socket.on("report-updated", (data) => {
    logEvent(`Report updated: ${JSON.stringify(data)}`);
  });

  socket.on("incident-added", (data) => {
    logEvent(`New incident added: ${JSON.stringify(data)}`);
  });
}