-- Diaarinumero / ytunnus and pdf correction for Kulosaaren yhteiskoulun osakeyhtiö
UPDATE lupa
SET diaarinumero = '783/430/98',
    jarjestaja_ytunnus = '0213552-3',
    meta = '{
      "esittelija": "Helena Porkola",
      "esittelija_nimike": "Erikoissuunnittelija",
      "korvattavat_diaarit": "",
      "liitetiedosto": "Kulosaaren yhteiskoulun osakeyhtiö.pdf"
    }'
WHERE diaarinumero = '816/430/98';

-- Diaarinumero correction for Ada Äijälän koulu
UPDATE lupa
SET diaarinumero = '816/430/98',
    jarjestaja_ytunnus = '0202651-1'
WHERE diaarinumero = '999/999/99';

-- Diaarinumero correction for Pohjois-Karjalan Koulutuskuntayhtymä
UPDATE lupa
SET diaarinumero = '213/532/2012',
    jarjestaja_ytunnus = '0212371-7'
WHERE diaarinumero = '2/532/2018';