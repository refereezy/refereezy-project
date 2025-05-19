let socket;

document.addEventListener('DOMContentLoaded', function() {
  // Check authentication
  const clientId = getClientId();
  const loginSection = document.getElementById('login-section');
  const testingTools = document.getElementById('testing-tools');
  const loginForm = document.getElementById('loginForm');
  const logoutBtn = document.getElementById('logoutBtn');
  const logoutContainer = document.getElementById('logout-container');
  
  // Manage logout button visibility
  if (logoutContainer) {
    logoutContainer.style.display = clientId ? 'block' : 'none';
  }
  
  // Update connected devices count
  function updateConnectedDevicesCount() {
    const connectedDevices = document.getElementById('connectedDevices');
    if (connectedDevices) {
      fetch('/api/stats/devices')
        .then(response => response.json())
        .then(data => {
          connectedDevices.textContent = data.count || '0';
        })
        .catch(error => {
          console.error('Error fetching connected devices:', error);
          connectedDevices.textContent = 'Error';
        });
    }
  }
  
  // Update active matches count
  function updateActiveMatchesCount() {
    const activeMatchCount = document.getElementById('activeMatchCount');
    if (activeMatchCount) {
      fetch('/api/stats/matches')
        .then(response => response.json())
        .then(data => {
          activeMatchCount.textContent = data.count || '0';
        })
        .catch(error => {
          console.error('Error fetching active matches:', error);
          activeMatchCount.textContent = 'Error';
        });
    }
  }
    // Initialize the testing tools if user is authenticated
  if (clientId) {
    loginSection.classList.add('hidden');
    testingTools.classList.remove('hidden');
    
    // Mostrar el botón de logout
    if (logoutBtn && document.getElementById('logout-container')) {
      document.getElementById('logout-container').style.display = 'block';
    }
    
    // Connect to socket server
    connectSocket();
    
    // Update stats every 10 seconds
    updateConnectedDevicesCount();
    updateActiveMatchesCount();
    setInterval(() => {
      updateConnectedDevicesCount();
      updateActiveMatchesCount();
    }, 10000);
  } else {
    loginSection.classList.remove('hidden');
    testingTools.classList.add('hidden');
    
    // Ocultar el botón de logout
    if (document.getElementById('logout-container')) {
      document.getElementById('logout-container').style.display = 'none';
    }
  }
  
  // Login form submission
  loginForm.addEventListener('submit', async function(event) {
    event.preventDefault();
    
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value.trim();
    const loginStatus = document.getElementById('loginStatus');
    
    loginStatus.textContent = 'Iniciando sesión...';
    loginStatus.className = 'status info';
      const success = await handleLogin(email, password, loginStatus);
    if (success) {
      setTimeout(() => {
        loginSection.classList.add('hidden');
        testingTools.classList.remove('hidden');
        
        // Mostrar el botón de logout tras login exitoso
        if (logoutContainer) {
          logoutContainer.style.display = 'block';
        }
        
        connectSocket();
      }, 1000);
    }
  });
    // Logout button
  if (logoutBtn) {
    logoutBtn.addEventListener('click', function() {
      logout();
      
      // Ocultar el botón de logout tras cerrar sesión
      if (logoutContainer) {
        logoutContainer.style.display = 'none';
      }
    });
  }
});

function connectSocket() {
  // Create socket connection if not already connected
  if (!socket || !socket.connected) {
    socket = io(`http://${window.location.hostname}:3000`, {
      transports: ["websocket"]
    });
    
    // Initialize socket events
    initializeSocket(socket);
  }
}

function initializeSocket(socket) {
  const connectionStatus = document.getElementById('connectionStatus');
  const eventLog = document.getElementById('eventLog');
  const registerStatus = document.getElementById('registerStatus');
  const validateStatus = document.getElementById('validateStatus');
  const pairStatus = document.getElementById('pairStatus');
  const clockStatusResult = document.getElementById('clockStatusResult');
  const reportTimerStatus = document.getElementById('reportTimerStatus');
  const incidentStatus = document.getElementById('incidentStatus');
  
  // UI Elements
  const clockCodeInput = document.getElementById('clockCodeInput');
  const registerBtn = document.getElementById('registerBtn');
  const validateCodeInput = document.getElementById('validateCodeInput');
  const refereeIdInput = document.getElementById('refereeIdInput');
  const validateBtn = document.getElementById('validateBtn');
  const unregisterBtn = document.getElementById('unregisterBtn');
  const clockStatusBtn = document.getElementById('clockStatusBtn');
  const clockStatusInput = document.getElementById('clockStatusInput');
  const pairCodeInput = document.getElementById('pairCodeInput');
  const pairIdInput = document.getElementById('pairIdInput');
  const pairTokenInput = document.getElementById('pairTokenInput');
  const pairBtn = document.getElementById('pairBtn');
  const reportIdInput = document.getElementById('reportIdInput');
  const clockCodeForReportInput = document.getElementById('clockCodeForReportInput');
  const sendReportBtn = document.getElementById('sendReportBtn');
  const updateReportBtn = document.getElementById('updateReportBtn');
  const timerMinInput = document.getElementById('timerMinInput');
  const timerSecInput = document.getElementById('timerSecInput');
  const updateTimerBtn = document.getElementById('updateTimerBtn');
  const incidentReportIdInput = document.getElementById('incidentReportIdInput');
  const incidentTypeInput = document.getElementById('incidentTypeInput');
  const incidentMinuteInput = document.getElementById('incidentMinuteInput');
  const incidentDescInput = document.getElementById('incidentDescInput');
  const addIncidentBtn = document.getElementById('addIncidentBtn');
  
  // Utility function to log events
  function logEvent(message) {
    const logEntry = document.createElement('div');
    logEntry.textContent = `${new Date().toLocaleTimeString()}: ${message}`;
    eventLog.prepend(logEntry);
    
    // Keep only the last 50 log entries
    while (eventLog.children.length > 50) {
      eventLog.removeChild(eventLog.lastChild);
    }
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
  if (registerBtn) {
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
  }
  
  // Unregister clock code
  if (unregisterBtn) {
    unregisterBtn.addEventListener('click', () => {
      const clockCode = clockCodeInput.value.trim();
      
      if (!clockCode) {
        registerStatus.textContent = "Please enter a clock code";
        registerStatus.className = "status error";
        return;
      }

      socket.emit("unregister", clockCode);
      registerStatus.textContent = "Unregister request sent...";
      registerStatus.className = "status info";
      logEvent(`Unregistering clock code: ${clockCode}`);
    });
  }

  // Check clock status
  if (clockStatusBtn) {
    clockStatusBtn.addEventListener('click', () => {
      const clockCode = clockStatusInput.value.trim();
      
      if (!clockCode) {
        clockStatusResult.textContent = "Please enter a clock code";
        clockStatusResult.className = "status error";
        return;
      }

      socket.emit("check-clock-status", clockCode);
      clockStatusResult.textContent = "Checking status...";
      clockStatusResult.className = "status info";
      logEvent(`Checking status for clock code: ${clockCode}`);
    });
  }

  // Pair referee with clock
  if (pairBtn) {
    pairBtn.addEventListener('click', () => {
      const clockCode = pairCodeInput.value.trim();
      const id = parseInt(pairIdInput.value.trim());
      const token = pairTokenInput.value.trim();
      
      if (!clockCode || isNaN(id) || !token) {
        pairStatus.textContent = "Please enter all fields";
        pairStatus.className = "status error";
        return;
      }

      const pairingData = {
        id: id,
        token: token
      };

      socket.emit("pair-code", clockCode, JSON.stringify(pairingData));
      pairStatus.textContent = "Pairing request sent! Waiting for response...";
      pairStatus.className = "status info";
      logEvent(`Pairing clock code: ${clockCode} with referee ID: ${id}`);
    });
  }

  // Send report to clock
  if (sendReportBtn) {
    sendReportBtn.addEventListener('click', () => {
      const reportId = reportIdInput.value.trim();
      const clockCode = clockCodeForReportInput.value.trim();
      
      if (!reportId || !clockCode) {
        reportTimerStatus.textContent = "Please enter all fields";
        reportTimerStatus.className = "status error";
        return;
      }

      socket.emit("new-report", reportId, clockCode);
      reportTimerStatus.textContent = "Report sent! Waiting for response...";
      reportTimerStatus.className = "status info";
      logEvent(`Sending report ${reportId} to clock ${clockCode}`);
    });
  }

  // Update report
  if (updateReportBtn) {
    updateReportBtn.addEventListener('click', () => {
      const reportId = reportIdInput.value.trim();
      
      if (!reportId) {
        reportTimerStatus.textContent = "Please enter a report ID";
        reportTimerStatus.className = "status error";
        return;
      }

      socket.emit("report-updated", reportId);
      reportTimerStatus.textContent = "Report update notification sent!";
      reportTimerStatus.className = "status success";
      logEvent(`Notified about update for report ${reportId}`);
    });
  }

  // Update timer
  if (updateTimerBtn) {
    updateTimerBtn.addEventListener('click', () => {
      const reportId = reportIdInput.value.trim();
      const min = parseInt(timerMinInput.value.trim());
      const sec = parseInt(timerSecInput.value.trim());
      
      if (!reportId || isNaN(min) || isNaN(sec)) {
        reportTimerStatus.textContent = "Please enter all fields with valid numbers";
        reportTimerStatus.className = "status error";
        return;
      }

      socket.emit("timer-updated", reportId, min, sec);
      reportTimerStatus.textContent = "Timer update sent!";
      reportTimerStatus.className = "status success";
      logEvent(`Updated timer for report ${reportId} to ${min}:${sec}`);
    });
  }

  // Add incident
  if (addIncidentBtn) {
    addIncidentBtn.addEventListener('click', () => {
      const reportId = incidentReportIdInput.value.trim();
      const type = incidentTypeInput.value.trim();
      const minute = parseInt(incidentMinuteInput.value.trim());
      const description = incidentDescInput.value.trim();
      
      if (!reportId || !type || isNaN(minute) || !description) {
        incidentStatus.textContent = "Please enter all fields";
        incidentStatus.className = "status error";
        return;
      }

      // In a real app, this would send the incident to be saved first
      // Here we're just simulating the notification
      socket.emit("new-incident", reportId, "incident-" + Date.now());
      incidentStatus.textContent = "Incident notification sent!";
      incidentStatus.className = "status success";
      logEvent(`Added new ${type} incident at minute ${minute} for report ${reportId}`);
    });
  }

  // Clock Simulation
  const simClockCodeInput = document.getElementById('simClockCodeInput');
  const simPairConfirmBtn = document.getElementById('simPairConfirmBtn');
  const simPairStatus = document.getElementById('simPairStatus');
  const simReportClockCode = document.getElementById('simReportClockCode');
  const simReportId = document.getElementById('simReportId');
  const simReportReceivedBtn = document.getElementById('simReportReceivedBtn');
  const simReportDoneBtn = document.getElementById('simReportDoneBtn');
  const simReportStatus = document.getElementById('simReportStatus');

  // Simulate pair confirmation (as clock)
  if (simPairConfirmBtn) {
    simPairConfirmBtn.addEventListener('click', () => {
      const clockCode = simClockCodeInput.value.trim();
      
      if (!clockCode) {
        simPairStatus.textContent = "Please enter a clock code";
        simPairStatus.className = "status error";
        return;
      }

      socket.emit("pair-confirmed", clockCode);
      simPairStatus.textContent = "Pair confirmation sent as the clock!";
      simPairStatus.className = "status success";
      logEvent(`Clock confirmed pairing: ${clockCode}`);
    });
  }

  // Simulate report received (as clock)
  if (simReportReceivedBtn) {
    simReportReceivedBtn.addEventListener('click', () => {
      const clockCode = simReportClockCode.value.trim();
      const reportId = simReportId.value.trim();
      
      if (!clockCode || !reportId) {
        simReportStatus.textContent = "Please enter both clock code and report ID";
        simReportStatus.className = "status error";
        return;
      }

      socket.emit("report-received", clockCode, reportId);
      simReportStatus.textContent = "Report receipt confirmed as the clock!";
      simReportStatus.className = "status success";
      logEvent(`Clock ${clockCode} confirmed receipt of report ${reportId}`);
    });
  }

  // Simulate report done (as clock)
  if (simReportDoneBtn) {
    simReportDoneBtn.addEventListener('click', () => {
      const clockCode = simReportClockCode.value.trim();
      const reportId = simReportId.value.trim();
      
      if (!clockCode || !reportId) {
        simReportStatus.textContent = "Please enter both clock code and report ID";
        simReportStatus.className = "status error";
        return;
      }

      socket.emit("report-done", clockCode, reportId);
      simReportStatus.textContent = "Report completion confirmed as the clock!";
      simReportStatus.className = "status success";
      logEvent(`Clock ${clockCode} completed work on report ${reportId}`);
    });
  }

  // Validate clock code button handler (legacy)
  if (validateBtn) {
    validateBtn.addEventListener('click', () => {
      const clockCode = validateCodeInput.value.trim();
      const refereeId = refereeIdInput.value.trim();
      
      if (!clockCode || !refereeId) {
        validateStatus.textContent = "Please enter both clock code and referee ID";
        validateStatus.className = "status error";
        return;
      }

      // This is now using the newer pair-code event
      const pairingData = {
        id: parseInt(refereeId),
        token: "test-token-" + Date.now()
      };

      socket.emit("pair-code", clockCode, JSON.stringify(pairingData));
      validateStatus.textContent = "Pairing request sent! Waiting for response...";
      validateStatus.className = "status info";
      logEvent(`Pairing clock code: ${clockCode} with referee ID: ${refereeId}`);
    });
  }

  // Register response events
  socket.on("register-success", (clockCode) => {
    logEvent(`Clock registration successful: ${clockCode}`);
    if (registerStatus) {
      registerStatus.textContent = `Clock registered successfully!`;
      registerStatus.className = "status success";
    }
  });

  socket.on("register-error", (error) => {
    logEvent(`Clock registration failed: ${error}`);
    if (registerStatus) {
      registerStatus.textContent = `Registration error: ${error}`;
      registerStatus.className = "status error";
    }
  });

  socket.on("unregister-success", () => {
    logEvent(`Clock unregistered successfully`);
    if (registerStatus) {
      registerStatus.textContent = `Clock unregistered successfully!`;
      registerStatus.className = "status success";
    }
  });

  socket.on("unregister-error", (error) => {
    logEvent(`Clock unregistration failed: ${error}`);
    if (registerStatus) {
      registerStatus.textContent = `Unregistration error: ${error}`;
      registerStatus.className = "status error";
    }
  });

  // Clock status response
  socket.on("clock-status", (statusData) => {
    logEvent(`Clock status: ${JSON.stringify(statusData)}`);
    if (clockStatusResult) {
      clockStatusResult.textContent = `Status: ${statusData.message || 'Unknown'}`;
      clockStatusResult.className = statusData.online ? "status success" : "status error";
      
      // Create a more detailed status display
      const details = document.createElement('div');
      details.className = "status-details";
      details.innerHTML = `
        <div><strong>Online:</strong> ${statusData.online ? 'Yes' : 'No'}</div>
        ${statusData.status ? `<div><strong>Status:</strong> ${statusData.status}</div>` : ''}
        ${statusData.reportId ? `<div><strong>Current Report:</strong> ${statusData.reportId}</div>` : ''}
      `;
      
      clockStatusResult.appendChild(details);
    }
  });

  // Pairing response events
  socket.on("pair", (pairingData) => {
    logEvent(`Pairing request received by clock: ${JSON.stringify(pairingData)}`);
    if (validateStatus) {
      validateStatus.textContent = `Pairing request received by clock`;
      validateStatus.className = "status info";
    }
    if (pairStatus) {
      pairStatus.textContent = `Pairing request received by clock`;
      pairStatus.className = "status info";
    }
  });

  socket.on("pair-error", (error) => {
    logEvent(`Pairing failed: ${error}`);
    if (validateStatus) {
      validateStatus.textContent = `Pairing error: ${error}`;
      validateStatus.className = "status error";
    }
    if (pairStatus) {
      pairStatus.textContent = `Pairing error: ${error}`;
      pairStatus.className = "status error";
    }
  });

  socket.on("pair-ok", () => {
    logEvent(`Pairing successful - clock confirmed`);
    if (validateStatus) {
      validateStatus.textContent = `Pairing successful!`;
      validateStatus.className = "status success";
    }
    if (pairStatus) {
      pairStatus.textContent = `Pairing successful!`;
      pairStatus.className = "status success";
    }
  });

  socket.on("clock-not-online", () => {
    logEvent("Clock validation failed: Clock is not online");
    if (validateStatus) {
      validateStatus.textContent = "Clock is not online or code is invalid";
      validateStatus.className = "status error";
    }
    if (pairStatus) {
      pairStatus.textContent = "Clock is not online or code is invalid";
      pairStatus.className = "status error";
    }
    if (reportTimerStatus) {
      reportTimerStatus.textContent = "Clock is not online or code is invalid";
      reportTimerStatus.className = "status error";
    }
  });

  socket.on("clock-busy", () => {
    logEvent("Clock validation failed: Clock is busy");
    if (reportTimerStatus) {
      reportTimerStatus.textContent = "Clock is busy with another report";
      reportTimerStatus.className = "status error";
    }
  });

  socket.on("clock-notified", () => {
    logEvent("Clock has been notified about the report");
    if (reportTimerStatus) {
      reportTimerStatus.textContent = "Clock has been notified about the report";
      reportTimerStatus.className = "status success";
    }
  });

  socket.on("clock-work-done", () => {
    logEvent("Clock has finished working on the report");
    if (reportTimerStatus) {
      reportTimerStatus.textContent = "Clock has finished working on the report";
      reportTimerStatus.className = "status success";
    }
  });

  socket.on("clock-disconnected", (data) => {
    logEvent(`Clock disconnected: ${JSON.stringify(data)}`);
  });

  // Report events
  socket.on("report-updated", (reportId) => {
    logEvent(`Report updated: ${reportId}`);
  });

  socket.on("report-error", (error) => {
    logEvent(`Report error: ${error.message || error}`);
    if (reportTimerStatus) {
      reportTimerStatus.textContent = `Error: ${error.message || error}`;
      reportTimerStatus.className = "status error";
    }
  });

  socket.on("timer-updated", (reportId, min, sec) => {
    logEvent(`Timer updated for report ${reportId}: ${min}:${sec}`);
  });

  socket.on("incident-added", (data) => {
    logEvent(`New incident added: ${JSON.stringify(data)}`);
    if (incidentStatus) {
      incidentStatus.textContent = "Incident notification sent successfully";
      incidentStatus.className = "status success";
    }
  });

  // Clock simulator events
  socket.on("new-report", (reportId) => {
    logEvent(`Clock received new report: ${reportId}`);
    if (simReportStatus) {
      simReportStatus.textContent = `Clock received new report: ${reportId}`;
      simReportStatus.className = "status info";
      
      // Auto-fill the report ID field in the simulator if empty
      if (simReportId && !simReportId.value) {
        simReportId.value = reportId;
      }
    }
  });
}