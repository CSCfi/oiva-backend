-- Change diaarinumero column size in lupa, muutospyynto and lupahistoria tables.
ALTER TABLE lupahistoria ALTER COLUMN diaarinumero TYPE VARCHAR(255);
ALTER TABLE lupa ALTER COLUMN diaarinumero TYPE VARCHAR(255);
ALTER TABLE muutospyynto ALTER COLUMN diaarinumero TYPE VARCHAR(255);
