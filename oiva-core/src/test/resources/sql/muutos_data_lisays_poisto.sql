INSERT INTO muutos (id, muutospyynto_id, kohde_id, parent_id, koodisto, koodiarvo, arvo,
                    maaraystyyppi_id, meta,
                    maarays_id, tila, uuid, paatos_tila, muutosperustelukoodiarvo, org_oid)
VALUES (1, 1, 1, null, 'kieli', 'sv', null, 2, '{}', null, 'LISAYS', '2b02c730-ebef-11e9-8d25-0242ac110000', null, null, null),
       (2, 1, 1, null, 'kieli', 'fi__', null, 2, '{}', null, 'LISAYS', '2b02c730-ebef-11e9-8d25-0242ac110001', null, null, null),
       -- Alimaarays of 2
       (3, 1, 1, 2, 'kieli', 'fi_lisatty', null, 2, '{}', null, 'LISAYS', '2b02c730-ebef-11e9-8d25-0242ac110002', null, null, null),
       -- Alimaarays for maarays (id: 3)
       (4, 1, 1, 3, 'kieli', 'fi_alimaarays', null, 2, '{}', 3, 'LISAYS', '2b02c730-ebef-11e9-8d25-0242ac110003', null, null, null),
        -- poistot
       (5, 1, 1, null, 'koulutus', '123', null, 2, '{}', 1, 'POISTO', '2b02c730-ebef-11e9-8d25-0242ac110004', null, null, null),
       (6, 1, 1, 5, 'koulutus', '1231', null, 2, '{}', 2, 'POISTO', '2b02c730-ebef-11e9-8d25-0242ac110005', null, null, null)
    ;