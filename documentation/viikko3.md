### Viikkoraportti 3
###### 30.12.-4.1.
##### Käytetty aika: 12h

Kolmosviikolla ei totta puhuen saatu juuri mitään konkreettista aikaan. Aikaa on kulunut suhteellisesti eniten tiedonhakuun, sillä aihe on kiinnostanut selkeästi aika monia muitakin. LejOS-APIa on kuiteknin tutkiskeltu sen verran, että aiemmin itse (huonosti) koodattuja virityksiä on voitu korvata jo olemassaolevilla. Lähdettiin myös miettimään kartoitusta niin, että robotti merkitsee karttaan todennäköisyyksiä, että jossain ruudussa on este. Tämän toimivuutta on ollut kuitenkin hieman haastavaa debugata, sillä USB- ja BT-yhteydet ohjausyksiköstä tietokoneeseen eivät tunnu oikein luonnistuvan. Robotti ajelee nyt autuaasti pitkin seiniä -- jos viime viikolla oli jo tarkoitus miettiä liikkumisen aikana ympäristön tarkkailua, niin sitä on tarkoitus miettiä myös vastakin. Ja ehkä myös toteuttaa. Robotin rakenteessa on kyllä tilaa puskurille ja kosketussensorille, jos tarve sitä vaatii.

Konfiguraatioluokkaa on vähän virtaviivaistettu niin, että turhia konvertointeja eestaas on poisteltu. Lisäksi asetusvalikkoa oli aikaa luonnostella laivamatkalla, jossa sitä tosin ymmärrettävästi ei alettu testaamaan.  VirtualBoxia on myös päivitetty, ja GitHubin commit-historiaa onnistuttu sotkemaan melkoisesti kun aiemmin oli unohtunut määritellä oletuskäyttäjä.

Kartoituksessa pitäisi tulevaisuudessa merkata myös muita ruutuja kuin aivan viereisimmät, jos robotti näkee selkeästi pidemmälle. Näytteitä pitää luultavasti ottaa huomattavasti enemmän kuin nykyiset kolme kerralla, mutta isossa näytemäärässä ja isommalla kartalla ohjausyksikön rajallinen muistikapasiteetti tulee esteeksi. Paras vaihtoehto olisi tietenkin viestiä tietokoneen kanssa BT-yhteydellä ja tallentaa dataa sinne -- myös (mahdolliset) vaativammat laskelmat voisi siinä tapauksessa siirtää pois ohjausyksiköstä.

Testausta varten olisi syytä viritellä joku selkeä, yksinkertainen alue josta robotin pitäisi selvitä. Liikkumisen ja kääntymisen kalibrointi olisi myös hoidettava jotenkin. 

[Kuvakin robotista on nyt.](robo.jpg)
