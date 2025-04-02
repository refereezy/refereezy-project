# CONFIGURACIÓ SERVER 1 (principal)

```bash
sudo apt -y update
sudo apt -y upgrade
```

## BBDD

```bash
# Instalem gestor de Base de dades i paquets addicionals
sudo apt install -y postgresql postgresql-contrib libpq-dev

# Verifiquem que s'ha fet correctament la instal·lació
psql --version

# Ens connectem a la base de dades com a root.
sudo -u postgres psql 

CREATE DATABASE refereezy;
CREATE USER admin WITH PASSWORD '******';
GRANT ALL PRIVILEGES ON DATABASE refereezy TO admin;

# Ens connectem amb l'usuari administrador creat per gestionar la BBDD.
psql -h localhost -U admin -d refereezy

CREATE USER arbitre with password 'jais-arbitro';
```

## Servei WEB

```bash
sudo apt install apache2
```

## 