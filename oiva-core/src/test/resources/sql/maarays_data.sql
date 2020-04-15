INSERT INTO maarays (id, parent_id, lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, luoja,
                     luontipvm, paivittaja, paivityspvm, koodistoversio, uuid, org_oid)
VALUES (1, null, 1, 1, 'koulutus', '123', null, 1, null, null, '2020-01-01 00:00:00', null, null, 1,
        'cc3c1d00-43b6-11e8-b2ef-005056aa0e61', null),
       (2, 1, 1, 1, 'koulutus', '1231', null, 2, null, null, '2020-01-01 00:00:00', null, null, 1,
        'cc3c1d00-43b6-11e8-b2ef-005056aa0e62', null),
       (3, null, 1, 1, 'koulutus', '124', null, 1, null, null, '2020-01-01 00:00:00', null, null, 1,
        'cc3c1d00-43b6-11e8-b2ef-005056aa0e63', null);