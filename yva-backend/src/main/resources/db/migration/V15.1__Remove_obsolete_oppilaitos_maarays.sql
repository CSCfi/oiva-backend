-- Remove obsolete oppilaitos maarays

-- Naantalin kaupunki
DELETE
FROM maarays
WHERE id IN (SELECT m.id
             FROM maarays m
                      LEFT JOIN lupa l on m.lupa_id = l.id
             WHERE l.diaarinumero = '182/532/2012'
               AND m.koodisto = 'oppilaitos'
               AND m.org_oid IS NULL);

-- Mäntyharjun kunta
DELETE
FROM maarays
WHERE id IN (SELECT m.id
             FROM maarays m
                      LEFT JOIN lupa l on m.lupa_id = l.id
             WHERE l.diaarinumero = '26/532/2012'
               AND m.koodisto = 'oppilaitos'
               AND m.org_oid = '1.2.246.562.10.674256579531');
