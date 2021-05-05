-- Ending Axxell Utbilding (2064886-7) kansanopisto lupa.
WITH cur_lupa AS (
    UPDATE lupa
        SET loppupvm = '2021-07-31'
        WHERE jarjestaja_ytunnus = '2064886-7'
            AND
              diaarinumero = '9/532/2018' AND koulutustyyppi = '3' RETURNING id, diaarinumero, jarjestaja_ytunnus,
                  jarjestaja_oid, alkupvm, loppupvm, paatospvm, meta, koulutustyyppi, oppilaitostyyppi
),
     historia AS (
         INSERT INTO lupahistoria (lupa_id, diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm, voimassaololoppupvm,
                                   paatospvm, filename, koulutustyyppi, oppilaitostyyppi)
             SELECT id,
                    diaarinumero,
                    jarjestaja_ytunnus,
                    jarjestaja_oid,
                    'Uusimaa',
                    'Järjestämisluvan peruutus',
                    alkupvm,
                    loppupvm,
                    paatospvm,
                    meta ->> 'liitetiedosto',
                    koulutustyyppi,
                    oppilaitostyyppi
             FROM cur_lupa RETURNING id
     )

SELECT *
FROM historia;
