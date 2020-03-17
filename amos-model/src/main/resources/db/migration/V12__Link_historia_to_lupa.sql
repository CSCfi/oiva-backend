alter table lupahistoria
    add column lupa_id bigint references lupa default null;

update lupahistoria h
set lupa_id = (select id
               from lupa l
               where l.jarjestaja_oid = h.oid
                 and l.diaarinumero = h.diaarinumero
                 and l.alkupvm = h.voimassaoloalkupvm
                 and l.loppupvm = h.voimassaololoppupvm
                 and l.paatospvm = h.paatospvm);

-- force explicit handling when adding now rows to history
alter table lupahistoria
    alter column lupa_id drop default;