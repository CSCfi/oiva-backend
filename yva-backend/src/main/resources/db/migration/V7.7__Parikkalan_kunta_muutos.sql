-- End existing lupa for Parikkalan kunta
WITH old_lupa AS (
    UPDATE lupa
        SET loppupvm = '2019-12-31', jarjestaja_ytunnus='1913642-6'
        WHERE diaarinumero = '45/532/2012' RETURNING *
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
                        'Etelä-Karjala',
                        'Järjestämisluvan muutos',
                        alkupvm,
                        loppupvm,
                        paatospvm,
                        'Parikkalan_kunta.PDF'
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
                    '5/532/2019',
                    jarjestaja_ytunnus,
                    jarjestaja_oid,
                    '2020-01-01',
                    null,
                    '2019-09-24',
                    '{"esittelija": "Annika Bussman", "liitetiedosto": "Parikkalan_kunta_2020.pdf", "esittelija_nimike": "Opetusneuvos"}'
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
  "koulutustehtävämääräys-0": "JHL-opisto järjestää erityisesti julkisilla ja hyvinvointialoilla työskenteleville sekä muille ammatillisesti järjestäytyneille humanistista ja yhteiskunnallista koulutusta, kuten työhyvinvointiin, työelämän yhteistyötaitoihin ja yhdenvertaisuuteen liittyvää koulutusta.",
  "koulutustehtävämääräys-1": "",
  "koulutustehtävämääräys-2": ""
}'
FROM lupa l
WHERE l.id = lupa_id
  AND l.diaarinumero = '5/532/2019'
  AND koodisto = 'koulutustehtava';
