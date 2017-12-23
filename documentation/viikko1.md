### Viikkoraportti 1
###### 19.12.-23.12.
###### Käytetty aika: 12h

Kehitysympäristö viriteltiin VirtualBoxiin jo alkupalaverin takana. Palikkasetin raahauksen jälkeen testailtiin yksinkertaisia moottoritoimintoja ja antureita irrallaan, ja todettiin että USB-yhteys on koneen päässä katoavaista sorttia ja käytännössä aina ensimmäinen ohjelman lähetys ohjausyksikköön epäonnistuu --- minuutin odottelun jälkeen. 

Seuraavana päivänä päästiin jo parempaan vauhtiin. Mitään varsinaista suunnitelmaa ei ollut, mutta kokeilun jälkeen kasassa oli kahdella moottorilla ja ultraäänitunnistimella varustettu itsemurhaa yrittävä auto. Viimeksimainittu ei ollut haluttu ominaisuus, joten korjattiin robotti yrittämään väistämistä. Tukeva rakenne ei vaikuttanut kovin helpolta saavuttaa, mutta onneksi googletuksella tähän löytyi huomattavasti vinkkejä. Palikkasetin pienuus vaivasi heti alussa, mutta illalla kuitenkin valmiina oli nelipyöräinen auto jossa oli myös moottorilla toimiva ohjaus.

Mm. huomattavan pitkän junamatkan --- palikkalaatikon kanssa --- sisältäneen muutaman päivän mietintätauon aikana en kuitenkaan tälle autolle mitään järkevää funktiota keksinyt, mutta kun aiempia robolabroja ja muuta nettimateriaalia tutkiessa tuli kuitenkin mieleen huonetta kartoittava robotti.

Siispä kasailemaan yksinkertaisempaa robottia jossa mallina toimi näissä ilmeisen suosittu valinta [Castor Bot](http://www.nxtprograms.com/castor_bot/index.html). Huomattiin kuitenkin, että palikkasetistä puuttuivat nuo varsin olennaiselta vaikuttaneet tasapainottavat pyörät joten ensimmäinen versio saa luvan hiihtää suksien päällä. Ultraäänisensorin ja sitä ohjaavan moottorin huomattavan kiikkerä paikka ohjausyksikön päällä vaikuttaa myös miettimisen arvoiselta. Robotti kuitenkin liikkui ja pysyi pystyssä, kun sillä ajettiin aiemmalle robotille luotua testikoodia, jota ei nyt ensimmäiseen palautukseen GitHubiin kuitenkaan laiteta.

Seuraavalla viikolla pyritään varmaankin toteuttamaan ainakin jollain tasolla perusominaisuuksia toteuttava ohjelma. Alusta pitäen lienee syytä miettiä robotin konfiguroitavuutta suoraan ohjausyksiköstä. Lisäksi -- jos yliopistolla nyt väliviikolla ketään on tai edes itse olen kaupungissa -- voisin kysellä tai etsiskellä puuttuvia pyöriä. Kehitysympäristöä voisi myös yrittää viritellä.

(NB: Myös täällä ongelmana tunti ennen palautusta huomattu Labtoolin hajoaminen, joten dedis lipsuu siinä. GitHubin commiteista voinee kuitenkin kellonaikoja skouttailla.)

