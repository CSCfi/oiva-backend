-- Another lupa for existing organization
-- Invalid/obsolete lupa

INSERT INTO lupa (id, edellinen_lupa_id, paatoskierros_id, lupatila_id, asiatyyppi_id, diaarinumero,
                       jarjestaja_ytunnus, jarjestaja_oid, alkupvm, loppupvm, paatospvm, meta, maksu, luoja, luontipvm,
                       paivittaja, paivityspvm, uuid, koulutustyyppi, oppilaitostyyppi)
VALUES (16, null, 1, 3, 5, '66/666/2019', '5555555-5', '5.5.555.555.55.55555555555', '2019-01-01', null, '2018-12-31',
        '{"ministeri": "Testi Ministeri", "esittelija": "Testi Esittelijä"}',
        false, 'oiva', '2017-09-21 09:48:22.066839', null, null, 'cc3c1d00-43b6-11e8-b2ef-005056aa0e76', null, null),
       (17, null, 1, 3, 5, '77/777/2019', '6666666-6', '6.6.666.666.66.66666666666', '2017-01-01', '2017-10-01', '2018-12-31',
        '{"ministeri": "Testi Ministeri", "esittelija": "Testi Esittelijä"}',
        false, 'oiva', '2017-09-21 09:48:22.066839', null, null, 'cc3c1d00-43b6-11e8-b2ef-005056aa0e77', null, null);