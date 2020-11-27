-- Add koulutustyyppi and oppilaitostyyppi for lupahistoria
ALTER TABLE lupahistoria ADD COLUMN IF NOT EXISTS koulutustyyppi VARCHAR DEFAULT NULL;
ALTER TABLE lupahistoria ADD COLUMN IF NOT EXISTS oppilaitostyyppi VARCHAR DEFAULT NULL;
