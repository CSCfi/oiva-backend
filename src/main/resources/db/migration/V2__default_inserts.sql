
INSERT INTO oiva.asiatyyppi(tunniste, selite) VALUES ('UUSI','{"fi":"Uusi lupa"}');
INSERT INTO oiva.asiatyyppi(tunniste, selite) VALUES ('MUUTOS','{"fi":"Luvan muutos"}');
INSERT INTO oiva.asiatyyppi(tunniste, selite) VALUES ('AK','{"fi":"Ammattikoulu"}');
INSERT INTO oiva.asiatyyppi(tunniste, selite) VALUES ('LUKIO','{"fi":"Lukio"}');

INSERT INTO oiva.lupatila(tunniste, selite) VALUES ('LUONNOS','{"fi":"Lupa on luonnostilassa eli sitä voi muokata"}');
INSERT INTO oiva.lupatila(tunniste, selite) VALUES ('PASSIVOITU','{"fi":"Lupa on poistettu näkyviltä UIssa"}');
INSERT INTO oiva.lupatila(tunniste, selite) VALUES ('VALMIS','{"fi":"Lupa on valmis"}');
INSERT INTO oiva.lupatila(tunniste, selite) VALUES ('HYLATTY','{"fi":"Lupa on hylätty"}');

INSERT INTO oiva.maaraystyyppi(tunniste, selite) VALUES ('OIKEUS','{"fi":"Oikeus"}');
INSERT INTO oiva.maaraystyyppi(tunniste, selite) VALUES ('RAJOITE','{"fi":"Rajoite"}');
INSERT INTO oiva.maaraystyyppi(tunniste, selite) VALUES ('VELVOITE','{"fi":"Velvoite"}');
INSERT INTO oiva.maaraystyyppi(tunniste, selite) VALUES ('POIKKEUS','{"fi":"Poikkeus"}');

INSERT INTO oiva.tekstityyppi(tunniste, selite) VALUES ('PERUSTELU','{"fi":"Perustelu"}');
INSERT INTO oiva.tekstityyppi(tunniste, selite) VALUES ('MUU','{"fi":"Muu määräys"}');

INSERT INTO oiva.esitysmalli(templatepath,luoja,luontipvm) VALUES ('default','oiva','2016-12-19 15:06:28.53536');
INSERT INTO oiva.esitysmalli(templatepath,luoja,luontipvm) VALUES ('tekninen2015','oiva','2017-03-24 10:01:28.53536');
INSERT INTO oiva.esitysmalli(templatepath,luoja,luontipvm) VALUES ('syksy2016','oiva','2017-03-24 10:01:28.53536');
INSERT INTO oiva.esitysmalli(templatepath,luoja,luontipvm) VALUES ('kokonaiskierros2016','oiva','2017-03-24 10:01:28.53536');
INSERT INTO oiva.esitysmalli(templatepath,luoja,luontipvm) VALUES ('kokonaiskierros2016paattyva','oiva','2017-03-24 10:01:28.53536');
INSERT INTO oiva.esitysmalli(templatepath,luoja,luontipvm) VALUES ('kokonaiskierros2016uusi','oiva','2017-03-24 10:01:28.53536');
INSERT INTO oiva.esitysmalli(templatepath,luoja,luontipvm) VALUES ('kokonaiskierros2016hakemus','oiva','2017-03-24 10:01:28.53536');
INSERT INTO oiva.esitysmalli(templatepath,luoja,luontipvm) VALUES ('tutkintorakenne2017','oiva','2016-12-19 15:06:28.53536');

INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2015-05-18','2015-12-31','oiva','2015-05-18 12:22:38.414556','{"fi": "Tuntematon"}',1);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2014-12-04','2015-01-30','oiva','2015-05-19 11:37:21.044803','{"fi": "Kevään 2015 VALMA-päätökset"}',1);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2015-08-01','2015-08-01','oiva','2015-08-19 10:43:19.891572','{"fi": "Syksyn 2015 tekniset muutokset"}',2);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2015-10-01','2015-10-01','oiva','2015-10-01 15:08:39.638454','{"fi": "1.1.2016 voimaan tulevat fuusiot"}',1);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2016-01-01','2016-06-30','oiva','2016-03-04 09:18:47.493705','{"fi": "Kevään 2016 koulutustehtävän muutokset"}',1);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2015-12-18','2015-12-31','oiva','2016-03-08 09:55:50.392222','{"fi": "Kokeilujen jatkaminen 2019 loppuun asti"}',1);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2015-12-17','2015-12-31','oiva','2016-03-08 13:11:41.154148','{"fi": "Syksyn 2015 koulutustehtävän muutokset"}',1);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2016-07-01','2016-12-31','oiva','2016-09-27 10:56:55.269072','{"fi": "Syksyn 2016 uudet järjestämisluvat"}',3);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2016-10-01','2016-12-31','oiva','2016-09-29 09:29:35.1458','{"fi": "Koulutustehtävien tarkistus 2016"}',4);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2017-01-01','2017-01-02','oiva','2017-03-24 10:06:35.1458','{"fi": "Tyhjä rivi amkoute-kannassa"}',1);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2016-11-17','2016-12-31','oiva','2016-11-17 11:03:19.537641','{"fi": "2016 päättyvien lupien muutokset (1)"}',5);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2016-11-17','2016-12-31','oiva','2016-11-17 11:03:19.537641','{"fi": "2017 uusien lupien muutokset (2)"}',6);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2016-11-17','2016-12-31','oiva','2016-11-17 11:03:19.537641','{"fi": "Haetut koulutustehtävämuutokset syksy 2016 (3)"}',7);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2016-11-17','2016-12-31','oiva','2016-11-17 11:03:19.537641','{"fi": "Vuosien 2016 ja 2017 opiskelijamäärien muutokset (4)"}',4);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2016-11-17','2016-12-31','oiva','2016-11-17 11:03:19.537641','{"fi": "Vuoden 2017 opiskelijamäärämuutokset (5)"}',4);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2017-01-01','2017-01-02','oiva','2017-03-24 10:06:35.1458','{"fi": "Tyhjä rivi amkoute-kannassa"}',1);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2016-12-19','2017-01-15','oiva','2016-12-19 15:06:28.53536','{"fi": "Tekniset tutkintorakennemuutokset 1.8.2017"}',8);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2017-02-09','2017-02-28','oiva','2017-02-09 13:14:24.255977','{"fi": "1.1.2017 voimaan tulevat päätökset takautuvasti"}',4);
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2017-08-01','2017-12-31','oiva','2017-02-09 13:14:24.255977','{"fi": "1.1.2018 voimaan tulevat päätökset"}',4);

-- kohteet
INSERT INTO oiva.kohde(tunniste, meta, luoja, luontipvm) VALUES ('tutkinnotjakoulutukset','{"otsikko":{"fi":"Tutkinnot ja koulutukset","sv":"på svenska"}}','oiva','2017-03-19 07:46:22.066839');
INSERT INTO oiva.kohde(tunniste, meta, luoja, luontipvm) VALUES ('opetusjatutkintokieli','{"otsikko":{"fi":"Opetus- ja tutkintokieli","sv":"på svenska"}}','oiva','2017-03-19 07:46:22.066839');
INSERT INTO oiva.kohde(tunniste, meta, luoja, luontipvm) VALUES ('toimintaalue','{"otsikko":{"fi":"Toiminta-alue","sv":"på svenska"}}','oiva','2017-03-19 07:46:22.066839');
INSERT INTO oiva.kohde(tunniste, meta, luoja, luontipvm) VALUES ('opiskelijavuodet','{"otsikko":{"fi":"Opiskelijavuodet","sv":"på svenska"}}','oiva','2017-03-19 07:46:22.066839');
INSERT INTO oiva.kohde(tunniste, meta, luoja, luontipvm) VALUES ('muut','{"otsikko":{"fi":"Muut oikeudet, velvollisuudet, ehdot ja tehtävät","sv":"på svenska"}}','oiva','2017-03-19 07:46:22.066839');