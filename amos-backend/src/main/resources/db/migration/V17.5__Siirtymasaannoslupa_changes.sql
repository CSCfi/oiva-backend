-- Set end date for all siirtymasaannoslupa.
UPDATE lupa
SET loppupvm = '2021-12-31'
WHERE jarjestaja_ytunnus IN ('0763403-0', -- ABB Oy
                             '0108023-3', -- Finnair Oyj
                             '0188756-3', -- Kainuun Opisto Oy
                             '0950895-1', -- Konecranes Finland Oy
                             '0151534-8', -- Nanso Group Oy
                             '0112038-9', -- Nokia Oyj
                             '0201789-3', -- Opintotoiminnan keskusliitto ry
                             '1524361-1', -- Sanoma Oyj
                             '0215382-8', -- Työväen Sivistysliitto TSL ry
                             '1041090-0', -- UPM-Kymmene Oyj
                             '0773744-3' -- Wärtsilä Finland Oy
    );

-- Set correct alkupvm
UPDATE lupa
SET alkupvm = '2007-03-01'
WHERE jarjestaja_ytunnus IN ('0763403-0', -- ABB Oy
                             '0188756-3', -- Kainuun Opisto Oy
                             '0151534-8' -- Nanso Group Oy
    );

UPDATE lupa
SET alkupvm = '2006-12-15'
WHERE jarjestaja_ytunnus IN ('0108023-3', -- Finnair Oyj
                             '0112038-9', -- Nokia Oyj
                             '1041090-0', -- UPM-Kymmene Oyj
                             '0773744-3' -- Wärtsilä Finland Oy
    );

UPDATE lupa
SET alkupvm = '2006-06-01'
WHERE jarjestaja_ytunnus IN ('0188756-3'); -- Kainuun Opisto Oy


UPDATE lupa
SET alkupvm = '2014-01-01'
WHERE jarjestaja_ytunnus IN ('0950895-1'); -- Konecranes Finland Oy

UPDATE lupa
SET alkupvm = '2006-05-15'
WHERE jarjestaja_ytunnus IN ('0201789-3'); -- Opintotoiminnan keskusliitto ry

UPDATE lupa
SET alkupvm = '2007-01-09'
WHERE jarjestaja_ytunnus IN ('1524361-1'); -- Sanoma Oyj

UPDATE lupa
SET alkupvm = '2012-01-01'
WHERE jarjestaja_ytunnus IN ('0215382-8'); -- Työväen Sivistysliitto TSL ry
