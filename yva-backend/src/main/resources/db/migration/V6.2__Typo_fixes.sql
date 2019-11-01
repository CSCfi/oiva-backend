-- Fix typos in meta texts
UPDATE maarays SET meta = REPLACE(TEXT(meta), 'huippuurheilussa', 'huippu-urheilussa')::jsonb
WHERE TEXT(meta) LIKE '%huippuurheilussa%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'kompetensomräde och att sprida sadan', 'kompetensområde och att sprida sådan')::jsonb
WHERE TEXT(meta) LIKE '%kompetensomräde och att sprida sadan%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'utbildningsoch kompetensbehov', 'utbildnings- och kompetensbehov')::jsonb
WHERE TEXT(meta) LIKE '%utbildningsoch kompetensbehov%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), '-ja', '- ja')::jsonb
WHERE TEXT(meta) LIKE '%-ja %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'kieli- ja kulttuurkoulutusta.', 'kieli- ja kulttuurikoulutusta.')::jsonb
WHERE TEXT(meta) LIKE '%kieli- ja kulttuurkoulutusta.%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'ordnarfolkhögskolan', 'ordnar folkhögskolan')::jsonb
WHERE TEXT(meta) LIKE '%ordnarfolkhögskolan%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'kouluts', 'koulutus')::jsonb
WHERE TEXT(meta) LIKE '%kouluts%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'sosiaalisesti vahvistava koulutusta', 'sosiaalisesti vahvistava koulutus')::jsonb
WHERE TEXT(meta) LIKE '%sosiaalisesti vahvistava koulutusta%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'maahanmuuttajakoulutustaja perheiden', 'maahanmuuttajakoulutusta ja perheiden')::jsonb
WHERE TEXT(meta) LIKE '%maahanmuuttajakoulutustaja perheiden%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'kansanöpistoaatteeseen', 'kansanopistoaatteeseen')::jsonb
WHERE TEXT(meta) LIKE '%kansanöpistoaatteeseen%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'kulltuurin', 'kulttuurin')::jsonb
WHERE TEXT(meta) LIKE '%kulltuurin%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'persoonnallisuuden', 'persoonallisuuden')::jsonb
WHERE TEXT(meta) LIKE '%persoonnallisuuden%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'med Bibeln som nom', 'med Bibeln som norm')::jsonb
WHERE TEXT(meta) LIKE '%med Bibeln som nom%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ', ja', ' ja')::jsonb
WHERE TEXT(meta) LIKE '%, ja%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'taito- ja taideaineisiin"', 'taito- ja taideaineisiin."')::jsonb
WHERE TEXT(meta) LIKE '%taito- ja taideaineisiin"%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'ihmisenä kasvamaiseen', 'ihmisenä kasvamiseen')::jsonb
WHERE TEXT(meta) LIKE '%ihmisenä kasvamaiseen%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'koulutustahossa', 'koulutusta, jossa')::jsonb
WHERE TEXT(meta) LIKE '%koulutustahossa%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'kansalaisopiston järjestää', 'kansalaisopisto järjestää')::jsonb
WHERE TEXT(meta) LIKE '%kansalaisopiston järjestää%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'Ingä medborgarinstitut anordnar utbildning med betoning pä spräk', 'Ingå medborgarinstitut anordnar utbildning med betoning på språk')::jsonb
WHERE TEXT(meta) LIKE '%Ingä medborgarinstitut anordnar utbildning med betoning pä spräk%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'kansalaisopiston järjestää', 'kansalaisopisto järjestää')::jsonb
WHERE TEXT(meta) LIKE '%kansalaisopiston järjestää%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'bestär', 'består')::jsonb
WHERE TEXT(meta) LIKE '%bestär%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' spräk', ' språk')::jsonb
WHERE TEXT(meta) LIKE '% spräk%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'betoning pä', 'betoning på')::jsonb
WHERE TEXT(meta) LIKE '%betoning pä%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'ämnesomräden', 'ämnesområden')::jsonb
WHERE TEXT(meta) LIKE '%ämnesomräden%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'bildningsnivä', 'bildningsnivå')::jsonb
WHERE TEXT(meta) LIKE '%bildningsnivä%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' tili ', ' till ')::jsonb
WHERE TEXT(meta) LIKE '% tili %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'Kaskikuusen kansalalisopisto', 'Kaskikuusen kansalaisopisto')::jsonb
WHERE TEXT(meta) LIKE '%Kaskikuusen kansalalisopisto%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'osaamsen', 'osaamisen')::jsonb
WHERE TEXT(meta) LIKE '%osaamsen%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'yhteiskuntaaj', 'yhteiskuntaan')::jsonb
WHERE TEXT(meta) LIKE '%yhteiskuntaaj%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' pä ', ' på ')::jsonb
WHERE TEXT(meta) LIKE '% pä %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' mäl ', ' mål ')::jsonb
WHERE TEXT(meta) LIKE '% mäl %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' invänarnas ', ' invånarnas ')::jsonb
WHERE TEXT(meta) LIKE '% invänarnas %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' invänarnas ', ' invånarnas ')::jsonb
WHERE TEXT(meta) LIKE '% invänarnas %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' mängsidig ', ' mångsidig ')::jsonb
WHERE TEXT(meta) LIKE '% mängsidig %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' datateknin ', ' datateknik ')::jsonb
WHERE TEXT(meta) LIKE '% datateknin %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' sä ', ' så ')::jsonb
WHERE TEXT(meta) LIKE '% sä %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' kommuninvänarnas ', ' kommuninvånarnas ')::jsonb
WHERE TEXT(meta) LIKE '% kommuninvänarnas %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' syftartill ', ' syftar till ')::jsonb
WHERE TEXT(meta) LIKE '% syftartill %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' syftartill ', ' syftar till ')::jsonb
WHERE TEXT(meta) LIKE '% syftartill %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' sejkä ', ' sekä ')::jsonb
WHERE TEXT(meta) LIKE '% sejkä %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' järjestjää ', ' järjestää ')::jsonb
WHERE TEXT(meta) LIKE '% järjestjää %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' ki^lija ', ' kieli- ja ')::jsonb
WHERE TEXT(meta) LIKE '% ki^lija %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' kiel ', ' kieliin, ')::jsonb
WHERE TEXT(meta) LIKE '% kiel %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' välmäende ', ' välmående ')::jsonb
WHERE TEXT(meta) LIKE '% välmäende %';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'Ranuan kristillisen kansanopisto koulutus', 'Ranuan kristillisen kansanopiston koulutus')::jsonb
WHERE TEXT(meta) LIKE '%Ranuan kristillisen kansanopisto koulutus%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'taideja ilmaisuaineissa', 'taide- ja ilmaisuaineissa')::jsonb
WHERE TEXT(meta) LIKE '%taideja ilmaisuaineissa%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'sivistysja osaamistarpeisiin', 'sivistys- ja osaamistarpeisiin')::jsonb
WHERE TEXT(meta) LIKE '%sivistysja osaamistarpeisiin%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), 'opetus-, kasvatus-', 'opetus, kasvatus')::jsonb
WHERE TEXT(meta) LIKE '%opetus-, kasvatus-%';

UPDATE maarays SET meta = REPLACE(TEXT(meta), ' ja - osaamista ', ' ja -osaamista ')::jsonb
WHERE TEXT(meta) LIKE '% ja - osaamista %';
