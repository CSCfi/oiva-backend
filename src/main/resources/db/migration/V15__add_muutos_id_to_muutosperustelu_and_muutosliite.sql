ALTER TABLE muutosperustelu ADD muutos_id BIGINT;

-- muutosperustelu viittaus muutos
ALTER TABLE muutosperustelu ADD CONSTRAINT fk_muutos FOREIGN KEY (muutos_id)
REFERENCES muutos (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE muutosliite ADD muutos_id BIGINT;

-- muutosliite viittaus muutos
ALTER TABLE muutosliite ADD CONSTRAINT fk_muutos FOREIGN KEY (muutos_id)
REFERENCES muutos (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE CASCADE;
