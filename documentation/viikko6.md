
### Lopullinen palautus
###### 15.1.-21.1.
###### Käytetty aika 18h

Ns. muu elämä alkoi vaivata tässä vaiheessa melkoisesti eikä edellisraportin lopussa mainittu turnausväsymys varsinaisesti osoittanut laantumisen merkkejä. Kuitenkin intoa löytyi sen verran, että robottiin löytyi tilaa kosketussensorissa kiinni olevalle puskurille ja onpa paikka myös valosensorille -- tosin kääntyminen johtojen kanssa ei nykyisellä konfiguraatiolla kovin hyvin onnistuisi. Hetken harkittu koko robotin uudelleenkasaus tämän takia ei enää sitten lopulta kovin järkevältä tuntunutkaan.

Koodipuolella kaikenlainen logiikka on siirtynyt kokonaan robotista tietokoneelle. Liikkumisalgoritmiin ei ole panostettu yhtään, eikä edellisviikkona kokeiltua virheenkorjausta jääty jumppaamaan, mutta pääasia eli robotin ympäristöä ns. [occupancy grid mapping](https://en.wikipedia.org/wiki/Occupancy_grid_mapping)-menetelmällä piirtyy jollain tavalla ruudulle. On otettu huomioon se, että ultraäänisensorin näkökenttä ei ole aivan viivasuora, joten karttaan lisätään todennäköisyyksiä tietyn astealueen sisältä jokaisesta hyväksyttävästä sensorilukemasta. Ahtaassa kämpässä ei tosin ole tilaa rakentaa testirataa, joten mittausten oikeellisuudesta ei ole mitään takuita.

Millään saralla valmista -- oikeastaan edes demovalmista -- robotista ei tullut, ja työ tuntui kaikkine konffausjumituksineen suurimmaksi osaksi turhauttavalta. Aihealue osoittautui hyvin äkkiä melkoisen laajaksi ja tällaisen lyhyen projektin mittakaavassa joidenkin ominaisuuksien toteuttaminen ei ehkä ole mahdollista eikä varsinkaan järkevää. Opin projektin aikana kuitenkin melkoisen paljon tällaisen [SLAM](https://en.wikipedia.org/wiki/Simultaneous_localization_and_mapping)-kartoituksen hankaluuksista ja perusperiaatteista, mutta toivottavasti niihin ei tarvitse hyvin äkkiä koskea uudelleen.
