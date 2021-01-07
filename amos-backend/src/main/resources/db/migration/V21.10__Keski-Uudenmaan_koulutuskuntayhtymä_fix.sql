-- Fix Keski-Uudenmaan koulutuskuntayhtymä (0213834-5) lupa VN/2171/2020
WITH cur_lupa AS (
    UPDATE
        lupa SET paatospvm = '2020-06-12'
            WHERE jarjestaja_ytunnus = '0213834-5'
                AND asianumero = 'VN/2171/2020' RETURNING id
),
     ajoneuvoalan_eat AS (
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja,
                              koodistoversio)
             SELECT cl.id,
                    1,
                    'koulutus',
                    '457341',
                    1,
                    'oiva',
                    11
             FROM cur_lupa cl RETURNING id
     )

SELECT *
FROM cur_lupa;
