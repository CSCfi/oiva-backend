-- kymenlaakson opiston kannatusyhdistys ry
with new_lupa AS (

    -- create new lupa
    insert into lupa (paatoskierros_id, lupatila_id, asiatyyppi_id, diaarinumero, jarjestaja_ytunnus,
                      jarjestaja_oid, alkupvm, loppupvm, paatospvm, meta, maksu, luoja, luontipvm)
        select paatoskierros_id,
               lupatila_id,
               asiatyyppi_id,
               '47/531/2019',
               '0206976-5',
               jarjestaja_oid,
               '2020-01-01',
               null,
               '2019-09-27',
               '{"esittelija": "Annika Bussman", "liitetiedosto": "Kymenlaakson_Opiston_Kannatusyhdistys_ry_2020.PDF", "esittelija_nimike": "Opetusneuvos", "korvattavat_diaarit": ""}',
               maksu,
               luoja,
               luontipvm
        from lupa
        where diaarinumero = '50/532/2011'
        returning *),

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
           and lupa.diaarinumero = '50/532/2011'
         returning *),

     -- modify koulutustehtava
     koulutustehtava AS (update maarays set meta = '{
       "koulutustehtävämääräys-0": "Kymenlaakson Opisto on grundvigilainen kansanopisto. Opiston koulutus painottuu kulttuuri- ja taideaineisiin, kieliin ja kansainvälisyyteen sekä maahanmuuttajakoulutukseen. Opisto järjestää lisäksi työllisyyttä edistäviä koulutuksia sekä siirtymävaiheiden valmistavaa ja jatko-opintoihin valmentavaa koulutusta.",
       "koulutustehtävämääräys-1": "",
       "koulutustehtävämääräys-2": ""
     }'
         where lupa_id = (select id from new_lupa) and koodisto = 'koulutustehtava'
         returning *),

     -- insert erityinen koulutustehtava
     erityinen_koulutustehtava AS (insert into maarays (parent_id, lupa_id, kohde_id, koodisto, koodiarvo, arvo,
                                                        maaraystyyppi_id, meta, koodistoversio)
         select parent_id,
                (select id from new_lupa),
                kohde_id,
                koodisto,
                '2',
                arvo,
                maaraystyyppi_id,
                '{"erityinenkoulutustehtävämääräys-0": "Osana koulutustehtävää opisto järjestää koulutusta vaikeasti vammaisille. Vaikeasti vammaisten koulutus edellyttää sisäoppilaitoksessa yövalvojan paikallaoloa.", "erityinenkoulutustehtävämääräys-1": "", "erityinenkoulutustehtävämääräys-2": ""}',
                koodistoversio
         from maarays m
         where lupa_id = (select id from lupa where diaarinumero = '73/532/2011')
           and koodisto = 'erityinenkoulutustehtava'
         returning *)
SELECT *
FROM new_lupa;

-- ending dates and history for kymenlaakson opiston kannatusyhdistys
update lupa
set loppupvm='2019-12-31',
    jarjestaja_ytunnus='0206976-5'
where diaarinumero = '50/532/2011';

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
       'Kymenlaakso',
       'Järjestämisluvan muutos',
       alkupvm,
       loppupvm,
       paatospvm,
       'Kymenlaakson Opiston Kannatusyhdistys ry.PDF'
FROM lupa
where diaarinumero = '50/532/2011';

-- ending dates and history for karjalan evankelinen seura
update lupa
set loppupvm='2019-12-31',
    jarjestaja_ytunnus='0158579-2'
where diaarinumero = '73/532/2011';

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
       'Kymenlaakso',
       'Järjestämisluvan muutos',
       alkupvm,
       loppupvm,
       paatospvm,
       'Karjalan Evankelinen seura ry.PDF'
FROM lupa
where diaarinumero = '73/532/2011';