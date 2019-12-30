-- Add nuts1 FI2 maarays for all which has not defined toimi-alue in lupa.
WITH not_defined AS (
    SELECT l.id
    FROM lupa l
    WHERE l.id NOT IN
          (SELECT m.lupa_id FROM maarays m WHERE m.koodisto = 'kunta' OR m.koodisto = 'maakunta' OR koodisto = 'nuts1')
)
INSERT
INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja)
SELECT not_defined.id,
       3,
       'nuts1',
       'FI2',
       3,
       'oiva'
FROM not_defined;

-- Add nuts1 FI0 maarays for all which has alueellinen lupa.
WITH alueellinen AS (
    SELECT l.id
    FROM lupa l
    WHERE l.id IN
          (SELECT m.lupa_id FROM maarays m WHERE m.koodisto = 'kunta' OR m.koodisto = 'maakunta')
)
INSERT
INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja)
SELECT alueellinen.id,
       3,
       'nuts1',
       'FI0',
       3,
       'oiva'
FROM alueellinen;
