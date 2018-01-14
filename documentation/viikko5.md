
### Viikkoraportti 5
###### 10.1.-14.1.
##### Käytetty aika: 17h

Kun workflow saatiin BT-yhteyden kanssa kuntoon, päästiin kunnolla taas koodin pariin. Koodattiin robotti katselemaan viiteen eri kulmaan ja tekemään liikkumisvalintansa sen mukaan, pysähtyen kuitenkin jos matkalla eteen jotain tulee. Ensikokeilu kurssinkorjauksen kanssa ei ollut kovinkaan järkevä. Robotin ylitykset joka puolelta ovat edelleen liian isoja eli se jää kiinni ja törmäilee, mikä tuli harvinaisen selväksi kokeiltaessa pahvilaatikkojen kanssa.

Koodattiin graafinen käyttöliittymä tietokoneelle -- omassa hakemistossaan GitHubissa, saanut *hyvin* paljon vaikutteita demoista -- johon robotti saatiin loppujen lopuksi piirtämään reittiään ja visualisoimaan sensorihavaintoja. [Testausdokumentissa](testaus.md) lisää tietoa neliötesteistä. LejOSin MCLPoseProvider yhdessä DifferentialPilotin kanssa tuntuu tiettyyn rajaan asti pysyvän perässä liikkeissä, mutta toki ilman kompassia jossain vaiheessa sekoaminen tapahtuu, varsinkin jos robotti osuu johonkin tai alusta muuttuu -- jo hieman rutussa oleva teippi saattoi heittää kurssin pieleen.

Turnausväsymys vaivaa ja kun aika pitkään on ollut selvää, ettei alunperin suunniteltuja ominaisuuksia kovinkaan montaa saada toteutettua, viimeisen pätkän tavoite lienee vain saada robotti tekemään "jotain." Eräällä tavalla toki robotti kartoittaa jo ympäristöään, mutta itse liikkumisalgoritmi on edelleen melkoisen köpö.  

