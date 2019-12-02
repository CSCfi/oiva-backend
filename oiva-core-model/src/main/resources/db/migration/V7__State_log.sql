-- generic state change table for e.g. muutospyynto/uusi hakemus
CREATE TABLE IF NOT EXISTS asiatilamuutos (
    id bigserial NOT NULL PRIMARY KEY,
    alkutila varchar(50) NOT NULL,
    lopputila varchar(50) NOT NULL,
    muutosaika timestamp NULL,
    kayttajatunnus text NULL
);

-- link muutospyynto to state change
CREATE TABLE IF NOT EXISTS muutospyynto_asiatilamuutos (
    muutospyynto_id bigint NOT NULL REFERENCES muutospyynto(id),
    asiatilamuutos_id bigint NOT NULL UNIQUE REFERENCES asiatilamuutos(id),
    PRIMARY KEY (muutospyynto_id, asiatilamuutos_id)
);