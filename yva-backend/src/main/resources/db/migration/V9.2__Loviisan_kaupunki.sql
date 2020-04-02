-- loviisan kaupunki
with new_lupa AS (
    -- create new lupa
    insert into lupa (paatoskierros_id, lupatila_id, asiatyyppi_id, diaarinumero, jarjestaja_ytunnus,
                      jarjestaja_oid, alkupvm, loppupvm, paatospvm, meta, maksu, luoja, luontipvm)
        select paatoskierros_id,
               lupatila_id,
               asiatyyppi_id,
               '13/532/2019',
               '0203263-9',
               jarjestaja_oid,
               '2020-01-01',
               null,
               '2019-10-02',
               '{"esittelija": "Petra Heikkinen", "liitetiedosto": "Loviisan_kaupunki_2020.PDF", "esittelija_nimike": "Asiantuntija", "korvattavat_diaarit": ""}',
               maksu,
               luoja,
               luontipvm
        from lupa
        where diaarinumero = '188/532/2012'
        returning *),

     --- finnish lupa
     old_lupa AS (update lupa set loppupvm = '2019-12-31', jarjestaja_ytunnus = '0203263-9' where diaarinumero = '188/532/2012'),

     -- swedish lupa
     old_2 AS (update lupa set loppupvm = '2019-12-31', jarjestaja_ytunnus = '0203263-9' where diaarinumero = '189/532/2012'),

     -- Copy maaraykset from from old lupa to new one
     new_maaraykset AS (insert into maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, arvo, maaraystyyppi_id,
                                             meta, luoja, luontipvm, paivittaja, paivityspvm, koodistoversio,
                                             org_oid)
         select m.parent_id,
                new_lupa.id,
                m.kohde_id,
                m.koodisto,
                m.koodiarvo,
                m.arvo,
                m.maaraystyyppi_id,
                m.meta,
                m.luoja,
                m.luontipvm,
                m.paivittaja,
                m.paivityspvm,
                m.koodistoversio,
                m.org_oid
         from maarays m,
              lupa,
              new_lupa
         where lupa.id = m.lupa_id
           and lupa.diaarinumero = '188/532/2012'
         returning *),

     -- insert new opetuskieli
     opetuskieli AS (insert into maarays (lupa_id, kohde_id, koodisto, koodiarvo, maaraystyyppi_id)
         values ((select id from new_lupa), 2, 'kielikoodistoopetushallinto', 'sv', 1))
select *
from new_lupa;

-- modify koulutustehtava
update maarays
set meta = '{
  "koulutustehtävämääräys-0": "Loviisan kansalaisopisto - Lovisa medborgarinstitut järjestää koulutusta, joka painottuu taito- ja taideaineisiin, kieliin, tietotekniikkaan, liikuntaan sekä terveyttä edistäviin aineisiin.",
  "koulutustehtävämääräys-1": "",
  "koulutustehtävämääräys-2": ""
}'
from lupa l
where lupa_id = l.id
  and l.diaarinumero = '13/532/2019'
  and koodisto = 'koulutustehtava';

INSERT
INTO lupahistoria
(diaarinumero,
 ytunnus,
 oid,
 maakunta,
 tila,
 voimassaoloalkupvm,
 voimassaololoppupvm,
 paatospvm,
 filename)
SELECT diaarinumero,
       jarjestaja_ytunnus,
       jarjestaja_oid,
       'Uusimaa',
       'Järjestämisluvan muutos',
       alkupvm,
       loppupvm,
       paatospvm,
       'Loviisan kaupunki.PDF'
FROM lupa
where diaarinumero = '188/532/2012';