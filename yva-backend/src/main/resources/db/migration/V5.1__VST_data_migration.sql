UPDATE lupa
SET paatoskierros_id = 3
WHERE id IN (
    SELECT l.id
    FROM lupa l
             LEFT JOIN maarays m ON l.id = m.lupa_id
    WHERE m.koodisto = 'koulutusmuoto'
      AND m.koodiarvo = '3'
);

UPDATE maarays
SET koodisto  = 'vstoppilaitoksenalueellisuusjavaltakunnallisuus',
    koodiarvo = (SELECT CASE WHEN meta ->> 'urn:muumääräys-0' = 'alueellinen' THEN '2' ELSE '1' END AS alueelisuus)
WHERE lupa_id IN (
    SELECT l.id
    FROM lupa l
             LEFT JOIN maarays m ON l.id = m.lupa_id
    WHERE m.koodisto = 'koulutusmuoto'
      AND m.koodiarvo = '3'
)
  AND koodisto = 'kujamuutoikeudetmaarayksetjarajoitukset'
  AND koodiarvo = '10'
  AND (meta ->> 'urn:muumääräys-0' = 'alueellinen'
           OR meta ->> 'urn:muumääräys-0' = 'valtakunnallinen');