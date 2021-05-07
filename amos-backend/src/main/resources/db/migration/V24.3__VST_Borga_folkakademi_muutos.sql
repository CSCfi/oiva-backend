-- Borgå folkakademi (1942543-9) kansanopisto change.
WITH old_lupa AS (
    UPDATE lupa
        SET loppupvm = '2021-07-31'
        WHERE jarjestaja_ytunnus = '1942543-9'
            AND
              diaarinumero = '43/532/2011' AND koulutustyyppi = '3'
        RETURNING id, paatoskierros_id, lupatila_id, asiatyyppi_id,
            diaarinumero, jarjestaja_ytunnus,
            jarjestaja_oid, alkupvm, loppupvm, paatospvm, meta,
            koulutustyyppi, oppilaitostyyppi, asianumero
),
     historia AS (
         INSERT INTO lupahistoria (lupa_id, diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm, voimassaololoppupvm,
                                   paatospvm, filename, koulutustyyppi, oppilaitostyyppi, asianumero)
             SELECT id,
                    diaarinumero,
                    jarjestaja_ytunnus,
                    jarjestaja_oid,
                    'Uusimaa',
                    'Järjestämisluvan muutos',
                    alkupvm,
                    loppupvm,
                    paatospvm,
                    meta ->> 'liitetiedosto',
                    koulutustyyppi,
                    oppilaitostyyppi,
                    asianumero
             FROM old_lupa RETURNING id
     ),
     new_lupa AS (
         INSERT INTO lupa (edellinen_lupa_id, paatoskierros_id, lupatila_id, asiatyyppi_id, diaarinumero, jarjestaja_ytunnus,
                           jarjestaja_oid, alkupvm, paatospvm, meta, luoja, koulutustyyppi, oppilaitostyyppi,
                           asianumero)
             SELECT id,
                    paatoskierros_id,
                    lupatila_id,
                    2, -- MUUTOS
                    'VN/9289/2021',
                    jarjestaja_ytunnus,
                    jarjestaja_oid,
                    '2021-08-01',
                    '2021-04-20',
                    '{"liitetiedosto": "Borga_folkakademi_ab_2021.pdf"}',
                    'oiva',
                    koulutustyyppi,
                    oppilaitostyyppi,
                    'VN/9289/2021'
             FROM old_lupa RETURNING id
     ),
     copied_maarays AS (
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, luoja, org_oid)
             SELECT l.id,
                    m.kohde_id,
                    m.koodisto,
                    m.koodiarvo,
                    m.arvo,
                    m.maaraystyyppi_id,
                    m.meta,
                    'oiva',
                    m.org_oid
             FROM new_lupa l,
                  old_lupa,
                  maarays m
             WHERE m.lupa_id = old_lupa.id
             RETURNING id
     )

SELECT *
FROM historia;
