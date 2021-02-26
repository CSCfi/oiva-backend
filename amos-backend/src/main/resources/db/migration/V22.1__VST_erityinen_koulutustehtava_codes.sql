-- Fix vsterityinenkoulutustehtava code values for VST
UPDATE maarays
SET koodiarvo = '2'
WHERE id IN (SELECT m.id
             FROM lupa l
                      LEFT JOIN maarays m ON l.id = m.lupa_id
             WHERE l.diaarinumero IN ('87/532/2011',
                                      '23/532/2016',
                                      '33/532/2016',
                                      'VN/14847/2020',
                                      '47/531/2019',
                                      '5/532/2015',
                                      '51/532/2011',
                                      '15/532/2011',
                                      '9/532/2017',
                                      '19/532/2011',
                                      '73/532/2011')
               AND l.koulutustyyppi = '3'
               AND m.koodisto = 'vsterityinenkoulutustehtava');

UPDATE maarays
SET koodiarvo = '3'
WHERE id IN (SELECT m.id
             FROM lupa l
                      LEFT JOIN maarays m ON l.id = m.lupa_id
             WHERE l.diaarinumero IN ('20/532/2015',
                                      '249/532/2012',
                                      '18/532/2019',
                                      '24/532/2015',
                                      '1/532/2016')
               AND l.koulutustyyppi = '3'
               AND m.koodisto = 'vsterityinenkoulutustehtava');

UPDATE maarays
SET koodiarvo = '4'
WHERE id IN (SELECT m.id
             FROM lupa l
                      LEFT JOIN maarays m ON l.id = m.lupa_id
             WHERE l.diaarinumero IN ('25/531/2011',
                                      '96/532/2011',
                                      '98/532/2011',
                                      '102/532/2011',
                                      '105/532/2011',
                                      '100/532/2011')
               AND l.koulutustyyppi = '3'
               AND m.koodisto = 'vsterityinenkoulutustehtava');

UPDATE maarays
SET koodiarvo = '5'
WHERE id IN (SELECT m.id
             FROM lupa l
                      LEFT JOIN maarays m ON l.id = m.lupa_id
             WHERE l.diaarinumero = '42/532/2011'
               AND l.koulutustyyppi = '3'
               AND m.koodisto = 'vsterityinenkoulutustehtava');

UPDATE maarays
SET koodiarvo = '7'
WHERE id IN (SELECT m.id
             FROM lupa l
                      LEFT JOIN maarays m ON l.id = m.lupa_id
             WHERE l.diaarinumero = '44/440/2002'
               AND l.koulutustyyppi = '3'
               AND m.koodisto = 'vsterityinenkoulutustehtava');

WITH helsinki_aikuisopisto AS (
    SELECT *
    FROM lupa
    WHERE diaarinumero = '16/532/2012'
      AND koulutustyyppi = '3'
),
     remove_erityinen AS (
         DELETE FROM maarays m USING helsinki_aikuisopisto l
             WHERE m.lupa_id = l.id AND m.koodisto = 'vsterityinenkoulutustehtava' RETURNING m.id
     )

SELECT *
FROM remove_erityinen;
