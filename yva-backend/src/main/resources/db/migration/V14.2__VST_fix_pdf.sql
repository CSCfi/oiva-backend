-- CSCKUJA-394 Fix a single case with wrong file reference. Other cases fixes in kuja-template with file renames or adds.
UPDATE lupa SET meta = jsonb_set(meta, '{liitetiedosto}', '"Suomen_teologinen_opisto_ry.pdf"' ) WHERE id = (select id from lupa where meta ->> 'liitetiedosto' = 'Suomen teologinen opisto ry, nimi muutettu 15.03.2016.PDF');
