-- Create attribute yhteistyosopimus
update maarays
    SET meta = jsonb_set(meta, '{yhteistyosopimus}', '{}')
where maarays.koodiarvo = '8' and maarays.koodisto = 'oivamuutoikeudetvelvollisuudetehdotjatehtavat' and maarays.meta->>'yhteistyösopimus' IS NOT NULL;

-- Create attribute yhteistyosopimus->kuvaus with value copied from yhteistyösopimus->fi
UPDATE maarays
SET meta = jsonb_set(meta, '{yhteistyosopimus, kuvaus}', meta->'yhteistyösopimus'->'fi', true)
where maarays.koodiarvo = '8' and maarays.koodisto = 'oivamuutoikeudetvelvollisuudetehdotjatehtavat' and maarays.meta->>'yhteistyösopimus' IS NOT NULL;

--Delete attribute yhteistyösopimus
UPDATE maarays
SET meta = maarays.meta::jsonb - 'yhteistyösopimus'
where maarays.koodiarvo = '8' and maarays.koodisto = 'oivamuutoikeudetvelvollisuudetehdotjatehtavat' and maarays.meta->>'yhteistyösopimus' IS NOT NULL;
