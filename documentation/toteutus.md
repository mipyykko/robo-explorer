
### Toteutusdokumentti
###### päivitetty 21.1.2018 
Robotin puolella rakenne on jotakuinkin seuraava:

* RoboExplorer - pääluokka joka vain käynnistää robotin
	* logic: Explorer - itse robotin pääohjelma
	* comm: RoboConnection - yhteydet robotin ja tietokoneen välillä
	* config: Config - robotin asetukset ja niihin liittyvät metodit
	* ui: Menu - valikkorakenne (johon ei ole satsattu, en tiedä edes toimiiko se)  
		* Screen - näyttötoiminnot
	* util: apuluokkia esim. tekstin käsittelyyn

GUI-puolella rakenne taas on seuraavanlainen:

* Main - pääluokka joka vain käynnistää GUIn
	* logic: Logic - robotin logiikka
	* gui: GUI - graafinen käyttöliittymä  
		* RobotCanvas - kartan visuaalinen puoli
	* conn: RoboConnection - yhteydet robotin ja tietokoneen välillä
	* model: RobotMap - itse päivitettävä kartta  
		* RobotData - wrapper-objekti jolla siirretään robotin sensoridataa luokkien ja metodien välillä
	
Ohjelmassa käytetään LejOS-versiota [0.9.1beta-3](https://sourceforge.net/projects/nxt.lejos.p/files/0.9.1beta-3) sen tarjoamien lisäominaisuuksien takia.  Korkein Java-SDK-versio millä ohjelman on todettu kääntyvän robotilla toimivaksi on 1.7.



