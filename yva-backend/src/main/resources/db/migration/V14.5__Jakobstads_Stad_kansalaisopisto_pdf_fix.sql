-- CSCKUJA-407 Fix kansalaisopisto pdf references
UPDATE lupa SET meta = jsonb_set(meta, '{liitetiedosto}', '"Jakobstad_stad_kansalaisopisto.pdf"' ) WHERE id = (select id from lupa where meta ->> 'liitetiedosto' = 'Jakobstad stad.PDFz');
