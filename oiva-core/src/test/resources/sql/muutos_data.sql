INSERT INTO muutos (id, muutospyynto_id, kohde_id, parent_id, koodisto, koodiarvo, arvo,
                    maaraystyyppi_id, meta,
                    maarays_id, tila, uuid, paatos_tila, muutosperustelukoodiarvo, org_oid)
VALUES (1, 1, 1, null, 'kieli', 'sv', null, 2, '{
  "liitteet": [
    {
      "nimi": "meta_liite1",
      "polku": "testipolku/test.pdf",
      "tila": false,
      "koko": 17307,
      "meta": {},
      "tyyppi": "jokuliite2",
      "kieli": "fi",
      "uuid": "2b02c730-ebef-11e9-8d25-0242ac110002",
      "salainen": false,
      "tiedostoId": "file3",
      "removed": false
    },
    {
      "nimi": "meta_liite2",
      "polku": "testipolku/test2.pdf",
      "tila": false,
      "koko": 17307,
      "meta": {},
      "tyyppi": "jokuliite2",
      "kieli": "fi",
      "uuid": "2b02c730-ebef-11e9-8d25-0242ac110002",
      "salainen": false,
      "tiedostoId": "file3",
      "removed": false
    }
  ]
}', null, 'LUONNOS', '2b02c730-ebef-11e9-8d25-0242ac110000', null, null, null),
       (2, 1, 1, null, 'kieli', 'fi', null, 2, '{}', null, 'LUONNOS', '2b02c730-ebef-11e9-8d25-0242ac110001', null, null, null),
       -- Alimaarays of 2
       (3, 1, 1, 2, 'kieli', 'fi', null, 2, '{}', null, 'LUONNOS', '2b02c730-ebef-11e9-8d25-0242ac110002', null, null, null),
       -- Alimaarays of 3 aka. alialimaarays
       (4, 1, 1, 3, 'kieli', 'fi', null, 2, '{}', null, 'LUONNOS', '2b02c730-ebef-11e9-8d25-0242ac110003', null, null, null);