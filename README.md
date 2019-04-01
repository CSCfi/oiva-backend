                ____  _
               / __ \(_)
              | |  | |___   ____ _
              | |  | | \ \ / / _` |
              | |__| | |\ V / (_| |
     ____      \____/|_| \_/ \__,_|      _
    |  _ \           | |                | |
    | |_) | __ _  ___| | _____ _ __   __| |
    |  _ < / _` |/ __| |/ / _ \ '_ \ / _` |
    | |_) | (_| | (__|   <  __/ | | | (_| |
    |____/ \__,_|\___|_|\_\___|_| |_|\__,_|

# Buildaus- ja kehitysympäristön asentaminen

Tervetuloa Oiva-projektin pariin! Näiden ohjeiden myötä sinun on mahdollista saada projekti nopeastikin käyntiin, mutta älä lannistu, jos koet vastoinkäymisiä asennuksia tehdessäsi. Ohjeita päivitetään tarpeen vaatiessa, mutta toisinaan paras apu kipakka, kehittäjien suunnattu, kysymys.

## Alkuvalmistelut

Asenna koneellesi seuraavat asiat. Tarkemmat ohjeet on kerrottu alempana, numeroiduin otsikoin:
* [Docker](https://www.docker.com/).
* [Java 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
* [Maven](http://maven.apache.org/) (3.3.1 tai uudempi).
* [PrinceXML](https://www.princexml.com/).
* IDE, kuten [VS Code](https://code.visualstudio.com/) tai [IntelliJ](https://www.jetbrains.com/idea/).

Mikäli käytät VS Code:a, kannattaa siihen asentaa hyödyllisiä lisäosia, kuten seuraavat:
* Markdown All in One.

## 1. Docker
Asenna Docker. Dockeria käytetään virtuaalikoneiden verkon luontiin. Verkon myötä kehittäjillä on käytössään yhtenevä kehitysympäristö. Tässä vaiheessa riittää, että saat Dockerin asennettua. Sen käyttöön palataan näissä ohjeissa myöhemmin.

## 2. Java
Asenna Java. Mikäli sinulla on jo ennestään jokin toinen Java-versio asennettuna ja käytät Mac:ia, voit hallinnoida versioasennuksia [Jenv](http://www.jenv.be/):llä.

## 3. Maven
Asenna Maven. Mavenin suhteen ei tarvitse tehdä mitään erikoisia temppuja. Riittää, että siitä on asennettuna tarpeeksi tuore versio.

## 4. PrinceXML
Asenna PrinceXML. PrinceXML-kirjastoa käytetään PDF-tiedostojen generointiin. Projektissa on ollut tähän asti käytössä versio [Prince 10](https://www.princexml.com/releases/10/), mutta tuoreimman version käyttämiselle ei välttämättä ole esteitä. Asennuksen tehtyäsi varmista, että tiedoston `amos-backend/src/main/resources/config/application.yml` kohta `prince.exec.path` viittaa hakemistoon, johon PrinceXML on asennettu. Suorita tämän jälkeen seuraava komento:
```
./install-prince.sh
```

## 5. IDE
IDE:n suhteen ei ole sen kummempia vaatimuksia. VS Code ja IntelliJ ovat hyväksi todettuja vaihtoehtoja.

## 6. Projektin kääntäminen
Kun olet käynyt kaikki edellä listatut kohdat läpi ja asentanut tarvittavat asiat, voit kokeilla kääntää projektin koodin. Suorita seuraava komento projektin juurihakemistossa:
```
mvn clean package
```

## 7. Oiva-puolen palveluiden käynnistäminen
Projektin juurihakemistossa on tiedosto `oiva-docker.sh`. Siihen on sisäänleivottu komento, jota usein käytetään käynnistämään Docker-kontit. Eli Dockerin näkökulmasta riittää, että suoritat seuraavan komennon:
```
./oiva-docker.sh start --amos
```
*! Mikäli haluat käynnistää myös Kuja-puolen backend-palvelut, jätä lippu `--amos` pois yllä olevasta komennosta.*

*! Sovellusta ajettaessa täytyy konfiguraatioiden olla halutun profiilin mukaisessa, `application.yml` tai `application-dev.yml`, tiedostossa. Muuten konfiguraatiot luetaan projektin sisältä. Käännettäessä sovellus, pitää aina muistaa valita myös maven profiili -P dev tai -P prod, jotta pakettiin tulevat mukaan tarvittavat resurssit.*

## 8. Tietokantarakenteen luonti, populointi ja puhdistus
Alusta tietokannat:
    
    $ ./oiva-db.sh amos init
    $ ./oiva-db.sh yva init

Populoi tietokannat:

    $ ./oiva-db.sh amos generate --clean --populate
    $ ./oiva-db.sh yva generate --clean --populate

Tietokannan ajantasaisuudesta vastaa [Flyway](https://flywaydb.org/)-migraatiotyökalu. Sinun ei tarvitse asentaa työkalua.

## 9. Flyway-migraatioiden ajaminen Mavenilla ja JOOQ-tietokantaluokkien generointi

Maven Flyway plugin vie migraatioista löytyvät tietokantarakenteet (aluksi: ``resources/db/migration/V1__Baseline.sql``) 
käännöksen yhteydessä tietokantaan jos Mavenin generate-db -profiili enabloidaan.

Projektissa käytössä oleva JOOQ generoi suoraan tietokannasta käytettäviä DAO- ja entiteettiluokkia. Jos 
tietokantatauluihin tulee muutoksia, JOOQ-luokat pitää generoida uudelleen. JOOQ-luokat generoidaan
generate-db -profiiilin ajon yhteydessä.

**Aja Flyway ja JOOQ:**

    $ mvn -Doiva.dbhost=$POSTGRES_IP -Doiva.dbport=$POSTGRES_PORT -Doiva.dbname=$DBNAME -Doiva.dbusername=$DBUSER -Doiva.dbpassword=$DBPASSWORD compile -Pgenerate-db


## Tietokannan puhdistus ja populointi Maven SQL pluginilla

#### Pudota flyway-migraatiokanta ja sovelluksen skeemat

    $ mvn -Doiva.dbhost=$POSTGRES_IP -Doiva.dbport=$POSTGRES_PORT -Doiva.dbname=$DBNAME -Doiva.dbusername=$DBUSER -Doiva.dbpassword=$DBPASSWORD initialize sql:execute@clean-db
   
#### Entityjen luominen 

    $ mvn -Doiva.dbhost=$POSTGRES_IP -Doiva.dbport=$POSTGRES_PORT -Doiva.dbname=$DBNAME -Doiva.dbusername=$DBUSER -Doiva.dbpassword=$DBPASSWORD compile -Pgenerate-db

#### Populoi tietokanta alustavalla datasetillä

Tietojoukko sisältää alkuvaiheen realistista lupadataa. Huom! pohjadata on yksityisessä oiva-deployment repository:ssa johon plugin viittaa.
    
    $ mvn -Doiva.dbhost=$POSTGRES_IP -Doiva.dbport=$POSTGRES_PORT -Doiva.dbname=$DBNAME -Doiva.dbusername=$DBUSER -Doiva.dbpassword=$DBPASSWORD initialize sql:execute@populate-db


## Aja kehitysversiota lokaali-Mavenilla

Käynnistä docker:

    $ ./oiva-docker.sh start
    
Docker-compose luo viisi palvelua: amos-postgres, amos-redis, yva-postgres, yva-redis ja nginx.

Backend-palvelun käynnistäminen kehityskäyttöön:

    $ ./oiva-backend.sh amos -c
    $ ./oiva-backend.sh yva -c

Mikäli haluat syöttää kehittäjäkohtaisia JVM-argumentteja backend-palvelulle niin luo `vars-KÄYTTÄJÄNIMESI.sh` tiedosto, esimerkiksi `vars-aheikkinen.sh`. Tiedosto voi sisältää bash-muuttujia jotka ladataan automaattisesti mukaan kun `launch-dev-backend.sh` suoritetaan.


## PDF-exportin konfiguroiminen [TODO: UPDATE]

PrinceXML tuottaa PDF-tiedostoja HTML-lähteistä jotka on muodostettu Pebble-frameworkillä (http://www.mitchellbosecke.com/pebble).
- Pebblen kontekstit muodostetaan fi.minedu.oiva.backend.service.TemplateRenderingService luokassa.
- Pebblen templatet ja resurssit on määritetty omassa git-respositoryssa (/oiva/template). Hanki templatet koneelle jolla ajat backendiä

Pebble-templateihin viitataan absoluuttisilla poluilla, esimerkiksi:

``/opt/oiva/backend/template/default/hakemus/hakemustemplate_fi.html``
``/opt/oiva/backend/template/valitus/vali_fi.PDF``

Polut muodostuvat kolmesta osasta: juuripolku, versiopolku ja templaattitiedosto. Yo. esimerkeissä 
- juuripolku on ``/opt/oiva/backend/``
- versiopolku on ``template/default/``
- templaattitiedosto on ``hakemus/hakemustemplate_fi.html``

Juuripolku tulee määrittää sovelluksen konfiguraatioissa (ks. alla) ja versiopolku voidaan valinnaisesti määrittää hakukierros-kohtaisesti tietokannassa (ks. alla)

Määritä sovelluksen application.yml tiedoston ``templates.base.path`` osoittamaan juurikansioon josta templatet löytyvät (tämä on templaten **juuripolku**). Esimerkiksi jos templatet löytyvät
polusta /opt/oiva/backend/template niin ko. asetuksen tulee olla ``templates.base.path: /opt/oiva/backend/``

Joissain tilanteissa ``oiva.hakukierros`` tietokantatauluun on voitu määritetty omat templatepolut ``templatepath`` sarakeeseen (tämä on templaten **versiopolku**). Tämä ei ole pakollista, sillä mikäli
ko. kenttä on tyhjä niin sovellus käyttää oletustemplateja (template/default/). 

Kokeile tuottaa PDF-tiedosto seuraavan rajapinnan kautta: http://localhost:8099/pdf/esikatselu/hakemus/xx jossa xx on hakemuksen id


## Sananen konfiguraatiosta

Konfiguraatiot otetaan käyttöön seuraavassa järjestyksessä:

1. Suorat komentoriviargumentit Mavenille (esim: ``-Dredis.host=myredishost.com``)
2. Profiilispesifiset konfiguraatiot jar-paketin ulkopuolella (ei käytössä toistaiseksi)
3. Profiilispesifiset konfiguraatiot (``<project-root>/src/main/resources/config/application-{profile}.yml``)
4. Yleiskonfiguraatio jar-paketin ulkopuolella (kehittäessä ``<project-root>/application.yml`, tai jar-paketin ulkopuolella palvelimella)
5. Yleiskonfiguraatio jar-paketissa (``<project-root>/src/main/resources/config/application.yml``)

Konfiguraatiotiedostojen Maven-filtteröintiä (e.g. ${placeholder}:ien korvausta) voi käyttää jar-paketin sisällä 
oleville konfiguraatioille.


## Testit

Testeihin käytetään ScalaTest-kirjastoa ja Springin tarjoamia apuvälineitä. Yksikkö- ja integraatiotestit erotellaan
tiedostopäätteellä. Yksikkötestit ovat muotoa \*Test, esim: *MinunHienoTest*, ja integraatiotestit ovat muotoa \*IT, esim:
*MinunHienoIntegroivaIT*.

Ennen testien ajoa luo backend-projektin juureen `application-test.yml` konfiguraatiotiedosto jossa määrittelet ainakin seuraavat arvot:
- `opintopolku.apiCredentials.username`
- `opintopolku.apiCredentials.password`

sekä mahdollisesti myös:
- `spring.datasource`

**HUOM!** Nykyinen tietokantojen luontiskripti ei luo tarvittavaa testi-tietokantaa joten se tulee luoda manuaalisesti:
    $ CREATE DATABASE "oiva-test" WITH OWNER oiva;

Aja yksikkötestit: 
    
    $ mvn test
    
Aja integraatiotestit:
    
    $ mvn integration-test # huom, lisää kanta-IP:t jos tarvitsee

**HUOM!** Varmista että src/test/resources/application-test.yml konfiguraation ``opintopolku.apiOrganization.oid`` arvo vastaa testikäyttäjän organisaatiota.
Testikäyttäjän tunnukset syötetään seuraavien konfiguraatioiden kautta ``opintopolku.apiCredentials.username`` ja ``opintopolku.apiCredentials.password``.
Helpoin tapa organisaatio OID:n tarkastamiseen on kirjautua OIVA:n DEV-ympäristöön testikäyttäjän tunnuksilla ja tarkastaa minkä organisaation tiedot sovellus lataa.

Yksittäisen testin voi ajaa seuraavasti:

    $ mvn -DwildcardSuites=fi.minedu.oiva.backend.test.SortingTest test


## Debug ja JRebel

Remote debug ja JRebel voidaan enabloida Spring Boot Maven pluginin konfiguraatiossa kohdassa``<jvmArguments>``. 
Blokkiin voidaan syöttää arvo komentokehotteelta ympäristömuuttujalla ``jvm.fork.arguments``.

Esimerkki (yhdelle riville):

    $ mvn spring-boot:run -Djvm.fork.arguments='-Djava.rmi.server.hostname=localhost -Xdebug 
    -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
    -Dproject.path=/path/to/oiva/backend -noverify 
    -agentpath:/patg/to/jrebel6/lib/libjrebel64.dylib'
    
    
**HUOM:** ``<jvmArguments>``-blokki forkkaa uuden JVM-prosessin Spring bootille.
        
    
## Paketointi serveriympäristöihin

Voit rakentaa paketit ajamalla:

mvn package -P dev  ( tai prof profiililla )

Jos käytät samalla paketin ulkopuolista konfiguraatiota. Mikäli haluat konffit paketin sisään, anna ne mukaan paketoinnissa.

Paketit rakennetaan oikeilla tietokanta-IP:illä, postgressin salasanalla ja Mavenin profiililla 
(yleensä -Pprod, tekee prodiin ja stagingiin sopivan paketin):

    $ mvn -Doiva.dbhost=oivatestdb.csc.fi -Dredis.host=127.0.0.1 -Doiva.dbpassword=<INSERT REAL PASSWORD HERE> -Pprod package # build prod package
   
Tällöin asetukset ovat hardkoodattuina paketin sisällä, eikä ulkoista konfiguraatiota tarvita.   
   
   
## Serverillä manuaalinen backendin käynnistys stagingissa:

    $ java -jar oiva-backend.jar --spring.profiles.active=staging --server.port=8080
    
   Manuaalinen konffiesimerkki:
    
    java -jar oiva-backend.jar --server.port=8080 --oiva.dbhost=192.168.1.165 --oiva.dbpassword=oiva --redis.host=192.168.1.199  --spring.profiles.active=prod
