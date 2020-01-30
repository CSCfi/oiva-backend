-- Kymenlaakson opiston kannatusyhdistys
update lupa set loppupvm='2019-12-31' where diaarinumero='52/531/2017';
insert into lupahistoria (diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm, voimassaololoppupvm, paatospvm, filename)
select diaarinumero, jarjestaja_ytunnus, jarjestaja_oid, 'Kymenlaakso', 'Järjestämisluvan peruutus', alkupvm, loppupvm, paatospvm, diaarinumero  from lupa where diaarinumero='52/531/2017';

-- Tampereen urheiluhierojakoulu
update lupa set loppupvm='2019-12-31' where diaarinumero='70/531/2017';
insert into lupahistoria (diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm, voimassaololoppupvm, paatospvm, filename)
select diaarinumero, jarjestaja_ytunnus, jarjestaja_oid, 'Pirkanmaa', 'Järjestämisluvan peruutus', alkupvm, loppupvm, paatospvm, diaarinumero  from lupa where diaarinumero='70/531/2017';

-- Reisjärven kristillinen kansanopistoyhdistys
update lupa set loppupvm='2019-12-31' where diaarinumero='96/531/2017';
insert into lupahistoria (diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm, voimassaololoppupvm, paatospvm, filename)
select diaarinumero, jarjestaja_ytunnus, jarjestaja_oid, 'Pohjois-Pohjanmaa', 'Järjestämisluvan peruutus', alkupvm, loppupvm, paatospvm, diaarinumero  from lupa where diaarinumero='96/531/2017';

-- Vuolle setlementti
update lupa set loppupvm='2019-12-31' where diaarinumero='94/531/2017';
insert into lupahistoria (diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm, voimassaololoppupvm, paatospvm, filename)
select diaarinumero, jarjestaja_ytunnus, jarjestaja_oid, 'Pohjois-Pohjanmaa', 'Järjestämisluvan peruutus', alkupvm, loppupvm, paatospvm, diaarinumero  from lupa where diaarinumero='94/531/2017';