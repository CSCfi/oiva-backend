
INSERT INTO esitysmalli (id, templatepath, valituspdf, luoja, luontipvm, paivittaja, paivityspvm, uuid)
VALUES (4, 'lupahistoria/lukiot', null, 'kuja', DEFAULT, null, null, DEFAULT);

INSERT INTO paatoskierros (id, alkupvm, loppupvm, oletus_paatospvm, esitysmalli_id, luoja, luontipvm, paivittaja,
                           paivityspvm, meta, uuid)
VALUES (4, '1917-12-06', '2019-12-31', null, 4, 'kuja', DEFAULT, null, null, '{
  "fi": "Lukiot"
}', DEFAULT);

UPDATE lupa l1
SET paatoskierros_id = 4
FROM lupa l
         LEFT JOIN maarays m ON l.id = m.lupa_id
WHERE m.koodisto = 'koulutusmuoto'
  AND m.koodiarvo = '2' AND l1.id = l.id;