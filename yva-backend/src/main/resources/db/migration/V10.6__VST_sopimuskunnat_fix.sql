-- Kemin kaupunki
UPDATE maarays m
SET meta = '{
  "oppilaitosmaarays": "Sopimuskunta"
}'
FROM lupa l
WHERE l.diaarinumero = '15/532/2013'
  AND m.lupa_id = l.id
  AND m.koodisto = 'kunta'
  AND m.koodiarvo IN ('241', '751', '845');

-- SASKY koulutusyhtymä
DELETE
FROM maarays
WHERE lupa_id = (SELECT id FROM lupa WHERE diaarinumero = '3/532/2015')
    AND koodisto = 'kunta'
    AND koodiarvo IN ('250', '214')
   OR (meta = '{}' AND koodiarvo IN ('230', '099', '608'));

-- Laitilan kaupunki
DELETE
FROM maarays
WHERE lupa_id = (SELECT id FROM lupa WHERE diaarinumero = '183/532/2012')
  AND koodisto = 'kunta'
  AND koodiarvo != '400'
  AND meta != '{}';

UPDATE maarays m
SET meta = '{
  "oppilaitosmaarays": "Sopimuskunta"
}'
FROM lupa l
WHERE l.diaarinumero = '183/532/2012'
  AND m.lupa_id = l.id
  AND m.koodisto = 'kunta'
  AND m.koodiarvo IN ('304', '631', '833', '895', '918');

-- Mikkelin kaupunki
DELETE
FROM maarays
WHERE lupa_id = (SELECT id FROM lupa WHERE diaarinumero = '164/532/2012')
  AND koodisto = 'kunta'
  AND koodiarvo = '97';

UPDATE maarays m
SET meta = '{
  "oppilaitosmaarays": "Sopimuskunta"
}'
FROM lupa l
WHERE l.diaarinumero = '164/532/2012'
  AND m.lupa_id = l.id
  AND m.koodisto = 'kunta'
  AND m.koodiarvo = '097';

-- Linnalan setlementti ry
DELETE
FROM maarays
WHERE lupa_id = (SELECT id FROM lupa WHERE diaarinumero = '17/532/2015')
  AND koodisto = 'kunta'
  AND koodiarvo = '46';

UPDATE maarays m
SET meta = '{
  "oppilaitosmaarays": "Sopimuskunta"
}'
FROM lupa l
WHERE l.diaarinumero = '17/532/2015'
  AND m.lupa_id = l.id
  AND m.koodisto = 'kunta'
  AND m.koodiarvo = '046';

-- Porvoon kaupunki
DELETE
FROM maarays
WHERE lupa_id = (SELECT id FROM lupa WHERE diaarinumero = '4/532/2013')
  AND koodisto = 'kunta'
  AND koodiarvo = '18';

UPDATE maarays m
SET meta = '{
  "oppilaitosmaarays": "Sopimuskunta"
}'
FROM lupa l
WHERE l.diaarinumero = '4/532/2013'
  AND m.lupa_id = l.id
  AND m.koodisto = 'kunta'
  AND m.koodiarvo = '018';

-- Sotkamon kunta
DELETE
FROM maarays m USING maarays m2
WHERE m.lupa_id = (SELECT id FROM lupa WHERE diaarinumero = '31/532/2016')
  AND m.lupa_id = m2.lupa_id
  AND m.id < m2.id
  AND m.koodisto = m2.koodisto
  AND m.koodiarvo = m2.koodiarvo
  AND m.koodisto = 'kunta'
  AND m.koodiarvo = '290';

-- Säkylän kunta
DELETE
FROM maarays
WHERE lupa_id = (SELECT id FROM lupa WHERE diaarinumero = '185/532/2012')
  AND koodisto = 'kunta'
  AND koodiarvo = '783'
  AND meta != '{}';
