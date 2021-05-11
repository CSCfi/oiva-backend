-- Add rajoiteId property for rajoite maarays meta data.
UPDATE maarays
SET meta = jsonb_set(meta, '{rajoiteId}',
                     to_jsonb(substring(text(meta -> 'changeObjects' -> 0 -> 'anchor'), 'rajoitteet_([^.]+)')))
WHERE (parent_id IS NOT NULL OR (koodisto = 'kujalisamaareet' AND maaraystyyppi_id = 2))
  AND meta -> 'changeObjects' -> 0 -> 'anchor' IS NOT NULL
  AND text(meta -> 'changeObjects' -> 0 -> 'anchor') ~ 'rajoitteet_([^.]+)';
