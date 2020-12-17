-- Change sopimuskunta Honkajoki -> Kankaanpää for Sasky koulutusyhtymä
UPDATE maarays
SET koodiarvo = '214'
FROM lupa l
WHERE l.diaarinumero = '3/532/2015'
  AND l.jarjestaja_ytunnus = '0204964-1'
  AND lupa_id = l.id
  AND koodisto = 'kunta'
  AND koodiarvo = '099'
