-- Remove Gaeli from languages for Vetelin kunta (0184278-7)
DELETE
FROM maarays
WHERE id IN (SELECT m.id
            FROM maarays m
                     LEFT JOIN lupa l on m.lupa_id = l.id
            WHERE l.diaarinumero = '33/532/2012'
              AND m.koodisto IN ('kielikoodistoopetushallinto', 'kieli')
              AND m.koodiarvo = 'gd');