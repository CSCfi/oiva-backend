-- Set koulutustyyppi
UPDATE lupa l
SET koulutustyyppi = m.koodiarvo
FROM maarays m
WHERE m.lupa_id = l.id
  AND m.koodisto = 'koulutusmuoto';

-- Oppilaitostyyppi for vst
UPDATE lupa l
SET oppilaitostyyppi = m.koodiarvo
FROM maarays m
WHERE l.koulutustyyppi = '3'
  AND m.lupa_id = l.id
  AND m.koodisto = 'vsttyyppi';
