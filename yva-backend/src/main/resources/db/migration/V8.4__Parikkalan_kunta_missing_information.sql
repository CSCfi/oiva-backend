-- Fix and add missing information for parikkalan kunta

UPDATE maarays
SET meta = '{
  "koulutustehtävämääräys-0": "Simpelejärven opiston koulutuksen tavoitteena on edistää opiskelijan kehittymistä, elinikäistä oppimista, osallisuutta, hyvinvointia ja aktiivista kansalaisuutta. Opisto järjestää koulutusta, joka painottuu taito- ja taideaineisiin, kieliin, kotitalouteen, tietotekniikkaan ja terveyttä edistäviin aineisiin.",
  "koulutustehtävämääräys-1": "",
  "koulutustehtävämääräys-2": ""
}'
FROM lupa l
WHERE l.id = lupa_id
  AND l.diaarinumero = '5/532/2019'
  AND koodisto = 'koulutustehtava';

insert into maarays (lupa_id, kohde_id, koodisto,
                     koodiarvo, maaraystyyppi_id,
                     meta, luoja, luontipvm)
select id, 1, 'kunta', '689',
       1, '{"oppilaitosmaarays": "Sopimuskunta"}',
       'kuja', current_timestamp
       from lupa l where l.diaarinumero = '5/532/2019';