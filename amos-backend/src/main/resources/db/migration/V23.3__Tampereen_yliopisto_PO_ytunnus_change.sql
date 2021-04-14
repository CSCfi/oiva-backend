-- Change PO Tampereen Yliopisto to Tampereen korkeakoulusäätiö sr
UPDATE lupa
SET jarjestaja_ytunnus = '2844561-8',
    jarjestaja_oid     = '1.2.246.562.10.23681417467'
WHERE jarjestaja_oid = '1.2.246.562.10.72255864398'
  and jarjestaja_ytunnus = '0155668-4'
  and koulutustyyppi = '1';
