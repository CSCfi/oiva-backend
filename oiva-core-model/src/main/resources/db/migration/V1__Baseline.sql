-- sequence to automate diaarinumero
CREATE SEQUENCE IF NOT EXISTS diaarinumero_seq;

-- lupa
CREATE TABLE IF NOT EXISTS lupa
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
CREATE TABLE IF NOT EXISTS asiatyyppi
(
   id bigserial NOT NULL PRIMARY KEY
  ,tunniste varchar(255) NOT NULL
  ,selite jsonb
  ,uuid UUID NOT NULL UNIQUE DEFAULT public.uuid_generate_v1()
);

-- lupatila (LUONNOS, PASSIVOITU, VALMIS)
CREATE TABLE IF NOT EXISTS lupatila
(
   id bigserial NOT NULL PRIMARY KEY
  ,tunniste varchar(255) NOT NULL
  ,selite jsonb
  ,uuid UUID NOT NULL UNIQUE DEFAULT public.uuid_generate_v1()
);

-- maaraystyyppi (OIKEUS, VELVOITE, RAJOITE)
CREATE TABLE IF NOT EXISTS maaraystyyppi
(
   id bigserial NOT NULL PRIMARY KEY
  ,tunniste varchar(255) NOT NULL
  ,selite jsonb
  ,uuid UUID NOT NULL UNIQUE DEFAULT public.uuid_generate_v1()
);

-- paatoskierros
CREATE TABLE IF NOT EXISTS paatoskierros
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
CREATE TABLE IF NOT EXISTS esitysmalli
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
CREATE TABLE IF NOT EXISTS maarays
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
CREATE TABLE IF NOT EXISTS kohde
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
CREATE TABLE IF NOT EXISTS liite (
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


CREATE TABLE IF NOT EXISTS lupa_liite
(
    id bigserial NOT NULL PRIMARY KEY
    ,lupa_id bigint NOT NULL
    ,liite_id bigint NOT NULL
);

-- uusi taulu lupahistorialle, vanha voidaan poistaa
CREATE TABLE IF NOT EXISTS lupahistoria
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
CREATE TABLE IF NOT EXISTS muutospyynto
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
CREATE TABLE IF NOT EXISTS muutos
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
CREATE TABLE IF NOT EXISTS muutosliite (
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
ALTER TABLE lupa DROP CONSTRAINT IF EXISTS fk_paatoskierros CASCADE;
ALTER TABLE lupa ADD CONSTRAINT fk_paatoskierros FOREIGN KEY (paatoskierros_id)
  REFERENCES paatoskierros (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- lupa viittaus lupatila
ALTER TABLE lupa DROP CONSTRAINT IF EXISTS fk_lupatila CASCADE;
ALTER TABLE lupa ADD CONSTRAINT fk_lupatila FOREIGN KEY (lupatila_id)
  REFERENCES lupatila (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- lupa viittaus asiatyyppi
ALTER TABLE lupa DROP CONSTRAINT IF EXISTS fk_asiatyyppi CASCADE;
ALTER TABLE lupa ADD CONSTRAINT fk_asiatyyppi FOREIGN KEY (asiatyyppi_id)
  REFERENCES asiatyyppi (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- päätoskierros viittaus esitysmalli
ALTER TABLE paatoskierros DROP CONSTRAINT IF EXISTS fk_esitysmalli CASCADE;
ALTER TABLE paatoskierros ADD CONSTRAINT fk_esitysmalli FOREIGN KEY (esitysmalli_id)
  REFERENCES esitysmalli(id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- määräys viittaus lupa
ALTER TABLE maarays DROP CONSTRAINT IF EXISTS fk_lupa CASCADE;
ALTER TABLE maarays ADD CONSTRAINT fk_lupa FOREIGN KEY (lupa_id)
  REFERENCES lupa (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- määräys viittaus kohde
ALTER TABLE maarays DROP CONSTRAINT IF EXISTS fk_kohde CASCADE;
ALTER TABLE maarays ADD CONSTRAINT fk_kohde FOREIGN KEY (kohde_id)
  REFERENCES kohde (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- määräys viittaus määräystyyppi
ALTER TABLE maarays DROP CONSTRAINT IF EXISTS fk_maaraystyyppi CASCADE;
ALTER TABLE maarays ADD CONSTRAINT fk_maaraystyyppi FOREIGN KEY (maaraystyyppi_id)
  REFERENCES maaraystyyppi (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE lupa_liite  DROP CONSTRAINT IF EXISTS fk_lupa CASCADE;
ALTER TABLE lupa_liite  ADD CONSTRAINT fk_lupa FOREIGN KEY (lupa_id)
  REFERENCES lupa (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE lupa_liite  DROP CONSTRAINT IF EXISTS fk_liite CASCADE;
ALTER TABLE lupa_liite  ADD CONSTRAINT fk_liite FOREIGN KEY (liite_id)
  REFERENCES liite (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutospyyntö viittaus lupa
ALTER TABLE muutospyynto DROP CONSTRAINT IF EXISTS fk_lupa CASCADE;
ALTER TABLE muutospyynto ADD CONSTRAINT fk_lupa FOREIGN KEY (lupa_id)
  REFERENCES lupa (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutospyyntö viittaus päätöskierros
ALTER TABLE muutospyynto DROP CONSTRAINT IF EXISTS fk_paatoskierros CASCADE;
ALTER TABLE muutospyynto ADD CONSTRAINT fk_paatoskierros FOREIGN KEY (paatoskierros_id)
  REFERENCES paatoskierros (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutos viittaus muutospyyntö
ALTER TABLE muutos DROP CONSTRAINT IF EXISTS fk_muutospyynto CASCADE;
ALTER TABLE muutos ADD CONSTRAINT fk_muutospyynto FOREIGN KEY (muutospyynto_id)
  REFERENCES muutospyynto (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutos viittaus kohde
ALTER TABLE muutos DROP CONSTRAINT IF EXISTS fk_kohde CASCADE;
ALTER TABLE muutos ADD CONSTRAINT fk_kohde FOREIGN KEY (kohde_id)
  REFERENCES kohde (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutosliite viittaus muutospyyntö
ALTER TABLE muutosliite DROP CONSTRAINT IF EXISTS fk_muutospyynto CASCADE;
ALTER TABLE muutosliite ADD CONSTRAINT fk_muutospyynto FOREIGN KEY (muutospyynto_id)
  REFERENCES muutospyynto (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutosliite viittaus muutos
ALTER TABLE muutosliite DROP CONSTRAINT IF EXISTS fk_muutos CASCADE;
ALTER TABLE muutosliite ADD CONSTRAINT fk_muutos FOREIGN KEY (muutos_id)
  REFERENCES muutos (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;
