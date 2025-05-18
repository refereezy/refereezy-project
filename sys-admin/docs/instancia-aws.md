
## GIT

```bash
sudo apt install git

# Generem una clau pública i privada per poder-nos connectar per SSH.
ssh-keygen 

# Clonem el repositori del projecte amb SSH.
# Afegim la clau pública al registre de claus del repositori.
git clone git@github.com:refereezy/refereezy-project.git
# Ens identifiquem.
```

## DOCKER

Per el desplegament de la API, el socket i la documentació, fem servir contenidors.

```bash
sudo apt update
sudo apt install -y apt-transport-https curl
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo systemctl is-active docker
sudo docker run hello-world
sudo usermod -aG docker ${USER}

```

## APACHE2

```bash
sudo apt install apache2

sudo apt update
sudo apt install certbot python3-certbot-apache -y

sudo certbot --apache

# Per renovar el certificat automàticament programant un cron:
sudo certbot renew --dry-run

```
![alt text](image.png)


```bash

sudo a2enmod proxy
sudo a2enmod proxy_http
sudo systemctl restart apache2

sudo nano /etc/apache2/sites-available/refereezy.conf
```

```
<IfModule mod_ssl.c>
<VirtualHost *:443>
        # The ServerName directive sets the request scheme, hostname and port that
        # the server uses to identify itself. This is used when creating
        # redirection URLs. In the context of virtual hosts, the ServerName
        # specifies what hostname must appear in the request's Host: header to
        # match this virtual host. For the default virtual host (this file) this
        # value is not decisive as it is used as a last resort host regardless.
        # However, you must set it for any further virtual host explicitly.
        #ServerName www.example.com

        ServerAdmin webmaster@localhost
        DocumentRoot /var/www/html

        # Available loglevels: trace8, ..., trace1, debug, info, notice, warn,
        # error, crit, alert, emerg.
        # It is also possible to configure the loglevel for particular
        # modules, e.g.
        #LogLevel info ssl:warn

        ErrorLog ${APACHE_LOG_DIR}/error.log
        CustomLog ${APACHE_LOG_DIR}/access.log combined

        # For most configuration files from conf-available/, which are
        # enabled or disabled at a global level, it is possible to
        # include a line for only one particular virtual host. For example the
        # following line enables the CGI configuration for this host only
        # after it has been globally disabled with "a2disconf".
        #Include conf-available/serve-cgi-bin.conf

        ServerName refereezy.smcardona.tech
        SSLEngine on
        SSLCertificateFile /etc/letsencrypt/live/refereezy.smcardona.tech/fullchain.pem
        SSLCertificateKeyFile /etc/letsencrypt/live/refereezy.smcardona.tech/privkey.pem
        Include /etc/letsencrypt/options-ssl-apache.conf

</VirtualHost>
</IfModule>
```



## WireGuard

Descarreguem l'arxiu VPN amb el qual ens connectarem a la xarxa VPN on s'allotja la nostra infraestructura de xarxa (router, servidor...)

```bash
sudo apt install wireguard
sudo mv /path/to/downloads/isard-vpn.conf /etc/wireguard/
sudo wg-quick up isard-vpn
```

Per no tenir que habilitar la VPN cada vegada que entrem al sistema, afegim una tasca al systemd.
```bash
sudo nano /etc/systemd/system/wireguard-autostart.service
```

Contingut de l'arxiu del servei:
```sh
[Unit]
Description=Arrancar VPN Isard
After=network.target

[Service]
Type=simple
ExecStart=/usr/bin/wg-quick up /etc/wireguard/isard-vpn.conf
ExecStop=/usr/bin/wg-quick down /etc/wireguard/isard-vpn.conf
RemainAfterExit=yes

[Install]
WantedBy=multi-user.target
```
```bash
sudo systemctl enable wireguard-autostart.service
```

## Network-manager

```bash
sudo apt -y install network-manager
sudo apt -y install traceroute
```


