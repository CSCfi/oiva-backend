INSERT INTO maarays
(lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja, koodistoversio)
SELECT l.id,
       3,
       'vstetaopetus',
       '1',
       1,
       'kuja',
       1
FROM lupa l
WHERE diaarinumero IN
      ('9/532/2015',
       '9/532/2018',
       '11/532/2018',
       '10/532/2015',
       '13/532/2015',
       '14/532/2015',
       '8/532/2015',
       '11/532/2015');