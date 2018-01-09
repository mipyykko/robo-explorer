### Viikkoraportti 4
###### 5.1.-9.1.
###### Käytetty aika: 15h

Nelospätkä näyttää koodin osalta samalta kuin kolmosen lopussa. Kuitenkin aikaa käytettiin varmasti suhteellisesti eniten tähän mennessä, vaikka samaan aikarakoon oli sovitettava myös lomailua. Raivoisan google-fun tuloksena kävi ilmi, että uusimmassa tälle ohjausyksikölle tarkoitetussa LejOS-versiossa ([0.9.1beta-3](https://sourceforge.net/projects/nxt.lejos.p/files/0.9.1beta-3)) on paljon aiemmasta puuttuvia valmiita ominaisuuksia, jotka tällaiseen kartoittavaan ja ympäristöään tutkivaan robottiin sopivat, mm. [Monte Carlo-lokalisaatio](https://en.wikipedia.org/wiki/Monte_Carlo_localization). 

Päivitys ei yhdessä RojbOS-virtuaalimasiinan kanssa sujunut aivan kivuttomasti, ja yhdessä BT-yhteyden toimimattomuuden kanssa tämä esti tehokkaasti testailun. Yhteys saatiin kuitenkin pelaamaan sopivasti kaksi tuntia ennen palautusta -- samalla kun USB-yhteys hajosi -- ja robotti saatiin kuin saatiinkin keskustelemaan BT-yhteydellä läppärillä pyörivän MCL-demo-ohjelman (ks. yllä) kanssa niin, että se osasi ympäristöään tarkkailemalla paikantaa itsensä ja korjata pienet virheet liikkeissä. 

Yhteyksien testailun kanssa oli paljon odotusaikaa, jota käytettiin myös hieman robotin rakenteen säätämiseen, ja nyt ultraäänisensoria siirrettiin hieman alemmas joka tosin teki	robotista hieman etupainoisemman. Kosketussensorillekin kokeiltiin sopivaa paikkaa.

Tunnelin päästä näkyy siis jo hieman valoa. Tulevalla pätkällä lienee siis luvassa kokeilua läppärin kanssa kommunikoivan robotin kanssa ja testauksen mietintää. Robotin rakenteeseen ei tässä vaiheessa kannattane tuhlata aikaa, vaan itse toiminnallisuuden tulisi nyt olla pääasia ja turhauttavan säädön jälkeen onkin mukava päästä kirjoittamaan lisää itse koodia.

