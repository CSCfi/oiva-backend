-- Change alkupvm for Kirkkopalvelut ry and Kalajoen Kristillisen Opiston kannatusyhdistys ry
UPDATE lupa
SET alkupvm = '2017-01-01'
WHERE diaarinumero = '33/532/2016'
   OR diaarinumero = '23/532/2016';

-- Add missing erityinenkoulutustehtava for Ranuan kristillinen kansanopistoyhdistys
INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, meta)
SELECT l.id, 4, 'erityinenkoulutustehtava', '1', 1, '{
  "erityinenkoulutustehtävämääräys-0": "Osana erityistä koulutustehtävää opisto järjestää koulutusta vaikeasti vammaisille. Vaikeasti vammaisten koulutus edellyttää sisäoppilaitoksessa yövalvojan paikallaoloa.",
  "erityinenkoulutustehtävämääräys-1": "",
  "erityinenkoulutustehtävämääräys-2": ""
}'
FROM lupa l
WHERE l.diaarinumero = '19/532/2011';

-- Change erityinenkoulutustehtava text for Toimihenkilöjärjestöjen Opintoliitto
UPDATE maarays m
SET meta = '{
  "erityinenkoulutustehtävämääräys-0": "Osana Aktiivi-instituutin koulutustehtävää on työelämän aktiiviseen kansalaisuuteen ja työelämän kehittämiseen liittyvä koulutus.",
  "erityinenkoulutustehtävämääräys-1": "",
  "erityinenkoulutustehtävämääräys-2": ""
}'
FROM lupa l
WHERE l.id = m.lupa_id
  AND l.diaarinumero = '1/532/2016'
  AND m.koodisto = 'erityinenkoulutustehtava';
