-- axxellin luvan lopetuspvm
update lupa set loppupvm='2018-12-31' where jarjestaja_ytunnus = '2064886-7' and loppupvm='2012-12-31';
-- cargotecin luvan loppumispvm oikein
update lupa set loppupvm='2019-05-31' where jarjestaja_ytunnus='0986820-1' and loppupvm is null;