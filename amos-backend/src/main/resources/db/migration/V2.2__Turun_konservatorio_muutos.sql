-- End existing lupa for Turun Konservatorion kannatausyhdistys (0204843-8)
UPDATE lupa
SET loppupvm = '2019-12-31'
WHERE jarjestaja_ytunnus = '0204843-8'
  AND diaarinumero = '176/531/2017';

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
    VALUES ('176/531/2017', '0204843-8', '1.2.246.562.10.329381985810',
            'Varsinais-Suomi',
            'Järjestämisluvan peruutus', '2018-01-01', '2019-12-31', '2017-10-06',
            '176-531-2017.pdf');

WITH new_lupa AS (
    -- Create new lupa for Turun musiikinopetus Oy (2962876-6)
    INSERT INTO lupa (paatoskierros_id, lupatila_id, asiatyyppi_id,
                      diaarinumero, jarjestaja_ytunnus, jarjestaja_oid, alkupvm, loppupvm, paatospvm,
                      meta, maksu, luoja, paivittaja, paivityspvm)
        VALUES (19,
                3,
                2,
                '67/531/2019',
                '2962876-6',
                '1.2.246.562.10.43675188015',
                '2020-01-01',
                null,
                '2019-09-25',
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
             VALUES ('Turun_musiikinopetus_Oy',
                     'paatosKirjeet2019/Turun_musiikinopetus_Oy.pdf',
                     'oiva',
                     'paatosKirje',
                     'fi') RETURNING id
     ),
     link AS (
         -- Create lupa_liite link
         INSERT INTO lupa_liite (lupa_id, liite_id) SELECT new_lupa.id, l_liite.id
                                                    FROM new_lupa,
                                                         l_liite
     )

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
       m.koodistoversio
FROM new_lupa,
     maarays m
         LEFT JOIN lupa l ON m.lupa_id = l.id
WHERE l.jarjestaja_ytunnus = '0204843-8'
  AND l.diaarinumero = '176/531/2017';

-- Update koulutus koodisto version
UPDATE maarays m
SET koodistoversio = 12
FROM lupa l
WHERE m.lupa_id = l.id
  AND m.koodisto = 'koulutus'
  AND m.koodiarvo = '324201'
  AND l.jarjestaja_ytunnus = '2962876-6'
  AND l.diaarinumero = '67/531/2019';
