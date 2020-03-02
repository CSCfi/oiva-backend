insert into kohde (tunniste, meta, luoja, luontipvm)
values ('tarkoitus', '{
  "otsikko": {
    "fi": "Oppilaitoksen tarkoitus",
    "sv": "på svenska"
  }
}', 'kuja', current_date);

-- add lupa for snellman-korkeakoulun kannatusyhdistys (0370501-4)
with new_lupa AS (
    insert into lupa (paatoskierros_id, lupatila_id, asiatyyppi_id, diaarinumero, jarjestaja_ytunnus, jarjestaja_oid,
                      alkupvm, paatospvm, meta, luontipvm, koulutustyyppi, oppilaitostyyppi)
        values ('3', -- ~"vst"
                '3', -- "valmis"
                '1', -- uusi lupa
                '44/440/2002',
                '0370501-4',
                '1.2.246.562.10.27019854697',
                '2002-10-01',
                '2002-09-25',
                '{
                  "toissijainen_diaarinumero": "27/440/2001",
                  "esittelijä": "Arja Mäkeläinen",
                  "esittelija_nimike": "Opetusneuvos",
                  "liitetiedosto": "Snellman-korkeakoulun ylläpitämislupa.pdf"
                }',
                current_date,
                '3', -- korkeakoulutus
                '6' -- vsttyypit: 6 muut oppilaitokset
               ) returning *),
     vsttyyppi AS (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id)
             select id,
                    '7', -- muut oikeudet, velvollisuudet jne.
                    'vsttyypit',
                    '6',
                    '1'
             from new_lupa),
     oppilaitos AS (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, meta, org_oid)
             select id,
                    '5', -- oppilaitos
                    'oppilaitos',
                    '1',
                    '1',
                    '{"oppilaitosmääräys-0": "Snellman-korkeakoulu, Helsinki", "oppilaitosmääräys-1": "", "oppilaitosmääräys-2": ""}',
                    '1.2.246.562.10.38970512679'
             from new_lupa),
     opetuskieli AS (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, meta)
             select id,
                    '2', -- opetuskieli
                    'kielikoodistoopetushallinto',
                    'fi',
                    '1',
                    '{}'
             from new_lupa),
     oppilaitokset_tarkoitus AS (
         insert into maarays (lupa_id, kohde_id, maaraystyyppi_id, meta, koodiarvo)
             select id,
                    (select id from kohde where tunniste = 'tarkoitus'), -- oppilaitoksen rakoitus
                    '1',
                    '{"oppilaitoksentarkoitus-0": "Snellman-korkeakoulun tarkoituksena on edistää opiskelijoiden inhimillistä ja ammatillista kehitystä. Lähtökohtana on laaja-alainen käsitys sivistyksestä, johon kuuluu filosofisia, tieteellisiä, taiteellisia ja eettis-käytännöllisiä opintoja. Toiminnassa painottuu steinerpedagoginen ja fenomenologinen lähestymistapa, joka pyrkii ottamaan huomioon myös ihmisen ja maailman henkisen ulottuvuuden. Koulutuksen tueksi kehitetään siihen liittyvää tutkimusta ja pyritään laajaan kansalliseen ja kansainväliseen yhteistoimintaan, varsinkin vuorovaikutukseen muiden opettajankoulutuslaitosten kanssa.\nYleisopinnot toimivat elämänhallintaan ja työelämässä jaksamiseen suuntautuvana sekä sosiaalisten taitojen ja taiteellisen luovuuden kehittämiseen pyrkivänä elinikäisen oppimisen linjana ja myös perustana jatko-opinnoille Snellman-korkeakoulussa. Muuta, pääasiassa steinerpedagogiikalle pohjautuvaa koulutusta ovat mm. puhe-ja draamataiteen, goetheanistisen kuvataiteen sekä biodynaamisen viljelyn ja luonnonhoidon koulutus."}',
                    ''
             from new_lupa),
     koulutustehtava AS (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, meta)
             select id,
                    '3', -- koulutustehtava
                    'koulutustehtava',
                    'fi',
                    '1',
                    '{"koulutustehtävämääräys-0": "Snellman-korkeakoulu on valtakunnallinen oppilaitos, jonka tehtävänä on steinerpedagogiselta pohjalta kehitettyjen yleissivistävien opintojen järjestäminen. Tavoitteena on yksilön kykyjen, fyysisten, psyykkisten sekä henkisten voimavarojen herättäminen ja keinojen tarjoaminen näiden kykyjen elinikäiselle ja tasapainoiselle kehittämiselle.", "koulutustehtävämääräys-1": "Yleisopinnot toimivat elämänhallintaan ja työelämässä jaksamiseen suuntautuvana sekä sosiaalisten taitojen ja taiteellisen luovuuden kehittämiseen pyrkivänä elinikäisen oppimisen linjana ja myös perustana jatko-opinnoille Snellman-korkeakoulussa. Muuta, pääasiassa steinerpedagogiikalle pohjautuvaa koulutusta ovat mm. puhe-ja draamataiteen, goetheanistisen kuvataiteen sekä biodynaamisen viljelyn ja luonnonhoidon koulutus."}'
             from new_lupa),
     erityinen_koulutustehtava AS (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, meta)
             select id,
                    '4', -- erityinen koulutustehtava
                    'vsterityinenkoulutustehtava',
                    '1',
                    '1',
                    '{"erityinenkoulutustehtävämääräys-0": "Oppilaitoksen erityisenä koulutustehtävänä on antaa steinerpedagogista opettajankoulutusta."}'
             from new_lupa)
select *
from new_lupa;