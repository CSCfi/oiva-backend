-- Add new lupa for Etelä-Karjalan kesäyliopisto oy (3146323-5)
WITH new_lupa AS (
    INSERT INTO lupa (paatoskierros_id, lupatila_id, asiatyyppi_id, diaarinumero, jarjestaja_ytunnus,
                      jarjestaja_oid, alkupvm, paatospvm, meta, luoja, koulutustyyppi, oppilaitostyyppi,
                      asianumero)
        VALUES (19,
                3, -- VALMIS
                1, -- UUSI
                'VN/18546/2020',
                '3146323-5',
                '1.2.246.562.10.85896876159',
                '2021-01-01',
                '2020-09-15',
                '{
                  "liitetiedosto": "Etela-Karjalan_Kesayliopisto_Oy.pdf"
                }',
                'oiva',
                '3', -- VST
                '4', -- Kesäyliopistot
                'VN/18546/2020') RETURNING id
),
     kunta AS (
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, luoja)
             SELECT l.id,
                    13,
                    'kunta',
                    '405', -- Lappeenranta
                    null,
                    1,
                    null,
                    'oiva'
             FROM new_lupa l RETURNING id
     ),
     opetuskieli AS (
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, luoja)
             SELECT l.id,
                    14,
                    'kielikoodistoopetushallinto',
                    'fi',
                    null,
                    1,
                    null,
                    'oiva'
             FROM new_lupa l RETURNING id
     ),
     koulutustehtava AS (
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, luoja)
             SELECT l.id,
                    15,
                    'opetustehtava',
                    '1',
                    null,
                    1,
                    '{"koulutustehtävämääräys-0": "Etelä-Karjalan kesäyliopisto järjestää avointa korkeakouluopetusta ja muuta alueen sivistys- ja osaamistarpeisiin vastaavaa vapaan sivistystyön koulutusta. Kesäyliopiston koulutustarjonnassa painottuvat opetusala ja varhaiskasvatus, sosiaali- ja terveysala, hallinto, kulttuuri, ympäristö ja kestävä kehitys. Kesäyliopisto ottaa huomioon tarjonnassaan alueen erityislaadusta nousevat taito- ja osaamistarpeet mukaan lukien seniorit ja maahanmuuttajat.", "koulutustehtävämääräys-1": "", "koulutustehtävämääräys-2": ""}',
                    'oiva'
             FROM new_lupa l RETURNING id
     ),
     oppilaitos AS (
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, luoja, org_oid)
             SELECT l.id,
                    17,
                    'oppilaitos',
                    '1', -- Etelä-Karjalan kesäyliopisto
                    null,
                    1,
                    null,
                    'oiva',
                    '1.2.246.562.10.90530417352'
             FROM new_lupa l RETURNING id
     )

SELECT *
FROM new_lupa;
