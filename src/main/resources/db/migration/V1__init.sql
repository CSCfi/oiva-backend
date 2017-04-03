
-- schema
set search_path = oiva, pg_catalog;
DROP SCHEMA IF EXISTS oiva CASCADE;
CREATE SCHEMA oiva AUTHORIZATION oiva;

-- lupa
DROP TABLE IF EXISTS lupa CASCADE;
CREATE TABLE lupa
(
   id bigserial not null primary key
  ,edellinen_lupa_id bigint null
  ,paatoskierros_id bigint not null
  ,lupatila_id bigint not null
  ,asiatyyppi_id bigint not null
  ,diaarinumero varchar(20) unique not null
  ,jarjestaja_ytunnus varchar(10) not null
  ,jarjestaja_oid varchar(255) not null -- koulutuksen järjestäjän oid
  ,alkupvm date not null
  ,loppupvm date null
  ,paatospvm date null
  ,meta jsonb
  ,maksu boolean
  ,luoja text null
  ,luontipvm timestamp not null default current_timestamp
  ,paivittaja text null
  ,paivityspvm timestamp null
);

-- tyypit ja tilat:

-- asiatyyppi (UUSI, MUUTOS, AMK, LUKIO)
DROP TABLE IF EXISTS asiatyyppi CASCADE;
CREATE TABLE asiatyyppi
(
   id bigserial not null primary key
  ,tunniste varchar(255) not null
  ,selite jsonb
);

-- lupatila (LUONNOS, PASSIVOITU, VALMIS)
DROP TABLE IF EXISTS lupatila CASCADE;
CREATE TABLE lupatila
(
   id bigserial not null primary key
  ,tunniste varchar(255) not null
  ,selite jsonb
);

-- maaraystyyppi (OIKEUS, VELVOITE, RAJOITE)
DROP TABLE IF EXISTS maaraystyyppi CASCADE;
CREATE TABLE maaraystyyppi
(
   id bigserial not null primary key
  ,tunniste varchar(255) not null
  ,selite jsonb
);

-- tekstityyppi (KYSYMYS, VASTAUS, PERUSTELU, MUU MAARAYS)
DROP TABLE IF EXISTS tekstityyppi CASCADE;
CREATE TABLE tekstityyppi
(
   id bigserial not null primary key
  ,tunniste varchar(255) not null
  ,selite jsonb
);

-- paatoskierros
DROP TABLE IF EXISTS paatoskierros CASCADE;
CREATE TABLE paatoskierros
(
   id bigserial not null primary key
  ,alkupvm date not null
  ,loppupvm date null
  ,oletus_paatospvm date null
  ,esitysmalli_id bigint not null
  ,luoja text null
  ,luontipvm timestamp not null default current_timestamp
  ,paivittaja text null
  ,paivityspvm timestamp null
  ,meta jsonb -- nimi, kuvaus, haun ohjetekstit
);

-- esitysmalli
DROP TABLE IF EXISTS esitysmalli CASCADE;
CREATE TABLE esitysmalli
(
   id bigserial not null primary key
  ,templatepath varchar(255) default 'default'
  ,valituspdf varchar(255) null
  ,luoja text null
  ,luontipvm timestamp not null default current_timestamp
  ,paivittaja text null
  ,paivityspvm timestamp null
);

-- määräys
DROP TABLE IF EXISTS maarays CASCADE;
CREATE TABLE maarays
(
   id bigserial not null primary key
  ,lupa_id bigint not null
  ,kohde_id bigint not null
  ,koodisto varchar(255) null -- määräykseen liittyvä esim. opintopolun koodisto
  ,arvo text not null
  ,maaraystyyppi_id bigint not null
  ,tekstityyppi_id bigint null -- kysymys, perustelu, vastaus, muu
  ,selite jsonb -- määräykseen liittyvä teksti
  ,luoja text null
  ,luontipvm timestamp not null default current_timestamp
  ,paivittaja text null
  ,paivityspvm timestamp null
);

-- kohde
DROP TABLE IF EXISTS kohde CASCADE;
CREATE TABLE kohde
(
   id bigserial not null primary key
  ,tunniste varchar(255) null -- kohteen tunniste
  ,meta jsonb -- kohteen metadata (tageilla lisätietoa, muu määräyksillä käyttäjän antama nimi)
  ,luoja text null
  ,luontipvm timestamp not null default current_timestamp
  ,paivittaja text null
  ,paivityspvm timestamp null
);



-- kohde <-> päätöskierros
-- päätöskierroskohtaiset pakolliset kohteet
DROP TABLE IF EXISTS paatoskierros_kohde_link CASCADE;
CREATE TABLE paatoskierros_kohde_link
(
   kohde_id bigint not null
  ,paatoskierros_id bigint not null
  ,pakollisuus boolean null
);


-- muutoshistoria
-- muutoshistoria.tyyppi (MUUTOS, UUSI, VIRHE, TMS)
DROP TABLE IF EXISTS muutoshistoria CASCADE;
CREATE TABLE muutoshistoria
(
   id bigserial not null primary key
  ,maarays_id bigint not null
  ,selite jsonb -- kohteen kuvaus lokalisoituna
  ,tyyppi varchar not null
  ,kayttaja text null
  ,paivays timestamp not null default current_timestamp
);


-- liite
DROP TABLE IF EXISTS liite CASCADE;
CREATE TABLE liite (
  id              BIGSERIAL    NOT NULL PRIMARY KEY,
  nimi            VARCHAR(255) NOT NULL,
  polku           VARCHAR(255) NOT NULL,
  -- if attachment already saved properly, or just in draft state
  tila            BOOLEAN      NOT NULL DEFAULT FALSE,
  -- system level timestamps updated by trigger --
  luoja           text NOT NULL,
  luontipvm       TIMESTAMP    NOT NULL DEFAULT current_timestamp,
  paivittaja      text NULL,
  paivityspvm     TIMESTAMP    NULL,
  --metadata--
  koko            BIGINT       NULL,
  meta            JSONB        NULL
);

-- tiedote
DROP TABLE IF EXISTS tiedote CASCADE;
CREATE TABLE tiedote (
  id bigserial not null primary key,
  otsikko jsonb not null,
  sisalto jsonb not null,
  luoja      VARCHAR(255) NOT NULL,
  luontipvm  TIMESTAMP    NOT NULL DEFAULT current_timestamp,
  paivittaja      VARCHAR(255) NOT NULL,
  paivityspvm  TIMESTAMP    NOT NULL DEFAULT current_timestamp

);

-- fuusio
DROP TABLE IF EXISTS fuusio CASCADE;
CREATE TABLE fuusio
(
   id bigserial not null primary key
  ,lupa_id bigint not null
  ,meta jsonb
  ,luoja text null
  ,luontipvm timestamp not null default now()
  ,paivittaja text null
  ,paivityspvm timestamp null
);


-- viittaukset:

-- lupa viittaus päätöskierros
ALTER TABLE lupa ADD CONSTRAINT fk_paatoskierros FOREIGN KEY (paatoskierros_id)
REFERENCES paatoskierros (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- lupa viittaus lupatila
ALTER TABLE lupa ADD CONSTRAINT fk_lupatila FOREIGN KEY (lupatila_id)
REFERENCES lupatila (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- lupa viittaus asiatyyppi
ALTER TABLE lupa ADD CONSTRAINT fk_asiatyyppi FOREIGN KEY (asiatyyppi_id)
REFERENCES asiatyyppi (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- päätoskierros viittaus esitysmalli
ALTER TABLE paatoskierros ADD CONSTRAINT fk_esitysmalli FOREIGN KEY (esitysmalli_id)
REFERENCES esitysmalli(id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- määräys viittaus lupa
ALTER TABLE maarays ADD CONSTRAINT fk_lupa FOREIGN KEY (lupa_id)
REFERENCES lupa (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- määräys viittaus kohde
ALTER TABLE maarays ADD CONSTRAINT fk_kohde FOREIGN KEY (kohde_id)
REFERENCES kohde (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- määräys viittaus määräystyyppi
ALTER TABLE maarays ADD CONSTRAINT fk_maaraystyyppi FOREIGN KEY (maaraystyyppi_id)
REFERENCES maaraystyyppi (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- määräys viittaus tekstityyppi
ALTER TABLE maarays ADD CONSTRAINT fk_tekstityyppi FOREIGN KEY (tekstityyppi_id)
REFERENCES tekstityyppi (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- päätöskierros_kohde_link viittaus kohde
ALTER TABLE paatoskierros_kohde_link ADD CONSTRAINT fk_kohde FOREIGN KEY (kohde_id)
REFERENCES kohde (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- paatoskierros_kohde_link viittaus päätöskierros
ALTER TABLE paatoskierros_kohde_link ADD CONSTRAINT fk_paatoskierros FOREIGN KEY (paatoskierros_id)
REFERENCES paatoskierros (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- fuusio viittaus lupa
ALTER TABLE fuusio ADD CONSTRAINT fk_fuusio_lupa FOREIGN KEY (lupa_id)
REFERENCES lupa (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutoshistoria viittaus määräys
ALTER TABLE muutoshistoria ADD CONSTRAINT fk_maarays FOREIGN KEY (maarays_id)
REFERENCES maarays (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;


-- sequences:

-- sequence to automate diaarinumero
CREATE SEQUENCE oiva.diaarinumero_seq;
ALTER TABLE lupa ALTER COLUMN diaarinumero SET DEFAULT concat(nextval('oiva.diaarinumero_seq'),'/999/', date_part('year'::text, CURRENT_TIMESTAMP::date))::TEXT;