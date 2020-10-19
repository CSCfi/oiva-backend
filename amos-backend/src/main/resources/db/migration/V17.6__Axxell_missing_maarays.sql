-- Add missing osaamisala and tutkintokieli for current Axxell lupa.
WITH cur_lupa AS (
    SELECT id
    FROM lupa
    WHERE jarjestaja_ytunnus = '2064886-7'
      AND asianumero = 'VN/14042/2020'
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
         INSERT INTO maarays (lupa_id, parent_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja,
                              koodistoversio)
             SELECT cl.id,
                    tta.id,
                    1,
                    'osaamisala',
                    '2387',
                    2,
                    'oiva',
                    3
             FROM cur_lupa cl,
                  talotekniikan_at tta RETURNING id
     ),
     tutkintokielet AS (
         INSERT INTO maarays (lupa_id, parent_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja,
                              koodistoversio)
             SELECT cl.id, m.id, 2, 'kieli', 'FI', 3, 'oiva', 1
             FROM cur_lupa cl,
                  maarays m
             WHERE m.lupa_id = cl.id
               AND m.koodisto = 'koulutus'
               AND m.koodiarvo IN (
                                   '321141', '324146', '324602', '331101', '334112', '334145', '341102', '351107',
                                   '351108', '351203', '351301', '351407', '351603', '351605', '351703', '351741',
                                   '351805', '352101', '352201', '352301', '352441', '352903', '354245', '354445',
                                   '354845', '355209', '361101', '361104', '361201', '361301', '361401', '361902',
                                   '364245', '364902', '364946', '381141', '381402', '381408', '384145', '427141',
                                   '427601', '467942') RETURNING id
     )

SELECT *
from cur_lupa;
