-- Add missing osaamisala for current Sasky lupa.
WITH cur_lupa AS (
    SELECT id
    FROM lupa
    WHERE jarjestaja_ytunnus = '0204964-1'
      AND asianumero = 'VN/13446/2020'
),
     talotekniikan_at AS (
         SELECT m.id
         FROM maarays m,
              cur_lupa l
         WHERE m.lupa_id = l.id
           AND koodisto = 'koulutus'
           AND koodiarvo = '354245'
     ),
     kylmaasennus AS (
         UPDATE maarays SET parent_id = (SELECT tta.id
                                         FROM talotekniikan_at tta)
             WHERE koodisto = 'osaamisala' AND koodiarvo = '2387' RETURNING id
     )

SELECT *
from kylmaasennus;
