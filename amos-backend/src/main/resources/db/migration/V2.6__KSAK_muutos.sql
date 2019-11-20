-- add 384148 Ruokapalvelujen at to Koillis-Suomen Aikuiskoulutus (KSAK)

with old as (
    update lupa set loppupvm = '2019-12-31'
        where diaarinumero = '89/531/2017'
        returning *
),
     -- insert into lupahistoria
     historia as (
         insert into lupahistoria (diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm, voimassaololoppupvm,
                                   paatospvm, filename)
             SELECT diaarinumero,
                    jarjestaja_ytunnus,
                    jarjestaja_oid,
                    'Pohjois-Pohjanmaa',
                    'Järjestämisluvan muutos',
                    alkupvm,
                    loppupvm,
                    paatospvm,
                    '89-531-2017.pdf'
             from old
             returning id
     ),
     -- create new row to lupa table
     new_lupa as (
         insert into lupa (paatoskierros_id, lupatila_id, asiatyyppi_id, diaarinumero, jarjestaja_ytunnus,
                           jarjestaja_oid, alkupvm,
                           loppupvm, paatospvm, meta, maksu, luoja, paivittaja, paivityspvm)
             SELECT 19,
                    lupatila_id,
                    asiatyyppi_id,
                    '98/531/2019',
                    jarjestaja_ytunnus,
                    jarjestaja_oid,
                    '2020-01-01',
                    null,
                    '2019-12-16',
                    '{"ministeri": "Li Andersson", "esittelija": "Tiina Polo", "ministeri_nimike": "Opetusministeri", "esittelija_nimike": "Opetusneuvos"}',
                    maksu,
                    luoja,
                    paivittaja,
                    paivityspvm
             from old
             returning *
     ),
     -- create new link to file
     liite as (
         insert into liite (nimi, polku, luoja, tyyppi, kieli)
             values ('KSAK', 'paatosKirjeet2019/KSAK_päätös.pdf', 'oiva', 'paatosKirje', 'fi')
             returning id
     ),
     link as (
         insert into lupa_liite (lupa_id, liite_id)
             select new_lupa.id, liite.id
             from new_lupa,
                  liite
     ),
     -- copy original maaraykset
     copied_maarays as (
         insert into maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, luoja,
                              luontipvm, paivittaja,
                              paivityspvm, koodistoversio)
             select m.parent_id,
                    new_lupa.id,
                    m.kohde_id,
                    m.koodisto,
                    m.koodiarvo,
                    m.arvo,
                    m.maaraystyyppi_id,
                    m.meta,
                    m.luoja,
                    m.luontipvm,
                    m.paivittaja,
                    m.paivityspvm,
                    m.koodistoversio
             from new_lupa,
                  maarays m
                      join old on m.lupa_id = old.id
             returning *
     ),
     -- copy new maarays
     new_maarays as (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, luoja, koodistoversio)
             select id, 1, 'koulutus', '384148', 1, 'oiva', 7 from new_lupa
             returning *
     )
select *
from copied_maarays
union
select *
from new_maarays;