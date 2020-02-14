--### Change correct oppilaitos oids ###--

--  Vihreä Sivistys- ja Opintokeskus – Gröna Bildnings- och Studiecentret
UPDATE maarays m
SET org_oid = '1.2.246.562.10.91873037302'
FROM lupa l
WHERE l.diaarinumero = '234/532/2012'
  AND l.id = m.lupa_id
  AND m.koodisto = 'oppilaitos';

-- Riveria, kansanopisto
UPDATE maarays m
SET org_oid = '1.2.246.562.10.70718546214'
FROM lupa l
WHERE l.diaarinumero = '12/532/2011'
  AND l.id = m.lupa_id
  AND m.koodisto = 'oppilaitos';

-- Riveria, kesäyliopisto
UPDATE maarays m
SET org_oid = '1.2.246.562.10.53946183299'
FROM lupa l
WHERE l.diaarinumero = '213/532/2012'
  AND l.id = m.lupa_id
  AND m.koodisto = 'oppilaitos';

-- Pekka Halosen akatemia
UPDATE maarays m
SET org_oid = '1.2.246.562.10.197113642410'
FROM lupa l
WHERE l.diaarinumero = '61/532/2011'
  AND l.id = m.lupa_id
  AND m.koodisto = 'oppilaitos';

-- Espoon työväenopisto
UPDATE maarays m
SET org_oid = '1.2.246.562.10.89711434574'
FROM lupa l
WHERE l.diaarinumero = '1/532/2014'
  AND l.id = m.lupa_id
  AND m.koodisto = 'oppilaitos';

-- Suomen diakonissaopisto kansanopisto
UPDATE maarays m
SET org_oid = '1.2.246.562.10.93886778971'
FROM lupa l
WHERE l.diaarinumero = '38/532/2016'
  AND l.id = m.lupa_id
  AND m.koodisto = 'oppilaitos';

-- Haapaveden opisto
UPDATE maarays m
SET org_oid = '1.2.246.562.10.84639302383'
FROM lupa l
WHERE l.diaarinumero = '66/532/2011'
  AND l.id = m.lupa_id
  AND m.koodisto = 'oppilaitos';

-- Oriveden opisto
UPDATE maarays m
SET org_oid = '1.2.246.562.10.41201203548'
FROM lupa l
WHERE l.diaarinumero = '9/532/2015'
  AND l.id = m.lupa_id
  AND m.koodisto = 'oppilaitos';
