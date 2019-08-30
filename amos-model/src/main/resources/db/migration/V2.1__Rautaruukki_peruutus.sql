-- End existing lupa for Rautaruukki Oyj (0113276-9)
UPDATE lupa
SET loppupvm = '2019-08-31'
WHERE jarjestaja_ytunnus = '0113276-9'
  AND diaarinumero = '162/531/2017';

-- Create lupahistoria row
INSERT INTO lupahistoria (diaarinumero,
                          ytunnus,
                          oid,
                          maakunta,
                          tila,
                          voimassaoloalkupvm,
                          voimassaololoppupvm,
                          paatospvm,
                          filename) VALUES ('162/531/2017', '0113276-9', '1.2.246.562.10.55418547786', 'Uusimaa',
                                            'Järjestämisluvan peruutus', '2018-01-01', '2019-08-31', '2017-10-06',
                                            '162-531-2017.pdf');

