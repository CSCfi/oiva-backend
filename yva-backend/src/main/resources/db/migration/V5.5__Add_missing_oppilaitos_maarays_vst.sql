-- Add missing oppilaitos maarays for some of the VST lupa

-- Mikkelin kesäyliopisto
INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja, org_oid)
SELECT l.id, 5, 'oppilaitos', '1', 1, 'kuja', '1.2.246.562.10.60058326608'
FROM lupa l
WHERE l.diaarinumero = '175/532/2012';

-- Opintokeskus Kansalaisfoorumi
INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja, org_oid)
SELECT l.id, 5, 'oppilaitos', '1', 1, 'kuja', '1.2.246.562.10.88633964114'
FROM lupa l
WHERE l.diaarinumero = '243/532/2012';

-- Lappfjärds folkhögskola
INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja, org_oid)
SELECT l.id, 5, 'oppilaitos', '1', 1, 'kuja', '1.2.246.562.10.88630525025'
FROM lupa l
WHERE l.diaarinumero = '10/532/2018';

-- Jämsän kristinen kansanopisto
INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja, org_oid)
SELECT l.id, 5, 'oppilaitos', '1', 1, 'kuja', '1.2.246.562.10.15410868853'
FROM lupa l
WHERE l.diaarinumero = '9/532/2017';

-- Naantalin opisto
INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja, org_oid)
SELECT l.id, 5, 'oppilaitos', '1', 1, 'kuja', '1.2.246.562.10.46413608491'
FROM lupa l
WHERE l.diaarinumero = '182/532/2012';

-- Mäntyharjun kansalaisopisto
INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja, org_oid)
SELECT l.id, 5, 'oppilaitos', '1', 1, 'kuja', '1.2.246.562.10.67425657953'
FROM lupa l
WHERE l.diaarinumero = '26/532/2012';

-- Axxel fölkhogskolan
INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja, org_oid)
SELECT l.id, 5, 'oppilaitos', '1', 1, 'kuja', '1.2.246.562.10.522875047910'
FROM lupa l
WHERE l.diaarinumero = '9/532/2018';

-- Kronoby folkhögskola
-- Remove extra kieli maarays 'svenska'
DELETE
FROM maarays m USING lupa l
WHERE l.diaarinumero = '27/532/2011'
  AND m.koodisto = 'kieli'
  AND m.koodiarvo = 'svenska';
-- Remove wrong oppilaitos org_id from Nordiska Konstskolan som filial
UPDATE maarays m
SET org_oid = null
FROM lupa l
WHERE l.id = m.lupa_id
  AND l.diaarinumero = '27/532/2011'
  AND m.meta ->> 'oppilaitosmääräys-0' = 'Nordiska Konstskolan som filial'

