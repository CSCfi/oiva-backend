-- End existing lupa for Koulutusyhtymä Tavastia (0205303-4)
UPDATE lupa
SET loppupvm = '2019-12-31'
WHERE jarjestaja_ytunnus = '0205303-4'
  AND diaarinumero = '85/531/2018';

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
VALUES ('85/531/2018', '0205303-4', '1.2.246.562.10.83097956748',
        'Kanta-Häme',
        'Järjestämisluvan muutos', '2019-01-01', '2019-12-31', '2018-12-14',
        '85-531-2018.pdf');

WITH new_lupa AS (
    -- Create new lupa for Koulutusyhtymä Tavastia (0205303-4)
    INSERT INTO lupa (paatoskierros_id, lupatila_id, asiatyyppi_id,
                      diaarinumero, jarjestaja_ytunnus, jarjestaja_oid, alkupvm, loppupvm, paatospvm,
                      meta, maksu, luoja, paivittaja, paivityspvm)
        VALUES (19,
                3,
                2,
                '96/531/2018',
                '0205303-4',
                '1.2.246.562.10.83097956748',
                '2020-01-01',
                null,
                '2019-12-16',
                '{
                  "ministeri": "Li Andersson",
                  "esittelija": "Jukka Lehtinen",
                  "ministeri_nimike": "Opetusministeri",
                  "esittelija_nimike": "Opetusneuvos"
                }',
                false,
                'oiva',
                null,
                null) RETURNING id
),
     l_liite AS (
         -- Create lupa liite
         INSERT INTO liite (nimi, polku, luoja, tyyppi, kieli)
             VALUES ('Koulutusyhtymä Tavastia',
                     'paatosKirjeet2019/Koulutuskuntayhtyma_Tavastia.pdf',
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
                 WHERE l.jarjestaja_ytunnus = '0205303-4'
                   AND l.diaarinumero = '85/531/2018'
     ),
     ajoneuvo_at AS (
         -- Add Ajoneuvoalan ammattitutkinto (354345)
         INSERT
             INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, koodistoversio)
                 SELECT new_lupa.id,
                        1,
                        'koulutus',
                        '354345',
                        null,
                        1,
                        null,
                        12
                 FROM new_lupa RETURNING id
     ),
     ajoneuvo_eat AS (
         -- Add Ajoneuvoalan erikoisammattitutkinto (457341)
         INSERT
             INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, koodistoversio)
                 SELECT new_lupa.id,
                        1,
                        'koulutus',
                        '457341',
                        null,
                        1,
                        null,
                        12
                 FROM new_lupa RETURNING id
     )

SELECT *
FROM new_lupa;
