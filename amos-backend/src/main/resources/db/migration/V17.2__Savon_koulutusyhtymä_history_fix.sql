-- Change Savon koulutusyhtymä old lupa alkupvm, paatospvm to match history
UPDATE lupa
SET alkupvm   = '2019-01-01',
    paatospvm = '2018-12-14'
WHERE diaarinumero = '46/531/2018'
  AND jarjestaja_ytunnus = '1852679-9'
