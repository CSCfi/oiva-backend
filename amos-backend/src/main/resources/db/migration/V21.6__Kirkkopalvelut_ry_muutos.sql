-- Kirkkopalvelut ry kansanopisto change.
WITH old_lupa AS (
    UPDATE lupa
        SET loppupvm = '2020-12-31'
        WHERE jarjestaja_ytunnus = '0215281-7'
            AND
              diaarinumero = '33/532/2016' RETURNING id, paatoskierros_id, lupatila_id, asiatyyppi_id,
            diaarinumero, jarjestaja_ytunnus,
            jarjestaja_oid, alkupvm, loppupvm, paatospvm, meta, koulutustyyppi, oppilaitostyyppi, asianumero
),
     historia AS (
         INSERT INTO lupahistoria (diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm, voimassaololoppupvm,
                                   paatospvm, filename, koulutustyyppi, oppilaitostyyppi, asianumero)
             SELECT diaarinumero,
                    jarjestaja_ytunnus,
                    jarjestaja_oid,
                    'Uusimaa',
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
                    'VN/14847/2020',
                    jarjestaja_ytunnus,
                    jarjestaja_oid,
                    '2021-01-01',
                    '2020-10-08',
                    '{"liitetiedosto": "Kirkkopalvelut_ry_kansanopisto_2021.pdf"}',
                    'oiva',
                    koulutustyyppi,
                    oppilaitostyyppi,
                    'VN/14847/2020'
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
    WHERE asianumero = 'VN/14847/2020'
),
     koulutustehtava AS (
         -- Change opetustehtava text
         UPDATE maarays SET meta = jsonb_set(meta, '{koulutustehtävämääräys-0}',
                                             '"Luther-opiston koulutustehtävän arvopohja on evankelisluterilaisessa kristillisyydessä. Opiston koulutus painottuu seurakunnalliseen ja kirkolliseen toimintaan sekä kristillisen kasvatuksen ja uskonnon pedagogiikan sisältöihin. Opisto järjestää myös kasvatus- ja perhetoimintaa tukevaa koulutusta, kansainvälisyys-, taide-, kulttuuri- ja musiikin koulutusta. Painopisteitä ovat myös kestävän kehityksen, työelämässä jaksamisen sekä aktiivista kansalaisuutta edistävän koulutuksen järjestäminen. Opiston koulutus painottuu myös ympäristö-, luonto- ja elämyskasvatukseen sekä terveyttä ja hyvinvointia edistävien aineiden ja liikunnan koulutukseen. Opisto järjestää koulutusta myös maahanmuuttajille."')
             FROM new_lupa
             WHERE lupa_id = new_lupa.id
                 AND koodisto = 'koulutustehtava'
     ),
     kaustinen_kunta AS (
         -- Add Kaustinen as kunta maarays
         INSERT INTO maarays (lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, luoja, org_oid)
            SELECT l.id,
                   m.kohde_id,
                   m.koodisto,
                   '236', -- Kaustinen
                   m.arvo,
                   m.maaraystyyppi_id,
                   m.meta,
                   'oiva',
                   m.org_oid
            FROM new_lupa l,
                 maarays m
            WHERE m.lupa_id = l.id AND m.koodisto = 'kunta' AND m.koodiarvo = '186'
             RETURNING id
     )

SELECT *
FROM new_lupa;
