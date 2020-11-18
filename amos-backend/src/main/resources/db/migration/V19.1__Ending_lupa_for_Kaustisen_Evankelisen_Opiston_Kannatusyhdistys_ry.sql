-- Ending current Ammatillinen lupa for Kaustisen Evankelisen Opiston Kannatusyhdisty ry (0178980-8)
WITH cur_lupa AS (
    UPDATE lupa
        SET loppupvm = '2020-12-31'
        WHERE jarjestaja_ytunnus = '0178980-8'
            AND
              diaarinumero = '41/531/2017' RETURNING diaarinumero, jarjestaja_ytunnus, jarjestaja_oid, alkupvm, loppupvm, paatospvm
),
     historia AS (
         INSERT INTO lupahistoria (diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm, voimassaololoppupvm,
                                   paatospvm, filename)
             SELECT diaarinumero,
                    jarjestaja_ytunnus,
                    jarjestaja_oid,
                    'Keski-Pohjanmaa',
                    'Järjestämisluvan peruutus',
                    alkupvm,
                    loppupvm,
                    paatospvm,
                    diaarinumero
             FROM cur_lupa
     )

SELECT *
FROM cur_lupa;
