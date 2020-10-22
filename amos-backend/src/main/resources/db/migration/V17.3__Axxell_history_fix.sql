-- Add missing maarays (364145 Yrkesexamen inom hästhushållning) for old Axxell lupa.
WITH old_lupa AS (
    SELECT id
    FROM lupa
    WHERE jarjestaja_ytunnus = '2064886-7' AND diaarinumero = '5/531/2019' ORDER BY luontipvm DESC LIMIT 1
),
     new_maarays AS (
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja, koodistoversio)
             SELECT ol.id, 1, 'koulutus', '364145', 1, 'oiva', null FROM old_lupa ol RETURNING id
     )

SELECT *
from new_maarays;
