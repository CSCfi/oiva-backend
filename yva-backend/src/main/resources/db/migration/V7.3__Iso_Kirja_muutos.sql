-- End existing lupa for Iso Kirja -opisto (0145808-0)
WITH old_lupa AS (
    UPDATE lupa
        SET loppupvm = '2019-12-31', jarjestaja_ytunnus = '0145808-0'
        WHERE diaarinumero = '41/532/2016' RETURNING *
),
     historia AS (
         -- Create lupahistoria row
         INSERT
             INTO lupahistoria
                 (diaarinumero,
                  ytunnus,
                  oid,
                  maakunta,
                  tila,
                  voimassaoloalkupvm,
                  voimassaololoppupvm,
                  paatospvm,
                  filename)
                 SELECT diaarinumero,
                        jarjestaja_ytunnus,
                        jarjestaja_oid,
                        'Keski-Suomi',
                        'Järjestämisluvan muutos',
                        alkupvm,
                        loppupvm,
                        paatospvm,
                        'Iso Kirja ry.pdf'
                 FROM old_lupa l
     ),
     new_lupa AS (
         INSERT INTO lupa (paatoskierros_id,
                           lupatila_id,
                           asiatyyppi_id,
                           diaarinumero,
                           jarjestaja_ytunnus,
                           jarjestaja_oid, alkupvm,
                           loppupvm, paatospvm, meta)
             SELECT paatoskierros_id,
                    lupatila_id,
                    2,
                    '11/532/2018',
                    jarjestaja_ytunnus,
                    jarjestaja_oid,
                    '2020-01-01',
                    null,
                    '2019-06-14',
                    '{"esittelija": "Annika Bussman", "liitetiedosto": "Iso_Kirja_ry_2020.pdf", "esittelija_nimike": "Opetusneuvos"}'
             FROM old_lupa
             RETURNING *
     ),
     copy_maarays AS (
         -- copy original maaraykset
         INSERT INTO maarays (parent_id,
                              lupa_id,
                              kohde_id,
                              koodisto,
                              koodiarvo,
                              arvo,
                              maaraystyyppi_id,
                              meta,
                              luoja,
                              luontipvm,
                              paivittaja,
                              paivityspvm,
                              koodistoversio,
                              org_oid)
             SELECT m.parent_id,
                    new_lupa.id,
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
                    m.koodistoversio,
                    m.org_oid
             FROM new_lupa,
                  old_lupa o,
                  maarays m
             WHERE m.lupa_id = o.id
             RETURNING *
     )

SELECT * from new_lupa;

UPDATE maarays
SET meta = '{
  "koulutustehtävämääräys-0": "Iso Kirja -opisto on arvo- ja aatetaustaltaan helluntaikristillinen. Koulutus painottuu teologisiin ja muihin yleissivistäviin aineisiin, jotka antavat valmiuksia seurakuntaelämässä toimimiseen aktiivisena yhteiskunnan jäsenenä sekä kehittävät lähetys- ja kehitysyhteistyössä tarvittavia valmiuksia. Opisto järjestää myös etäopetusta.",
  "koulutustehtävämääräys-1": "",
  "koulutustehtävämääräys-2": ""
}'
FROM lupa l
WHERE l.id = lupa_id
  AND l.diaarinumero = '11/532/2018'
  AND koodisto = 'koulutustehtava';
