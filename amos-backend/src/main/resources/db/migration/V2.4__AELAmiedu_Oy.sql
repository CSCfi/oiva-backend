-- End existing lupa for Ammattienedistämislaitossäätiö (0114371-6)
UPDATE lupa
SET loppupvm = '2019-12-31'
WHERE jarjestaja_ytunnus = '0116354-9'
  AND diaarinumero = '60/531/2018';

-- Create lupahistoria row for Ammattienedistämislaitossäätiö
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
VALUES ('60/531/2018', '0116354-9', '1.2.246.562.10.53542906168',
        'Uusimaa',
        'Järjestämisluvan peruutus', '2018-01-01', '2019-12-31', '2017-10-06',
        '60_531_2018.pdf');

-- End existing lupa for Ami-säätiön (0213612-0)
UPDATE lupa
SET loppupvm = '2019-12-31'
WHERE jarjestaja_ytunnus = '0213612-0'
  AND diaarinumero = '75/531/2018';

-- Create lupahistoria row for Ami-säätiö
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
VALUES ('75/531/2018', '0213612-0', '1.2.246.562.10.53542906168',
        'Uusimaa',
        'Järjestämisluvan peruutus', '2018-01-01', '2019-12-31', '2017-10-06',
        '75_531_2018.pdf');

WITH new_lupa AS (
    -- Create new lupa for AEL-Amiedu Oy (3008326-5)
    INSERT INTO lupa (paatoskierros_id, lupatila_id, asiatyyppi_id,
                      diaarinumero, jarjestaja_ytunnus, jarjestaja_oid, alkupvm, loppupvm, paatospvm,
                      meta, maksu, luoja, paivittaja, paivityspvm)
        VALUES (19,
                3,
                2,
                '60/531/2019',
                '3008326-5',
                '1.2.246.562.10.82346919515',
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
     new_maarays AS (
         -- Merge maaraykset from old Ammattienedistämislaitossäätiö and Ammattienedistämislaitossäätiö lupa.
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
                 WHERE ((l.jarjestaja_ytunnus = '0116354-9'
                     AND l.diaarinumero = '60/531/2018')
                     OR (l.jarjestaja_ytunnus = '0213612-0'
                         AND l.diaarinumero = '75/531/2018'))
                   -- Drop oppilasmaara
                   AND (koodisto != 'koulutussektori'
                     -- Drop some of the osaamisalarajoitukset
                     AND koodiarvo NOT IN ('2387', '3123', '1531', '1617', '2309', '2311', '2332'))
     ),
     l_liite AS (
         -- Create lupa liite
         INSERT INTO liite (nimi, polku, luoja, tyyppi, kieli)
             VALUES ('AEL-Amiedu_Oy',
                     'paatosKirjeet2019/AEL-Amiedu_Oy.pdf',
                     'oiva',
                     'paatosKirje',
                     'fi') RETURNING id
     ),
     l_link AS (
         -- Create lupa_liite link
         INSERT INTO lupa_liite (lupa_id, liite_id) SELECT new_lupa.id, l_liite.id
                                                    FROM new_lupa,
                                                         l_liite
     ),
     elain_eat AS (
         -- Add Eläintenhoidon erikoisammattitutkinto (467905)
         INSERT
             INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, koodistoversio)
                 SELECT new_lupa.id,
                        1,
                        'koulutus',
                        '467905',
                        null,
                        1,
                        null,
                        12
                 FROM new_lupa RETURNING id
     )


-- Add oppilasmaara maarays with sum of Ammattienedistämislaitossäätiö and Ami-säätiö
INSERT
INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, luoja)
SELECT new_lupa.id,
       4,
       'koulutussektori',
       '3',
       2484,
       1,
       'oiva'
FROM new_lupa;

-- Update correct parent ids for osaamisalarajoite
UPDATE maarays osaamisala
SET parent_id = koulutus.id
FROM maarays koulutus,
     lupa
WHERE koulutus.lupa_id = lupa.id
  AND osaamisala.lupa_id = lupa.id
  AND lupa.diaarinumero = '60/531/2019'
  AND ((koulutus.koodiarvo = '352201' AND osaamisala.koodiarvo = '1505') OR
       (koulutus.koodiarvo = '354245' AND osaamisala.koodiarvo = '2387') OR
       (koulutus.koodiarvo = '457241' AND osaamisala.koodiarvo = '3137'));

-- Update kokeilu text
UPDATE maarays
SET meta = '{
  "kokeilu": {
    "fi": "Koulutuksen järjestäjä voi ammatillisesta koulutuksesta annetun lain (531/2017) 132 §:n nojalla toteuttaa loppuun välinehuoltoalan perustutkintoa koskevan kokeilun.",
    "sv": ""
  }
}'
FROM lupa l
WHERE l.diaarinumero = '60/531/2019'
  AND koodisto = 'oivamuutoikeudetvelvollisuudetehdotjatehtavat'
  AND koodiarvo = '7';

-- Update kuljettajakoulutus koodiarvo
UPDATE maarays
SET koodiarvo = '5'
FROM lupa l
WHERE l.diaarinumero = '60/531/2019'
  AND lupa_id = l.id
  AND koodisto = 'kuljettajakoulutus';