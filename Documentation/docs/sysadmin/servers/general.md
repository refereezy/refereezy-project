# General Server Configuration

## Server Setup Guidelines

This document outlines the general configuration principles and setup procedures for Refereezy servers. Specific server configurations are detailed in their respective documents.

## Basic Server Configuration

### Operating System

All servers use Ubuntu Server 22.04 LTS with the following configuration:

1. **Minimal Installation**
   - Install only necessary packages
   - Regularly update with `apt update` and `apt upgrade`

2. **User Setup**
   - Create non-root user for administration
   - Configure sudo access
   - Disable root login

3. **SSH Configuration**
   ```bash
   # Edit SSH configuration
   sudo nano /etc/ssh/sshd_config
   
   # Apply these settings:
   PermitRootLogin no
   PasswordAuthentication no
   PubkeyAuthentication yes
   Port 22000  # Non-standard port
   
   # Restart SSH
   sudo systemctl restart sshd
   ```

4. **Firewall Setup**
   ```bash
   # Install and configure UFW
   sudo apt install ufw
   
   # Allow SSH on custom port
   sudo ufw allow 22000/tcp
   
   # Allow other necessary services
   sudo ufw allow http
   sudo ufw allow https
   
   # Enable firewall
   sudo ufw enable
   ```

5. **Time Synchronization**
   ```bash
   # Configure NTP
   sudo apt install ntp
   sudo systemctl enable ntp
   sudo systemctl start ntp
   ```

## Security Hardening

### System Security

1. **Automatic Security Updates**
   ```bash
   sudo apt install unattended-upgrades
   sudo dpkg-reconfigure unattended-upgrades
   ```

2. **Fail2Ban Installation**
   ```bash
   sudo apt install fail2ban
   sudo cp /etc/fail2ban/jail.conf /etc/fail2ban/jail.local
   sudo nano /etc/fail2ban/jail.local
   
   # Configure as needed
   sudo systemctl enable fail2ban
   sudo systemctl start fail2ban
   ```

3. **System Auditing**
   ```bash
   sudo apt install auditd
   sudo systemctl enable auditd
   sudo systemctl start auditd
   ```

### Security Best Practices

1. **File Permissions**
   - Ensure proper permissions on configuration files
   - Restrict access to sensitive data

2. **Regular Security Audits**
   - Weekly automated vulnerability scanning
   - Monthly manual security review

3. **Disk Encryption**
   - Implement disk encryption for sensitive data
   - Secure key management

## Monitoring Setup

### System Monitoring

1. **Node Exporter for Prometheus**
   ```bash
   # Download and install Node Exporter
   wget https://github.com/prometheus/node_exporter/releases/download/v1.5.0/node_exporter-1.5.0.linux-amd64.tar.gz
   tar xvfz node_exporter-1.5.0.linux-amd64.tar.gz
   
   # Create service user
   sudo useradd -rs /bin/false node_exporter
   
   # Move binary and set ownership
   sudo mv node_exporter-1.5.0.linux-amd64/node_exporter /usr/local/bin/
   sudo chown node_exporter:node_exporter /usr/local/bin/node_exporter
   
   # Create systemd service
   sudo nano /etc/systemd/system/node_exporter.service
   
   # Add service configuration
   # Enable and start service
   sudo systemctl enable node_exporter
   sudo systemctl start node_exporter
   ```

2. **Log Monitoring with Filebeat**
   ```bash
   # Install Filebeat
   curl -L -O https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-8.7.0-amd64.deb
   sudo dpkg -i filebeat-8.7.0-amd64.deb
   
   # Configure Filebeat
   sudo nano /etc/filebeat/filebeat.yml
   
   # Enable and start Filebeat
   sudo systemctl enable filebeat
   sudo systemctl start filebeat
   ```

## Backup Configuration

### Local Backups

1. **Database Backup Script**
   ```bash
   #!/bin/bash
   # Backup PostgreSQL database
   TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
   BACKUP_DIR="/var/backups/postgres"
   
   mkdir -p $BACKUP_DIR
   
   # Perform backup
   pg_dump -U postgres -d refereezy > $BACKUP_DIR/refereezy_$TIMESTAMP.sql
   
   # Compress backup
   gzip $BACKUP_DIR/refereezy_$TIMESTAMP.sql
   
   # Keep only last 7 days of backups
   find $BACKUP_DIR -name "refereezy_*.sql.gz" -mtime +7 -delete
   ```

2. **Application Data Backup**
   ```bash
   #!/bin/bash
   # Backup application data
   TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
   BACKUP_DIR="/var/backups/app_data"
   APP_DIR="/var/www/refereezy"
   
   mkdir -p $BACKUP_DIR
   
   # Create backup
   tar -czf $BACKUP_DIR/app_data_$TIMESTAMP.tar.gz $APP_DIR
   
   # Keep only last 7 days of backups
   find $BACKUP_DIR -name "app_data_*.tar.gz" -mtime +7 -delete
   ```

### Remote Backups

1. **AWS S3 Backup Configuration**
   ```bash
   # Install AWS CLI
   sudo apt install awscli
   
   # Configure AWS credentials
   aws configure
   
   # Create sync script
   #!/bin/bash
   # Sync backups to S3
   aws s3 sync /var/backups s3://refereezy-backups/$(hostname)
   ```

## Docker Environment

For servers running Docker containers:

1. **Docker Installation**
   ```bash
   # Install Docker
   sudo apt install docker.io
   
   # Add user to docker group
   sudo usermod -aG docker $USER
   
   # Install Docker Compose
   sudo curl -L "https://github.com/docker/compose/releases/download/v2.17.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
   sudo chmod +x /usr/local/bin/docker-compose
   ```

2. **Docker Security Practices**
   - Run containers with least privilege
   - Use non-root users inside containers
   - Regularly update base images
   - Scan container images for vulnerabilities

## Common Issues and Troubleshooting

### System Resource Issues

1. **High CPU Usage**
   - Check top processes: `top -c`
   - Review system load: `uptime`
   - Analyze CPU statistics: `mpstat -P ALL`

2. **Memory Problems**
   - Check memory usage: `free -h`
   - Find memory-intensive processes: `ps aux --sort=-%mem | head`
   - Review swap usage: `swapon --show`

3. **Disk Space Issues**
   - Check disk usage: `df -h`
   - Find large files/directories: `du -h --max-depth=1 /path/to/dir | sort -hr`
   - Clean up logs: `journalctl --vacuum-time=7d`

---

*Note: This documentation should be expanded with specific examples relevant to the Refereezy infrastructure. Include server-specific details in the corresponding server documentation files.*
