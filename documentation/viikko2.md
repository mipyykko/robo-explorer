### Viikkoraportti 2
###### 24.12.-29.12.
##### Käytetty aika: 11h

Luotiin uusi projektipohja esimerkin pohjalta. Aluksi lähdetty koodaamaan lähinnä pakettirakennetta ja konfiguraatioluokkaa johon heti kättelyssä mahdollisuus ladata ja tallentaa asetuksia ohjausyksikön muistiin lähinnä säästämään aikaa ja hermoja testausvaihetta silmälläpitäen. Aiemman kokeilun perusteella tiedän suurinpiirtein mitä logiikkapuolelle on tulossa ainakin perusominaisuuksia varten. Koska robotti hiihtää edelleen suksien varassa, testausta on turha aloittaa ennen puuttuvien osien saamista.

Kotimatkalla junassa oli hyvää aikaa kirjoittaa mm. näyttörutiineja ja ihmetellä LejOSin Java-tulkin köyhyyttä. (Myöhemmin viikolla säädin jonkin aikaa yrittäen saada vempeleen tajuamaan Javan seiskaversiota.) Puuttuvat pikkupyörät käytiin hakemassa Kumpulasta ja todettiin heti, että robotti liikkuu ja kääntyy niiden avulla melkoisen varmasti. Sensorirakennelma tuntui vähän huteralta, joten sitä vahvistettiin. Koodattiin perusominaisuus, jossa robotti ensin katselee vasemmalle, eteen ja oikealle ja sitten tekee jonkinlaisen päätöksen kulkusuunnasta näkemänsä perusteella. Ultrasensori on aivan liian korkealla ja takana joten vähänkin korkeampia esineitä robotti ei havaitse.

Siispä rakennetta uusiksi: ohjausyksikköä vähän taaksepäin ja sensorirakennelmaa eteenpäin ja alas. Yhdestä sensoriportista on nyt pakko tinkiä koska tukirakennelma peittää sen -- toisaalta sensoreita ei taida mahdollista puskuria lukuunottamatta enää tähän lisää tulla. Nyt robotti havaitsee ympäristönsä paremmin, ja kun sen mitat on määritelty, se myös tekee käännöksensä suht tarkkaan. Kartoitus ei oikeastaan tähän asti ollut tehnyt juuri mitään, mutta sitä täytyy miettiä esimerkiksi kulkusuuntien suhteen. Robotti voi edelleen surrata ulos kartalta, jolloin seurauksena on virheilmoitus.

Seuraavan viikon ohjelmassa onkin hyvin todennäköisesti juuri jonkinlaisen sisäisen kompassin virittely -- robotin kääntymiset on syytä saada synkattua oikeaan kulkusuuntaan ja ehkäpä myös olemassaolevaan karttaan. Kartoitusmetodi on melkoinen sekamelska nykyisellään. Myös konfiguraatiot olisi hyvä saada valikkoon muokattaviksi. Robotin rakenne vaikuttaa nyt suht hyvältä, mutta esimerkiksi puskurille kosketusantureineen olisi tilaa ja tilausta. Testaus tulee olemaan kotona tilanpuutteen takia varsin hankalaa.