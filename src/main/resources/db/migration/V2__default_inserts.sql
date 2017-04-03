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

INSERT INTO oiva.tekstityyppi(tunniste, selite) VALUES ('KYSYMYS','{"fi":"Kysymys"}');
INSERT INTO oiva.tekstityyppi(tunniste, selite) VALUES ('VASTAUS','{"fi":"Vastaus"}');
INSERT INTO oiva.tekstityyppi(tunniste, selite) VALUES ('PERUSTELU','{"fi":"Perustelu"}');
INSERT INTO oiva.tekstityyppi(tunniste, selite) VALUES ('MUU','{"fi":"Muu määräys"}');


INSERT INTO oiva.esitysmalli(templatepath,luoja,luontipvm) VALUES ('default','oiva','2016-12-19 15:06:28.53536');
INSERT INTO oiva.esitysmalli(templatepath,luoja,luontipvm) VALUES ('tutkintorakenne2017','oiva','2016-12-19 15:06:28.53536');
INSERT INTO oiva.paatoskierros(alkupvm,loppupvm,luoja,luontipvm,meta,esitysmalli_id) VALUES ('2016-12-19','2017-02-15','oiva','2016-12-19 15:06:28.53536','{"fi": "Tekniset tutkintorakennemuutokset 1.8.2017"}',2);


-- kohteet
INSERT INTO oiva.kohde(tunniste, meta, luoja, luontipvm) VALUES ('opetuskieli','{"selite":{"fi":"Oppilaitoksen opetuskieli"},"otsikko":{"fi":"Opetuskieli"}}','oiva','2017-03-06 11:59:22.066839');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('jarjestamiskunta','{"selite":{"fi":"määräykseen liittyvä jarjestamiskunta"},"otsikko":{"fi":"Järjestämiskunta"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('koulutusala','{"selite":{"fi":"määräykseen liittyvä koulutusala"},"otsikko":{"fi":"Koulutusala"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('tutkinto','{"selite":{"fi":"määräykseen liittyvä tutkinto"},"otsikko":{"fi":"Tutkinto"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('vieraskieliset','{"selite":{"fi":"määräykseen liittyvä vieraskielinen koulutus"},"otsikko":{"fi":"Vieraskielinen koulutus"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara','{"selite":{"fi":"määräykseen liittyvä opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitoksen opiskelijamäärä"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus','{"selite":{"fi":"määräykseen liittyvä erityisopetukset opiskelijamäärä"},"otsikko":{"fi":"Erityisopetuksen opiskelijamäärä"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('vieraskieliset_englanti','{"selite":{"fi":"määräykseen liittyvä englanninkielinen koulutus"},"otsikko":{"fi":"Englanninkielinen koulutus"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('vieraskieliset_saame','{"selite":{"fi":"määräykseen liittyvä saamenkielinen koulutus"},"otsikko":{"fi":"Saamenkielinen koulutus"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2017','{"selite":{"fi":"määräykseen liittyvä vuoden 2017 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2017"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2016','{"selite":{"fi":"määräykseen liittyvä vuoden 2016 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2016"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2015','{"selite":{"fi":"määräykseen liittyvä vuoden 2015 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2015"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2014','{"selite":{"fi":"määräykseen liittyvä vuoden 2014 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2014"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2013','{"selite":{"fi":"määräykseen liittyvä vuoden 2013 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2013"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2012','{"selite":{"fi":"määräykseen liittyvä vuoden 2012 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2012"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2011','{"selite":{"fi":"määräykseen liittyvä vuoden 2011 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2011"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2010','{"selite":{"fi":"määräykseen liittyvä vuoden 2010 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2010"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2009','{"selite":{"fi":"määräykseen liittyvä vuoden 2009 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2009"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2008','{"selite":{"fi":"määräykseen liittyvä vuoden 2008 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2008"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2007','{"selite":{"fi":"määräykseen liittyvä vuoden 2007 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2007"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2006','{"selite":{"fi":"määräykseen liittyvä vuoden 2006 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2006"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2005','{"selite":{"fi":"määräykseen liittyvä vuoden 2005 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2005"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2004','{"selite":{"fi":"määräykseen liittyvä vuoden 2004 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2004"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2003','{"selite":{"fi":"määräykseen liittyvä vuoden 2003 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2003"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2002','{"selite":{"fi":"määräykseen liittyvä vuoden 2002 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2002"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2001','{"selite":{"fi":"määräykseen liittyvä vuoden 2001 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2001"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_2000','{"selite":{"fi":"määräykseen liittyvä vuoden 2000 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 2000"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_1999','{"selite":{"fi":"määräykseen liittyvä vuoden 1999 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 1999"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('opiskelijamaara_1998','{"selite":{"fi":"määräykseen liittyvä vuoden 1998 opiskelijamäärä"},"otsikko":{"fi":"Opiskelijamäärä 1998"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2017','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2017 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2017"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2016','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2016 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2016"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2015','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2015 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2015"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2014','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2014 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2014"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2013','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2013 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2013"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2012','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2012 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2012"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2011','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2011 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2011"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2010','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2010 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2010"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2009','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2009 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2009"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2008','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2008 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2008"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2007','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2007 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2007"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2006','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2006 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2006"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2005','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2005 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2005"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2004','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2004 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2004"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2003','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2003 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2003"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2002','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2002 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2002"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2001','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2001 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2001"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_2000','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 2000 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 2000"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_1999','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 1999 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 1999"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitos_1998','{"selite":{"fi":"määräykseen liittyvä sisäoppilaitoksen vuoden 1998 opiskelijamäärä"},"otsikko":{"fi":"Sisäoppilaitos 1998"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2017','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2017 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2017"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2016','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2016 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2016"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2015','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2015 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2015"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2014','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2014 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2014"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2013','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2013 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2013"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2012','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2012 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2012"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2011','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2011 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2011"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2010','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2010 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2010"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2009','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2009 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2009"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2008','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2008 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2008"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2007','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2007 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2007"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2006','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2006 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2006"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2005','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2005 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2005"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2004','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2004 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2004"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2003','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2003 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2003"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2002','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2002 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2002"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2001','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2001 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2001"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_2000','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 2000 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 2000"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_1999','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 1999 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 1999"}}','oiva-essi');
INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_1998','{"selite":{"fi":"määräykseen liittyvä erityisopetukset vuoden 1998 opiskelijamäärä"},"otsikko":{"fi":"Eritysopetus 1998"}}','oiva-essi');


-- vanhat tagit:

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('valinehuoltoalan_perustutkinto'
,'{ "tag_id":"1","osio":"KOKEILU","koodi": 1,"selite": {"fi": " ", "sv": null},"tyyppi": "tutkintorakenne","laajuus": null,"otsikko": {"fi": "Välinehuoltoalan perustutkinto (kokeilu)", "sv": null},"otsikko": {"fi": "Välinehuoltoalan perustutkinto (kokeilu)", "sv": null},"tutkinto": "371113","osaamisala": null,"koulutusala": "7","kokeilu_paattyy": "2018-12-31","viimeinen_sisaanotto": null,"ensimmainen_sisaanotto": "2014-01-02"}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('perustason_ensihoidon_osaamisala'
,'{"tag_id":"2","osio":"KOKEILU","koodi": 2, "selite": {"fi": " ", "sv": null}, "tyyppi": "tutkintorakenne", "laajuus": null, "otsikko": {"fi": "Perustason ensihoidon osaamisala (kokeilu)"}, "otsikko": {"fi": "Perustason ensihoidon osaamisala (kokeilu)"}, "tutkinto": "371101", "osaamisala": "1656", "koulutusala": "7", "kokeilu_paattyy": "2018-12-31", "viimeinen_sisaanotto": null, "ensimmainen_sisaanotto": "2014-01-02"}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('tieto_ja_tietoliikennetekniikan_perustutkinto_hyvinvointi'
,'{"tag_id":"3","osio":"KOKEILU","koodi": 3, "selite": {"fi": "Kokeilussa suoritettava osaamisala painottuu hyvinvointiteknologiaan. Osaamisalan nimi ja tutkintonimike ratkaistaan kokeiluohjelman yhteydessä."}, "tyyppi": "tutkintorakenne", "laajuus": null, "otsikko": {"fi": "Tieto- ja tietoliikennetekniikan perustutkinto, painottuen hyvinvointiteknologiaan (kokeilu)"}, "otsikko": {"fi": "Tieto- ja tietoliikennetekniikan perustutkinto, painottuen hyvinvointiteknologiaan (kokeilu)"}, "tutkinto": null, "osaamisala": null, "koulutusala": "5", "kokeilu_paattyy": "2018-12-31", "viimeinen_sisaanotto": null, "ensimmainen_sisaanotto": "2014-01-01"}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sahko_ja_automaatiotekniikan_perustutkinto_hyvinvointi'
,'{"tag_id":"4","osio":"KOKEILU","koodi": 4, "selite": {"fi": "Kokeilussa suoritettava osaamisala painottuu hyvinvointiteknologiaan. Osaamisalan nimi ja tutkintonimike ratkaistaan kokeiluohjelman yhteydessä."}, "tyyppi": "tutkintorakenne", "laajuus": null, "otsikko": {"fi": "Sähkö- ja automaatiotekniikan perustutkinto, painottuen hyvinvointiteknologiaan (kokeilu)"}, "otsikko": {"fi": "Sähkö- ja automaatiotekniikan perustutkinto, painottuen hyvinvointiteknologiaan (kokeilu)"}, "tutkinto": null, "osaamisala": null, "koulutusala": "5", "kokeilu_paattyy": "2018-12-31", "viimeinen_sisaanotto": null, "ensimmainen_sisaanotto": "2014-01-01"}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('kone_ja_metallialan_perustutkinto_hyvinvointi'
,'{"tag_id":"5","osio":"KOKEILU","koodi": 5, "selite": {"fi": "Kokeilussa suoritettava osaamisala painottuu hyvinvointiteknologiaan. Osaamisalan nimi ja tutkintonimike ratkaistaan kokeiluohjelman yhteydessä."}, "tyyppi": "tutkintorakenne", "laajuus": null, "otsikko": {"fi": "Kone- ja metallialan perustutkinto, painottuen hyvinvointiteknologiaan (kokeilu)"}, "otsikko": {"fi": "Kone- ja metallialan perustutkinto, painottuen hyvinvointiteknologiaan (kokeilu)"}, "tutkinto": null, "osaamisala": null, "koulutusala": "5", "kokeilu_paattyy": "2018-12-31", "viimeinen_sisaanotto": null, "ensimmainen_sisaanotto": "2014-01-01"}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('metsakoneenkuljettaja_yhteistyo'
,'{"tag_id":"6","osio":"MUU_EHTO","koodi": 1, "selite": {"fi": "Metsäalan perustutkinnon metsäkoneenkuljettajan osaamisalan koulutus järjestetään Länsirannikon Koulutus Oy:n ja Työtehoseura ry:n välisenä yhteistyönä siten, että osa koulutuksesta järjestetään Työtehoseura ry:n toimesta ja sen koulutuspisteessä siten kuin siitä erikseen sovitaan. Länsirannikon Koulutus Oy toimii koulutuksen järjestäjänä, ja opiskelijat ovat Länsirannikon Koulutus Oy:n opiskelijoita. Osapuolten yhteistyösopimuksessa päätetään tarkemmin koulutuksen järjestäjäkohtaisesta tavoiteopiskelijamäärästä koulutustarve huomioon ottaen.", "sv": null}, "otsikko": {"fi": "Yhteistyövelvoite metsäkoneenkuljetuksen koulutuksen järjestämisessä", "sv": null}, "otsikko": {"fi": "Metsäkoneenkuljettaja, yhteistyö, Länsirannikon Koulutus Oy", "sv": null}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('metsakoneenkuljettaja_yhteistyo_tampere'
,'{"tag_id":"7","osio":"MUU_EHTO","koodi": 2, "selite": {"fi": "Metsäalan perustutkinnon metsäkoneenkuljetuksen osaamisalan koulutus järjestetään Tampereen kaupungin, Seinäjoen koulutuskuntayhtymän, Keski-Pohjanmaan koulutusyhtymän ja Etelä-Karjalan koulutuskuntayhtymän välisenä yhteistyönä siten, että osa koulutuksesta järjestetään Seinäjoen koulutuskuntayhtymän, Keski-Pohjanmaan koulutusyhtymän ja Etelä-Karjalan koulutuskuntayhtymän toimesta ja niiden koulutuspisteissä siten kuin siitä erikseen sovitaan. Tampereen kaupunki toimii koulutuksen järjestäjänä, ja opiskelijat ovat Tampereen kaupungin opiskelijoita. Osapuolten yhteistyösopimuksessa päätetään tarkemmin koulutuksen järjestäjäkohtaisesta tavoiteopiskelijamäärästä koulutustarve huomioon ottaen.", "sv": null}, "otsikko": {"fi": "Yhteistyövelvoite metsäkoneenkuljetuksen koulutuksen järjestämisessä", "sv": null}, "otsikko": {"fi": "Metsäkoneenkuljettaja, yhteistyö, Tampereen kaupunki", "sv": null}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('metsakoneenkuljettaja_yhteistyo_etelasavo'
,'{"tag_id":"8","osio":"MUU_EHTO","koodi": 3, "selite": {"fi": "Metsäalan perustutkinnon metsäkoneenkuljetuksen osaamisalan koulutus järjestetään Etelä-Savon Koulutus Oy:n ja Itä-Savon koulutuskuntayhtymän välisenä yhteistyönä siten, että osa koulutuksesta järjestetään Itä-Savon koulutuskuntayhtymä toimesta ja sen koulutuspisteessä siten kuin siitä erikseen sovitaan. Etelä-Savon Koulutus Oy toimii koulutuksen järjestäjänä, ja opiskelijat ovat Etelä-Savon Koulutus Oy:n opiskelijoita. Osapuolten yhteistyösopimuksessa päätetään tarkemmin koulutuksen järjestäjäkohtaisesta tavoiteopiskelijamäärästä koulutustarve huomioon ottaen.", "sv": null}, "otsikko": {"fi": "Yhteistyövelvoite metsäkoneenkuljetuksen koulutuksen järjestämisessä", "sv": null}, "otsikko": {"fi": "Metsäkoneenkuljettaja, yhteistyö, Etelä-Savon Koulutus Oy", "sv": null}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('lakkaava_koulutus_bolvallius_saatio'
,'{"tag_id":"9","osio":"MUU_EHTO","koodi": 4, "selite": {"fi": "S. ja A. Bovalliuksen säätiö velvoitetaan huolehtimaan yhteistyössä Itä-Karjalan Kansanopistoseura r.y:n ja Kirkkopalvelut ry:n kanssa vammaisten opiskelijoiden valmentavan ja kuntouttavan opetuksen ja ohjauksen järjestämisestä siten, että sekä Itä-Karjalan Kansanopistoseura r.y:n ja Kirkkopalvelut ry:n opiskelijoilla on mahdollisuus suorittaa opintonsa loppuun henkilökohtaisen opetuksen järjestämistä koskevan suunnitelman mukaisesti 2016 lukien S. ja A. Bovalliuksen säätiön järjestämänä koulutuksena.", "sv": null}, "otsikko": {"fi": "Yhteistyövelvoite lakkaavien koulutusten loppuun saattamiseksi", "sv": null}, "otsikko": {"fi": "Lakkaava koulutus, yhteistyö, S. ja A. Bovalliuksen säätiö", "sv": null}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('metsakoneenkuljettaja_yhteistyo_pkky'
,'{"tag_id":"10","osio":"MUU_EHTO","koodi": 5, "selite": {"fi": "Metsäalan perustutkinnon metsäkoneenkuljetuksen osaamisalan koulutus järjestetään Pohjois-Karjalan koulutuskuntayhtymän ja Kajaanin kaupungin välisenä yhteistyönä siten, että osa koulutuksesta järjestetään Kajaanin kaupungin toimesta ja sen koulutuspisteessä siten kuin siitä erikseen sovitaan. Pohjois-Karjalan koulutuskuntayhtymä toimii koulutuksen järjestäjänä, ja opiskelijat ovat Pohjois-Karjalan koulutuskuntayhtymän opiskelijoita. Osapuolten yhteistyösopimuksessa päätetään tarkemmin koulutuksen järjestäjäkohtaisesta tavoiteopiskelijamäärästä koulutustarve huomioon ottaen.", "sv": null}, "otsikko": {"fi": "Yhteistyövelvoite metsäkoneenkuljetuksen koulutuksen järjestämisessä", "sv": null}, "otsikko": {"fi": "Metsäkoneenkuljettaja, yhteistyö, Pohjois-Karjalan koulutuskuntayhtymä", "sv": null}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('siirtyvat_opiskelija_mjk'
,'{"tag_id":"11","osio":"MUU_EHTO","koodi": 6, "selite": {"fi": "Koulutuksen järjestäjä huolehtii sille siirtyvien MJK-koulutuskeskus ry:n ammatillisen peruskoulutuksen opiskelijoiden koulutuksen loppuunsaattamisesta ammatillisen peruskoulutuksen vuotuisen kokonaisopiskelijamääränsä puitteissa.", "sv": null}, "otsikko": {"fi": "Siirtyvien opiskelijoiden koulutuksen loppuun saattaminen", "sv": null}, "otsikko": {"fi": "Siirtyvät opiskelijat, MJK-koulutuskeskus ry", "sv": null}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('sisaoppilaitosmuotoinen_koulutus'
,'{"tag_id":"12","osio":"MUU_EHTO","koodi": 7, "selite": {"fi": "Koulutuksen järjestäjä voi järjestää koulutusta sisäoppilaitosmuotoisesti.", "sv": null}, "otsikko": {"fi": "Sisäoppilaitosmuotoinen koulutus", "sv": null}, "otsikko": {"fi": "Sisäoppilaitosmuotoinen koulutus", "sv": null}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('urheilijoiden_ammatillinen_peruskoulutus'
,'{"tag_id":"13","osio":"MUU_EHTO","koodi": 8, "selite": {"fi": "Koulutuksen järjestäjän tehtävänä on järjestää urheilijoiden ammatillista peruskoulutusta.", "sv": null}, "otsikko": {"fi": "Urheilijoiden ammatillinen peruskoulutus", "sv": null}, "otsikko": {"fi": "Urheilijoiden ammatillinen peruskoulutus", "sv": null}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('jarjestajaverkon_kehittaminen'
,'{"tag_id":"14","osio":"MUU_EHTO","koodi": 9, "selite": {"fi": "Valtiontalouden säästötoimenpiteet heikentävät koulutuksen järjestäjien toimintaedellytyksiä. Tähän liittyen tulee huolehtia siitä, että ammatillisen koulutuksen järjestäjät ovat työelämän ja yksilöiden osaamistarpeisiin vastaamisen kannalta riittävän vahvoja toimijoita. Ministeriö korostaa aktiivisten toimenpiteiden käynnistämistä järjestäjäfuusioiden aikaan saamiseksi.", "sv": null}, "otsikko": {"fi": "Järjestäjäverkon rakenteellinen kehittäminen", "sv": null}, "otsikko": {"fi": "Järjestäjäverkon rakenteellinen kehittäminen", "sv": null}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('tutkintoon_johtava_koulutus'
,'{"tag_id":"15","osio":"ERITYISOPETUS","koodi": "TUTKINTOONVALMISTAVA", "selite": {"fi": "Tutkintoon johtava/valmistava koulutus", "sv": null}, "otsikko": {"fi": "Tutkintoon johtava/valmistava koulutus", "sv": null}, "otsikko": {"fi": "Tutkintoon johtava/valmistava koulutus", "sv": null}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('valma'
,'{"tag_id":"16","osio":"ERITYISOPETUS","koodi": "VALMA", "selite": {"fi": "Ammatilliseen perustutkintoon valmentava koulutus (VALMA)", "sv": "Handledande utbildning för grundläggande yrkesutbildning (VALMA)"}, "otsikko": {"fi": "Ammatilliseen perustutkintoon valmentava koulutus (VALMA)", "sv": "Handledande utbildning för grundläggande yrkesutbildning (VALMA)"}, "otsikko": {"fi": "VALMA", "sv": "VALMA"}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('telma'
,'{"tag_id":"17","osio":"ERITYISOPETUS","koodi": "TELMA", "selite": {"fi": "Työhön ja itsenäiseen elämään valmentava koulutus (TELMA)", "sv": "Utbildning som förbereder för arbete och ett självständigt liv (TELMA)"}, "otsikko": {"fi": "Työhön ja itsenäiseen elämään valmentava koulutus (TELMA)", "sv": "Utbildning som förbereder för arbete och ett självständigt liv (TELMA)"}, "otsikko": {"fi": "TELMA", "sv": "TELMA"}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('erityisopetus_kehittamis_ohjaus_tuki'
,'{"tag_id":"18","osio":"ERITYISOPETUS","koodi": "KEHITTAMISOHJAUSTUKI", "selite": {"fi": "Erityisopetukseen liittyvät kehittämis-, ohjaus- ja tukitehtävät", "sv": null}, "otsikko": {"fi": "Erityisopetukseen liittyvät kehittämis-, ohjaus- ja tukitehtävät", "sv": null}, "otsikko": {"fi": "Erityisopetukseen liittyvät kehittämis-, ohjaus- ja tukitehtävät", "sv": null}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('Kuljettajakoulutus_perustaso'
,'{"tag_id":"19","osio":"KULJETTAJAKOULUTUS","koodi": "PERUS", "selite": {"fi": "<p>Koulutuksen järjestäjällä on lupa toimia ammattipätevyydestä annetun lain (273/2007) ja valtioneuvoston asetuksen (640/2007) mukaisena kuorma- ja linja-autonkuljettajan perustason ammattipätevyyskoulutusta antavana koulutuskeskuksena. Perustason ammattipätevyyskoulutukseen hyväksytty koulutuskeskus saa antaa myös jatkokoulutusta edellä mainitun lain 10 §:n nojalla.</p><p>Koulutuksen järjestäjällä on kuorma- ja linja-auton kuljettajien ammattipätevyyskoulutuksen koulutuskeskuksena tämän päätöksen nojalla lupa järjestää perustason ammattipätevyyskoulutusta sekä ammatillisena peruskoulutuksena että ammatillisena lisäkoulutuksena.</p><p>Kuorma- ja linja-auton kuljettajien perustason ammattipätevyyskoulutuksessa tulee noudattaa kuorma- ja linja-auton kuljettajien ammattipätevyydestä annettua lakia (273/2007)  ja valtioneuvoston asetusta (640/2007)  sekä Opetushallituksen hyväksymiä  tutkintojen perusteita.</p><p>Koulutuksen järjestäjän tulee  ennen kuorma- ja linja-auton kuljettajien ammattipätevyyskoulutuksen jatkokoulutuksen käynnistämistä haettava Liikenteen turvallisuusvirasto Trafilta hyväksyminen koulutusohjelmalleen tai noudatettava Trafin aiemmin hyväksymää voimassa olevaa jatkokoulutuksen koulutusohjelmaa. Koulutusohjelman vahvistaminen on voimassa enintään viisi vuotta.</p><p>Koulutuksen järjestäjä vastaa siitä, että opetus annetaan koulutuskeskukseksi hyväksymistä koskevassa hakemuksessa ja sen lisäksi annettujen tietojen sekä koulutuskeskukseksi hyväksymisen ehtojen mukaisesti. Jos hakemuksessa annetuissa tai muissa hakijan antamissa tiedoissa tapahtuu muutoksia, on koulutuskeskuksen välittömästi ilmoitettavaa niistä valvovalle viranomaiselle.</p>"}, "otsikko": {"fi": "Perustason ammattipätevyyskoulutus ja jatkokoulutus"}, "otsikko": {"fi": "Kuljettajakoulutus, perustaso"}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('Kuljettajakoulutus_jatkokoulutus'
,'{"tag_id":"20","osio":"KULJETTAJAKOULUTUS","koodi": "JATKO", "selite": {"fi": "<p>Koulutuksen järjestäjällä on lupa toimia ammattipätevyydestä annetun lain (273/2007) ja valtioneuvoston asetuksen (640/2007) mukaisena kuorma- ja linja-autonkuljettajan ammattipätevyyden jatkokoulutusta antavana koulutuskeskuksena.</p><p>Kuorma- ja linja-auton kuljettajien ammattipätevyyden jatkokoulutuksessa tulee noudattaa kuorma- ja linja-auton kuljettajien ammattipätevyydestä annettua lakia (273/2007) ja valtioneuvoston asetusta (640/2007) sekä Liikenteen turvallisuusvirasto Trafin hyväksymää koulutusohjelmaa.</p><p>Koulutuksen järjestäjän tulee  ennen kuorma- ja linja-auton kuljettajien ammattipätevyyskoulutuksen jatkokoulutuksen toteuttamista haettava Liikenteen turvallisuusvirasto Trafilta hyväksyminen koulutusohjelmalleen tai noudatettava Trafin aiemmin hyväksymää voimassa olevaa jatkokoulutuksen koulutusohjelmaa. Koulutusohjelman vahvistaminen on voimassa enintään viisi vuotta.</p><p>Koulutuksen järjestäjä vastaa siitä, että opetus annetaan koulutuskeskukseksi hyväksymistä koskevassa hakemuksessa ja sen lisäksi annettujen tietojen sekä koulutuskeskukseksi hyväksymisen ehtojen mukaisesti. Jos hakemuksessa annetuissa tai muissa hakijan antamissa tiedoissa tapahtuu muutoksia, on koulutuksen järjestäjän välittömästi ilmoitettavaa niistä valvovalle viranomaiselle.</p>"}, "otsikko": {"fi": "Jatkokoulutus"}, "otsikko": {"fi": "Kuljettajakoulutus, jatkokoulutus"}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('valma_ei_tutkintotavoitteinen_koulutus'
,'{"tag_id":"22","osio":"EI_TUTKINTOTAVOITTEINEN_KOULUTUS","koodi": "VALMA", "selite": {"fi": "Ammatilliseen perustutkintoon valmentava koulutus (VALMA)", "sv": "Handledande utbildning för grundläggande yrkesutbildning (VALMA)"}, "otsikko": {"fi": "Ammatilliseen perustutkintoon valmentava koulutus (VALMA)", "sv": "Handledande utbildning för grundläggande yrkesutbildning (VALMA)"}, "otsikko": {"fi": "VALMA", "sv": "VALMA"}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('telma_ei_tutkintotavoitteinen_koulutus'
,'{"tag_id":"23","osio":"EI_TUTKINTOTAVOITTEINEN_KOULUTUS","koodi": "TELMA", "selite": {"fi": "Työhön ja itsenäiseen elämään valmentava koulutus (TELMA)", "sv": "Utbildning som förbereder för arbete och ett självständigt liv (TELMA)"}, "otsikko": {"fi": "Työhön ja itsenäiseen elämään valmentava koulutus (TELMA)", "sv": "Utbildning som förbereder för arbete och ett självständigt liv (TELMA)"}, "otsikko": {"fi": "TELMA", "sv": "TELMA"}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('valmentava_kuntouttava_yhteistyo_kirkkopalvelut'
,'{"tag_id":"24","osio":"MUU_EHTO","koodi": 10, "selite": {"fi": "Kirkkopalvelut ry velvoitetaan huolehtimaan yhteistyössä S. ja A. Bovalliuksen säätiön kanssa vammaisten opiskelijoiden valmentavan ja kuntouttavan opetuksen ja ohjauksen järjestämisestä siten, että Kirkkopalvelut ry:n opiskelijoilla on mahdollisuus suorittaa opintonsa loppuun henkilökohtaisen opetuksen järjestämistä koskevan suunnitelman mukaisesti 2016 lukien S. ja A. Bovalliuksen säätiön järjestämänä koulutuksena.", "sv": null}, "otsikko": {"fi": "Yhteistyövelvoite valmentavan ja kuntouttavan opetuksen ja ohjauksen järjestämisessä", "sv": null}, "otsikko": {"fi": "Valmentava ja kuntouttava, yhteistyö, Kirkkopalvelut ry", "sv": null}}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('valinehuoltoalan_perustutkinto_2014_2019'
,'{"tag_id":"27","osio":"KOKEILU","koodi": 6, "selite": {"fi": " ", "sv": null}, "tyyppi": "tutkintorakenne", "laajuus": null, "otsikko": {"fi": "Välinehuoltoalan perustutkinto (kokeilu)", "sv": null}, "otsikko": {"fi": "Välinehuoltoalan perustutkinto (kokeilu 2014-2019)", "sv": null}, "tutkinto": "371113", "osaamisala": null, "koulutusala": "7", "kokeilu_paattyy": "2019-12-31", "viimeinen_sisaanotto": null, "ensimmainen_sisaanotto": "2014-01-02"}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('perustason_ensihoidon_osaamisala_2014_2019'
,'{"tag_id":"28","osio":"KOKEILU","koodi": 7, "selite": {"fi": " ", "sv": null}, "tyyppi": "tutkintorakenne", "laajuus": null, "otsikko": {"fi": "Perustason ensihoidon osaamisala (kokeilu)"}, "otsikko": {"fi": "Perustason ensihoidon osaamisala (kokeilu 2014-2019)"}, "tutkinto": "371101", "osaamisala": "1656", "koulutusala": "7", "kokeilu_paattyy": "2019-12-31", "viimeinen_sisaanotto": null, "ensimmainen_sisaanotto": "2014-01-02"}'
,'oiva-essi'
);

INSERT INTO oiva.kohde(tunniste, meta, luoja) VALUES ('metsakoneenkuljettaja_yhteistyo_tampere_11'
,'{"tag_id":"31","osio":"MUU_EHTO","koodi": 11, "selite": {"fi": "Metsäalan perustutkinnon metsäkoneenkuljetuksen osaamisalan koulutus järjestetään Tampereen kaupungin, Seinäjoen koulutuskuntayhtymän, Keski-Pohjanmaan koulutusyhtymän, Etelä-Karjalan koulutuskuntayhtymän ja Työtehoseura ry:n välisenä yhteistyönä siten, että osa koulutuksesta järjestetään Seinäjoen koulutuskuntayhtymän, Keski-Pohjanmaan koulutusyhtymän, Etelä-Karjalan koulutuskuntayhtymän ja Työtehoseura ry:n toimesta ja niiden koulutuspisteissä siten kuin siitä erikseen sovitaan. Tampereen kaupunki toimii koulutuksen järjestäjänä, ja opiskelijat ovat Tampereen kaupungin opiskelijoita. Osapuolten yhteistyösopimuksessa päätetään tarkemmin koulutuksen järjestäjäkohtaisesta tavoiteopiskelijamäärästä koulutustarve huomioon ottaen.", "sv": null}, "otsikko": {"fi": "Yhteistyövelvoite metsäkoneenkuljetuksen koulutuksen järjestämisessä", "sv": null}, "otsikko": {"fi": "Metsäkoneenkuljettaja, yhteistyö, Tampereen kaupunki", "sv": null}}'
,'oiva-essi'
);