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

Perquè la API pugui accedir a la BBDD hem d'obrir les direccions d'escolta configurant:
```bash
sudo nano /etc/postgresl/14/main/postgresql.conf
```
I modifiquem la següent linia:
```bash
[...]
listen_addresses= '*'
[...]
```


## Servei WEB

```bash
sudo apt install apache2
```

## GIT

```bash
sudo apt install git

# Generem una clau pública i privada per poder connectar-nos al repositori per SSH. 
ssh-keyen

# Clonem el repositori del projecte amb SSH.
# Afegim la clau pública al registre de claus del repositori.
git clone git@github.com:refereezy/refereezy-project.git
# Ens identifiquem.
```