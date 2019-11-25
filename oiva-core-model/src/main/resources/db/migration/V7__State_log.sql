-- generic state change table for e.g. muutospyynto/uusi hakemus
CREATE TABLE IF NOT EXISTS tilamuutos (
    id bigserial NOT NULL PRIMARY KEY,
    alkutila varchar(50) NOT NULL,
    lopputila varchar(50) NOT NULL,
    muutosaika timestamp NULL,
    kayttajatunnus text NULL
);

-- link muutospyynto to state change
CREATE TABLE IF NOT EXISTS muutospyynto_tilamuutos (
    muutospyynto_id bigint NOT NULL REFERENCES muutospyynto(id),
    tilamuutos_id bigint NOT NULL REFERENCES tilamuutos(id),
    PRIMARY KEY (muutospyynto_id, tilamuutos_id)
);