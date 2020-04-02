-- add lupa for Kansanvalistusseura sr (0116589-4)
WITH new_lupa AS (
    insert into lupa (paatoskierros_id, lupatila_id, asiatyyppi_id, diaarinumero, jarjestaja_ytunnus, jarjestaja_oid,
                      alkupvm, paatospvm, meta, luontipvm, koulutustyyppi, oppilaitostyyppi)
        values (3, -- liikunnankoulutuskeskukset
                3, -- "valmis"
                1, -- uusi lupa
                '7/532/2019',
                '0116589-4',
                '1.2.246.562.10.353744225710',
                '2020-01-01',
                '2019-10-17',
                '{
                  "esittelijä": "Petra Heikkinen",
                  "esittelija_nimike": "Asiantuntija",
                  "liitetiedosto": "Kansanvalistusseura.pdf"
                }',
                current_date,
                '3', -- vst
                '2' -- vsttyypit: kansalaisopisto
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
                    '2',
                    '1'
             from new_lupa),
     oppilaitos AS (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, org_oid)
             select id,
                    '5', -- oppilaitos
                    'oppilaitos',
                    '1',
                    '1',
                    '1.2.246.562.10.92541507692'
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
                    '{"koulutustehtävämääräys-0": "Etelä-Helsingin kansalaisopisto järjestää koulutusta, jonka painoalueita ovat kielet, musiikki ja terveyttä edistävät aineet."}'
             from new_lupa)
select *
from new_lupa;