-- NB! These sql statements must be idempotent. When this file is changed, all statements are executed.


-- axxellin luvan lopetuspvm
update lupa set loppupvm='2018-12-31' where jarjestaja_ytunnus = '2064886-7' and loppupvm='2012-12-31';
-- cargotecin luvan loppumispvm oikein
update lupa set loppupvm='2019-05-31' where jarjestaja_ytunnus='0986820-1' and loppupvm is null;

-- update maarays code values altered in 2019/12
update maarays set koodiarvo = '1', koodistoversio='2' where koodisto = 'kuljettajakoulutus' and koodiarvo='3';
update maarays set koodiarvo = '1', koodistoversio='3' where koodisto = 'kuljettajakoulutus' and koodiarvo='5';
update maarays set koodiarvo = '2', koodistoversio='2' where koodisto = 'kuljettajakoulutus' and koodiarvo='4';
update maarays set koodiarvo = '2', koodistoversio='3' where koodisto = 'kuljettajakoulutus' and koodiarvo='6';
update maarays set koodistoversio = '1' where koodisto = 'kuljettajakoulutus' and koodistoversio is null;
