CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE oiva.asiatyyppi ADD COLUMN uuid UUID NOT NULL UNIQUE DEFAULT uuid_generate_v1();
ALTER TABLE oiva.esitysmalli ADD COLUMN uuid UUID NOT NULL UNIQUE DEFAULT uuid_generate_v1();
ALTER TABLE oiva.fuusio ADD COLUMN uuid UUID NOT NULL UNIQUE DEFAULT uuid_generate_v1();
ALTER TABLE oiva.kohde ADD COLUMN uuid UUID NOT NULL UNIQUE DEFAULT uuid_generate_v1();
ALTER TABLE oiva.liite ADD COLUMN uuid UUID NOT NULL UNIQUE DEFAULT uuid_generate_v1();
ALTER TABLE oiva.lupa ADD COLUMN uuid UUID NOT NULL UNIQUE DEFAULT uuid_generate_v1();
ALTER TABLE oiva.lupahistoria ADD COLUMN uuid UUID NOT NULL UNIQUE DEFAULT uuid_generate_v1();
ALTER TABLE oiva.lupatila ADD COLUMN uuid UUID NOT NULL UNIQUE DEFAULT uuid_generate_v1();
ALTER TABLE oiva.maarays ADD COLUMN uuid UUID NOT NULL UNIQUE DEFAULT uuid_generate_v1();
ALTER TABLE oiva.maaraystyyppi ADD COLUMN uuid UUID NOT NULL UNIQUE DEFAULT uuid_generate_v1();
ALTER TABLE oiva.muutoshistoria ADD COLUMN uuid UUID NOT NULL UNIQUE DEFAULT uuid_generate_v1();
ALTER TABLE oiva.paatoskierros ADD COLUMN uuid UUID NOT NULL UNIQUE DEFAULT uuid_generate_v1();
ALTER TABLE oiva.tekstityyppi ADD COLUMN uuid UUID NOT NULL UNIQUE DEFAULT uuid_generate_v1();
