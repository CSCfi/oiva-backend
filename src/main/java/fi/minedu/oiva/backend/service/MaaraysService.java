package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Kohde;
import fi.minedu.oiva.backend.entity.Maarays;
import fi.minedu.oiva.backend.entity.Maaraystyyppi;
import fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static fi.minedu.oiva.backend.jooq.Tables.KOHDE;
import static fi.minedu.oiva.backend.jooq.Tables.LUPA;
import static fi.minedu.oiva.backend.jooq.Tables.MAARAYS;
import static fi.minedu.oiva.backend.jooq.Tables.MAARAYSTYYPPI;

@Service
public class MaaraysService implements RecordMapping<Maarays> {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private OpintopolkuService opintopolkuService;

    public Collection<Maarays> getAll() {
        return dsl.select(MAARAYS.fields()).from(MAARAYS).fetchInto(Maarays.class);
    }

    public Collection<Maarays> getByLupa(final Long lupaId) {

        final Map<Long, Maarays> maaraykset = new HashMap<>();
        final Map<Long, Kohde> kohteet = new HashMap<>();
        final Map<Long, Maaraystyyppi> maaraystyypit = new HashMap<>();

        final Result<Record> result = dsl.select().from(LUPA)
                .leftOuterJoin(MAARAYS).on(MAARAYS.LUPA_ID.eq(LUPA.ID))
                .leftOuterJoin(KOHDE).on(KOHDE.ID.eq(MAARAYS.KOHDE_ID))
                .leftOuterJoin(MAARAYSTYYPPI).on(MAARAYSTYYPPI.ID.eq(MAARAYS.MAARAYSTYYPPI_ID))
                .where(LUPA.ID.eq(lupaId))
                .fetch();

        result.forEach(record -> {
            putToMap(maaraykset, record, MAARAYS.fields(), Maarays.class);
            putToMap(kohteet, record, KOHDE.fields(), Kohde.class);
            putToMap(maaraystyypit, record, MAARAYSTYYPPI.fields(), Maaraystyyppi.class);
        });
        maaraykset.values().stream().forEach(maarays -> {
            maarays.setKohde(kohteet.getOrDefault(maarays.getKohdeId(), null));
            maarays.setMaaraystyyppi(maaraystyypit.getOrDefault(maarays.getMaaraystyyppiId(), null));
        });

        final Collection<Maarays> maaraysList = maaraykset.values().stream().filter(Maarays::isYlinMaarays).collect(Collectors.toList());
        maaraysList.forEach(maarays -> maaraykset.values().stream().filter(maarays::isParentOf).forEach(maarays::addAliMaarays));

        return maaraysList;
    }

    protected Maarays withKoodisto(final Maarays maarays) {
        if (maarays.hasKoodistoAndKoodiArvo()) {
            opintopolkuService.getKoodi(maarays).ifPresent(koodi -> {
                maarays.setKoodi(koodi);
                if (koodi.isKoodisto("koulutus")) {
                    final String koodiArvo = koodi.koodiArvo();
                    opintopolkuService.getKoulutustyyppiKoodiForKoulutus(koodiArvo).ifPresent(koulutustyyppiKoodit -> koulutustyyppiKoodit.stream().forEach(maarays::addYlaKoodi));
                    opintopolkuService.getKoulutusalaKoodiForKoulutus(koodiArvo).ifPresent(maarays::addYlaKoodi);
                }
            });
        }
        if(maarays.hasAliMaarays()) maarays.aliMaaraysList().stream().forEach(this::withKoodisto);
        return maarays;
    }
}