-- Add missing kieli maarays 'sv' to Hangon kaupunki
INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, luoja, koodistoversio)
SELECT m.lupa_id,
       m.kohde_id,
       m.koodisto,
       'sv',
       m.arvo,
       m.maaraystyyppi_id,
       m.meta,
       m.luoja,
       m.koodistoversio
FROM maarays m
         LEFT JOIN lupa l on m.lupa_id = l.id
WHERE l.diaarinumero = '17/532/2017'
  AND m.koodisto = 'kieli'
  AND m.koodiarvo = 'fi';