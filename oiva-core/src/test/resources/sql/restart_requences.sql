-- Restart sequences so pre inserted data can use smaller id numbers.
ALTER SEQUENCE lupa_id_seq RESTART WITH 100;
ALTER SEQUENCE maarays_id_seq RESTART WITH 100;
ALTER SEQUENCE muutospyynto_id_seq RESTART WITH 100;
ALTER SEQUENCE muutos_id_seq RESTART WITH 100;
ALTER SEQUENCE liite_id_seq RESTART WITH 100;
ALTER SEQUENCE lupa_liite_id_seq RESTART WITH 100;
ALTER SEQUENCE lupahistoria_id_seq RESTART WITH 100;
