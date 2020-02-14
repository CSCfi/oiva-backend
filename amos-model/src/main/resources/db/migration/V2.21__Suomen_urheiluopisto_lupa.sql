-- Suomen urheiluopisto
-- 106/531/2017 is the latest. Delete it from history and modify ending date
delete from lupahistoria where diaarinumero = '106/531/2017';
update lupa set loppupvm = null where diaarinumero = '106/531/2017';

-- Preserve data for kielteinen päätös
update lupa set loppupvm = alkupvm where diaarinumero = '13/531/2018'