-- Fix history of Kouvolan Aikuiskoulutussäätiö (0161067-9)
WITH cur_lupa AS (
    UPDATE lupa SET loppupvm = '2020-07-31'
        WHERE jarjestaja_ytunnus = '0161067-9' AND asianumero = 'VN/13114/2020'
        RETURNING id
),
     history AS (
         UPDATE lupahistoria SET voimassaololoppupvm = '2020-07-31', kumottupvm = '2020-06-23'
             FROM cur_lupa l
             WHERE lupa_id = l.id
     ),
     sosiaali_terveys_pt AS ( -- Remove Sosiaali- ja terveysalan perustutkinto (371101)
         DELETE FROM maarays
             WHERE lupa_id = (SELECT l.id FROM cur_lupa l)
                 AND koodisto = 'koulutus' AND koodiarvo = '371101'
     )

SELECT *
FROM cur_lupa;

WITH cur_lupa AS (
    UPDATE lupa SET loppupvm = '2020-12-31'
        WHERE jarjestaja_ytunnus = '0161067-9' AND asianumero = 'VN/2258/2020'
        RETURNING id
),
     history AS (
         UPDATE lupahistoria SET voimassaololoppupvm = '2020-12-31', kumottupvm = null
             FROM cur_lupa l
             WHERE lupa_id = l.id
     ),
     maarakennuskoneen_osaamisala AS (
         INSERT INTO maarays (lupa_id, parent_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja,
                              koodistoversio)
             SELECT l.id,
                    m.id,
                    1,
                    'osaamisala',
                    '1758',
                    2,
                    'oiva',
                    3
             FROM cur_lupa l,
                  maarays m
             WHERE m.lupa_id = l.id
               AND m.koodisto = 'koulutus'
               AND m.koodiarvo = '352201'
             RETURNING id
     ),
     paatoskirje AS (
         INSERT INTO liite (nimi, polku, luoja, kieli, koko, tyyppi)
             VALUES ('VN_2258_2020 päätöskirje',
                     '2020/6/24/kouvola/VN_2258_2020-OKM-1 Ammatillisten tutkintojen ja koulutuksen järjestämisluvan muutos 1.8 920857_3_3.PDF',
                     'oiva', 'fi', 247952, 'paatosKirje') RETURNING id
     ),
     link AS (
         INSERT INTO lupa_liite (lupa_id, liite_id)
             SELECT l.id, p.id
             FROM cur_lupa l,
                  paatoskirje p
     )

SELECT *
FROM cur_lupa;
