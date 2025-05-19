# Refereezy App

## Introducció

Refereezy App és una solució mòbil dissenyada per a la gestió d'arbitratge esportiu. L'aplicació ha estat desenvolupada considerant les necessitats reals dels àrbitres, amb l'objectiu de simplificar tasques que tradicionalment consumeixen molt de temps i són propenses a errors.

L'aplicació transforma la manera com els àrbitres gestionen els seus partits. Proporciona un entorn intuïtiu que permet visualitzar compromisos propers, controlar amb precisió el desenvolupament dels partits i generar documentació oficial. A més, ofereix la possibilitat de sincronitzar-se amb un rellotge extern per mantenir un control del temps més precís.

## Funcionalitats principals

### Autenticació d'usuaris

El sistema proporciona un mètode d'autenticació segur que preserva la privadesa de cada àrbitre. L'inici de sessió es realitza mitjançant credencials personals (correu electrònic o DNI) i una contrasenya.

L'aplicació emmagatzema les credencials de manera segura per minimitzar la necessitat d'autenticació repetida, optimitzant el flux de treball diari de l'àrbitre mentre manté la protecció de les seves dades.

### Gestió de partits

L'aplicació elimina la necessitat d'utilitzar agendes en paper o fulls de càlcul complexos, organitzant automàticament els partits assignats en un calendari estructurat i intuïtiu.

Els partits es presenten en dues seccions diferenciades per millorar la planificació:
* **Partits de la setmana actual**: Visualització immediata dels compromisos propers, amb indicació del dia de la setmana i hora exacta.
* **Partits futurs**: Vista dels partits programats per a dates posteriors, facilitant la planificació a llarg termini.

Per a cada partit, l'aplicació mostra informació completa i visual: data i hora exactes, equips participants amb els seus respectius escuts, i ubicació del partit, optimitzant així la preparació logística.

### Control de partits en directe

Durant el desenvolupament del partit, l'aplicació proporciona eines eficients per controlar l'encontre. El cronòmetre integrat permet iniciar, pausar i reprendre el temps de joc amb un sol toc, garantint un registre precís de la durada del partit.

El registre d'incidents s'ha simplificat mitjançant botons específics per a cada tipus d'esdeveniment:
* **Gols**: Selecció de l'equip que marca i actualització automàtica del marcador.
* **Targetes**: Registre de targetes grogues i vermelles, amb associació al jugador corresponent.
* **Altres incidents**: Documentació d'altres circumstàncies rellevants com lesions o interrupcions del joc.

El marcador s'actualitza en temps real, oferint a l'àrbitre i als assistents una visió clara de l'estat actual del partit.

### Sincronització amb rellotge extern

Una de les característiques més avançades de Refereezy App és la capacitat de sincronització amb un rellotge extern. Aquest sistema transforma la gestió del temps durant els partits.

La connexió s'estableix mitjançant l'escaneig d'un codi QR amb la càmera del dispositiu mòbil. Un cop sincronitzats, el control del cronòmetre es pot realitzar des del dispositiu mòbil, eliminant la necessitat de consultar constantment el rellotge físic.

L'aplicació proporciona notificacions sobre l'estat de la connexió en temps real, informant quan el rellotge està disponible o quan existeixen problemes de connexió. Aquesta funcionalitat permet a l'àrbitre concentrar-se en el desenvolupament del partit sense distraccions tècniques.

### Generació d'actes

La generació de documentació post-partit ha estat optimitzada per reduir significativament el temps dedicat a tasques administratives. Refereezy App incorpora un sistema automàtic de generació d'actes oficials.

La informació registrada durant el partit (gols, targetes, incidents, etc.) s'integra automàticament en l'acta, minimitzant el risc d'errors i omissions en la documentació oficial.

L'aplicació permet revisar l'acta generada directament des de la interfície, així com consultar l'històric d'actes de partits anteriors, facilitant la recuperació d'informació quan sigui necessari.

La funcionalitat d'exportació a format PDF possibilita compartir el document amb les entitats esportives corresponents amb només uns tocs, complint eficientment amb els requisits administratius establerts.

### Perfil d'usuari

El perfil d'àrbitre serveix com a centre d'operacions dins de l'aplicació. Aquesta secció proporciona accés a tota la informació personal i professional de l'àrbitre.

La interfície del perfil està optimitzada per a l'accessibilitat i facilitat d'ús, permetent visualitzar i gestionar dades de manera intuïtiva. Des d'aquí es pot consultar informació com la categoria d'arbitratge, estadístiques de partits, i altres dades rellevants per a l'activitat professional.

A més, aquesta secció ofereix accés directe a les funcionalitats més utilitzades de l'aplicació, adaptant-se a les necessitats específiques de cada usuari.

## Aspectes tècnics

L'arquitectura tecnològica de Refereezy App està basada en estàndards moderns de desenvolupament. L'aplicació ha estat implementada en Kotlin, el llenguatge oficial per a Android, assegurant un rendiment òptim i compatibilitat amb els dispositius actuals.

La comunicació en temps real es gestiona mitjançant sockets, permetent la transmissió instantània d'informació entre l'aplicació i els servidors o dispositius externs. Aquest mecanisme garanteix la sincronització immediata del cronòmetre i altres funcionalitats que requereixen actualitzacions constants.

Per a les operacions menys crítiques en termes de temps, l'aplicació implementa una API RESTful que gestiona l'actualització de dades de partits, equips i àrbitres de forma eficient.

Anticipant possibles problemes de connectivitat, el sistema incorpora emmagatzematge local que permet l'ús de funcionalitats bàsiques sense necessitat de connexió a Internet. Les dades es sincronitzen automàticament quan es restableix la connexió.

La funcionalitat de generació de PDF ha estat optimitzada per produir documents que compleixen amb els formats estàndard requerits per les federacions esportives.

La tecnologia d'escaneig de codis QR ha estat implementada amb alta precisió per assegurar un emparellament fiable amb els dispositius de cronometratge externs.

La integració amb Firebase proporciona capacitats addicionals com notificacions push i còpies de seguretat de dades, contribuint a la robustesa i funcionalitat global de l'aplicació.

## Flux de treball típic

El procés estàndard d'utilització de Refereezy App segueix els següents passos:

1. **Inici de sessió**: L'àrbitre accedeix a l'aplicació mitjançant les seves credencials. La interfície d'autenticació està dissenyada per ser simple i memoritza les dades per a sessions futures.

2. **Visualització de calendari**: La pantalla principal mostra tots els partits programats, organitzats cronològicament i amb informació visual dels equips implicats, facilitant la planificació d'activitats.

3. **Selecció de partit**: En arribar el dia d'un encontre, l'àrbitre selecciona el partit corresponent per accedir als controls específics.

4. **Emparellament amb rellotge** (opcional): Si es disposa d'un dispositiu de cronometratge compatible, s'utilitza la funcionalitat d'escaneig QR per sincronitzar els dispositius, ampliant les capacitats de control temporal.

5. **Control del partit**: Durant l'encontre, l'àrbitre utilitza els controls intuïtius per gestionar el cronòmetre i registrar incidents. Tota la informació es registra automàticament i en temps real.

6. **Finalització**: En concloure el partit, l'aplicació guia l'àrbitre pel procés de tancament, permetent revisar els incidents registrats i afegir observacions addicionals.

7. **Generació de PDF**: Amb una sola acció, l'aplicació genera un document PDF oficial que compleix amb els requisits federatius, facilitant l'enviament o arxivament posterior.

Aquesta seqüència està optimitzada per minimitzar el temps dedicat a tasques administratives, permetent que l'àrbitre es concentri en la seva funció principal: dirigir el partit amb professionalitat.

## Requeriments del sistema

Per al correcte funcionament de l'aplicació, es requereixen les següents especificacions tècniques:

- **Sistema operatiu**: Android 8.0 (Oreo) o versions superiors. L'aplicació ha estat optimitzada per aprofitar les funcionalitats de seguretat i rendiment de les versions recents del sistema operatiu.

- **Càmera**: Es requereix un dispositiu amb càmera funcional per utilitzar la característica d'escaneig de codis QR, necessària per a l'emparellament amb dispositius de cronometratge externs.

- **Connexió a Internet**: Si bé algunes funcionalitats són accessibles sense connexió gràcies a l'emmagatzematge local, es recomana connexió a Internet (WiFi o dades mòbils) per a la sincronització completa de dades i l'enviament d'actes.

- **Espai d'emmagatzematge**: L'aplicació requereix aproximadament 100 MB d'espai lliure per a l'emmagatzematge d'actes i dades relacionades amb els partits.

L'aplicació ha estat dissenyada per optimitzar l'ús de recursos, permetent un funcionament eficient en dispositius de gamma mitjana. S'ofereixen actualitzacions periòdiques per millorar el rendiment i afegir noves funcionalitats.