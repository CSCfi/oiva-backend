-- Fix typos in meta texts for Västra Nylands folkhögskola
UPDATE maarays m SET meta = REPLACE(TEXT(m.meta), 'ett internationell och', 'ett internationellt och')::jsonb
FROM lupa l WHERE l.id = m.lupa_id AND l.diaarinumero = '5/532/2018';

-- Fix typos in meta texts for Kristinestads medborgarinstitut
UPDATE maarays m SET meta = REPLACE(TEXT(m.meta), 'tväspräkighet', 'tvåspråkighet')::jsonb
FROM lupa l WHERE l.id = m.lupa_id AND l.diaarinumero = '119/532/2012';

-- Fix typos in meta texts for Vörå medborgarinstitut
UPDATE maarays m SET meta = REPLACE(TEXT(m.meta), 'Vörä-Oravais-Maxmo', 'Vörå-Oravais-Maxmo')::jsonb
FROM lupa l WHERE l.id = m.lupa_id AND l.diaarinumero = '121/532/2012';
