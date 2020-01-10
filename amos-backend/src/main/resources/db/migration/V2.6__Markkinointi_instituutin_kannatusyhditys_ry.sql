-- Create lupahistoria row for Markkinointi-instituutin kannatusyhditys ry
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
VALUES ('33/531/2019', '0201689-0', '1.2.246.562.10.964165788610',
        'Uusimaa',
        'Järjestämisluvan korjaus', '2020-01-01', '2020-01-01', '2019-09-27',
        '33-531-2019.pdf');

WITH l AS (
    SELECT id
    FROM lupa
    WHERE diaarinumero = '33/531/2019'
),
     l_liite AS (
         -- Create lupa liite
         INSERT INTO liite (nimi, polku, luoja, tyyppi, kieli)
             VALUES ('Markkinointi-instituutin kannatusyhditys ry korjauspaatos 16.12.2019',
                     'paatosKirjeet2019/Markkinointiinstituutin_korjaus_16122019.pdf',
                     'oiva',
                     'korjaukset2018',
                     'fi') RETURNING id
     ),
     link AS (
         -- Create lupa_liite link
         INSERT INTO lupa_liite (lupa_id, liite_id) SELECT l.id, l_liite.id
                                                    FROM l,
                                                         l_liite
     )
     -- Add oppilasmaara Liiketoiminnan perustutkinto (331101) for Markkinointi-instituutin kannatusyhditys ry
INSERT
INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, koodistoversio)
SELECT l.id,
       1,
       'koulutus',
       '331101',
       1,
       12
FROM l;
