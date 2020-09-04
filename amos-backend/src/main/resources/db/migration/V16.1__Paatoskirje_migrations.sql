-- Jyväskylän koulutuskuntayhtymä Gradia
WITH jyvaskyla_lupa AS (
    -- Fix asianumero
    UPDATE lupa SET asianumero = 'VN/2155/2020'
        WHERE jarjestaja_ytunnus = '0208201-1' AND asianumero = 'VN/2175/2020' RETURNING id
),
     paatoskirje AS (
         INSERT INTO liite (nimi, polku, luoja, kieli, koko, tyyppi)
             VALUES ('VN_2155_2020 päätöskirje',
                     '2020/9/2/jyvaskyla/VN_2155_2020- Ammatillisten tutkintojen ja koulutuksen järjestämisluvan muutos 1.8.2020_jyväskylä .PDF',
                     'oiva', 'fi', 247952, 'paatosKirje') RETURNING id
     ),
     link AS (
         INSERT INTO lupa_liite (lupa_id, liite_id)
             SELECT l.id, p.id
             FROM jyvaskyla_lupa l,
                  paatoskirje p
     )

SELECT *
FROM jyvaskyla_lupa;

-- Keski-Uudenmaan koulutuskuntayhtymä
WITH keskiuudenmaa_lupa AS (
    SELECT id
    FROM lupa
    WHERE jarjestaja_ytunnus = '0213834-5'
      AND asianumero = 'VN/2171/2020'
),
     paatoskirje AS (
         INSERT INTO liite (nimi, polku, luoja, kieli, koko, tyyppi)
             VALUES ('VN_2171_2020 päätöskirje', CONCAT('2020/9/2/keskiuudenmaa/VN_2171_2020- Ammatillisten tutkintojen ja koulutuksen järjestämislupa 1.8.2020_Keuda.PDF'),
                     'oiva', 'fi', 426482, 'paatosKirje') RETURNING id
     ),
     link AS (
         INSERT INTO lupa_liite (lupa_id, liite_id)
             SELECT l.id, p.id
             FROM keskiuudenmaa_lupa l,
                  paatoskirje p
     )

SELECT *
FROM keskiuudenmaa_lupa;

-- Pohjois-Karjalan koulutuskuntayhtymä
WITH pohjoiskarjala_lupa AS (
    SELECT id
    FROM lupa
    WHERE jarjestaja_ytunnus = '0212371-7'
      AND asianumero = 'VN/2288/2020'
),
     paatoskirje AS (
         INSERT INTO liite (nimi, polku, luoja, kieli, koko, tyyppi)
             VALUES ('VN_2288_2020 päätöskirje', CONCAT('2020/9/2/pohjoiskarjala/VN_2288_2020- Ammatillisten tutkintojen ja koulutuksen järjestämisluvan muutos 1.8.2020_pohjois karjala.pdf'),
                     'oiva', 'fi', 365748, 'paatosKirje') RETURNING id
     ),
     link AS (
         INSERT INTO lupa_liite (lupa_id, liite_id)
             SELECT l.id, p.id
             FROM pohjoiskarjala_lupa l,
                  paatoskirje p
     )

SELECT *
FROM pohjoiskarjala_lupa;
