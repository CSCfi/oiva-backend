-- Add koulutustyyppi for muutospyynto
ALTER TABLE muutospyynto ADD COLUMN IF NOT EXISTS koulutustyyppi VARCHAR DEFAULT NULL;
