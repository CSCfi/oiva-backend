-- Fix Ahlmanin koulu säätiö (0155402-1) lupa VN/2826/2020
WITH cur_lupa AS (
    SELECT id FROM lupa WHERE jarjestaja_ytunnus = '0155402-1' AND asianumero = 'VN/2826/2020'
),
     metsaalan_pt AS (
         SELECT m.id, m.kohde_id
         FROM maarays m
                  LEFT JOIN cur_lupa l ON m.lupa_id = l.id
         WHERE l.id = l.id
           AND m.koodisto = 'koulutus'
           AND m.koodiarvo = '361301'
     ),
     osaamisala_1773 AS (
         -- Add missing metsäkoneenkuljetuksen osaamisalarajoitus (1773)
         INSERT INTO maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja,
                              koodistoversio)
             SELECT m.id,
                    l.id,
                    m.kohde_id,
                    'osaamisala',
                    '1773',
                    2,
                    'oiva',
                    3
             FROM cur_lupa l,
                  metsaalan_pt m RETURNING id
     )

SELECT *
FROM osaamisala_1773;
