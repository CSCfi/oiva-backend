-- Changes to Pohjois-karjalan kky 0212371-7

WITH old AS (
    UPDATE lupa SET loppupvm = '2020-07-31'
        WHERE diaarinumero = '81/531/2018'
        RETURNING id, diaarinumero, jarjestaja_ytunnus, jarjestaja_oid, alkupvm, loppupvm, paatospvm, lupatila_id,
            asiatyyppi_id, maksu, luoja, paivittaja, paivityspvm
),
     -- insert into lupahistoria
     historia AS (
         INSERT INTO lupahistoria (diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm, voimassaololoppupvm,
                                   paatospvm, filename, lupa_id)
             SELECT o.diaarinumero,
                    o.jarjestaja_ytunnus,
                    o.jarjestaja_oid,
                    'Pohjois-karjala',
                    'Järjestämisluvan muutos',
                    o.alkupvm,
                    o.loppupvm,
                    o.paatospvm,
                    '81-531-2018.pdf',
                    o.id
             FROM old o
             RETURNING id
     ),
     -- create new row to lupa table
     new_lupa AS (
         INSERT INTO lupa (paatoskierros_id, lupatila_id, asiatyyppi_id, asianumero, jarjestaja_ytunnus,
                           jarjestaja_oid, alkupvm,
                           loppupvm, paatospvm, meta, maksu, luoja, paivittaja, paivityspvm)
             SELECT 19,
                    o.lupatila_id,
                    o.asiatyyppi_id,
                    'VN/2288/2020',
                    o.jarjestaja_ytunnus,
                    o.jarjestaja_oid,
                    '2020-08-01',
                    null,
                    '2020-06-12',
                    '{"ministeri": "Li Andersson", "esittelija": "Anne Mårtensson", "ministeri_nimike": "Opetusministeri", "esittelija_nimike": ""}',
                    o.maksu,
                    o.luoja,
                    o.paivittaja,
                    o.paivityspvm
             FROM old o
             RETURNING id
     ),
     -- copy original maaraykset
     copied_maarays AS (
         INSERT INTO maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, luoja,
                              luontipvm, paivittaja,
                              paivityspvm, koodistoversio)
             SELECT m.parent_id,
                    l.id,
                    m.kohde_id,
                    m.koodisto,
                    m.koodiarvo,
                    m.arvo,
                    m.maaraystyyppi_id,
                    m.meta,
                    m.luoja,
                    m.luontipvm,
                    m.paivittaja,
                    m.paivityspvm,
                    CASE WHEN m.koodisto = 'koulutus' THEN 12 ELSE m.koodistoversio END
             FROM new_lupa l,
                  maarays m
                      JOIN old ON m.lupa_id = old.id
             WHERE (koodisto != 'koulutus'
                 OR (koodisto = 'koulutus' AND koodiarvo NOT IN ('351502', '341101')))
             RETURNING id
     ),
     -- Kalatalouden at (maa- ja metsätalousalat)
     kalatalous_at AS (
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja, koodistoversio)
             SELECT l.id, 1, 'koulutus', '361401', 1, 'oiva', 12 FROM new_lupa l
             RETURNING id
     ),
     -- Välinehuoltoalan pt (terveys- ja hyvinvointialat)
     valinehuoltoala_pt AS (
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja, koodistoversio)
             SELECT l.id, 1, 'koulutus', '371113', 1, 'oiva', 12 FROM new_lupa l
             RETURNING id
     ),
     -- Maarakennusalan eat (tekniikan alat)
     maarakennus_eat AS (
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja, koodistoversio)
             SELECT l.id, 1, 'koulutus', '458206', 1, 'oiva', 12 FROM new_lupa l
             RETURNING id
     ),
     -- Tieto- ja viestintätekniikan pt (Tietojenkäsittely ja tietoliikenne (ICT))
     tieto_viestinta_pt AS (
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja, koodistoversio)
             SELECT l.id, 1, 'koulutus', '341102', 1, 'oiva', 12 FROM new_lupa l
             RETURNING id
     )

SELECT *
FROM new_lupa;

-- update child maarays references to correct parents
UPDATE maarays child
SET parent_id = (SELECT new_parent.id
                 FROM maarays old_parent
                          JOIN maarays new_parent
                               ON old_parent.koodisto = new_parent.koodisto AND
                                  old_parent.koodiarvo = new_parent.koodiarvo
                 WHERE child.lupa_id = new_parent.lupa_id
                   AND child.parent_id = old_parent.id)
FROM lupa
WHERE lupa.asianumero = 'VN/2288/2020'
  AND child.lupa_id = lupa.id
  AND parent_id IS NOT NULL;
