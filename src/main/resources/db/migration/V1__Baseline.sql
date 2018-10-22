
-- schema
set search_path = oiva, pg_catalog;
DROP SCHEMA IF EXISTS oiva CASCADE;
CREATE SCHEMA oiva AUTHORIZATION oiva;

-- uuid
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- sequence to automate diaarinumero
CREATE SEQUENCE diaarinumero_seq;

-- lupa
DROP TABLE IF EXISTS lupa CASCADE;
CREATE TABLE lupa
(
   id bigserial NOT NULL PRIMARY KEY
  ,edellinen_lupa_id bigint NULL
  ,paatoskierros_id bigint NOT NULL
  ,lupatila_id bigint NOT NULL
  ,asiatyyppi_id bigint NOT NULL
  ,diaarinumero varchar(20) unique NOT NULL DEFAULT concat(nextval('diaarinumero_seq'),'/999/', date_part('year'::text, CURRENT_TIMESTAMP::date))::TEXT
  ,jarjestaja_ytunnus varchar(10) NOT NULL
  ,jarjestaja_oid varchar(255) NOT NULL -- koulutuksen järjestäjän oid
  ,alkupvm date NOT NULL
  ,loppupvm date NULL
  ,paatospvm date NULL
  ,meta jsonb
  ,maksu boolean
  ,luoja text NULL
  ,luontipvm timestamp NOT NULL DEFAULT current_timestamp
  ,paivittaja text NULL
  ,paivityspvm timestamp NULL
  ,uuid UUID NOT NULL UNIQUE DEFAULT public.uuid_generate_v1()
);

-- tyypit ja tilat:

-- asiatyyppi (UUSI, MUUTOS, AMK, LUKIO)
DROP TABLE IF EXISTS asiatyyppi CASCADE;
CREATE TABLE asiatyyppi
(
   id bigserial NOT NULL PRIMARY KEY
  ,tunniste varchar(255) NOT NULL
  ,selite jsonb
  ,uuid UUID NOT NULL UNIQUE DEFAULT public.uuid_generate_v1()
);

-- lupatila (LUONNOS, PASSIVOITU, VALMIS)
DROP TABLE IF EXISTS lupatila CASCADE;
CREATE TABLE lupatila
(
   id bigserial NOT NULL PRIMARY KEY
  ,tunniste varchar(255) NOT NULL
  ,selite jsonb
  ,uuid UUID NOT NULL UNIQUE DEFAULT public.uuid_generate_v1()
);

-- maaraystyyppi (OIKEUS, VELVOITE, RAJOITE)
DROP TABLE IF EXISTS maaraystyyppi CASCADE;
CREATE TABLE maaraystyyppi
(
   id bigserial NOT NULL PRIMARY KEY
  ,tunniste varchar(255) NOT NULL
  ,selite jsonb
  ,uuid UUID NOT NULL UNIQUE DEFAULT public.uuid_generate_v1()
);

-- paatoskierros
DROP TABLE IF EXISTS paatoskierros CASCADE;
CREATE TABLE paatoskierros
(
   id bigserial NOT NULL PRIMARY KEY
  ,alkupvm date NOT NULL
  ,loppupvm date NULL
  ,oletus_paatospvm date NULL
  ,esitysmalli_id bigint NOT NULL
  ,luoja text NULL
  ,luontipvm timestamp NOT NULL DEFAULT current_timestamp
  ,paivittaja text NULL
  ,paivityspvm timestamp NULL
  ,meta jsonb -- nimi, kuvaus, haun ohjetekstit
  ,uuid UUID NOT NULL UNIQUE DEFAULT public.uuid_generate_v1()
);

-- esitysmalli
DROP TABLE IF EXISTS esitysmalli CASCADE;
CREATE TABLE esitysmalli
(
   id bigserial NOT NULL PRIMARY KEY
  ,templatepath varchar(255) DEFAULT 'default'
  ,valituspdf varchar(255) NULL
  ,luoja text NULL
  ,luontipvm timestamp NOT NULL DEFAULT current_timestamp
  ,paivittaja text NULL
  ,paivityspvm timestamp NULL
  ,uuid UUID NOT NULL UNIQUE DEFAULT public.uuid_generate_v1()
);

-- määräys
DROP TABLE IF EXISTS maarays CASCADE;
CREATE TABLE maarays
(
   id bigserial NOT NULL PRIMARY KEY
  ,parent_id bigint
  ,lupa_id bigint NOT NULL
  ,kohde_id bigint NOT NULL
  ,koodisto varchar(255) NULL -- määräykseen liittyvä esim. opintopolun koodisto
  ,koodiarvo text NOT NULL -- koodiston koodiarvo
  ,arvo varchar(255) -- määräykseen liittyvä erillinen arvo, esim opiskelijoiden lukumäärä
  ,maaraystyyppi_id bigint NOT NULL
  ,meta jsonb -- määräykseen liittyvä UI:sta tuleva teksti
  ,luoja text NULL
  ,luontipvm timestamp NOT NULL DEFAULT current_timestamp
  ,paivittaja text NULL
  ,paivityspvm timestamp NULL
  ,koodistoversio int
  ,uuid UUID NOT NULL UNIQUE DEFAULT public.uuid_generate_v1()
);

-- kohde
DROP TABLE IF EXISTS kohde CASCADE;
CREATE TABLE kohde
(
   id bigserial NOT NULL PRIMARY KEY
  ,tunniste varchar(255) NULL -- kohteen tunniste
  ,meta jsonb -- kohteen metadata (tageilla lisätietoa, muu määräyksillä käyttäjän antama nimi)
  ,luoja text NULL
  ,luontipvm timestamp NOT NULL DEFAULT current_timestamp
  ,paivittaja text NULL
  ,paivityspvm timestamp NULL
  ,uuid UUID NOT NULL UNIQUE DEFAULT public.uuid_generate_v1()
);

-- liite
DROP TABLE IF EXISTS liite CASCADE;
CREATE TABLE liite (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  nimi VARCHAR(255) NOT NULL,
  polku VARCHAR(255) NOT NULL,
  -- if attachment already saved properly, or just in draft state
  tila BOOLEAN NOT NULL DEFAULT FALSE,
  -- system level timestamps updated by trigger --
  luoja text NOT NULL,
  luontipvm TIMESTAMP NOT NULL DEFAULT current_timestamp,
  paivittaja text NULL,
  paivityspvm TIMESTAMP NULL,
  --metadata--
  koko BIGINT NULL,
  meta JSONB NULL,
  tyyppi VARCHAR(255) NOT NULL,
  kieli VARCHAR(2) NOT NULL,
  uuid UUID NOT NULL UNIQUE DEFAULT public.uuid_generate_v1()
);


DROP TABLE IF EXISTS lupa_liite CASCADE;
CREATE TABLE lupa_liite
(
    id bigserial NOT NULL PRIMARY KEY
    ,lupa_id bigint NOT NULL
    ,liite_id bigint NOT NULL
);

-- uusi taulu lupahistorialle, vanha voidaan poistaa
DROP TABLE IF EXISTS lupahistoria CASCADE;
CREATE TABLE lupahistoria
(
    id bigserial NOT NULL PRIMARY KEY
    ,diaarinumero varchar(20) NOT NULL
    ,ytunnus varchar(10) NOT NULL
    ,oid varchar(255) NOT NULL
    ,maakunta varchar(100) NOT NULL
    ,tila varchar(100) NOT NULL
    ,voimassaoloalkupvm date NOT NULL
    ,voimassaololoppupvm date NOT NULL
    ,paatospvm date NOT NULL
    ,filename varchar(255) NOT NULL
    ,uuid UUID NOT NULL UNIQUE DEFAULT public.uuid_generate_v1()
);


-- muutospyynto taulu
DROP TABLE IF EXISTS muutospyynto CASCADE;
CREATE TABLE muutospyynto
(
    id bigserial NOT NULL PRIMARY KEY
    ,lupa_id bigint NOT NULL
    ,hakupvm date NULL
    ,voimassaalkupvm date NULL
    ,voimassaloppupvm date NULL
    ,paatoskierros_id bigint NOT NULL
    ,tila varchar(20) NOT NULL
    ,jarjestaja_ytunnus varchar(10) NOT NULL
    ,luoja text NULL
    ,luontipvm timestamp NOT NULL DEFAULT current_timestamp
    ,paivittaja text NULL
    ,paivityspvm timestamp NULL
    ,uuid UUID NOT NULL UNIQUE DEFAULT public.uuid_generate_v1()
    ,meta jsonb
);


-- muutos taulu
DROP TABLE IF EXISTS muutos CASCADE;
CREATE TABLE muutos
(
    id bigserial NOT NULL PRIMARY KEY
    ,muutospyynto_id bigint NOT NULL
    ,kohde_id bigint NOT NULL
    ,parent_id bigint -- muutoksen parent
    ,koodisto varchar(255) NULL -- muutokseen liittyvä esim. opintopolun koodisto
    ,koodiarvo text NOT NULL -- koodiston koodiarvo
    ,arvo varchar(255) -- muutokseen liittyvä erillinen arvo, esim opiskelijoiden lukumäärä
    ,maaraystyyppi_id bigint NOT NULL
    ,meta jsonb -- muutokseen liittyvä UI:sta tuleva teksti
    ,luoja text NULL
    ,luontipvm timestamp NOT NULL DEFAULT current_timestamp
    ,paivittaja text NULL
    ,paivityspvm timestamp NULL
    ,maarays_id bigint -- mahdollinen suora määräys johon muutos kohdistetaan (muokkaus)
    ,tila VARCHAR(10) NULL
    ,uuid UUID NOT NULL UNIQUE DEFAULT public.uuid_generate_v1()
    ,paatos_tila varchar(20)
    ,muutosperusteluKoodiarvo text
);

-- muutosliite
DROP TABLE IF EXISTS muutosliite CASCADE;
CREATE TABLE muutosliite (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  nimi VARCHAR(255) NOT NULL,
  polku VARCHAR(255) NOT NULL,
  tila BOOLEAN NOT NULL DEFAULT FALSE,
  luoja text NOT NULL,
  luontipvm TIMESTAMP NOT NULL DEFAULT current_timestamp,
  paivittaja text NULL,
  paivityspvm TIMESTAMP NULL,
  koko BIGINT NULL,
  meta JSONB NULL,
  muutospyynto_id bigint NOT NULL,
  uuid UUID NOT NULL UNIQUE DEFAULT public.uuid_generate_v1(),
  muutos_id BIGINT
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

ALTER TABLE lupa_liite  ADD CONSTRAINT fk_lupa FOREIGN KEY (lupa_id)
  REFERENCES lupa (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE lupa_liite  ADD CONSTRAINT fk_liite FOREIGN KEY (liite_id)
  REFERENCES liite (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutospyyntö viittaus lupa
ALTER TABLE muutospyynto ADD CONSTRAINT fk_lupa FOREIGN KEY (lupa_id)
  REFERENCES lupa (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutospyyntö viittaus päätöskierros
ALTER TABLE muutospyynto ADD CONSTRAINT fk_paatoskierros FOREIGN KEY (paatoskierros_id)
  REFERENCES paatoskierros (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutos viittaus muutospyyntö
ALTER TABLE muutos ADD CONSTRAINT fk_muutospyynto FOREIGN KEY (muutospyynto_id)
  REFERENCES muutospyynto (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutos viittaus kohde
ALTER TABLE muutos ADD CONSTRAINT fk_kohde FOREIGN KEY (kohde_id)
  REFERENCES kohde (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutosliite viittaus muutospyyntö
ALTER TABLE muutosliite ADD CONSTRAINT fk_muutospyynto FOREIGN KEY (muutospyynto_id)
  REFERENCES muutospyynto (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutosliite viittaus muutos
ALTER TABLE muutosliite ADD CONSTRAINT fk_muutos FOREIGN KEY (muutos_id)
  REFERENCES muutos (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;
