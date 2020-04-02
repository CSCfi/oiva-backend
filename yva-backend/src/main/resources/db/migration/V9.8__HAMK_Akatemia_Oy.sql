-- End lupa for Hämeen liitto (0826048-0)
WITH old_lupa AS (
    UPDATE lupa
        SET loppupvm = '2019-12-31', jarjestaja_ytunnus = '0826048-0'
        WHERE diaarinumero = '212/532/2012' RETURNING *
),
     historia AS (
         -- Create lupahistoria row
         INSERT
             INTO lupahistoria
                 (diaarinumero,
                  ytunnus,
                  oid,
                  maakunta,
                  tila,
                  voimassaoloalkupvm,
                  voimassaololoppupvm,
                  paatospvm,
                  filename)
                 SELECT diaarinumero,
                        jarjestaja_ytunnus,
                        jarjestaja_oid,
                        'Kanta-Häme',
                        'Järjestämisluvan peruutus',
                        alkupvm,
                        loppupvm,
                        paatospvm,
                        'Hämeen_liitto.PDF'
                 FROM old_lupa l
     )

SELECT * FROM old_lupa;

-- Add lupa for HAMK Akatemia Oy (3007351-8)
WITH new_lupa AS (
    insert into lupa (paatoskierros_id, lupatila_id, asiatyyppi_id, diaarinumero, jarjestaja_ytunnus, jarjestaja_oid,
                      alkupvm, paatospvm, meta, luontipvm, koulutustyyppi, oppilaitostyyppi)
        values (3, -- liikunnankoulutuskeskukset
                3, -- "valmis"
                1, -- uusi lupa
                '8/532/2019',
                '3007351-8',
                '1.2.246.562.10.20830260814',
                '2020-01-01',
                '2019-10-17',
                '{
                  "esittelijä": "Annika Bussman",
                  "esittelija_nimike": "Opetusneuvos",
                  "liitetiedosto": "HAMK_Akatemia_Oy.pdf"
                }',
                current_date,
                '3', -- vst
                '4' -- vsttyypit: kesäyliopisto
               ) returning id),
     koulutusmuoto AS (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id)
             select id,
                    '7', -- muut oikeudet, velvollisuudet jne.
                    'yleissivistavankoulutusmuodot',
                    '3',
                    '1'
             from new_lupa),
     vsttyyppi AS (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id)
             select id,
                    '7', -- muut oikeudet, velvollisuudet jne.
                    'vsttyypit',
                    '4',
                    '1'
             from new_lupa),
     oppilaitos AS (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, org_oid)
             select id,
                    '5', -- oppilaitos
                    'oppilaitos',
                    '1',
                    '1',
                    '1.2.246.562.10.88917381134'
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
     koulutustehtava AS (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, meta)
             select id,
                    '3', -- koulutustehtava
                    'koulutustehtava',
                    '1',
                    '1',
                    '{"koulutustehtävämääräys-0": "Hämeen kesäyliopiston ydintehtävänä on avoimen korkeakouluopetuksen järjestäminen. Kesäyliopisto järjestää myös työelämässä tarvittavia taitoja ja osaamista kehittävää sekä muuta vapaan sivistystyön koulutusta. Koulutukseen voi osallistua elämän eri vaiheissa, ilman pohjakoulutus-, ikä- tai muita vaatimuksia ja ilman erillistä hakumenettelyä. Koulutustarjonnassa painottuvat humanistiset alat, taiteet ja kulttuuri, kasvatusala, kauppa, hallinto ja oikeustiede, luonnontieteet, maatalous- ja metsätiede, tekniikka, lääketiede, terveys ja hyvinvointi, sosiaalitieteet sekä yhteiskuntatieteet."}'
             from new_lupa)
select *
from new_lupa;