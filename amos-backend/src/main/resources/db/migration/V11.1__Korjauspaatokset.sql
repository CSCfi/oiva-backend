-- seinäjoen koulutuskuntayhtymän _korjattu_ päätös
insert into lupahistoria (diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm, voimassaololoppupvm,
                          kumottupvm, paatospvm, filename)
values ('54/531/2018',
        '1007629-5',
        '1.2.246.562.10.73539475928',
        'Etelä-Pohjanmaan',
        'Korvattu',
        '2019-01-01',
        '2019-03-21',
        '2019-03-21',
        '2018-12-14',
        '54_531_2018_Seinäjoen_kk.PDF');

-- axxell utbildning: move active paatos to history and create new _korvaava_ päätös
-- test envs are missing newest lupa and its maaraykset (119 rows)
update lupa
set loppupvm = '2019-04-23'
where diaarinumero = '5/531/2019';

insert into lupahistoria (diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm, voimassaololoppupvm,
                          kumottupvm, paatospvm, filename)
select diaarinumero,
       jarjestaja_ytunnus,
       jarjestaja_oid,
       'Uusimaa',
       'Korvattu',
       alkupvm,
       loppupvm,
       '2019-04-23',
       paatospvm,
       '5_531_2019 Axxell.PDF'
from lupa
where diaarinumero = '5/531/2019';

WITH new_lupa AS (
    insert into lupa (edellinen_lupa_id, paatoskierros_id, lupatila_id, asiatyyppi_id, diaarinumero,
                                    jarjestaja_ytunnus,
                                    jarjestaja_oid, alkupvm, loppupvm, paatospvm, meta)
    select edellinen_lupa_id,
           paatoskierros_id,
           lupatila_id,
           '2', -- muutos,
           diaarinumero,
           jarjestaja_ytunnus,
           jarjestaja_oid,
           alkupvm,
           null,
           '2019-04-23',
           meta
    from lupa
    where diaarinumero = '5/531/2019'
    returning *),

     copy_maaraykset AS (
         insert into maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id, meta, luoja,
                              luontipvm, paivittaja, paivityspvm, koodistoversio)
             select maarays.parent_id,
                    new_lupa.id,
                    maarays.kohde_id,
                    maarays.koodisto,
                    maarays.koodiarvo,
                    maarays.arvo,
                    maarays.maaraystyyppi_id,
                    maarays.meta,
                    maarays.luoja,
                    maarays.luontipvm,
                    maarays.paivittaja,
                    maarays.paivityspvm,
                    maarays.koodistoversio
             from maarays,
                  new_lupa
             where maarays.lupa_id =
                   (select id from lupa where diaarinumero = '5/531/2019' and loppupvm is not null)
     )

select *
from new_lupa;


-- suomen yrittäjäopisto: create new _korvattu_ päätös to history
insert into lupahistoria (diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm, voimassaololoppupvm,
                          kumottupvm,
                          paatospvm, filename)
select diaarinumero,
       ytunnus,
       oid,
       maakunta,
       tila,
       '2018-01-01',
       '2018-01-01',
       '2017-12-12',
       '2017-10-06',
       'Ammatillisten+tutkintojen+ja+koulutuksen+järjestäm.PDF'
from lupahistoria
where diaarinumero = '22/531/2017';