alter table lupahistoria
    add column kumottupvm date default null;

update lupahistoria
set kumottupvm = voimassaololoppupvm
where voimassaoloalkupvm = voimassaololoppupvm;