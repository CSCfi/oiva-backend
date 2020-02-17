--## Fix typos in meta texts ##--

-- Pietarsaaren kaupunki
UPDATE maarays m SET meta = REPLACE(TEXT(m.meta), 'utgäende frän', 'utgående från')::jsonb
FROM lupa l WHERE l.id = m.lupa_id AND l.diaarinumero = '86/532/2012' AND TEXT(m.meta) LIKE '%utgäende frän%';

-- Lahden kaupunki
UPDATE maarays m SET meta = REPLACE(TEXT(m.meta), 'VVellamo', 'Wellamo')::jsonb
FROM lupa l WHERE l.id = m.lupa_id AND l.diaarinumero = '17/532/2012' AND TEXT(m.meta) LIKE '%VVellamo%';

-- Maalahden kunta
UPDATE maarays m SET meta = REPLACE(TEXT(m.meta), 'ur ett välmäende', 'ur ett välmående')::jsonb
FROM lupa l WHERE l.id = m.lupa_id AND l.diaarinumero = '106/532/2012' AND TEXT(m.meta) LIKE '%ur ett välmäende%';

-- Paimion kaupunki
UPDATE maarays m SET meta = REPLACE(TEXT(m.meta), 'kuluttuuriin', 'kulttuuriin')::jsonb
FROM lupa l WHERE l.id = m.lupa_id AND l.diaarinumero = '91/532/2012' AND TEXT(m.meta) LIKE '%kuluttuuriin%';

-- Pöytyän kunta
UPDATE maarays m SET meta = REPLACE(TEXT(m.meta), 'huomioideen', 'huomioidaan')::jsonb
FROM lupa l WHERE l.id = m.lupa_id AND l.diaarinumero = '93/532/2012' AND TEXT(m.meta) LIKE '%huomioideen%';

-- Raahen kaupunki
UPDATE maarays m SET meta = REPLACE(TEXT(m.meta), 'tuke ayksilön', 'tukea yksilön')::jsonb
FROM lupa l WHERE l.id = m.lupa_id AND l.diaarinumero = '152/532/2012' AND TEXT(m.meta) LIKE '%tuke ayksilön%';

-- Raision kaupunki
UPDATE maarays m SET meta = REPLACE(TEXT(m.meta), 'tevreyttä', 'terveyttä')::jsonb
FROM lupa l WHERE l.id = m.lupa_id AND l.diaarinumero = '81/532/2012' AND TEXT(m.meta) LIKE '%tevreyttä%';

-- Suonenjoen kaupunki
UPDATE maarays m SET meta = REPLACE(TEXT(m.meta), 'kotiin.ja', 'kotiin ja')::jsonb
FROM lupa l WHERE l.id = m.lupa_id AND l.diaarinumero = '18/532/2017' AND TEXT(m.meta) LIKE '%kotiin.ja%';
