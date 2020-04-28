-- Kirkkopalvelu ry PO lupa
WITH existing_lupa AS (
    SELECT id
    FROM lupa
    WHERE diaarinumero = '74/530/2016'
),
     kunta_opetustehtava AS (
         INSERT INTO maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta,
                              koodistoversio, org_oid)
             SELECT DISTINCT kunta.id,
                             l.id,
                             kunta.kohde_id,
                             m.koodisto,
                             m.koodiarvo,
                             m.arvo,
                             2, -- RAJOITE
                             m.meta,
                             m.koodistoversio,
                             m.org_oid
             FROM maarays m,
                  maarays kunta,
                  existing_lupa l
             WHERE m.lupa_id = l.id
               AND kunta.lupa_id = l.id
               AND m.koodisto = 'opetustehtava'
               AND m.koodiarvo IN ('16', '11')
               AND kunta.koodisto = 'kunta'
               AND kunta.koodiarvo IN ('186',
                                       '700',
                                       '408')
     ),
     opetus_kieli AS (
         INSERT INTO maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta,
                              koodistoversio, org_oid)
             SELECT null,
                    l.id,
                    2, -- Kielet
                    'kielikoodistoopetushallinto',
                    'fi',
                    null,
                    1, -- OIKEUS
                    '{}',
                    null,
                    null
             FROM existing_lupa l
     ),
     enintaan_120 AS (
         UPDATE maarays SET koodisto = 'kujalisamaareet', koodiarvo = '1' -- enintään
             FROM existing_lupa l
             WHERE lupa_id = l.id AND koodisto = 'koulutussektori' AND arvo = '120' RETURNING kohde_id, koodisto
     ),
     lisaksi AS (
         INSERT INTO maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta,
                              koodistoversio, org_oid)
             SELECT null,
                    l.id,
                    e.kohde_id,
                    e.koodisto,
                    '4',
                    null,
                    2, -- RAJOITE
                    '{}',
                    null,
                    null
             FROM enintaan_120 e,
                  existing_lupa l RETURNING id, kohde_id, koodisto
     ),
     ali_enintaan_40 AS (
         INSERT INTO maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta,
                              koodistoversio, org_oid)
             SELECT lisaksi.id,
                    l.id,
                    lisaksi.kohde_id,
                    lisaksi.koodisto,
                    '1',
                    '40',
                    2, -- RAJOITE
                    '{}',
                    null,
                    null
             FROM lisaksi,
                  existing_lupa l RETURNING id, kohde_id
     ),
     ali_opetustehtava AS (
         INSERT INTO maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta,
                              koodistoversio, org_oid)
             SELECT ali_enintaan_40.id,
                    l.id,
                    ali_enintaan_40.kohde_id,
                    'opetustehtava',
                    '16',
                    null,
                    2, -- RAJOITE
                    '{}',
                    null,
                    null
             FROM ali_enintaan_40,
                  existing_lupa l RETURNING id
     ),
     ali_ajalta AS (
         UPDATE maarays SET koodisto = 'kujalisamaareet', koodiarvo = '3', parent_id = ali_opetustehtava.id -- ajalta
             FROM existing_lupa l, ali_opetustehtava
             WHERE lupa_id = l.id AND koodisto = 'koulutussektori' AND arvo = ''
     ),
     jarjestamismuoto AS (
         UPDATE maarays SET koodisto = 'opetuksenjarjestamismuoto', koodiarvo = '3', meta = null -- Osittain sisäoppilaitosmuotoinen
            FROM existing_lupa l
            WHERE lupa_id = l.id AND koodisto = 'kujamuutoikeudetmaarayksetjarajoitukset' AND TEXT(meta) LIKE '%Sisäoppilaitosmuotoinen%'
     )

SELECT *
FROM existing_lupa;
