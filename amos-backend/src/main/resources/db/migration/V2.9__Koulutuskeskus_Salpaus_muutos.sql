-- End existing lupa for Koulutuskeskus Salpaus (0993644-6)
UPDATE lupa
SET loppupvm = '2019-12-31'
WHERE jarjestaja_ytunnus = '0993644-6'
  AND diaarinumero = '48/531/2018';

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
VALUES ('48/531/2018', '0993644-6', '1.2.246.562.10.594252633210',
        'Päijät-Häme',
        'Järjestämisluvan muutos', '2019-01-01', '2019-12-31', '2018-12-14',
        '48-531-2018.pdf');

WITH new_lupa AS (
    -- Create new lupa for Koulutuskeskus Salpaus (0993644-6)
    INSERT INTO lupa (paatoskierros_id, lupatila_id, asiatyyppi_id,
                      diaarinumero, jarjestaja_ytunnus, jarjestaja_oid, alkupvm, loppupvm, paatospvm,
                      meta, maksu, luoja, paivittaja, paivityspvm)
        VALUES (19,
                3,
                2,
                '32/531/2019',
                '0993644-6',
                '1.2.246.562.10.594252633210',
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
             VALUES ('Koulutuskeskus Salpaus',
                     'paatosKirjeet2019/Koulutuskeskus_Salpaus.pdf',
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
                 WHERE l.jarjestaja_ytunnus = '0993644-6'
                   AND l.diaarinumero = '48/531/2018'
     ),
     hius_eat AS (
         -- Add Ajoneuvoalan ammattitutkinto (354345)
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
