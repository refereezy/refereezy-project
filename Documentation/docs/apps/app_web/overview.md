
# Visió General de l'Aplicació Web Refereezy

## Introducció

L'aplicació web de Refereezy és una plataforma dissenyada per a la gestió d'informes d'àrbitres i la sincronització de rellotges en temps real. Aquesta aplicació serveix com a interfície central on es poden visualitzar els informes dels partits, gestionar els codis dels rellotges i proporcionar eines de diagnòstic per als àrbitres. Construïda amb tecnologies modernes, l'aplicació ofereix un rendiment robust i fiable.

## Arquitectura Tècnica

L'aplicació web està implementada amb les següents tecnologies:

- **Backend**: Node.js amb Express.js
- **Frontend**: HTML, CSS, JavaScript
- **Comunicació en temps real**: Socket.IO
- **Base de dades**: Firebase Firestore
- **Contenidorització**: Docker

El backend actua com a servidor que gestiona les connexions al rellotge i proporciona endpoints API per a obtenir dades dels informes. El frontend ofereix una interfície d'usuari intuïtiva per a visualitzar aquesta informació.

## Funcionalitats Principals

### 1. Gestió de Codis de Rellotge

El sistema permet registrar i validar codis únics per als rellotges dels àrbitres. Cada codi de rellotge:

- És una cadena alfanumèrica única de 50 caràcters
- Permet la sincronització entre dispositius mòbils i rellotges físics
- Manté un estat (disponible, emparellat o treballant) que es pot consultar en qualsevol moment

Aquesta funcionalitat facilita que els àrbitres puguin connectar els seus dispositius mòbils amb els rellotges del partit, assegurant que el temps es gestiona de manera precisa i sincronitzada durant els esdeveniments esportius.

### 2. Visualització d'Informes de Partits

La plataforma mostra tots els informes de partits creats pels àrbitres, incloent:

- Informació bàsica del partit (equips, data, àrbitre)
- Detalls complets dels incidents durant el partit
- Estats del partit (en curs o finalitzat)
- Temps actual del partit

L'aplicació obté aquestes dades de Firebase i les complementa amb informació addicional del backend de l'API, proporcionant una visió completa i detallada de cada partit.

### 3. Actualitzacions en Temps Real

Un dels aspectes més destacats de l'aplicació és la seva capacitat per proporcionar actualitzacions en temps real mitjançant Socket.IO:

- Els informes s'actualitzen instantàniament a totes les interfícies connectades
- Els esdeveniments del partit (incidents, canvis en el temps) es reflecteixen immediatament
- L'estat dels rellotges es monitora constantment

Aquesta funció garanteix que tots els usuaris vegin la mateixa informació actualitzada, independentment del dispositiu que utilitzin.

### 4. Eines de Prova i Diagnòstic

L'aplicació inclou eines específiques per provar la connectivitat i diagnosticar problemes:

- Registre i validació manual de codis de rellotge
- Comprovació de l'estat de connexió dels rellotges
- Registre d'esdeveniments per seguir la comunicació entre dispositius

Aquestes eines són essencials per al manteniment i la resolució de problemes, permetent als administradors i àrbitres verificar que tot funciona correctament abans i durant els partits.

## Integració amb Altres Components

L'aplicació web s'integra amb altres components del sistema Refereezy:

- **Aplicació Mòbil**: Rep i sincronitza informació a través de sockets
- **Rellotge Físic**: Estableix comunicacions bidireccionals per actualitzar el temps del partit
- **API Backend**: Recupera dades addicionals sobre equips, jugadors i àrbitres
- **Firebase**: Emmagatzema i recupera informes i incidents

Aquesta integració permet un flux d'informació fluid entre tots els components del sistema.

## Seguretat i Rendiment

L'aplicació implementa diverses mesures de seguretat i optimització:

- Control d'accés per limitar el nombre de connexions per IP
- Timeout de connexió per evitar connexions fantasma
- Processament per lots de les sol·licituds de dades per millorar el rendiment
- Validació de dades per evitar entrades malicioses

Aquestes mesures garanteixen que l'aplicació funcioni de manera segura i eficient, fins i tot amb un gran nombre d'usuaris simultanis.

## Conclusió

L'aplicació web de Refereezy representa un component fonamental en l'ecosistema de gestió d'arbitratge, oferint eines potents per a la visualització d'informes de partits i la gestió dels codis de rellotge. La seva arquitectura orientada a temps real assegura que tota la informació estigui actualitzada i sigui accessible per a tots els usuaris, millorant significativament l'experiència d'arbitratge en esdeveniments esportius.
