-- Keski-Uudenmaan koulutuskuntayhtymä
with new_lupa as (
    insert into lupa (edellinen_lupa_id,
                      paatoskierros_id,
                      lupatila_id,
                      asiatyyppi_id,
                      jarjestaja_ytunnus,
                      jarjestaja_oid,
                      alkupvm,
                      loppupvm,
                      paatospvm,
                      meta,
                      maksu,
                      luoja,
                      asianumero)
        select id,
               paatoskierros_id,
               lupatila_id,
               asiatyyppi_id,
               jarjestaja_ytunnus,
               jarjestaja_oid,
               '2020-08-01',
               null,
               '2019-06-12',
               '{"ministeri": "Li Andersson", "esittelija": "Tarja Koskimäki", "ministeri_nimike": "Opetusministeri", "esittelija_nimike": "Ylitarkastaja"}',
               maksu,
               luoja,
               'VN/2171/2020'
        from lupa
        where diaarinumero = '40/531/2018' returning *),

     copy_maaraykset as (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, koodistoversio, parent_id)
             select new_lupa.id, m.kohde_id, m.koodisto, m.koodiarvo, m.maaraystyyppi_id, m.koodistoversio, parent_id
             from maarays m,
                  new_lupa
             where lupa_id = (select id from lupa where diaarinumero = '40/531/2018')),

     update_old_lupa as (update lupa set loppupvm = '2020-07-31' where diaarinumero = '40/531/2018'),

     historia as (
         insert into lupahistoria (diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm,
                                   voimassaololoppupvm, paatospvm, filename, lupa_id)
             select diaarinumero,
                    jarjestaja_ytunnus,
                    jarjestaja_oid,
                    'Uusimaa',
                    'Koulutustehtävän muutos',
                    alkupvm,
                    '2020-07-31',
                    paatospvm,
                    '40/531/2018',
                    id
             from lupa
             where diaarinumero = '40/531/2018'),

     koulutus_ajoneuvo as (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, koodistoversio)
             select new_lupa.id,
                    (select id from kohde where tunniste = 'tutkinnotjakoulutukset'),
                    'koulutus',
                    '457341',
                    (select id from maaraystyyppi where tunniste = 'OIKEUS'),
                    '12'
             from new_lupa),

     koulutus_elaintenhoito as (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, koodistoversio)
             select new_lupa.id,
                    (select id from kohde where tunniste = 'tutkinnotjakoulutukset'),
                    'koulutus',
                    '364902',
                    (select id from maaraystyyppi where tunniste = 'OIKEUS'),
                    '12'
             from new_lupa),

     koulutus_esitys as (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, koodistoversio)
             select new_lupa.id,
                    (select id from kohde where tunniste = 'tutkinnotjakoulutukset'),
                    'koulutus',
                    '324503',
                    (select id from maaraystyyppi where tunniste = 'OIKEUS'),
                    '12'
             from new_lupa),

     koulutus_matkail as (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, koodistoversio)
             select new_lupa.id,
                    (select id from kohde where tunniste = 'tutkinnotjakoulutukset'),
                    'koulutus',
                    '384146',
                    (select id from maaraystyyppi where tunniste = 'OIKEUS'),
                    '12'
             from new_lupa),

     koulutus_tieto as (
         insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, koodistoversio)
             select new_lupa.id,
                    (select id from kohde where tunniste = 'tutkinnotjakoulutukset'),
                    'koulutus',
                    '341102',
                    (select id from maaraystyyppi where tunniste = 'OIKEUS'),
                    '12'
             from new_lupa)

select *
from new_lupa;

-- update child maarays references to correct parents
update maarays child
set parent_id = (select new_parent.id
                 from maarays old_parent
                          join maarays new_parent
                               on old_parent.koodiarvo = new_parent.koodiarvo and
                                  old_parent.koodiarvo = new_parent.koodiarvo
                 where child.lupa_id = new_parent.lupa_id
                   and child.parent_id = old_parent.id)
from lupa
where lupa.asianumero = 'VN/2171/2020'
  and child.lupa_id = lupa.id
  and parent_id is not null;

-- insert new rajoite
insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id, koodistoversio, parent_id)
select lupa.id,
       (select id from kohde where tunniste = 'opetusjatutkintokieli'),
       'kieli',
       'en',
       (select id from maaraystyyppi where tunniste = 'RAJOITE'),
       '12',
       maarays.id
from lupa,
     maarays
where lupa.asianumero = 'VN/2171/2020'
  and maarays.lupa_id = lupa.id
  and maarays.koodisto = 'koulutus'
  and maarays.koodiarvo = '384145';

-- delete
delete
from maarays using lupa
where lupa.asianumero = 'VN/2171/2020'
  and maarays.lupa_id = lupa.id
  and maarays.koodisto = 'koulutus'
  and maarays.koodiarvo in ('341101', '351502');