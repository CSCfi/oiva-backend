-- Create separate lukio lupa from esi- ja perusopetus / lukio compination lupa.
WITH lukio_lupa AS (
    INSERT INTO lupa (paatoskierros_id, lupatila_id, asiatyyppi_id, diaarinumero, jarjestaja_ytunnus,
                      jarjestaja_oid,
                      alkupvm, loppupvm, paatospvm, meta, maksu, koulutustyyppi)
        SELECT paatoskierros_id,
               lupatila_id,
               asiatyyppi_id,
               'OKM' || diaarinumero,
               jarjestaja_ytunnus,
               jarjestaja_oid,
               alkupvm,
               loppupvm,
               paatospvm,
               meta,
               maksu,
               2 -- Lukio
        FROM lupa
        WHERE diaarinumero IN ('1008/430/98', '8/430/2000', '1007/430/98', '820/430/98', '789/430/98', '900/430/98',
                               '7/530/2016', '816/430/98', '92/430/2000', '770/430/98', '51/530/2016', '897/430/98',
                               '899/430/98', '992/430/1998', '702/430/1998', '25/530/2013', '989/430/1998',
                               '78/530/2015')
        RETURNING id, diaarinumero
),
     lukio_maarays AS (
         -- Copy maarays for lukio lupa
         INSERT INTO maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta,
                              koodistoversio)
             SELECT parent_id,
                    lu.id,
                    kohde_id,
                    koodisto,
                    koodiarvo,
                    arvo,
                    maaraystyyppi_id,
                    m.meta,
                    koodistoversio
             FROM maarays m,
                  lukio_lupa lu,
                  lupa l
             WHERE m.lupa_id = l.id
               AND lu.diaarinumero = 'OKM' || l.diaarinumero
               -- Remove esi- ja perusopetus opetustehtava and koulutusmuoto from lukio lupa
               AND NOT ((m.koodisto  IN ('koulutusmuoto', 'yleissivistavankoulutusmuodot') AND m.koodiarvo = '1') OR
                        (m.koodisto = 'opetustehtava' AND m.koodiarvo = '18'))
               AND l.diaarinumero IN
                   ('1008/430/98', '8/430/2000', '1007/430/98', '820/430/98', '789/430/98', '900/430/98',
                    '7/530/2016', '816/430/98', '92/430/2000', '770/430/98', '51/530/2016', '897/430/98',
                    '899/430/98', '992/430/1998', '702/430/1998', '25/530/2013', '989/430/1998',
                    '78/530/2015') RETURNING *
     )

SELECT *
FROM lukio_lupa;

-- Update old compination lupa to esi- ja perusopetus koulutustyyppi.
WITH perus_lupa AS (
    UPDATE lupa
        SET koulutustyyppi = 1
        WHERE diaarinumero IN ('1008/430/98', '8/430/2000', '1007/430/98', '820/430/98', '789/430/98', '900/430/98',
                               '7/530/2016', '816/430/98', '92/430/2000', '770/430/98', '51/530/2016', '897/430/98',
                               '899/430/98', '992/430/1998', '702/430/1998', '25/530/2013', '989/430/1998',
                               '78/530/2015')
        RETURNING id
)

-- Remove lukio opetustehtava and koulutusmuoto from esi- ja perusopetus lupa
DELETE
FROM maarays
WHERE id IN (SELECT m.id
             FROM maarays m,
                  perus_lupa pl
             WHERE lupa_id = pl.id
               AND ((m.koodisto IN ('koulutusmuoto', 'yleissivistavankoulutusmuodot') AND m.koodiarvo = '2') OR
                    (m.koodisto = 'opetustehtava' AND m.koodiarvo = '13')));
