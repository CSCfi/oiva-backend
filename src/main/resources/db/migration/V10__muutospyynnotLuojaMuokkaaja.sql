ALTER TABLE oiva.muutospyynto ADD luoja text null;
ALTER TABLE oiva.muutospyynto ADD luontipvm timestamp not null default current_timestamp;
ALTER TABLE oiva.muutospyynto ADD paivittaja text null;
ALTER TABLE oiva.muutospyynto ADD paivityspvm timestamp null;
