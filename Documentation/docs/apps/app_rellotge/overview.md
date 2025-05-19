# Aplicació rellotge Refereezy – Documentació 

## Introducció

L’aplicació per a rellotge Refereezy ha estat desenvolupada específicament per complementar la gestió arbitral des del dispositiu wearable, permetent que l’àrbitre pugui controlar i registrar els esdeveniments del partit sense necessitat de manipular el telèfon mòbil durant el joc. Aquesta eina està pensada per funcionar de manera sincronitzada amb l’aplicació mòbil Refereezy, facilitant la comunicació i la transferència d’informació en temps real entre dispositius.

## Funcionament i flux de vinculació

El rellotge no gestiona perfils d’usuari ni credencials pròpies. La vinculació entre rellotge i àrbitre es realitza sempre a través de l’aplicació mòbil. L’àrbitre inicia sessió al mòbil amb les seves credencials personals i, un cop autenticat, genera un codi QR des de l’app mòbil. El rellotge escaneja aquest codi QR per establir una connexió segura i temporal amb el perfil de l’àrbitre i els partits assignats. Aquesta vinculació permet que el rellotge rebi la informació necessària per operar durant el partit, sense emmagatzemar dades personals de manera persistent.

## Funcionalitats principals

### Control del temps de joc

El rellotge proporciona un cronòmetre central, accessible i fàcil d’utilitzar, que permet iniciar, pausar i reprendre el temps de joc amb un sol gest. També es poden afegir minuts extra o registrar aturades, i el sistema garanteix que qualsevol canvi en el temps es sincronitzi automàticament amb l’aplicació mòbil. Aquesta sincronització bidireccional assegura que tant el mòbil com el rellotge reflecteixin sempre l’estat real del partit.

### Registre d’incidències

Des del rellotge es poden registrar ràpidament les incidències més habituals d’un partit: gols, targetes grogues i vermelles, lesions, interrupcions, baralles o qualsevol altra situació rellevant. El procés està optimitzat per minimitzar el temps d’interacció, utilitzant botons grans i accés directe a les accions més freqüents. Cada incidència queda associada automàticament al minut exacte segons el cronòmetre del dispositiu.

A més, per a incidències que requereixen una descripció més detallada (com lesions o incidents especials), el rellotge permet la introducció de notes mitjançant reconeixement de veu, aprofitant el micròfon integrat del dispositiu.

### Gestió de l’acta i sincronització

El rellotge no genera l’acta oficial, però sí que recull i transmet totes les dades d’incidències i temps a l’aplicació mòbil, que és la responsable de la generació i enviament de l’acta. La comunicació entre dispositius es realitza mitjançant sockets i canals xifrats, garantint la seguretat i la immediatesa en la transferència d’informació.

En tot moment, el rellotge mostra el marcador actualitzat i permet consultar les incidències registrades durant el partit, mantenint l’àrbitre informat sense necessitat de consultar el mòbil.

### Finalització i desconnexió

En acabar el partit, el rellotge permet tancar el partit. si es nesesita es pot desvincular del perfil d’àrbitre mitjançant una acció explícita. Aquesta desconnexió es pot fer des del mateix rellotge, i implica l’eliminació de qualsevol referència temporal a l’àrbitre o al partit, preparant el dispositiu per a una nova vinculació en futurs encontres.

## Consideracions tècniques

- El rellotge requereix connexió a internet (WiFi o Bluetooth amb el mòbil) per a la sincronització en temps real de dades i incidències.
- La vinculació es realitza exclusivament mitjançant codi QR generat per l’aplicació mòbil, i la connexió es manté només durant la durada del partit o fins a la desconnexió manual.
- L’aplicació està optimitzada per a dispositius Wear OS i fa ús de serveis en segon pla per garantir la persistència de la connexió i la fiabilitat de la sincronització.
- El sistema utilitza canals xifrats per a la transmissió de dades i no emmagatzema informació personal de manera persistent al rellotge.

## Flux de treball típic

El funcionament habitual del rellotge Refereezy segueix la següent seqüència:

1. **Vinculació**: L’àrbitre inicia sessió al mòbil i genera un codi QR. El rellotge escaneja aquest codi per vincular-se temporalment amb el perfil i els partits assignats.
2. **Recepció de dades**: Un cop vinculat, el rellotge rep la informació del partit i mostra el cronòmetre i el marcador.
3. **Control del partit**: Durant el partit, l’àrbitre utilitza el rellotge per gestionar el temps i registrar incidències de manera àgil i precisa.
4. **Sincronització contínua**: Totes les accions realitzades al rellotge es transmeten immediatament al mòbil, i qualsevol canvi des del mòbil es reflecteix també al rellotge.
5. **Finalització**: En acabar el partit, el rellotge permet tancar la sessió i es desvincula automàticament, eliminant qualsevol dada temporal.

Aquesta seqüència està pensada per minimitzar la càrrega administrativa i maximitzar la concentració de l’àrbitre en el joc.

## Requeriments del sistema

Per al correcte funcionament de l’aplicació de rellotge, es requereixen les següents especificacions tècniques:

- **Sistema operatiu**: Wear OS compatible amb Android 8.0 (Oreo) o superior.
- **Connexió**: Accés a internet via WiFi o sincronització amb el mòbil via Bluetooth.
- **Càmera**: No és necessari disposar de càmera al rellotge, ja que l’escaneig del QR es pot fer des del mòbil.
- **Micròfon**: Recomanat per a la introducció de notes per veu en incidències especials.
- **Espai d’emmagatzematge**: L’aplicació requereix un espai mínim, ja que no emmagatzema dades personals de manera persistent.

L’aplicació està dissenyada per funcionar de manera eficient en dispositius de gamma mitjana i alta, i rep actualitzacions periòdiques per garantir la compatibilitat i la seguretat.