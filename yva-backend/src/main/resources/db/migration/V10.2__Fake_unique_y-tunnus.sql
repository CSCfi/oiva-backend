-- CSCKUJA-364 lupas need to have unique y-tunnus but these organisations don't officially have them.
-- 0000000-1 and 0000000-2 are identified as invalid by the y-tunnus registry and should be safe for our purposes.
update lupa set jarjestaja_ytunnus = '0000000-1' where jarjestaja_oid = '1.2.246.562.10.962346066210';
update lupa set jarjestaja_ytunnus = '0000000-2' where jarjestaja_oid = '1.2.246.562.10.21063335612';
