-- Fix paatospvm and diaarinumero for all siirtymasaannoslupa.
CREATE TEMP TABLE siirtyma
(
    ytunnus      VARCHAR,
    diaarinumero VARCHAR,
    paatospvm    DATE
);

INSERT INTO siirtyma (ytunnus, diaarinumero, paatospvm)
VALUES ('0763403-0', '38/530/2007', '2007-02-21'),  -- ABB Oy
       ('0108023-3', '551/530/2006', '2006-12-13'), -- Finnair Oyj
       ('0188756-3', '289/530/2006', '2006-05-12'), -- Kainuun Opisto Oy
       ('0950895-1', '13/531/2014', '2014-04-22'),  -- Konecranes Finland Oy
       ('0151534-8', '57/530/2007', '2007-02-22'),  -- Nanso Group Oy
       ('0112038-9', '560/530/2006', '2006-12-13'), -- Nokia Oyj
       ('0201789-3', '178/530/2006', '2006-05-08'), -- Opintotoiminnan keskusliitto ry
       ('1524361-1', '3/530/2007', '2007-01-09'),   -- Sanoma Oyj
       ('0215382-8', '81/531/2011', '2012-11-22'),  -- Työväen Sivistysliitto TSL ry
       ('1041090-0', '574/530/2006', '2006-12-13'), -- UPM-Kymmene Oyj
       ('0773744-3', '563/530/2006', '2006-12-13') -- Wärtsilä Finland Oy
;

-- Set correct diaarinumero and paatospvm
UPDATE lupa l
SET diaarinumero = s.diaarinumero,
    paatospvm    = s.paatospvm
FROM siirtyma s
WHERE l.jarjestaja_ytunnus = s.ytunnus;

DROP TABLE siirtyma;
