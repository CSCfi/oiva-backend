-- End existing lupa for Toimela ry (0202493-8)
WITH old_lupa AS (
    UPDATE lupa
        SET loppupvm = '2019-12-31', jarjestaja_ytunnus = '0202493-8'
        WHERE diaarinumero = '201/532/2012' RETURNING *
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
                        'Uusimaa',
                        'Järjestämisluvan peruutus',
                        alkupvm,
                        loppupvm,
                        paatospvm,
                        'Toimela_ry.pdf'
                 FROM old_lupa l
     )

SELECT * FROM old_lupa;