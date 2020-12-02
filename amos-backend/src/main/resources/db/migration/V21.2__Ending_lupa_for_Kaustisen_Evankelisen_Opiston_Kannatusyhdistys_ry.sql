-- Kaustisen Evankelisen Opiston Kannatusyhdistys ry ending lupa
WITH cur_lupa AS (
    UPDATE lupa
        SET loppupvm = '2020-12-31'
        WHERE jarjestaja_ytunnus = '0178980-8'
            AND
              diaarinumero = '8/532/2013' RETURNING diaarinumero, jarjestaja_ytunnus, jarjestaja_oid, alkupvm, loppupvm, paatospvm, meta
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
                    meta->>'liitetiedosto'
             FROM cur_lupa
     )

SELECT *
FROM cur_lupa;
