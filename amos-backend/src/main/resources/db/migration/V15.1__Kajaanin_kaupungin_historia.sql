insert into lupahistoria (diaarinumero, ytunnus, oid, maakunta, tila, voimassaoloalkupvm, voimassaololoppupvm,
                          paatospvm, filename, lupa_id)
select diaarinumero,
       jarjestaja_ytunnus,
       jarjestaja_oid,
       'Kainuu',
       'Järjestämisluvan muutos',
       alkupvm,
       loppupvm,
       paatospvm,
       diaarinumero,
       id
from lupa
where diaarinumero = '30/531/2017';