-- End existing lupa for Rastor Oy (0114371-6)
UPDATE lupa
SET loppupvm = '2019-12-31'
WHERE jarjestaja_ytunnus = '0114371-6'
  AND diaarinumero = '161/531/2017';

-- Create lupahistoria row for Rastor Oy
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
    VALUES ('161/531/2017', '0114371-6', '1.2.246.562.10.84205146883',
            'Uusimaa',
            'Järjestämisluvan peruutus', '2018-01-01', '2019-12-31', '2017-10-06',
            '161-531-2017.pdf');

-- End existing lupa for Markkinointi-instituutin Kannatusyhdistys ry (0201689-0)
UPDATE lupa
SET loppupvm = '2019-12-31'
WHERE jarjestaja_ytunnus = '0201689-0'
  AND diaarinumero = '138/531/2017';

-- Create lupahistoria row for Markkinointi-instituutin Kannatusyhdistys ry
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
    VALUES ('138/531/2017', '0201689-0', '1.2.246.562.10.964165788610',
            'Uusimaa',
            'Järjestämisluvan peruutus', '2018-01-01', '2019-12-31', '2017-10-06',
            '138-531-2017.pdf');

WITH new_lupa AS (
    -- Create new lupa for Markkinointi-instituutin Kannatusyhdistys ry (0201689-0)
    INSERT INTO lupa (paatoskierros_id, lupatila_id, asiatyyppi_id,
                      diaarinumero, jarjestaja_ytunnus, jarjestaja_oid, alkupvm, loppupvm, paatospvm,
                      meta, maksu, luoja, paivittaja, paivityspvm)
        VALUES (19,
                3,
                2,
                '33/531/2019',
                '0201689-0',
                '1.2.246.562.10.964165788610',
                '2020-01-01',
                null,
                '2019-09-27',
                '{
                  "ministeri": "Li Andersson",
                  "esittelija": "Tarja Koskimäki",
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
             VALUES ('Markkinointi-instituutin kannatusyhdistys ry',
                     'paatosKirjeet2019/Markkinointi-instituutin_Kannatusyhdistys_ry.pdf',
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
     maarays AS (
         -- Merge maaraykset from Rastor and old Markkinointi-instituutin kannatatusyhdistys lupa.
         INSERT
             INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, koodistoversio)
                 SELECT DISTINCT new_lupa.id,
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
                 WHERE ((l.jarjestaja_ytunnus = '0114371-6'
                     AND l.diaarinumero = '161/531/2017')
                     OR (l.jarjestaja_ytunnus = '0201689-0'
                         AND l.diaarinumero = '138/531/2017'))
                   AND ((koodisto = 'koulutus' AND koodiarvo
                     -- Don't include ended koulutus
                     NOT IN ('334117', '331101', '334101', '334102', '334104', '334105', '334106',
                             '334108', '334114',
                             '334115', '334118', '437101', '437102', '437107', '437108', '437109', '437110',
                             '457305', '457306',
                             '458901', '354111', '458204', '458207', '437111') OR
                     -- Don't include oppilasmaara
                         (koodisto != 'koulutus' AND koodisto != 'koulutussektori')))
     ),
     m_talotek AS (
         -- Add Talotekniikan erikoisammattitutkinto (457241)
         INSERT
             INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, koodistoversio)
                 SELECT new_lupa.id,
                        1,
                        'koulutus',
                        '457241',
                        null,
                        1,
                        null,
                        12
                 FROM new_lupa RETURNING id
     ),
     rajoite AS (
         -- Add kylmätekniikan osaamisala rajoite for Talotekniikan erikoisammattitutkinto
         INSERT
             INTO maarays (lupa_id, parent_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta,
                           koodistoversio, luoja)
                 SELECT new_lupa.id,
                        m_talotek.id,
                        1,
                        'osaamisala',
                        '3137',
                        null,
                        2,
                        null,
                        null,
                        'oiva'
                 FROM m_talotek,
                      new_lupa RETURNING id
     )

     -- Add oppilasmaara maarays with sum of Markkinointi-instituutti and Rastor
INSERT
INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, luoja)
SELECT new_lupa.id,
       4,
       'koulutussektori',
       '3',
       1430,
       1,
       'oiva'
FROM new_lupa;

