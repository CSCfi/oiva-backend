-- Update vsterityinenkoulutustehtava koodisto correct values
UPDATE maarays m
SET koodiarvo = 1
FROM lupa l
WHERE l.id = m.lupa_id
  AND m.koodisto = 'vsterityinenkoulutustehtava'
  AND diaarinumero IN
      ('72/532/2011',
       '22/532/2011');

UPDATE maarays m
SET koodiarvo = 2
FROM lupa l
WHERE l.id = m.lupa_id
  AND m.koodisto = 'vsterityinenkoulutustehtava'
  AND diaarinumero IN
      ('87/532/2011',
       '23/532/2016',
       '33/532/2016',
       '47/531/2019',
       '5/532/2015',
       '51/532/2011',
       '15/532/2011');

UPDATE maarays m
SET koodiarvo = 3
FROM lupa l
WHERE l.id = m.lupa_id
  AND m.koodisto = 'vsterityinenkoulutustehtava'
  AND diaarinumero IN
      ('20/532/2015',
       '249/532/2012',
       '18/532/2019',
       '24/532/2015',
       '1/532/2016');

UPDATE maarays m
SET koodiarvo = 4
FROM lupa l
WHERE l.id = m.lupa_id
  AND m.koodisto = 'vsterityinenkoulutustehtava'
  AND diaarinumero IN
      ('25/531/2011',
       '96/532/2011',
       '98/532/2011',
       '102/532/2011',
       '105/532/2011',
       '100/532/2011');

UPDATE maarays m
SET koodiarvo = 5
FROM lupa l
WHERE l.id = m.lupa_id
  AND m.koodisto = 'vsterityinenkoulutustehtava'
  AND diaarinumero = '42/532/2011';
