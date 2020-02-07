-- End existing lupa for Rautjärven kunta (0206951-1)
WITH old_lupa AS (
    UPDATE lupa
        SET loppupvm = '2019-12-31', jarjestaja_ytunnus = '0206951-1'
        WHERE diaarinumero = '77/532/2012' RETURNING *
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
                        'Järjestämisluvan peruutus',
                        alkupvm,
                        loppupvm,
                        paatospvm,
                        'Rautjärven kunta.PDF'
                 FROM old_lupa l
     )

SELECT * FROM old_lupa;