-- Fix osaamisala and tutkintokieli for current Peimari lupa.
WITH cur_lupa AS (
    SELECT id
    FROM lupa
    WHERE jarjestaja_ytunnus = '0823246-3'
      AND diaarinumero = '11/531/2019'
),
     jate_osaamisala_at AS (
         UPDATE maarays m SET parent_id = ymparisto_at.id
             FROM cur_lupa cl,
                 maarays ymparisto_at
             WHERE m.lupa_id = cl.id
                 AND m.koodisto = 'osaamisala'
                 AND m.koodiarvo = '2347'
                 AND ymparisto_at.lupa_id = cl.id
                 AND ymparisto_at.koodisto = 'koulutus'
                 AND ymparisto_at.koodiarvo = '355945' RETURNING m.id
     ),
     metsakone_osaamisala_pt AS (
         UPDATE maarays m SET parent_id = ymparisto_at.id, koodiarvo = '1773'
             FROM cur_lupa cl,
                 maarays ymparisto_at
             WHERE m.lupa_id = cl.id
                 AND m.koodisto = 'osaamisala'
                 AND m.koodiarvo = '1588'
                 AND ymparisto_at.lupa_id = cl.id
                 AND ymparisto_at.koodisto = 'koulutus'
                 AND ymparisto_at.koodiarvo = '361301' RETURNING m.id
     ),
     metsakone_osaamisala_at AS (
         UPDATE maarays m SET parent_id = ymparisto_at.id
             FROM cur_lupa cl,
                 maarays ymparisto_at
             WHERE m.lupa_id = cl.id
                 AND m.koodisto = 'osaamisala'
                 AND m.koodiarvo = '2423'
                 AND ymparisto_at.lupa_id = cl.id
                 AND ymparisto_at.koodisto = 'koulutus'
                 AND ymparisto_at.koodiarvo = '364345' RETURNING m.id
     ),
     tutkintokieli_kalatalouden_pt AS (
         UPDATE maarays kieli SET parent_id = tutkinto.id
             FROM cur_lupa cl,
                 maarays tutkinto
             WHERE kieli.koodisto = 'kieli'
                 AND kieli.parent_id = 15155
                 AND kieli.lupa_id = cl.id
                 AND tutkinto.lupa_id = cl.id
                 AND tutkinto.koodisto = 'koulutus'
                 AND tutkinto.koodiarvo = '361401' RETURNING kieli.id
     ),
     tutkintokieli_luonto_pt AS (
         UPDATE maarays kieli SET parent_id = tutkinto.id
             FROM cur_lupa cl,
                 maarays tutkinto
             WHERE kieli.koodisto = 'kieli'
                 AND kieli.parent_id = 15156
                 AND kieli.lupa_id = cl.id
                 AND tutkinto.lupa_id = cl.id
                 AND tutkinto.koodisto = 'koulutus'
                 AND tutkinto.koodiarvo = '361902' RETURNING kieli.id
     ),
     tutkintokieli_kalatalouden_at AS (
         UPDATE maarays kieli SET parent_id = tutkinto.id
             FROM cur_lupa cl,
                 maarays tutkinto
             WHERE kieli.koodisto = 'kieli'
                 AND kieli.parent_id = 15145
                 AND kieli.lupa_id = cl.id
                 AND tutkinto.lupa_id = cl.id
                 AND tutkinto.koodisto = 'koulutus'
                 AND tutkinto.koodiarvo = '364445' RETURNING kieli.id
     )

SELECT *
from cur_lupa;