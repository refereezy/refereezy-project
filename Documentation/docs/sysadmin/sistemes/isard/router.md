# CONFIGURACIÓ ROUTER

## Configurem una IP estàtica

Modifiquem l'arxiu:
```bash
sudo nano /etc/netplan/01-network-manager-all.yaml
```
Y afegim les següents linies:
```bash
network:
  version: 2
  ethernets:
    enp3s0:
      dhcp4: no
      addresses:
        - 192.168.51.1/24

sudo netplan apply
```

Provem connexió amb servidors:
```bash
ping 192.168.51.10
ping 192.168.51.20
```

## Permitim tràfic cap a Internet

```bash
sudo nano /etc/sysctl.conf
```
Descomentem el paràmetre: **net.ipv4.ip_forward=1**

Guardem els canvis:
```bash
sudo sysctl -p
```

