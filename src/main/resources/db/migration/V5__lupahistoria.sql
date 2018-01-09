DROP TABLE IF EXISTS oiva.lupahistoria CASCADE;
CREATE TABLE oiva.lupahistoria
(
    id bigserial not null primary key
    ,diaarinumero varchar(20) not null
    ,ytunnus varchar(10) not null
    ,oid varchar(255) not null
    ,maakunta varchar(100) not null
    ,tila varchar(100) not null
    ,voimassaolo varchar(100) not null
    ,paatospvm varchar(20) not null
    ,filename varchar(255) not null
);