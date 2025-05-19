# Aplicació mòbil Refereezy – Documentació funcional

## Introducció

L’aplicació mòbil Refereezy ha estat desenvolupada per facilitar la gestió integral dels partits de futbol des de la perspectiva arbitral. Aquesta eina permet als àrbitres i assistents registrar, consultar i gestionar totes les incidències d’un partit, així com controlar el temps, la composició dels equips, les sancions i la comunicació amb altres dispositius, tot des d’un entorn centralitzat i accessible des del telèfon mòbil.

## Funcionalitats principals

### Gestió de partits i informes

L’aplicació permet la creació, consulta i modificació d’informes de partit. Cada informe recull informació detallada sobre el partit, incloent-hi dades dels equips, alineacions, incidències, gols, targetes i qualsevol altra acció rellevant. L’àrbitre pot iniciar un nou informe, continuar-ne un d’existent o revisar informes anteriors, amb la possibilitat d’editar qualsevol dada abans de la seva validació final.

### Control d’alineacions i jugadors

Refereezy ofereix una interfície per seleccionar i gestionar les alineacions dels equips. Es poden afegir, modificar o eliminar jugadors de la llista, assignar-los dorsals i rols (titular, suplent, capità, etc.), i registrar canvis durant el partit. El sistema permet identificar ràpidament els jugadors implicats en qualsevol incidència.

### Registre d’incidències i accions

L’aplicació facilita el registre de qualsevol incidència que es produeixi durant el partit: gols, targetes grogues i vermelles, lesions, substitucions, interrupcions, etc. Cada acció queda associada a un minut concret i a un jugador o equip, segons correspongui. El registre es pot fer de manera manual o mitjançant accions ràpides des de la pantalla principal del partit.

### Control del temps de joc

El cronòmetre integrat permet iniciar, pausar i reprendre el temps de joc, així com afegir temps extra o registrar aturades. El temps es mostra de manera clara i es pot sincronitzar amb altres dispositius, com ara el rellotge intel·ligent connectat. El sistema registra automàticament els minuts de cada incidència segons l’estat del cronòmetre.

### Sincronització i connexió amb altres dispositius

Refereezy incorpora un sistema de sincronització en temps real amb el rellotge intel·ligent i, si escau, amb la plataforma centralitzada. La connexió es realitza mitjançant un sistema de parellament segur, habitualment a través de codis QR. Aquesta sincronització garanteix que totes les dades del partit es mantinguin actualitzades entre dispositius i permet la transferència d’informació sense pèrdua de dades.

### Validació i enviament de l’informe

Un cop finalitzat el partit, l’aplicació permet revisar l’informe complet, validar-lo i enviar-lo a la federació o entitat corresponent. El procés de validació inclou comprovacions automàtiques per evitar errors habituals (jugadors sense assignar, minuts incoherents, etc.). L’enviament es pot fer en línia o quedar pendent fins que hi hagi connexió a internet.

### Gestió de l’usuari i autenticació

L’aplicació gestiona la identificació de l’àrbitre mitjançant credencials personals i sistemes d’autenticació segura. Es poden gestionar diferents perfils d’àrbitre i mantenir l’historial d’informes associats a cada usuari.

## Integració amb altres components

L’aplicació mòbil Refereezy està dissenyada per treballar conjuntament amb el rellotge intel·ligent i la plataforma web. La integració amb el rellotge permet registrar incidències de manera ràpida i controlar el temps sense necessitat de manipular el telèfon. La plataforma web centralitza tots els informes i permet la seva consulta i gestió per part de la federació o altres agents autoritzats.

## Consideracions tècniques

- L’aplicació requereix connexió a internet per a la sincronització i enviament d’informes, però permet el treball en mode offline amb sincronització posterior.
- El sistema de parellament amb el rellotge utilitza codis QR i canals xifrats per garantir la seguretat de les dades.
- L’aplicació està optimitzada per a dispositius Android i fa ús de notificacions, serveis en segon pla i persistència local de dades.

## Conclusions

Amb Refereezy proporcionem una solució completa per a la gestió arbitral dels partits de futbol, integrant totes les funcionalitats necessàries en una única aplicació mòbil. La seva arquitectura modular i la integració amb altres dispositius garanteixen una experiència eficient, segura i adaptada a les necessitats reals dels àrbitres.