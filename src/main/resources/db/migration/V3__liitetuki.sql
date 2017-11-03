ALTER TABLE oiva.liite ADD tyyppi VARCHAR(255) NOT NULL;
ALTER TABLE oiva.liite ADD kieli VARCHAR(2) NOT NULL;

DROP TABLE IF EXISTS lupa_liite CASCADE;
CREATE TABLE oiva.lupa_liite
(
    id bigserial not null primary key
    ,lupa_id bigint not null
    ,liite_id bigint not null
);

ALTER TABLE oiva.lupa_liite  ADD CONSTRAINT fk_lupa FOREIGN KEY (lupa_id)
  REFERENCES lupa (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE oiva.lupa_liite  ADD CONSTRAINT fk_liite FOREIGN KEY (liite_id)
  REFERENCES liite (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;