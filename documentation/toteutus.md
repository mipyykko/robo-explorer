##Toteutusdokumentti
###### päivitetty 9.1.2018 
Ohjelman yleisrakenne on tällä hetkellä edelleen hieman vaiheessa, mutta suurinpiirtein näin:

* RoboExplorer - pääluokka joka vain käynnistää robotin
	* logic: Explorer - itse robotti ja sen logiikka
	* config: Config - robotin asetukset ja niihin liittyvät metodit
	* model: RoboMap - kartta ja siihen liittyvät toiminnot
	* ui: Menu - valikkorakenne
		Screen - näyttötoiminnot
	* util: apuluokkia esim. tekstin käsittelyyn

Ohjelmassa on siirrytty käyttämään LejOS-versiota [0.9.1beta-3](https://sourceforge.net/projects/nxt.lejos.p/files/0.9.1beta-3) sen tarjoamien lisäominaisuuksien takia. Tässä vaiheessa niitä ei tosin vielä käytetä, eli koodi kääntyy myös kurssin dokumentaatiossa käytetyllä versiolla. Korkein Java-SDK-versio millä ohjelman on todettu kääntyvän robotilla toimivaksi on 1.7.

Ohjelmasta puuttuu tällä hetkellä määrittelyssä toivottu logiikka vaikka käynnistäessä robotti jollain hämärällä perusteella johonkin suuntaan liikkuukin. Myöskään karttaa se ei ilmeisesti vielä osaa tehdä, vaikka debugatessa logiin joka siirron jälkeen sellaisen puskeekin - tyhjänä. Tähän lähdetäänkin seuraavaksi paneutumaan, kun tekniset vaikeudet on toivottavasti voitettu.
