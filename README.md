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

# Buildaus ja kehitysympäristön asennus - How-to

## Oiva 2.0 dependency version updates
Dependency                      | New version
------------------------------- | -------------
spring-boot-starter-parent      | 1.5.1.RELEASE
spring-boot-starter-data-redis  | 1.4.4.RELEASE
postgresql                      | 42.1.4
jooq                            | 3.9.1
jool                            | 0.9.12
jooq-codegen-maven              | 3.9.1
modelmapper-jooq                | 0.7.7
javaslang                       | 1.2.3
jackson                         | 2.8.7
flyway-core                     | 4.2.0
spring-security-test            | 4.2.1.RELEASE
spring-data-commons             | 1.13.0.RELEASE
spring-session                  | 1.3.0.RELEASE
jersey-rx-client-java8          | 2.25.1
pebble                          | 2.3.0
springfox-swagger2              | 2.6.1
springfox-swagger-ui            | 2.6.1

## Alkuvalmistelut

Koneelle täytyy asentaa: 
    
* Java 8
* Maven (3.3.1 tai uudempi)
 
Asenna myös Docker ja Docker Compose jos ympäristön pystytykseen halutaan käyttää Docker Composea. Ilman Dockeria 
tietokannat (PostgreSQL ja Redis) täytyy asentaa käsin. 

### PrinceXML

PrinceXML-kirjastoa käytetään PDF:ien generointiin, se täytyy asentaa koneille erikseen.

Lataa aluksi ympäristöösi oikea versio:

HUOM! Projektissa käytetään PrinceXML:n versiota 10r2 (alla olevat url-osoitteet voivat olla vanhoja)

Centos 64bit - esimerkki:

    $ wget http://www.princexml.com/download/prince-9.0-5.centos60.x86_64.rpm
    $ rpm -i prince-9.0-5.centos60.x86_64.rpm

YUM:

    $ yum --nogpgcheck localinstall prince-9.0-5.centos60.x86_64.rpm
    $ yum install libpng12
    
OSX:
    - Lataa princeXML http://www.princexml.com/download/ sivustolta
    - Ks. ohjeet http://www.princexml.com/doc/installing/#macos
    - Tarkasta että projektin resources/config/application.yml tiedoston prince.exec.path viitta oikeaan paikkaan

Aja tämän jälkeen skripti, jotta princeXML löytyy lokaalisti:
    $ sh installLocalDependencies.sh


## Käynnistä PostgreSQL-tietokanta, Redis

Aja joko lokaalisti tai dockerilla (suositeltu)

Docker-compose:

    $ docker-compose up


**HUOM!!! Paremetrien syöttö ei enää toimi Spring Boot-plugarin kanssa, kun pluginissa forkataan oma prosessi.**

Sovellusta ajettaessa PITÄÄ konfiguraatiot olla halutun profiilin mukaisessa `application.yml` tai `application-dev.yml 
tiedostossa, tai muuten konfiguraatiot luetaan projektin sisältä!

Käännettäessä sovellus, pitää aina muistaa valita myös maven profiili -P dev tai -P prod, jotta pakettiin tulee mukaan 
tarvittavat resurssit.

                    
### Lokaaliasennus (ilman Dockeria)

    CREATE USER oiva WITH PASSWORD 'oiva' CREATEDB;

    $ psql -U oiva -W template1 -h 127.0.0.1

    CREATE DATABASE oiva ENCODING 'UTF-8'

    CREATE DATABASE oiva-test ENCODING 'UTF-8'

## Tietokantarakenteen luonti, populointi ja puhdistus

**HUOM!** Tietokannan kehityskäyttöönottoa on helpotettu, suositeltu tapa on käyttää docker-tietokantaa ja populoida tietokanta testidatalla ajamalla `oiva-db.sh`.

Ts. käynnistä aluksi docker:

    $ ./oiva-docker.sh start
    
Luo ja populoi tietokannat

    $ ./oiva-db.sh amos generate --clean --populate
    $ ./oiva-db.sh yva generate --clean --populate

Alla on tarkemmat ohjeet miten tietokannan luonti ja populointi suoritetaan:


Tietokannan ajantasaisuudesta vastaa Flyway-migraatiotyökalu.

### Flyway-migraatioiden ajaminen Mavenilla ja JOOQ-tietokantaluokkien generointi

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
