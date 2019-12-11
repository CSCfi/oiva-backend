-- End existing lupa for Savon koulutuskuntayhtymä (1852679-9)
UPDATE lupa
SET loppupvm = '2019-12-31'
WHERE jarjestaja_ytunnus = '1852679-9'
  AND diaarinumero = '46/531/2018';

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
VALUES ('46/531/2018', '1852679-9', '1.2.246.562.10.99191194051',
        'Pohjois-Savo',
        'Järjestämisluvan muutos', '2019-01-01', '2019-12-31', '2018-12-14',
        '46-531-2018.pdf');

WITH new_lupa AS (
    -- Create new lupa for Savon koulutuskuntayhtymä (1852679-9)
    INSERT INTO lupa (paatoskierros_id, lupatila_id, asiatyyppi_id,
                      diaarinumero, jarjestaja_ytunnus, jarjestaja_oid, alkupvm, loppupvm, paatospvm,
                      meta, maksu, luoja, paivittaja, paivityspvm)
        VALUES (19,
                3,
                2,
                '37/531/2019',
                '1852679-9',
                '1.2.246.562.10.99191194051',
                '2020-01-01',
                null,
                '2019-12-16',
                '{
                  "ministeri": "Li Andersson",
                  "esittelija": "Anne Mårtensson",
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
             VALUES ('Savon koulutuskuntayhtymä',
                     'paatosKirjeet2019/Savon_koulutuskuntayhtymä.pdf',
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
                 WHERE l.jarjestaja_ytunnus = '1852679-9'
                   AND l.diaarinumero = '46/531/2018'
                   AND (m.koodisto != 'osaamisala'
                     -- Do not include obsolete osaamisalarajoite maarays
                     OR (m.koodisto = 'osaamisala' AND m.koodiarvo NOT IN
                                                       ('2332', '3123')))
     )

SELECT *
FROM new_lupa;

-- Update correct parent ids for osaamisalarajoite
UPDATE maarays osaamisala
SET parent_id = koulutus.id
FROM maarays koulutus,
     lupa l
WHERE koulutus.lupa_id = l.id
  AND osaamisala.lupa_id = l.id
  AND l.diaarinumero = '37/531/2019'
  AND ((koulutus.koodiarvo = '352201' AND osaamisala.koodiarvo = '1505') OR
       (koulutus.koodiarvo = '457241' AND osaamisala.koodiarvo = '3137'));

-- Update opiskelijamäärä
UPDATE maarays m
SET arvo = 40
FROM lupa l
WHERE l.id = m.lupa_id
  AND l.diaarinumero = '37/531/2019'
  AND m.koodisto = 'oivamuutoikeudetvelvollisuudetehdotjatehtavat'
  AND m.koodiarvo = '2'
  AND m.arvo = '27';
