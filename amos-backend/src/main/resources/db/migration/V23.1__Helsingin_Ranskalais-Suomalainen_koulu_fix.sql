-- Fix Helsingin Ranskalais-Suomalainen koulu (0245912-2) PO lupa 57/592/2015, 50/530/2016
WITH cur_lupa AS (
    SELECT id
    FROM lupa
    WHERE jarjestaja_ytunnus = '0245912-2'
      AND diaarinumero = '57/592/2015, 50/530/2016'
      AND koulutustyyppi = '1'
),
     kieli_fi AS (
         SELECT m.lupa_id,
                m.kohde_id,
                m.koodisto,
                m.koodiarvo,
                m.arvo,
                m.maaraystyyppi_id,
                m.meta,
                m.luoja,
                m.koodistoversio,
                m.org_oid
         FROM maarays m
                  LEFT JOIN cur_lupa l ON m.lupa_id = l.id
         WHERE l.id = l.id
           AND m.koodisto = 'kielikoodistoopetushallinto'
           AND m.koodiarvo = 'fi'
     ),
     kieli_fr AS (
         -- Add missing toissijainenopetuskieli 'fr'
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, meta, luoja,
                              koodistoversio)
             SELECT m.lupa_id,
                    m.kohde_id,
                    m.koodisto,
                    'fr',
                    m.maaraystyyppi_id,
                    '{"valikko": "toissijaiset"}',
                    'oiva',
                    m.koodistoversio
             FROM kieli_fi m RETURNING id
     ),
     kieli_sv AS (
         -- Add missing toissijainenopetuskieli 'sv'
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, meta, luoja,
                              koodistoversio)
             SELECT m.lupa_id,
                    m.kohde_id,
                    m.koodisto,
                    'sv',
                    m.maaraystyyppi_id,
                    '{"valikko": "toissijaiset"}',
                    'oiva',
                    m.koodistoversio
             FROM kieli_fi m RETURNING id
     )

SELECT *
FROM kieli_sv, kieli_fr;
