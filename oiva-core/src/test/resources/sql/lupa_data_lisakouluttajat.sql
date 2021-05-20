INSERT INTO lupa (id, edellinen_lupa_id, paatoskierros_id, lupatila_id, asiatyyppi_id, diaarinumero,
                       jarjestaja_ytunnus, jarjestaja_oid, alkupvm, loppupvm, paatospvm, meta, maksu, luoja, luontipvm,
                       paivittaja, paivityspvm, uuid, koulutustyyppi, oppilaitostyyppi)

-- Lisäkouluttajiin kuuluvien koulutuksenjärjestäjien lupia. Kaksi lupaa jokaista järjestäjää kohti (eri koulutustyypeillä).
VALUES (8, null, 1, 3, 1, '11/111/2019', '0763403-0', '1.1.111.111.11.11111111111', '2019-01-01', null, '2018-12-31',
        '{"ministeri": "Testi Ministeri", "esittelija": "Testi Esittelijä"}',
        false, 'oiva', '2017-04-26 13:49:05.834530', null, null, 'cc3c1d00-43b6-11e8-b2ef-005056aa0e68', '1', '1'),
       (9, null, 1, 3, 1, '11/111/2019', '0763403-0', '1.1.111.111.11.11111111111', '2019-01-01', null, '2018-12-31',
        '{"ministeri": "Testi Ministeri", "esittelija": "Testi Esittelijä"}',
        false, 'oiva', '2017-04-26 13:49:05.834530', null, null, 'cc3c1d00-43b6-11e8-b2ef-005056aa0e69', null, '1'),


       (10, null, 1, 3, 1, '22/222/2019', '0986820-1', '2.2.222.222.22.22222222222', '2019-01-01', null, '2018-12-31',
        '{"ministeri": "Testi Ministeri", "esittelija": "Testi Esittelijä"}',
        false, 'oiva', '2017-08-28 12:58:22.066839', null, null, 'cc3c1d00-43b6-11e8-b2ef-005056aa0e70', '2', '2'),
       (11, null, 1, 3, 1, '33/333/2019', '0986820-1', '3.3.333.333.33.33333333333', '2019-01-01', null, '2018-12-31',
        '{"ministeri": "Testi Ministeri", "esittelija": "Testi Esittelijä"}',
        false, 'oiva', '2017-09-20 09:25:22.066839', null, null, 'cc3c1d00-43b6-11e8-b2ef-005056aa0e71', null, '1'),

       (12, null, 1, 3, 1, '44/444/2019', '0188756-3', '4.4.444.444.44.44444444444', '2019-01-01', null, '2018-12-31',
        '{"ministeri": "Testi Ministeri", "esittelija": "Testi Esittelijä"}',
        false, 'oiva', '2017-09-21 09:48:22.066839', null, null, 'cc3c1d00-43b6-11e8-b2ef-005056aa0e72', '3', '2'),
       (13, null, 1, 3, 5, '55/555/2019', '0188756-3', '5.5.555.555.55.55555555555', '2019-01-01', null, '2018-12-31',
        '{"ministeri": "Testi Ministeri", "esittelija": "Testi Esittelijä"}',
        false, 'oiva', '2017-09-21 09:48:22.066839', null, null, 'cc3c1d00-43b6-11e8-b2ef-005056aa0e73', null, '1'),

       (14, null, 1, 3, 1, '66/666/2019', '0206976-5', '6.6.666.666.66.66666666666', '2019-01-01', null, '2018-12-31',
        '{"ministeri": "Testi Ministeri", "esittelija": "Testi Esittelijä"}',
        false, 'oiva', '2017-09-21 09:48:22.066839', null, null, 'cc3c1d00-43b6-11e8-b2ef-005056aa0e74', '1', null),
       (15, null, 1, 3, 1, '77/777/2019', '0206976-5', '7.7.777.777.77.77777777777', '2019-01-01', null, '2018-12-31',
        '{"ministeri": "Testi Ministeri", "esittelija": "Testi Esittelijä"}',
        false, 'oiva', '2017-09-21 09:48:22.066839', null, null, 'cc3c1d00-43b6-11e8-b2ef-005056aa0e75', '2', null);
