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

Tervetuloa Oiva-projektin pariin! Näiden ohjeiden myötä sinun on mahdollista saada projekti nopeastikin käyntiin. Älä kuitenkaan lannistu, jos koet vastoinkäymisiä asennuksia tehdessäsi. Toisinaan paras apu on kipakka kehittäjille suunnattu kysymys. Vastauksen saatuasi voit täydentää ohjeiden puutteita.

- [Buildaus- ja kehitysympäristön asentaminen](#Buildaus--ja-kehitysymp%C3%A4rist%C3%B6n-asentaminen)
  - [Pikaohje](#pikaohje)
  - [Alkuvalmistelut](#Alkuvalmistelut)
    - [1. Docker](#1-Docker)
    - [2. Java](#2-Java)
    - [3. Maven](#3-Maven)
    - [4. PrinceXML](#4-PrinceXML)
    - [5. IDE](#5-IDE)
    - [6. Projektin kääntäminen](#6-Projektin-k%C3%A4%C3%A4nt%C3%A4minen)
        - [6.1 Huomioita](#61-Huomioita)
    - [7. Tietokanta](#7-Tietokanta)
    - [8. Palveluiden käynnistäminen](#8-Palveluiden-k%C3%A4ynnist%C3%A4minen)
        - [8.1 Docker-palvelut](#81-Docker-palvelut)
        - [8.2 Sovelluspalvelin](#82-Sovelluspalvelin)
        - [8.3 Huomioita](#83-Huomioita)
- [Lisätietoa ja tarkempia kuvauksia](#lisätietoa-ja-tarkempia-kuvauksia)
  - [Debuggaus](#debuggaus)
  - [Apuskriptit](#apuskriptit)
  - [Testien ajaminen](#testien-ajaminen)
      - [Yksikkötestit](#yksikkötestit)
      - [Integraatiotestit](#integraatiotestit)
  - [Pebblen ja PrinceXML:n käyttö](#pebblen-ja-princexmln-käyttö)
  - [Debug ja JRebel](#Debug-ja-JRebel)
  - [Sananen konfiguraatiosta](#Sananen-konfiguraatiosta)
  - [Paketointi serveriympäristöihin](#Paketointi-serveriymp%C3%A4rist%C3%B6ihin)
  - [Serverillä manuaalinen backendin käynnistys stagingissa:](#Serverill%C3%A4-manuaalinen-backendin-k%C3%A4ynnistys-stagingissa)

## Pikaohje

Ohjeet Oiva-palvelun käynnistykseen tilanteessa, jossa kaikki tarvittavat riippuvuuudet on asennettu ja tietokanta importattu jo aikaisemmin:
* Docker-konttien käynnistys: `./oiva-docker.sh start`
* Koodien kääntäminen: `mvn clean install`
* Sovelluspalvelimen käynnistys: `./oiva-backend.sh -c`
* Käyttöliittymäkoodit: oiva-frontend-repositoryn juuressa `npm install && npm start`

Oiva-palvelu vastaa osoitteesta https://localhost

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
Asenna [Docker](https://www.docker.com/get-started). Dockeria käytetään virtuaalikoneiden verkon luontiin. Verkon myötä kehittäjillä on käytössään yhtenevä kehitysympäristö. Tässä vaiheessa riittää, että saat Dockerin asennettua. Sen käyttöön palataan näissä ohjeissa myöhemmin.

Asenna myös [docker-machine](https://docs.docker.com/machine/install-machine/). Sitä tarvitaan integraatiotestien ajoon.

## 2. Java
Asenna Java 8. Mikäli sinulla on jo ennestään jokin toinen Java-versio asennettuna ja käytät Mac:ia, voit hallinnoida versioasennuksia [Jenv](http://www.jenv.be/):llä.

## 3. Maven
Asenna Maven. Mavenin suhteen ei tarvitse tehdä mitään erikoisia temppuja. Riittää, että siitä on asennettuna tarpeeksi tuore versio esim. >= 3.6

## 4. PrinceXML
Asenna PrinceXML. PrinceXML-kirjastoa käytetään PDF-tiedostojen generointiin. Projektissa on toistaiseksi käytössä versio [Prince 13.5](https://www.princexml.com/releases/13/), mutta tuoreemman version käyttämiselle ei liene esteitä.

Mikäli et tee asennusta hakemistoon `/usr/bin/prince`, voit määrittää ohjelman sijainnin käynnistysparametreilla `vars-username.sh`-tiedostoon. Tarkempi kuvaus vars-tiedostosta on kohdassa [8.2 Sovelluspalvelin](#82-Sovelluspalvelin). Esim:
```
OIVA_JAVA_OPTS="-Dprince.exec.path=/usr/local/bin/prince"
```

## 5. IDE
IDE:n suhteen ei ole sen kummempia vaatimuksia. VS Code ja IntelliJ ovat hyväksi todettuja vaihtoehtoja.

## 6. Projektin kääntäminen
Kun olet käynyt kaikki edellä listatut kohdat läpi ja asentanut tarvittavat asiat, voit kokeilla kääntää projektin koodin. Suorita seuraava komento projektin juurihakemistossa:
```
mvn clean install
```

## 6.1 Huomioita

* Pakettiin mukaan tuleviin resursseihin voi vaikuttaa käyttämällä joko Maven-profiilia -P dev tai -P prod. Oletusasetus on -P dev.
* Kun backendiin tehdään muutoksia, joudut todennäköisesti rakentamaan paketit uudestaan ja tyhjentämään Redisin
````
mvn clean install
docker exec oiva-backend_amos-redis_1 bash -c "redis-cli FLUSHALL"
````

## 7. Tietokanta
Projektissa on käytössä [PostgreSQL](https://www.postgresql.org/)-tietokanta, joka pyörii Docker-kontissa, virtuaalikoneiden verkossa. Tietokantaa ei siis tarvitse asentaa omalle koneelle.
Tietokannan ajantasaisuudesta vastaa [Flyway](https://flywaydb.org/)-migraatiotyökalu. Sinun ei tarvitse asentaa työkalua.

Manuaalisia tietokantaoperaatioita varten on olemassa `oiva-db.sh`-skripti. Sen avulla on mahdollista tehdä mm. tietokannan palautus ja [jOOQ](https://www.jooq.org/)-entiteettien luonti.

Palautusta varten Docker-kontit pitää olla ajossa. Näiden käynnistäminen on ohjeistettu kohdassa [8.1](#81-Docker-palvelut). Lisäksi oiva-deplyment-repository pitää olla kloonattuna oiva-backend-hakemiston rinnalla. Tämä yksityinen repository sisältää oivan todellisen lupadatan.

Tietokannan luonti tai resetointi Oivalle:
```
$ ./oiva-db.sh create
```

Jos tietokantatauluihin tulee muutoksia, JOOQ-luokat pitää generoida uudelleen. Uudelleengeneroinnissa käännetään oiva-core-model-moduuli, joka sisältää tietokantarakenteeseen vaikuttavat tietokantamigraatiot. Migraatiot ajetaan tietokantaa vasten, jonka sen hetkisen rakenteen perusteella muodostetaan DAO- ja entiteettiluokat java-koodina.
```
$ ./oiva-db.sh amos generate
```

## 8. Palvelun käynnistäminen

Palvelu voidaan käynnistää docker-palveluna tai lokaalina sovelluspalvelimena (suositeltu kehityskäyttöön).

### 8.1 Docker-palvelu

Käynnistä docker:

    $ ./oiva-docker.sh start
    
Projektin juurihakemistossa sijaitsevaan `oiva-docker.sh`-tiedostoon on sisäänleivottu komento, jota usein käytetään käynnistämään Docker-kontit. Tyypillistä `docker-compose up` -komentoa ei siis tarvitse tässä projektissa käsin ajaa.
    
Docker-compose luo 3 palvelua: amos-postgres, amos-redis ja nginx.

### 8.2 Sovelluspalvelin

Konfiguraatioihin pitää lisätä kehittäjäkohtaiset JVM-argumentit backend-palvelulle. Luo `vars-KÄYTTÄJÄNIMESI.sh` tiedosto, esimerkiksi `vars-aheikkinen.sh`. Käyttäjänimen saa selville ajamalla terminaalissa komennon `whoami`. Tiedosto voi sisältää bash-muuttujia jotka ladataan automaattisesti mukaan kun `oiva-backend.sh` suoritetaan.

Syötä tiedostoon testi-opintopolun tarvitsemat käyttäjätunnus ja salasana. Nämä tiedot voi kysyä muilta kehittäjiltä.
 
    OIVA_JAVA_OPTS="-Dopintopolku.username='käyttäjätunnus' -Dopintopolku.password='salasana'"
    
Backend-palvelun käynnistäminen kehityskäyttöön:

    $ ./oiva-backend.sh -c

Backendin lisäksi yleensä tarvitaan frontendin käynnistys. Se tapahtuu oiva-frontend repositoryn juuressa:

    $ npm install && npm start

Tämän jälkeen Oivaa voi käyttää osoitteessa https://localhost

**Huom**

Https-liikenne edellyttää sertifikaattia. Kehitysympäristössä eli localhost-osoitteissa käytetään self signed -sertifikaattia, jota ei ole varmennettu luotettavasti. Selain varoittaa invalidista sertifikaatista, mutta varoituksen voi ohittaa.

### 8.3 Huomioita

Oivan backend-sovellus käynnistyy porttiin 8099 ja vastaa swagger-rajapintadokumentaatiolla osoitteesta http://localhost:8099.

Dockerin avulla käynnistettyä nginx-palvelinta käytetään reverse proxyna kehitysympäristössä. Sen avulla ohjataan https-liikenne oikeisiin kohdeosoitteisiin seuraavasti:

Oivan ohjaukset

|Selaimen osoite|Kohde|
|-----------------|---------------------|
|https://localhost|http://localhost:3000|
|https://localhost/api|http://localhost:8099|


# Lisätietoa ja tarkempia kuvauksia

## Debuggaus

Sovelluksen voi ajaa debug-moodissa lisäämällä d-parametrin käynnistyskomentoon, esim: 

`./oiva-backend.sh amos -c -d`

Yhteys otetaan Javan remote debuggerilla porttiin 5005.

## Apuskriptit

Muun muassa sovelluksen käynnistykseen ja testien ajoon liittyen on olemassa .sh-loppuisia skriptejä. Skriptien lyhyet infot saa näkyviin ajamalla skriptejä parametrilla `--help`. 

## Testien ajaminen

Yksikkötesteihin käytetään ScalaTest-kirjastoa ja Springin tarjoamia apuvälineitä. Yksikkö- ja integraatiotestit erotellaan
tiedostopäätteellä. Yksikkötestit ovat muotoa \*Test, esim: *MinunHienoTest*, ja integraatiotestit ovat muotoa \*IT, esim:
*MinunHienoIntegroivaIT*.

### Yksikkötestit

Aja yksikkötestit: 
    
    $ mvn test -DskipJavaUnitTests=false

Toistaiseksi muut kuin scalan yksikkötestit on asetettu oletuksena ohitettavaksi CI-ympäristön konfiguraatioista johtuen.  

### Integraatiotestit

Esiehtona integraatiotestien ajamiseen on docker-machinen asennus. Docker-machinen sisällä ajetaan redis-välimuistia ja postgresql-tietokantaa, joten niitä ei tarvitse itse käynnistää.

Aja integraatiotestit:
    
    $ ./run-integration-tests.sh

- Testit on mahdollista ajaa debug-moodissa käyttämällä parametria `--debug`, jolloin testiajo jää odottamaan debuggerin yhdistämistä ennen varsinaisten testien ajoa. Remote debuggerin portti on oletuksena 5005.
- Yksittäisen luokan testien ajaminen on mahdollista esim. `--tests '*MuutospyyntoControllerIT'`. Kaikki MuutospyyntoControllerIT-loppuisten luokkien testit ajetaan.
- Jos halutaan ajaa yksittäinen testi, niin loppuun pitää lisätä testifunktion nimi risuaidalla eroteltuna, esim. saveWithoutLogin-funktion ajaminen `--tests '*MuutospyyntoControllerIT#saveWithoutLogin'`

Mikäli testien ajo päätyy porttivirheeseen, esim. `Bind for 0.0.0.0:15432 failed: port is already allocated`, niin helpoimmalla pääsee poistamalla docker-machinen luoman virtuaalikoneen komennolla `docker-machine rm test-db-machine`.
Virtuaaliympäristön pystytys kestää hieman aikaa, joten samaa ympäristöä kierrätetään eri testiajojen välillä.

## Pebblen ja PrinceXML:n käyttö

PrinceXML tuottaa PDF-tiedostoja HTML-lähteistä jotka on muodostettu [Pebble-frameworkillä](http://www.mitchellbosecke.com/pebble).
- Pebblen templatet ja resurssit on määritetty omassa git-respositoryssaan (oiva-template). Hanki templatet koneelle jolla ajat backendiä

Pebblen käyttämät template-tiedostot määräytyvät kolmen polun mukaisesti: juuripolku, versiopolku ja templaattitiedosto. 
- juuripolku (base path) on ``/opt/oiva/backend/``
- versiopolku on ``template/default/``
- templaattitiedosto on ``hakemus/hakemustemplate_fi.html``

Pebble-templateihin viitataan absoluuttisilla tai suhteellisilla poluilla.

Esimerkki dev-profiililla luettavasta konfiguraatiosta (application-dev.yml) ja **juuripolusta**
```
templates:
  base.path: ../../oiva-template
```

**Versiopolku** voidaan valinnaisesti määrittää hakukierros-kohtaisesti tietokannassa (ks. alla)

Joissain tilanteissa ``oiva.esitysmalli`` tietokantatauluun on voitu määritetty omat templatepolut ``templatepath`` sarakeeseen (tämä on templaten **versiopolku**). Tämä ei ole pakollista, sillä mikäli
ko. kenttä on tyhjä niin sovellus käyttää oletustemplateja (template/default/).

Käytettävä **templaattitiedosto** määräytyy templaattitiedostojen välisten kutsujen perusteella.

Kokeile tuottaa PDF-tiedosto seuraavan rajapinnan kautta: http://localhost:8099/pdf/esikatsele/{uuid}, jossa {uuid} on luvan uuid.

## Debug ja JRebel

Remote debug ja JRebel voidaan enabloida Spring Boot Maven pluginin konfiguraatiossa kohdassa``<jvmArguments>``. 
Blokkiin voidaan syöttää arvo komentokehotteelta ympäristömuuttujalla ``jvm.fork.arguments``.

Esimerkki (yhdelle riville):

    $ mvn spring-boot:run -Djvm.fork.arguments='-Djava.rmi.server.hostname=localhost -Xdebug 
    -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
    -Dproject.path=/path/to/oiva/backend -noverify 
    -agentpath:/patg/to/jrebel6/lib/libjrebel64.dylib'
    
    
**HUOM:** ``<jvmArguments>``-blokki forkkaa uuden JVM-prosessin Spring bootille.

## Sananen konfiguraatiosta

Konfiguraatiot otetaan käyttöön seuraavassa järjestyksessä:

1. Suorat komentoriviargumentit Mavenille (esim: ``-Dredis.host=myredishost.com``)
2. Profiilispesifiset konfiguraatiot jar-paketin ulkopuolella (ei käytössä toistaiseksi)
3. Profiilispesifiset konfiguraatiot (``<project-root>/src/main/resources/config/application-{profile}.yml``)
4. Yleiskonfiguraatio jar-paketin ulkopuolella (kehittäessä ``<project-root>/application.yml`, tai jar-paketin ulkopuolella palvelimella)
5. Yleiskonfiguraatio jar-paketissa (``<project-root>/src/main/resources/config/application.yml``)

Konfiguraatiotiedostojen Maven-filtteröintiä (e.g. ${placeholder}:ien korvausta) voi käyttää jar-paketin sisällä 
oleville konfiguraatioille.        
    
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
