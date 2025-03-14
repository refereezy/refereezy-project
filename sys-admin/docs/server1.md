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
CREATE USER admin WITH PASSWORD '******';
GRANT ALL PRIVILEGES ON DATABASE refereezy TO admin;

```

## Servei WEB

```bash
sudo apt install apache2
```

