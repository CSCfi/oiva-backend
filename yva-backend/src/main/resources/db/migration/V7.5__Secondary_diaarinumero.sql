-- Imatran kaupunki
UPDATE lupa
SET meta = meta || jsonb '{
  "toissijainen_diaarinumero": "8/530/2016"
}'
WHERE diaarinumero = '85/530/2017';

-- Kouluyhdistys Pestalozzi Schulverein Skolföreningen r.y.
UPDATE lupa
SET meta = meta || jsonb '{
  "toissijainen_diaarinumero": "41/530/2015"
}'
WHERE diaarinumero = '78/530/2015';

-- Oulun kaupunki
UPDATE lupa
SET meta = meta || jsonb '{
  "toissijainen_diaarinumero": "79/530/2016"
}'
WHERE diaarinumero = '32/530/2017';

-- Sastamalan kaupunki
UPDATE lupa
SET meta = meta || jsonb '{
  "toissijainen_diaarinumero": "863/430/98"
}'
WHERE diaarinumero = '925/430/98';

-- Suomalais-venäläinen koulu
UPDATE lupa
SET meta = meta || jsonb '{
  "toissijainen_diaarinumero": "57/592/2015"
}'
WHERE diaarinumero = '76/530/2015';

-- Helsingin ranskalais-suomalainen koulu
UPDATE lupa
SET meta = meta || jsonb '{
  "toissijainen_diaarinumero": "57/592/2015"
}'
WHERE diaarinumero = '50/530/2016';

-- Remove maarays and lupa with toisijainen_diaarinumero
DELETE
FROM maarays
WHERE lupa_id IN
      (SELECT id
       FROM lupa
       WHERE diaarinumero IN ('8/530/2016',
                              '41/530/2015',
                              '79/530/2016',
                              '863/430/98',
                              '57/592/2015'));

DELETE
FROM lupa
WHERE diaarinumero IN ('8/530/2016',
                       '41/530/2015',
                       '79/530/2016',
                       '863/430/98',
                       '57/592/2015');
