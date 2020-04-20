-- CSCKUJA-383 four cases had org_id in wrong, redundant (no meta) row

delete from maarays where lupa_id = (select id from lupa where diaarinumero = '9/532/2017') and koodisto = 'oppilaitos' and meta is null;
update maarays set org_oid = '1.2.246.562.10.15410868853' from lupa where lupa_id = lupa.id and lupa.diaarinumero = '9/532/2017' and koodisto = 'oppilaitos';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '10/532/2018') and koodisto = 'oppilaitos' and meta is null;
update maarays set org_oid = '1.2.246.562.10.88630525025' from lupa where lupa_id = lupa.id and lupa.diaarinumero = '10/532/2018' and koodisto = 'oppilaitos';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '9/532/2018') and koodisto = 'oppilaitos' and meta is null;
update maarays set org_oid = '1.2.246.562.10.522875047910' from lupa where lupa_id = lupa.id and lupa.diaarinumero = '9/532/2018' and koodisto = 'oppilaitos';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '243/532/2012') and koodisto = 'oppilaitos' and meta is null;
update maarays set org_oid = '1.2.246.562.10.88633964114' from lupa where lupa_id = lupa.id and lupa.diaarinumero = '243/532/2012' and koodisto = 'oppilaitos';
