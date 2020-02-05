-- delete existing row (dev is out of sync with production)
delete from lupahistoria where diaarinumero='22/531/2017';

-- insert row to Suomen yrittajaopisto luphistoria
insert into lupahistoria (diaarinumero, ytunnus,
                          oid, maakunta,
                          tila, voimassaoloalkupvm,
                          voimassaololoppupvm, paatospvm,
                          filename)
select diaarinumero,
       jarjestaja_ytunnus,
       jarjestaja_oid,
       'Etelä-Pohjanmaa',
       'Järjestämisluvan muutos',
       alkupvm,
       loppupvm,
       paatospvm,
       '22/531/2017'
from lupa
where diaarinumero = '22/531/2017';