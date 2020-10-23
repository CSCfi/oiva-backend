-- Add koulutustyyppi for kohde
ALTER TABLE kohde ADD COLUMN IF NOT EXISTS koulutustyyppi VARCHAR DEFAULT NULL;
