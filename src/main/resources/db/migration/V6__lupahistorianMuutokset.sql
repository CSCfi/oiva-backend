-- uusi taulu lupahistorialle, vanha voidaan poistaa
DROP TABLE IF EXISTS oiva.lupahistoria CASCADE;
CREATE TABLE oiva.lupahistoria
(
    id bigserial not null primary key
    ,diaarinumero varchar(20) not null
    ,ytunnus varchar(10) not null
    ,oid varchar(255) not null
    ,maakunta varchar(100) not null
    ,tila varchar(100) not null
    ,voimassaoloalkupvm date not null
    ,voimassaololoppupvm date not null
    ,paatospvm date not null
    ,filename varchar(255) not null
);
