-- READ ONLY: run against the same schema used by the deployed API.
SELECT table_name, column_name, data_type, character_maximum_length, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = current_schema() AND table_name IN ('roadmap', 'roadmap_steps')
ORDER BY table_name, ordinal_position;

SELECT conrelid::regclass AS table_name, conname, contype, pg_get_constraintdef(oid) AS definition
FROM pg_constraint WHERE conrelid IN ('roadmap'::regclass, 'roadmap_steps'::regclass)
ORDER BY conrelid, conname;

SELECT 'roadmap' AS table_name, max(id) AS largest_id, pg_get_serial_sequence('roadmap', 'id') AS sequence_name FROM roadmap
UNION ALL
SELECT 'roadmap_steps', max(id), pg_get_serial_sequence('roadmap_steps', 'id') FROM roadmap_steps;

SELECT schemaname, sequencename, last_value FROM pg_sequences
WHERE schemaname = current_schema() AND sequencename IN (
  SELECT split_part(pg_get_serial_sequence('roadmap', 'id'), '.', 2)
  UNION ALL SELECT split_part(pg_get_serial_sequence('roadmap_steps', 'id'), '.', 2)
);
