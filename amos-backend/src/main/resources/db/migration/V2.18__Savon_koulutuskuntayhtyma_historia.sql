
-- remove duplicate history row and set correct filename
delete from lupahistoria where diaarinumero = '46/531/2018' and tila = 'PAATOS';
update lupahistoria set filename=diaarinumero where diaarinumero = '46/531/2018';