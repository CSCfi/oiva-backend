DROP TABLE IF EXISTS muutosliite CASCADE;

CREATE TABLE IF NOT EXISTS muutos_liite
(
  muutos_id bigint NOT NULL REFERENCES muutos(id) ON UPDATE CASCADE ON DELETE CASCADE,
  liite_id bigint NOT NULL REFERENCES liite(id) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT muutos_liite_pkey PRIMARY KEY (muutos_id, liite_id)
);

CREATE TABLE IF NOT EXISTS muutospyynto_liite
(
  muutospyynto_id bigint NOT NULL REFERENCES muutospyynto(id) ON UPDATE CASCADE ON DELETE CASCADE,
  liite_id bigint NOT NULL REFERENCES liite(id) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT muutospyynto_liite_pkey PRIMARY KEY (muutospyynto_id, liite_id)
);