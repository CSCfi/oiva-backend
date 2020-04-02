-- End existing lupa for Rovaniemen kaupunki (1978283-1)
WITH old_lupa AS (
    UPDATE lupa
        SET loppupvm = '2019-12-31', jarjestaja_ytunnus = '1978283-1'
        WHERE diaarinumero = '83/532/2012' RETURNING *
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
                        'Lappi',
                        'Järjestämisluvan peruutus',
                        alkupvm,
                        loppupvm,
                        paatospvm,
                        'Rovaniemen_kaupunki.PDF'
                 FROM old_lupa l
     )

SELECT * FROM old_lupa;