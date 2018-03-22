-- muutospyynto taulu
DROP TABLE IF EXISTS oiva.muutospyynto CASCADE;
CREATE TABLE oiva.muutospyynto
(
    id bigserial not null primary key
    ,lupa_id bigint not null
    ,hakupvm date null
    ,voimassaalkupvm date null
    ,voimassaloppupvm date null
    ,paatoskierros_id bigint not null
    ,muutosperustelu_id bigint not null
	,tila varchar(20) not null
);

-- muutospyyntö viittaus lupa
ALTER TABLE muutospyynto ADD CONSTRAINT fk_lupa FOREIGN KEY (lupa_id)
REFERENCES lupa (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutospyyntö viittaus päätöskierros
ALTER TABLE muutospyynto ADD CONSTRAINT fk_paatoskierros FOREIGN KEY (paatoskierros_id)
REFERENCES paatoskierros (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutos taulu
DROP TABLE IF EXISTS oiva.muutos CASCADE;
CREATE TABLE oiva.muutos
(
    id bigserial not null primary key
    ,muutospyynto_id bigint not null
    ,kohde_id bigint not null
    ,parent_id bigint					 -- muutoksen parent
    ,koodisto varchar(255) null          -- muutokseen liittyvä esim. opintopolun koodisto
    ,koodiarvo text not null             -- koodiston koodiarvo
    ,arvo varchar(255)                   -- muutokseen liittyvä erillinen arvo, esim opiskelijoiden lukumäärä
    ,maaraystyyppi_id bigint not null
    ,meta jsonb                          -- muutokseen liittyvä UI:sta tuleva teksti
    ,luoja text null
    ,luontipvm timestamp not null default current_timestamp
    ,paivittaja text null
    ,paivityspvm timestamp null
    ,maarays_id bigint					 -- mahdollinen suora määräys johon muutos kohdistetaan (muokkaus)
);

-- muutos viittaus muutospyyntö
ALTER TABLE muutos ADD CONSTRAINT fk_muutospyynto FOREIGN KEY (muutospyynto_id)
REFERENCES muutospyynto (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutos viittaus kohde
ALTER TABLE muutos ADD CONSTRAINT fk_kohde FOREIGN KEY (kohde_id)
REFERENCES kohde (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutosperustelu taulu
DROP TABLE IF EXISTS oiva.muutosperustelu CASCADE;
CREATE TABLE oiva.muutosperustelu
(
    id bigserial not null primary key
    ,muutospyynto_id bigint not null
    ,koodisto varchar(255) null          -- muutosperustelun koodisto (Oiva oma)
    ,koodiarvo text not null             -- koodiston koodiarvo
    ,arvo varchar(255)                   -- muutosperustelun liittyvä erillinen arvo (tarvittaessa)
    ,meta jsonb                          -- muutosperustelun metadata (tarvittaessa)
    ,luoja text null
    ,luontipvm timestamp not null default current_timestamp
    ,paivittaja text null
    ,paivityspvm timestamp null
);

-- muutosperustelu viittaus muutospyyntö
ALTER TABLE muutosperustelu ADD CONSTRAINT fk_muutospyynto FOREIGN KEY (muutospyynto_id)
REFERENCES muutospyynto (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

-- muutosliite
DROP TABLE IF EXISTS muutosliite CASCADE;
CREATE TABLE muutosliite (
  id              BIGSERIAL    NOT NULL PRIMARY KEY,
  nimi            VARCHAR(255) NOT NULL,
  polku           VARCHAR(255) NOT NULL,
  tila            BOOLEAN      NOT NULL DEFAULT FALSE,
  luoja           text NOT NULL,
  luontipvm       TIMESTAMP    NOT NULL DEFAULT current_timestamp,
  paivittaja      text NULL,
  paivityspvm     TIMESTAMP    NULL,
  koko            BIGINT       NULL,
  meta            JSONB        NULL,
  muutospyynto_id bigint not null
);

-- muutosliite viittaus muutospyyntö
ALTER TABLE muutosliite ADD CONSTRAINT fk_muutospyynto FOREIGN KEY (muutospyynto_id)
REFERENCES muutospyynto (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;
