-- Fix Tampereen kaupunki (0211675-2) lupa and history

-- Correct VN/13512/2020 data and history
WITH vn_13512_2020 AS (
    SELECT id
    FROM lupa
    WHERE jarjestaja_ytunnus = '0211675-2'
      AND asianumero = 'VN/13512/2020'
),
     history AS (
         -- delete duplicate
         DELETE
             FROM lupahistoria
                 WHERE id =
                       (SELECT h.id
                        FROM lupahistoria h,
                             vn_13512_2020 l
                        WHERE h.lupa_id = l.id
                        LIMIT 1)
     )

SELECT *
FROM vn_13512_2020;

-- End lupa VN/23139/2020
WITH vn_23139_2020 AS (
    UPDATE lupa SET loppupvm = '2020-12-31'
        WHERE jarjestaja_ytunnus = '0211675-2'
            AND
              asianumero = 'VN/23139/2020' RETURNING id, diaarinumero, jarjestaja_ytunnus, jarjestaja_oid,
            alkupvm, loppupvm, paatospvm, koulutustyyppi, oppilaitostyyppi, asianumero
),
     history AS (
         INSERT INTO lupahistoria (lupa_id, diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm,
                                   voimassaololoppupvm,
                                   paatospvm, filename, koulutustyyppi, oppilaitostyyppi, kumottupvm, asianumero)
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
                    oppilaitostyyppi,
                    '2020-12-17',
                    asianumero
             FROM vn_23139_2020
     ),
     -- Correct VN/21793/2020 data
     vn_21793_2020 AS (
         UPDATE lupa SET edellinen_lupa_id = (SELECT id FROM vn_23139_2020)
             WHERE jarjestaja_ytunnus = '0211675-2'
                 AND asianumero = 'VN/21793/2020' RETURNING id
     ),
     -- Copy missing maarays from old lupa
     tieto_viestinta_at_eat AS (
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, luoja, koodistoversio)
             SELECT l.id,
                    kohde_id,
                    koodisto,
                    koodiarvo,
                    arvo,
                    maaraystyyppi_id,
                    'oiva',
                    koodistoversio
             FROM maarays,
                  vn_23139_2020 old_lupa,
                  vn_21793_2020 l
             WHERE lupa_id = old_lupa.id
               AND koodisto = 'koulutus'
               AND koodiarvo IN ('344103', '447102') RETURNING id
     ),
     -- Delete tutkinto that was removed in old lupa
     removals AS (
         DELETE FROM maarays
             WHERE id IN (
                 SELECT m.id
                 FROM maarays m,
                      vn_21793_2020 l
                 WHERE lupa_id = l.id
                   AND koodisto = 'koulutus'
                   AND koodiarvo IN ('354503', '457503')
             )
     )

SELECT *
FROM vn_21793_2020;
