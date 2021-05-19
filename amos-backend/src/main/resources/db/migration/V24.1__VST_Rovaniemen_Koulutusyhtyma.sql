-- Rovaniemen Koulutusyhtymä (0973110-9)
WITH cur_lupa AS (
    SELECT * FROM lupa WHERE koulutustyyppi = '3' AND diaarinumero = '98/532/2011' AND jarjestaja_ytunnus = '0973110-9'
),
     -- Update erityinen koulutustehtävä meta text
     erityinen_koulutus AS (
         UPDATE maarays m SET meta = jsonb_set(meta, '{erityinenkoulutustehtävämääräys-2}',
                                               '"Oppilaitoksella on tiettyihin lumilajeihin, etenkin maastohiihtoon, mäkihyppyyn ja yhdistettyyn sekä alppihiihtoon liittyvä huippu-urheilun erityinen koulutustehtävä, joka tarkoittaa ko. lajien huippu-urheilussa vaadittavan osaamisen ja asiantuntijapalveluiden kehittämistä ja jakamista liikunnan koulutuskeskusverkostossa sekä koko urheilun järjestelmissä. Lisäksi tehtävä tarkoittaa kehittämistoiminnan ja testauksen toteuttamista yhteistyössä muiden urheilutoimijoiden kanssa."')
             WHERE lupa_id = (SELECT id FROM cur_lupa) AND koodisto = 'vsterityinenkoulutustehtava' AND koodiarvo = '4' RETURNING id
     )

SELECT *
FROM erityinen_koulutus;
