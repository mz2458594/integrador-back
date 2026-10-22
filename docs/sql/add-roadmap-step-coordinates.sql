-- Run against the intended PostgreSQL database before deploying coordinate support.
-- Additive and safe to repeat; existing rows retain NULL coordinates.
BEGIN;
ALTER TABLE roadmap_steps ADD COLUMN IF NOT EXISTS x DOUBLE PRECISION;
ALTER TABLE roadmap_steps ADD COLUMN IF NOT EXISTS y DOUBLE PRECISION;
COMMIT;
