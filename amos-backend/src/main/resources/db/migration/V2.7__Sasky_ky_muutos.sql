-- End existing lupa for Sasky ky (0204964-1)
UPDATE lupa
SET loppupvm = '2019-12-31'
WHERE jarjestaja_ytunnus = '0204964-1'
  AND diaarinumero = '4/531/2018';

-- Create lupahistoria row
INSERT INTO lupahistoria
(diaarinumero,
 ytunnus,
 oid,
 maakunta,
 tila,
 voimassaoloalkupvm,
 voimassaololoppupvm,
 paatospvm,
 filename)
VALUES ('4/531/2018', '0204964-1', '1.2.246.562.10.50002259716',
        'Pirkanmaa',
        'Järjestämisluvan muutos', '2019-01-01', '2019-12-31', '2018-12-14',
        '4-531-2018.pdf');

WITH new_lupa AS (
    -- Create new lupa for Sasky ky (0204964-1)
    INSERT INTO lupa (paatoskierros_id, lupatila_id, asiatyyppi_id,
                      diaarinumero, jarjestaja_ytunnus, jarjestaja_oid, alkupvm, loppupvm, paatospvm,
                      meta, maksu, luoja, paivittaja, paivityspvm)
        VALUES (19,
                3,
                2,
                '44/531/2019',
                '0204964-1',
                '1.2.246.562.10.50002259716',
                '2020-01-01',
                null,
                '2019-12-16',
                '{
                  "ministeri": "Li Andersson",
                  "esittelija": "Jukka Lehtinen",
                  "ministeri_nimike": "Opetusministeri",
                  "esittelija_nimike": ""
                }',
                false,
                'oiva',
                null,
                null) RETURNING id
),
     l_liite AS (
         -- Create lupa liite
         INSERT INTO liite (nimi, polku, luoja, tyyppi, kieli)
             VALUES ('Sasky ky',
                     'paatosKirjeet2019/Sasky_ky.pdf',
                     'oiva',
                     'paatosKirje',
                     'fi') RETURNING id
     ),
     link AS (
         -- Create lupa_liite link
         INSERT INTO lupa_liite (lupa_id, liite_id) SELECT new_lupa.id, l_liite.id
                                                    FROM new_lupa,
                                                         l_liite
     ),
     new_maarays AS (
         -- Copy maarays for new lupa
         INSERT
             INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, koodistoversio)
                 SELECT new_lupa.id,
                        m.kohde_id,
                        m.koodisto,
                        m.koodiarvo,
                        m.arvo,
                        m.maaraystyyppi_id,
                        m.meta,
                        -- Update koulutus koodisto version number
                        CASE WHEN m.koodisto = 'koulutus' THEN 12 ELSE m.koodistoversio END
                 FROM new_lupa,
                      maarays m
                          LEFT JOIN lupa l ON m.lupa_id = l.id
                 WHERE l.jarjestaja_ytunnus = '0204964-1'
                   AND l.diaarinumero = '4/531/2018'
     ),
     majoitus_eat AS (
         -- Add Majoitus-ja ravitsemisalan esimiestyön erikoisammattitutkinto (487103)
         INSERT
             INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, koodistoversio)
                 SELECT new_lupa.id,
                        1,
                        'koulutus',
                        '487103',
                        null,
                        1,
                        null,
                        12
                 FROM new_lupa RETURNING id
     ),
     erityisruoka_eat AS (
         -- Add Erityisruokavaliopalvelujen erikoisammattitutkinto (487102)
         INSERT
             INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, koodistoversio)
                 SELECT new_lupa.id,
                        1,
                        'koulutus',
                        '487102',
                        null,
                        1,
                        null,
                        12
                 FROM new_lupa RETURNING id
     ),
     hius_eat AS (
         -- Add Hius-ja kauneudenhoitoalanerikoisammattitutkinto (487341)
         INSERT
             INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, koodistoversio)
                 SELECT new_lupa.id,
                        1,
                        'koulutus',
                        '487341',
                        null,
                        1,
                        null,
                        12
                 FROM new_lupa RETURNING id
     )

SELECT *
FROM new_lupa;
