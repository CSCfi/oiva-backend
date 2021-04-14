-- Fix Hoitopedagogisen Rudolf Steiner-koulun kannatusyhdistys (0116540-5) PO lupa 129/430/2003, 151/430/2004
WITH cur_lupa AS (
    SELECT id
    FROM lupa
    WHERE jarjestaja_ytunnus = '0116540-5'
      AND diaarinumero = '129/430/2003, 151/430/2004'
      AND koulutustyyppi = '1'
),
     joistalisalksi_20 AS (
         SELECT m.id
         FROM maarays m
                  LEFT JOIN cur_lupa l on m.lupa_id = l.id
         WHERE koodisto = 'kujalisamaareetjoistalisaksi'
           AND koodiarvo = '1'
           AND arvo = '20'
         LIMIT 1
     ),
     remove_joistalisaksi_20 AS (
         DELETE FROM maarays m
             WHERE id = (SELECT id FROM joistalisalksi_20) OR
                   parent_id = (SELECT id FROM joistalisalksi_20) RETURNING id
     )

SELECT *
FROM remove_joistalisaksi_20;
