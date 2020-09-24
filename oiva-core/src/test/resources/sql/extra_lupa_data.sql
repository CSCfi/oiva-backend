INSERT INTO lupa (id, edellinen_lupa_id, paatoskierros_id, lupatila_id, asiatyyppi_id, diaarinumero,
                       jarjestaja_ytunnus, jarjestaja_oid, alkupvm, loppupvm, paatospvm, meta, maksu, luoja, luontipvm,
                       paivittaja, paivityspvm, uuid, koulutustyyppi, oppilaitostyyppi)
VALUES (8, null, 1, 3, 1, '11/111/2020', '1111111-1', '1.1.111.111.11.11111111111', '2019-01-01', null, '2018-12-31',
        '{"ministeri": "Testi Ministeri", "esittelija": "Testi Esittelijä"}',
        false, 'oiva', '2017-04-26 13:49:05.834530', null, null, 'dc3c1d00-43b6-11e8-b2ef-005056aa0e61', '2', '1'),
       (9, null, 1, 3, 1, '22/222/2020', '1111111-1', '1.1.111.111.11.11111111111', '2019-01-01', null, '2018-12-31',
        '{"ministeri": "Testi Ministeri", "esittelija": "Testi Esittelijä"}',
        false, 'oiva', '2017-08-28 12:58:22.066839', null, null, 'dc3c1d00-43b6-11e8-b2ef-005056aa0e62', '2', '2'),
       -- Lupa coming affected in the future
       (10, null, 1, 3, 1, '23/223/2020', '1111111-1', '1.1.111.111.11.11111111111', '2030-01-01', null, '2020-01-01',
        '{"ministeri": "Testi Ministeri", "esittelija": "Testi Esittelijä"}',
        false, 'oiva', now(), null, null, 'dc3c1d00-43b6-11e8-b2ef-005056aa0e63', null, null);