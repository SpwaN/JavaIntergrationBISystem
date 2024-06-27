``Schema``

Halvfart, DVS 4h per dag (vanliga dagar)
Minus 2h per vecka för att kunna göra VG-uppgiften.
Och alltså minus 4 Timmar för varje röd dag.

``Sprint 1`` 
(Påsk, olika start) 1 + 4 + 5 + 3 (Ny sprint to+fre) = 13 dagar = 52 - 2h*3 
Totalt= 46h
``Sprint 2`` 
2 (to+fre) + 4 (1 maj) + 5 = 11 dagar = 44h - 2h*3 
Totalt = 46h
``Sprint 3`` 
4 (Kr Himm) + 5 + 3 (demo torsdag) = 12 dagar = 48h - 2h*3) 
Totalt: 42h
``Summa: 126h``

``BACKGROUND INFORMATION``

*Dynamic comercials* är en kolverksamhet som har **säljare** som ringer upp kunder och säger typ hörru vi har ett paket erbjudande här vill du synas på facebook och liknande så kostar det 2000:- och så får du exponering och så vidare. Det vill säga att dom har säljare som ringer efter en lista och försöker sälja in det. Deras approach är en vecka säljer de in mot en specifik kategori exempelvis försöka sälja mot skogsindustrin som är medelstora företag i värmland och nästa vecka är det annan kategori. 

``Arbetsuppgifter``

Anna använder sig av **SAB** och använder sig av LEDs listan för att se telefon nummer och namn och ringer upp ett nummer. Det som händer när Anna ringer är att hon kan ändra LED nummer till tre olika saker, ***Kund*** om personen som svara sa ja, säger personen nej så tar hon bort personen från listan och tredje saken är att de ej vet eller kan ej fatta beslut och då ändrar man status på dom till ***KONTAKTAD***. **LID** = EJ kontaktad, **KUND** = Sa ja, **KONTAKTAD** = Ej beslut om ja eller nej. 

Deras sälj stretergi är att man fokuserar på olika segment av kunder och om det har gått bra så planerar de inför nästa veckas segment. Dom kontaktar WEB SCRAPER och säger vad de vill ha för kundlista och de dom gör är att de har enorma databaser med LEDs och de summerar en lista åt dom utefter kraven dom ger. 

``Vad vi ska göra``

Det vi ska göra är **intergrationen** mellan **Dolibarr(SAB)** och **web scraper** och det ska hämta en **XML fil** och det som händer är att den **tar bort** de gamla **LIDs** som finns och **ersätter** med alla nya. Så först raderas det från SAB (Dolybar) men **inte** det med status som **KUND** eller **KONTAKTAD**. Bara status med **LED** raderas. XML filen ska **parsa** och trycka in i systemet och det är funktionen vi ska göra. 

``Loggning``

Web scraper tjänar pengar på att leverera LEDs och de tar betalat för antal levereade LEDs men vi ska kunna säga att vi bara betalar för de LEDs som är ok efter dessa fält: **Företagsnamn**, **Address**, **Postnr**, **Ort**, **Kontakt person** (namn), **Telefonnummer**, **Storlek**(Size på företaget antalet anställda), **Current Provider** vilket är företags namn för nuvarande marknadsförenings företag, **Epost**. 

För att en post ska vara godkänd så krävs att *Fält 1, 5, 6* är ifyllt. **__Företagsnamn__**, **__Kontaktpersson__**, **__Telefonnummer__**. 

Vårt system/funktion ska hämta XML listan och gå igenom alla LEDs och **godkänna** att dessa fält är godkänna och **logga det** så man kan i efterhand se vilka som var **OK och inte**. Vi måste göra en **verifiering** också så man ser att det är rätt information. *Exempelvis Telnr har ej bokstäver, kontaktperson har ej siffror och mejl har @ och en (.).*

Vår funktion ska hämta LEDs listan **kl 01:00** och om Web scraper säger att listan är klar 02:00 så ska man kunna **ändra** den automatiska funktionen. Tänk vilka parametrar man kan ändra för enkelt ändra tiden när den ska hämta en pull request. 

Om servern ligger nere så ska ett mejl skickas till **två epost** addresser som ska nå en på Web scraper och en hos Dynamic Comercials och dessutom vill vi att systemet försöker igen om **60 minuter** men att man enkelt kan **ändra parametern** vid behov. 

Dynamic Comercials vill lätt **kunna ta ut** loggarna för att se hur många som skickades som var **godkända** och **inte godkända** på alla saker som sker med dessa LEDs listor. Under vissa **perioder** och **tidsintervall**.

Detta ovan är **PERIOD 1**.

Nästa steg är att man tar ut loggarna på hur användarna har jobbat typ se hur många sälj en specifik användare har gjort och liknande.
