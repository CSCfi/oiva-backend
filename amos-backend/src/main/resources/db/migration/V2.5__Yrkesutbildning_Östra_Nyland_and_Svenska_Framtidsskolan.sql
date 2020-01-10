-- End existing lupa for Yrkesutbildning i Östra Nyland (0214081-6)
UPDATE lupa
SET loppupvm = '2019-12-31'
WHERE jarjestaja_ytunnus = '0214081-6'
  AND diaarinumero = '120/531/2017';

-- Create lupahistoria row for Yrkesutbildning i Östra Nyland
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
VALUES ('120/531/2017', '0214081-6', '1.2.246.562.10.88899064674',
        'Uusimaa',
        'Järjestämisluvan peruutus', '2018-01-01', '2019-12-31', '2017-10-06',
        '120-531-2017.pdf');

-- End existing lupa for Svenska Framtidsskolan i Helsingforsregionen Ab (1648362-5)
UPDATE lupa
SET loppupvm = '2019-12-31'
WHERE jarjestaja_ytunnus = '1648362-5'
  AND diaarinumero = '144/531/2017';

-- Create lupahistoria row for Svenska Framtidsskolan i Helsingforsregionen Ab
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
VALUES ('144/531/2017', '1648362-5', '1.2.246.562.10.43289496051',
        'Uusimaa',
        'Järjestämisluvan peruutus', '2018-01-01', '2019-12-31', '2017-10-06',
        '144-531-2017.pdf');

WITH new_lupa AS (
    -- Create new lupa for Svenska Framtidsskolan i Helsingforsregionen Ab (1648362-5)
    INSERT INTO lupa (paatoskierros_id, lupatila_id, asiatyyppi_id,
                      diaarinumero, jarjestaja_ytunnus, jarjestaja_oid, alkupvm, loppupvm, paatospvm,
                      meta, maksu, luoja, paivittaja, paivityspvm)
        VALUES (19,
                3,
                2,
                '20/531/2019',
                '1648362-5',
                '1.2.246.562.10.43289496051',
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
             VALUES ('Svenska Framtidsskolan i Helsingforsregionen Ab',
                     'paatosKirjeet2019/Svenska_Framtidsskolan_Helsingforsregionen_Ab.pdf',
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
                 WHERE ((l.jarjestaja_ytunnus = '0214081-6'
                     AND l.diaarinumero = '120/531/2017')
                     OR (l.jarjestaja_ytunnus = '1648362-5'
                         AND l.diaarinumero = '144/531/2017'))
                   AND ((koodisto = 'koulutus' AND koodiarvo
                     -- Don't include these koulutus
                     NOT IN ('321101', '321301', '321603', '334102', '351106', '351704', '334104',
                             '351204', '351701', '352401', '352902', '374114', '374124', '477106',
                             '381112', '381113', '381303', '381304', '384106', '487101') OR
                     -- Don't include oppilasmaara
                         (koodisto != 'koulutus' AND koodisto != 'koulutussektori')))
     )

-- Add oppilasmaara maarays with sum of Markkinointi-instituutti and Rastor
INSERT
INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, luoja)
SELECT new_lupa.id,
       4,
       'koulutussektori',
       '3',
       1087,
       1,
       'oiva'
FROM new_lupa;

-- Update kuljettajakoulutus koodiarvo
UPDATE maarays
SET koodiarvo = '5'
FROM lupa l
WHERE l.diaarinumero = '20/531/2019'
  AND lupa_id = l.id
  AND koodisto = 'kuljettajakoulutus';