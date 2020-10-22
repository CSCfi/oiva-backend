-- Add missing maarays (364145 Yrkesexamen inom hästhushållning) for current Axxell lupa.
WITH cur_lupa AS (
    SELECT id
    FROM lupa
    WHERE jarjestaja_ytunnus = '2064886-7' AND asianumero = 'VN/14042/2020'
),
     new_maarays AS (
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja, koodistoversio)
             SELECT ol.id, 1, 'koulutus', '364145', 1, 'oiva', 12 FROM cur_lupa ol RETURNING id
     )

SELECT *
from new_maarays;
