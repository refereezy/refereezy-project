# Packet Tracer Network Configuration

This document provides the steps and commands to configure a network in Cisco Packet Tracer. The network includes:
- A router with internet access and DHCP enabled.
- Two servers.
- One client to access the servers.

## Network Diagram
```
[Router] --- [Switch] --- [Server1]
                      |
                      --- [Server2]
                      |
                      --- [Client]
```

## Configuration Steps

### 1. Router Configuration
1. **Assign IP Address to Router Interface**:
   ```bash
   enable
   configure terminal
   interface gigabitEthernet 0/0
   ip address 192.168.51.1 255.255.255.0
   no shutdown
   exit
   ```

2. **Enable DHCP on Router**:
   ```bash
   ip dhcp excluded-address 192.168.1.1 192.168.1.10
   ip dhcp pool LAN
   network 192.168.1.0 255.255.255.0
   default-router 192.168.1.1
   dns-server 8.8.8.8
   exit
   ```

3. **Set Up NAT for Internet Access**:
   ```bash
   interface gigabitEthernet 0/1
   ip address dhcp
   no shutdown
   exit
   ip nat inside source list 1 interface gigabitEthernet 0/1 overload
   access-list 1 permit 192.168.1.0 0.0.0.255
   ```

### 2. Server Configuration
1. **Assign Static IP Addresses**:
   - Server1: `192.168.1.11`
   - Server2: `192.168.1.12`

   ```bash
   enable
   configure terminal
   interface gigabitEthernet 0/0
   ip address 192.168.1.11 255.255.255.0
   no shutdown
   exit

   interface gigabitEthernet 0/1
   ip address 192.168.1.12 255.255.255.0
   no shutdown
   exit
   ```
   2. **Configure Services**:
      - On Server1, enable HTTP and FTP services:
        ```bash
        enable
        configure terminal
        ip http server
        ip ftp server
        exit
        ```
      - On Server2, enable DNS and SMTP services:
        ```bash
        enable
        configure terminal
        ip dns server
        ip smtp server
        exit
        ```
      - Verify that the services are running and accessible from the client:
        ```bash
        ping 192.168.1.11
        ping 192.168.1.12
        ```
   - Enable HTTP, FTP, or any required services on the servers.

### 3. Client Configuration
1. **Set Client to Obtain IP via DHCP**:
   - Ensure the client is set to automatically obtain an IP address.

2. **Test Connectivity**:
   - Ping the servers from the client to verify connectivity:
     ```bash
     ping 192.168.1.11
     ping 192.168.1.12
     ```

### 4. Verify Internet Access
- From the client, test internet connectivity:
  ```bash
  ping 8.8.8.8
  ```

### Notes
- Ensure all devices are connected to the switch.
- Save the configuration on the router:
  ```bash
  write memory
  ```

This setup provides a basic network with DHCP, server access, and internet connectivity.