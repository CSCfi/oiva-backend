-- Remove ended koulutus maarays from Turun kaupunki (0204819-8)
DELETE
FROM maarays
WHERE koodisto = 'koulutus'
  AND koodiarvo = '355300'
  AND lupa_id IN (SELECT id FROM lupa WHERE diaarinumero = '22/531/2018');