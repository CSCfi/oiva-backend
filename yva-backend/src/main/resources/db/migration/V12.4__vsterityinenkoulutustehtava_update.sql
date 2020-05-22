-- CSCKUJA-386 texts wanted for removal are in all cases the content of meta -> erityinenkoulutustehtävämääräys-0
update maarays
set meta = jsonb_set(maarays.meta, '{erityinenkoulutustehtävämääräys-0}', '""')
where id in
      (select maarays.id
       from maarays
                left join lupa on maarays.lupa_id = lupa.id
       where lupa.diaarinumero in (
                                   '25/531/2011',
                                   '96/532/2011',
                                   '98/532/2011',
                                   '102/532/2011',
                                   '105/532/2011',
                                   '100/532/2011'
           )
         and maarays.koodisto = 'vsterityinenkoulutustehtava');
