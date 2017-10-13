
-- lisätään uusi esitysmalli
INSERT INTO oiva.esitysmalli(templatepath,luoja,luontipvm) VALUES ('paatoskierros2017','oiva','2017-10-13 09:05:28.53536');

-- päivitetään uusien lupien päätöskierroksen esitysmalli
UPDATE oiva.paatoskierros SET esitysmalli_id = 9 WHERE id = 19;