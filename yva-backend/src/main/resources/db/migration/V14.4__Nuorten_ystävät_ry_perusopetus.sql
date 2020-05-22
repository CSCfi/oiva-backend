-- Nuorten ystävät ry PO lupa
WITH existing_lupa AS (
    SELECT id
    FROM lupa
    WHERE diaarinumero = '1020/430/1998'
),
     delete_opetustehtava AS (
         DELETE FROM maarays WHERE lupa_id = (SELECT id FROM existing_lupa) AND koodisto = 'opetustehtava'
     ),
     deleted_erityistehtava AS (
         DELETE FROM maarays
             WHERE lupa_id = (SELECT id FROM existing_lupa) AND kohde_id = 4
     ),
     new_opetustehtava AS (
         INSERT INTO maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta,
                              koodistoversio, org_oid)
             SELECT null,
                    l.id,
                    3, -- Koulutustehtävät
                    'opetustehtava',
                    k.arvo,
                    null,
                    1, -- OIKEUS
                    null,
                    null,
                    null
             FROM existing_lupa l,
                  (SELECT * FROM (VALUES ('11'), ('18'), ('27'), ('28'), ('29'), ('30')) as koodi (arvo)) as k
             RETURNING id, koodisto, koodiarvo
     ),
     kunta_opetustehtava AS (
         INSERT INTO maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id)
             SELECT DISTINCT kunta.id,
                             l.id,
                             kunta.kohde_id,
                             m.koodisto,
                             m.koodiarvo,
                             2 -- RAJOITE
             FROM new_opetustehtava m,
                  maarays kunta,
                  existing_lupa l
             WHERE kunta.lupa_id = l.id
               AND m.koodisto = 'opetustehtava'
               AND m.koodiarvo IN ('18', '11')
               AND kunta.koodisto = 'kunta'
               AND kunta.koodiarvo IN ('140', -- Iisalmi
                                       '743') -- Seinäjoki
     ),
     jarjestamismuoto AS (
         INSERT INTO maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta,
                              koodistoversio, org_oid)
             SELECT null,
                    l.id,
                    7, -- Muut
                    'opetuksenjarjestamismuoto',
                    '2',
                    null,
                    1, -- OIKEUS
                    null,
                    null,
                    null
             FROM existing_lupa l RETURNING id, kohde_id
     ),
     jarjestamismuoto_opetustehtava AS (
         INSERT INTO maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta,
                              koodistoversio, org_oid)
             SELECT m.id,
                    l.id,
                    m.kohde_id,
                    'opetustehtava',
                    k.arvo,
                    null,
                    2, -- RAJOITE
                    null,
                    null,
                    null
             FROM jarjestamismuoto m,
                  existing_lupa l,
                  (SELECT * FROM (VALUES ('11'), ('18')) as koodi (arvo)) as k
     ),
     opiskelijamaara_enintaan_40 AS (
         UPDATE maarays m SET koodisto = 'kujalisamaareet', koodiarvo = '1', -- enintään
             meta = null
             FROM existing_lupa l
             WHERE lupa_id = l.id AND koodisto = 'koulutussektori' AND arvo = '40' RETURNING m.id, m.kohde_id
     ),
     enintaan_40_opetustehtava AS (
         INSERT INTO maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id)
             SELECT o.id,
                    l.id,
                    o.kohde_id,
                    'opetustehtava',
                    k.arvo,
                    2 -- RAJOITE
             FROM existing_lupa l,
                  opiskelijamaara_enintaan_40 o,
                  (SELECT * FROM (VALUES ('27'), ('28'), ('29'), ('30')) as koodi (arvo)) as k
     ),
     opiskelijamaara_iisalmi AS (
         UPDATE maarays m SET koodisto = 'kujalisamaareet', koodiarvo = '1', -- enintään
             meta = null
             FROM existing_lupa l
             WHERE lupa_id = l.id AND koodisto = 'koulutussektori' AND arvo = '10'
                 AND TEXT(meta) LIKE '%"urn:kuntamääräys-0": "140"%' RETURNING m.id, m.kohde_id
     ),
     ali_iisalmi_opiskelijamaara AS (
         INSERT INTO maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id)
             SELECT m.id,
                    l.id,
                    m.kohde_id,
                    'kunta',
                    '140', -- Iisalmi
                    null,
                    2      -- RAJOITE
             FROM opiskelijamaara_iisalmi m,
                  existing_lupa l
     ),
     opiskelijamaara_seinajoki AS (
         UPDATE maarays m SET koodisto = 'kujalisamaareet', koodiarvo = '1', -- enintään
             meta = null
             FROM existing_lupa l
             WHERE lupa_id = l.id AND koodisto = 'koulutussektori' AND arvo = '10'
                 AND TEXT(meta) LIKE '%"urn:kuntamääräys-0": "743"%' RETURNING m.id, m.kohde_id
     ),
     ali_seinajoki_opiskelijamaara AS (
         INSERT INTO maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id)
             SELECT m.id,
                    l.id,
                    m.kohde_id,
                    'kunta',
                    '743', -- Seinäjoki
                    2      -- RAJOITE
             FROM opiskelijamaara_seinajoki m,
                  existing_lupa l
     ),
     ali_muutoikeudet_muoto AS (
         INSERT INTO maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id)
             SELECT m.id,
                    l.id,
                    m.kohde_id,
                    'opetuksenjarjestamismuoto',
                    '2', -- Koulukotimuotoinen
                    2    -- RAJOITE
             FROM maarays m,
                  existing_lupa l
             WHERE m.koodisto = 'kujamuutoikeudetmaarayksetjarajoitukset'
     )

SELECT *
FROM existing_lupa;