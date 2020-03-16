-- add missing lupa for Sastamalan kaupunki (0144411-3)
WITH new_lupa AS (
    insert into lupa (paatoskierros_id, lupatila_id, asiatyyppi_id, diaarinumero, jarjestaja_ytunnus, jarjestaja_oid,
                      alkupvm, paatospvm, meta, luontipvm, koulutustyyppi, oppilaitostyyppi)
        values (3, -- liikunnankoulutuskeskukset
                3, -- "valmis"
                1, -- uusi lupa
                '199/532/2012',
                '0144411-3',
                '1.2.246.562.10.78370733186',
                '2013-01-01',
                '2012-12-12',
                '{
                  "esittelijä": "Kirsi Lähde",
                  "esittelija_nimike": "Opetusneuvos",
                  "liitetiedosto": "Sastamalan_kaupunki.pdf"
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
                    '3', -- vst
                    '1'
             from new_lupa),
     vsttyyppi AS (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id)
             select id,
                    '7', -- muut oikeudet, velvollisuudet jne.
                    'vsttyypit',
                    '2', --
                    '1'
             from new_lupa),
     oppilaitos AS (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, org_oid)
             select id,
                    '5', -- oppilaitos
                    'oppilaitos',
                    '1',
                    '1',
                    '1.2.246.562.10.66586003924'
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
                    '{"koulutustehtävämääräys-0": "Sastamalan Opisto järjestää koulutusta, jossa painottuvat taito- ja taideaineet, yhteiskunnalliset aineet, tietotekniikka sekä elämäntaitoja, elämänlaatua ja terveyttä edistävät aineet."}'
             from new_lupa)
select *
from new_lupa;
