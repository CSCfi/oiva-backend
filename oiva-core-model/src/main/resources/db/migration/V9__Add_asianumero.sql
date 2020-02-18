-- Asianumero is currently presented in 12 characters containing an incrementing
-- number over yearly items. 16 characters should be enough.
ALTER TABLE lupa ADD COLUMN IF NOT EXISTS asianumero VARCHAR(16) DEFAULT NULL;
ALTER TABLE lupahistoria ADD COLUMN IF NOT EXISTS asianumero VARCHAR(16) DEFAULT NULL;