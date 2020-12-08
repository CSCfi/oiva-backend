-- Nanso Group Oy ending lupa
WITH cur_lupa AS (
    UPDATE lupa
        SET loppupvm = '2020-12-31'
        WHERE jarjestaja_ytunnus = '0151534-8'
            AND
              diaarinumero = '57/530/2007' RETURNING id, diaarinumero, jarjestaja_ytunnus,
                  jarjestaja_oid, alkupvm, loppupvm, paatospvm, meta, koulutustyyppi, oppilaitostyyppi
),
     historia AS (
         INSERT INTO lupahistoria (lupa_id, diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm, voimassaololoppupvm,
                                   paatospvm, filename, koulutustyyppi, oppilaitostyyppi)
             SELECT id,
                    diaarinumero,
                    jarjestaja_ytunnus,
                    jarjestaja_oid,
                    'Pirkanmaa',
                    'Järjestämisluvan peruutus',
                    alkupvm,
                    loppupvm,
                    paatospvm,
                    diaarinumero,
                    koulutustyyppi,
                    oppilaitostyyppi
             FROM cur_lupa
     )

SELECT *
FROM cur_lupa;
