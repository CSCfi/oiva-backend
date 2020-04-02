update lupa
set diaarinumero='168/460/98',
    meta        = meta ||
                  ('{"toissijainen_diaarinumero": "73/430/1999,2/532/2017", "toissijainen_liitetiedosto": "Eurajoen kristillisen opiston kannatusyhdistys ry 2.pdf"}')::jsonb
where diaarinumero = '2/532/2017';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '73/430/1999');
delete from lupa where diaarinumero = '73/430/1999';

update lupa
set diaarinumero='24/530/2014',
    meta        = meta || '{"toissijainen_diaarinumero": "83/530/2016"}'
where diaarinumero = '83/530/2016';

update lupa
set diaarinumero='28/440/2000',
    meta        = meta || '{"toissijainen_diaarinumero": "31/440/2000,174/460/2000"}'
where diaarinumero = '31/440/2000';


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '129/430/2004'
  and old.diaarinumero = '151/430/2004';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '151/430/2004');
delete from lupa where diaarinumero = '151/430/2004';


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '101/530/2008'
  and old.diaarinumero = '60/530/2016';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '60/530/2016');
delete from lupa where diaarinumero = '60/530/2016';


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '2/440/1999'
  and old.diaarinumero = '77/430/2000';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '77/430/2000');
delete from lupa where diaarinumero = '77/430/2000';


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '235/530/2009'
  and old.diaarinumero = '12/530/2017';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '12/530/2017');
delete from lupa where diaarinumero = '12/530/2017';


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '132/460/1998'
  and old.diaarinumero = '37/440/1999';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '37/440/1999');
delete from lupa where diaarinumero = '37/440/1999';


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '567/530/2008'
  and old.diaarinumero = '62/530/2016';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '62/530/2016');
delete from lupa where diaarinumero = '62/530/2016';


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '74/530/2016'
  and old.diaarinumero = '148/530/2017';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '148/530/2017');
delete from lupa where diaarinumero = '148/530/2017';


update lupa l
set meta = l.meta ||
           ('{"toissijainen_diaarinumero": "", "toissijainen_liitetiedosto": "Kiteen Evankelisen Kansanopiston kannatusyhdistys ry 2.pdf,Kiteen Evankelisen Kansanopiston kannatusyhdistys ry 3.pdf"}')::jsonb
where l.diaarinumero = '138/460/1998';
delete from maarays where lupa_id in (select id from lupa where diaarinumero in ('172/460/1998', '24/532/2016'));
delete from lupa where diaarinumero in ('172/460/1998', '24/532/2016');


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '208/530/2007'
  and old.diaarinumero = '61/530/2016';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '61/530/2016');
delete from lupa where diaarinumero = '61/530/2016';


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '32/440/2001'
  and old.diaarinumero = '47/530/2017';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '47/530/2017');
delete from lupa where diaarinumero = '47/530/2017';


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '1020/430/1998'
  and old.diaarinumero = '10/530/2013';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '10/530/2013');
delete from lupa where diaarinumero = '10/530/2013';


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '154/460/1998'
  and old.diaarinumero = '2/530/2017';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '2/530/2017');
delete from lupa where diaarinumero = '2/530/2017';


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '22/530/2012'
  and old.diaarinumero = '12/530/2014';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '12/530/2014');
delete from lupa where diaarinumero = '12/530/2014';


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '2/440/2000'
  and old.diaarinumero = '84/530/2016';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '84/530/2016');
delete from lupa where diaarinumero = '84/530/2016';


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '562/530/2008'
  and old.diaarinumero = '5/530/2016';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '5/530/2016');
delete from lupa where diaarinumero = '5/530/2016';


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '204/430/2000'
  and old.diaarinumero = '154//430/2003';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '154//430/2003');
delete from lupa where diaarinumero = '154//430/2003';


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '32/440/1998'
  and old.diaarinumero = '149/530/2017';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '149/530/2017');
delete from lupa where diaarinumero = '149/530/2017';


-- Moved from repeateable migration to be sure this is always run before following updates. Case: populating data to empty db

-- UTU diaarinumeron korjaus
update lupa
set diaarinumero='990/430/1998'
where diaarinumero = '994/999/9999'
  and jarjestaja_oid = '1.2.246.562.10.79875033395';

update lupa l
set meta = l.meta ||
           ('{"toissijainen_diaarinumero": "62/530/2011,45/530/2011", "toissijainen_liitetiedosto": "Turun yliopisto.pdf,Turun yliopisto 3.pdf"}')::jsonb
where l.diaarinumero = '990/430/1998';
delete from maarays where lupa_id in (select id from lupa where diaarinumero in ('62/530/2011', '45/530/2011'));
delete from lupa where diaarinumero in ('62/530/2011', '45/530/2011');


update lupa l
set meta = l.meta || ('{"toissijainen_diaarinumero": "' || old.diaarinumero || '", "toissijainen_liitetiedosto": ' ||
                      (old.meta -> 'liitetiedosto') || '}')::jsonb
from lupa old
where l.diaarinumero = '270/530/2009'
  and old.diaarinumero = '18/530/2017';
delete from maarays where lupa_id = (select id from lupa where diaarinumero = '18/530/2017');
delete from lupa where diaarinumero = '18/530/2017';