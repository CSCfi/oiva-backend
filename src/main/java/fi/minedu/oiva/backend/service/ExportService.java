package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.entity.Maarays;
import fi.minedu.oiva.backend.entity.export.KoulutusLupa;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fi.minedu.oiva.backend.jooq.Tables.KOHDE;
import static fi.minedu.oiva.backend.jooq.Tables.LUPA;
import static fi.minedu.oiva.backend.jooq.Tables.MAARAYS;
import static fi.minedu.oiva.backend.util.ControllerUtil.options;

@Service
public class ExportService implements RecordMapping<Lupa> {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private LupaService lupaService;

    public Collection<Lupa> getJarjestysluvat() {
        return lupaService.getAll(options(Maarays.class));
    }

    public Collection<KoulutusLupa> getKoulutusLuvat() {
        final Function<Long, Collection<String>> koulutusKoodiArvot = (lupaId) -> dsl.select(MAARAYS.KOODIARVO).from(LUPA)
            .leftOuterJoin(MAARAYS).on(MAARAYS.LUPA_ID.eq(LUPA.ID))
            .leftOuterJoin(KOHDE).on(KOHDE.ID.eq(MAARAYS.KOHDE_ID))
            .where(LUPA.ID.eq(lupaId))
            .and(KOHDE.TUNNISTE.eq("tutkinnotjakoulutukset"))
            .and(MAARAYS.KOODISTO.eq("koulutus"))
            .fetchInto(String.class);

        return dsl.select(LUPA.ID, LUPA.JARJESTAJA_YTUNNUS, LUPA.JARJESTAJA_OID, LUPA.DIAARINUMERO, LUPA.ALKUPVM, LUPA.LOPPUPVM).from(LUPA)
            .orderBy(LUPA.JARJESTAJA_YTUNNUS, LUPA.ALKUPVM).fetchInto(KoulutusLupa.class).stream().map(koulutusLupa -> {
                koulutusLupa.setKoulutukset(koulutusKoodiArvot.apply(koulutusLupa.getId()));
                return koulutusLupa;
            }).collect(Collectors.toList());
    }
}