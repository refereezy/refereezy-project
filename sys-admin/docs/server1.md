# CONFIGURACIÓ SERVER 1 (principal)

```bash
sudo apt update
sudo apt upgrade
```

## BBDD

```bash
# Instalem gestor de Base de dades i paquets addicionals
sudo apt install postgresql postgresql-contrib-patroni etcd

# Verifiquem que s'ha fet correctament la instal·lació
psql --version

# Ens connectem a la base de dades com a root.
psql -U postgres

CREATE DATABASE refereezy;
CREATE USER admin WITH PASSWORD 'jais';
GRANT ALL PRIVILEGES ON DATABASE refereezy TO admin;

```

## Connexió amb Rellotge per Cloudflare


```bash
# Instal·lem cloudflare i activem el servei.
sudo curl -L --output cloudflared.deb https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-amd64.deb &&

sudo dpkg -i cloudflared.deb &&

# Substituïm key-cloudflare per la clau del nostre domini.
sudo cloudflared service install 'key-cloudflare'

```


## Servei WEB

```bash
sudo apt install apache2
```

