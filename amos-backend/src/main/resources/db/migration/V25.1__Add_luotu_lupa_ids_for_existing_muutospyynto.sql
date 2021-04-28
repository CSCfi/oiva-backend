-- Ammatillinen
UPDATE muutospyynto m
SET luotu_lupa_id = (SELECT l.id FROM lupa l WHERE l.asianumero = m.asianumero)
WHERE m.tila = 'PAATETTY' AND koulutustyyppi is null;

-- Perusopetus
UPDATE muutospyynto m
SET luotu_lupa_id = (SELECT l.id FROM lupa l WHERE l.diaarinumero = m.diaarinumero OR l.asianumero = m.asianumero)
WHERE m.tila = 'PAATETTY' AND koulutustyyppi = '1';

-- Lukio
UPDATE muutospyynto m
SET luotu_lupa_id = (SELECT l.id FROM lupa l WHERE l.asianumero = m.asianumero)
WHERE m.tila = 'PAATETTY' AND koulutustyyppi = '2';