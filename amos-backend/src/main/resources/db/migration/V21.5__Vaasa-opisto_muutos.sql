-- Vaasan kaupunki - Vaasa-opisto lupa change.
WITH old_lupa AS (
    UPDATE lupa
        SET loppupvm = '2020-12-31'
        WHERE jarjestaja_ytunnus = '0209602-6'
            AND
              diaarinumero = '21/532/2013' RETURNING id, paatoskierros_id, lupatila_id, asiatyyppi_id,
            diaarinumero, jarjestaja_ytunnus,
            jarjestaja_oid, alkupvm, loppupvm, paatospvm, meta, koulutustyyppi, oppilaitostyyppi, asianumero
),
     historia AS (
         INSERT INTO lupahistoria (diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm, voimassaololoppupvm,
                                   paatospvm, filename, koulutustyyppi, oppilaitostyyppi, asianumero)
             SELECT diaarinumero,
                    jarjestaja_ytunnus,
                    jarjestaja_oid,
                    'Pohjanmaa',
                    'Järjestämisluvan muutos',
                    alkupvm,
                    loppupvm,
                    paatospvm,
                    meta ->> 'liitetiedosto',
                    koulutustyyppi,
                    oppilaitostyyppi,
                    asianumero
             FROM old_lupa
     ),
     new_lupa AS (
         INSERT INTO lupa (paatoskierros_id, lupatila_id, asiatyyppi_id, diaarinumero, jarjestaja_ytunnus,
                           jarjestaja_oid, alkupvm, paatospvm, meta, luoja, koulutustyyppi, oppilaitostyyppi,
                           asianumero)
             SELECT paatoskierros_id,
                    lupatila_id,
                    2, -- MUUTOS
                    'VN/19428/2020',
                    jarjestaja_ytunnus,
                    jarjestaja_oid,
                    '2021-01-01',
                    '2020-10-27',
                    '{"liitetiedosto": "Vaasan_kaupunki_kansalaisopisto_2021.pdf"}',
                    'oiva',
                    koulutustyyppi,
                    oppilaitostyyppi,
                    'VN/19428/2020'
             FROM old_lupa RETURNING id
     ),
     copied_maarays AS (
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, luoja, org_oid)
             SELECT l.id,
                    m.kohde_id,
                    m.koodisto,
                    m.koodiarvo,
                    m.arvo,
                    m.maaraystyyppi_id,
                    m.meta,
                    'oiva',
                    m.org_oid
             FROM new_lupa l,
                  old_lupa,
                  maarays m
             WHERE m.lupa_id = old_lupa.id
             RETURNING id
     )

SELECT *
FROM old_lupa;

WITH new_lupa AS (
    SELECT id
    FROM lupa
    WHERE asianumero = 'VN/19428/2020'
),
     koulutustehtava AS (
         -- Change opetustehtava text
         UPDATE maarays SET meta = jsonb_set(meta, '{koulutustehtävämääräys-0}',
                                             '"Vaasa-opisto - Vasa Arbis järjestää koulutusta, jossa painottuvat kielet, kulttuuriaineet, taide-ja taitoaineet, terveyden edistäminen, historia ja yhteiskunnalliset aineet."')
             FROM new_lupa
             WHERE lupa_id = new_lupa.id
                 AND koodisto = 'koulutustehtava'
     ),
     opetuskieli_ruotsi AS (
         -- Add opetuskieli ruotsi
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, luoja)
             SELECT m.lupa_id,
                    m.kohde_id,
                    m.koodisto,
                    'sv',
                    m.arvo,
                    m.maaraystyyppi_id,
                    m.meta,
                    m.luoja
             FROM maarays m,
                  new_lupa
             WHERE lupa_id = new_lupa.id
               AND m.koodisto = 'kielikoodistoopetushallinto'
               AND m.koodiarvo = 'fi'
     )

SELECT *
FROM new_lupa;
