-- Remove obsolete kokeilut
DELETE
FROM maarays
WHERE id IN (SELECT m.id
             FROM lupa l
                      LEFT JOIN maarays m ON l.id = m.lupa_id
             WHERE (l.loppupvm IS NULL OR l.loppupvm > now())
               AND m.koodisto = 'oivamuutoikeudetvelvollisuudetehdotjatehtavat'
               AND m.koodiarvo = '7');
