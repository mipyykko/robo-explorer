## Suunnitelma
Toteutetaan robotti, joka tutkii ja kartoittaa ympäristöään ja kartoittaa sitä.


#### Toteutustapa
Robotti rakennetaan Lego Mindstorms NXT 1.0-sarjasta, ja sitä kontrolloiva ohjelma toteutetaan Javalla niin, että robotin ohjausyksikköön on asennettu leJOS-firmware. Ainakin aluksi käytetään virtuaalikoneella suoritettavassa RojbOS-käyttöjärjestelmässä Eclipse-kehitysympäristöä.

#### Perusominaisuudet
Robotilla on kaksi moottoreihin kytkettyä rengasta ja jokin tasapainottava kolmas kosketuspinta -- pyörä, suksi tai jokin vastaava, riippuen osien saatavuudesta. Robotissa on myös ultraäänisensori, joka pyörii oman moottorinsa ohjaamana eri suuntiin.  
  
Robotin toiminta:  
  
* Robotti asetetaan halutuun alkupisteeseen, jonka jälkeen ohjelma käynnistetään
* Robotti kalibroi sensorin, moottorien senhetkiset tilat ja alkaa kartoittaa tilaa tietynkokoisiin sektoreihin jaettuna
*  Joka sektoriin saapuessaan robotti pysähtyy hetkeksi katsomaan sivuilleen jonka jälkeen se jatkaa siihen suuntaan missä se ei ole vielä ollut tai mihin se voi vielä mennä 
* Jos robotti on kartoittanut koko alueen, se ilmoittaa siitä äänimerkillä ja näyttää kartan ruudullaan 
* Robotti kalibroi suuntaansa niin, että se matkaa mahdollisimman suoraan ja käännökset ovat mahdollisimman lähellä suorakulmaisia
#### Lisäominaisuudet
Nämä riippuvat ajankäytöstä, saatavilla olevista osista ja pystytäänkö (edes) perusominaisuudet toteuttamaan.
 
* Törmäyksen tunnistus kosketussensorilla?
* Robotin konfigurointi ohjausyksiköllä ennen ohjelman käynnistystä -- tilan koko, haluttu tarkkuus, robotin ulottuvuudet?
* Robotti palaa kotiin kun se on valmis?
* Kartan jakaminen tietokoneelle?
#### Mahdolliset ongelmatapaukset
* Kahden robottia liikuttavan moottorin synkronoinnin vaikeudet --- liikkuuko robotti tarpeeksi suoraan?
* Robotin painottaminen niin, että se on balanssissa
* Eksyminen, sivutörmäykset, jne.
