package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.oiva.Kohde;
import fi.minedu.oiva.backend.entity.oiva.Maarays;
import fi.minedu.oiva.backend.entity.oiva.Maaraystyyppi;
import fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.util.With;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fi.minedu.oiva.backend.jooq.Tables.*;

@Service
public class MaaraysService {

    private final static Logger logger = LoggerFactory.getLogger(MaaraysService.class);

    @Autowired
    private DSLContext dsl;

    @Autowired
    private KohdeService kohdeService;

    @Autowired
    private MaaraystyyppiService maaraystyyppiService;

    @Autowired
    private OpintopolkuService opintopolkuService;

    @Autowired
    private LupaService lupaService;

    protected SelectJoinStep baseMaaraysQuery() {
        return dsl.select(MAARAYS.fields()).from(LUPA)
            .leftOuterJoin(MAARAYS).on(MAARAYS.LUPA_ID.eq(LUPA.ID))
            .leftOuterJoin(LUPATILA).on(LUPATILA.ID.eq(LUPA.LUPATILA_ID));
    }

    protected Optional<Maarays> getById(final Long id) {
        return Optional.ofNullable(null != id ? dsl.select(MAARAYS.fields()).from(MAARAYS).where(MAARAYS.ID.eq(id)).fetchOneInto(Maarays.class) : null);
    }

    public Collection<Maarays> getByLupa(final Long lupaId, final String... with) {
        final SelectConditionStep<Record> query = baseMaaraysQuery().where(LUPA.ID.eq(lupaId));
        lupaService.baseLupaFilter().ifPresent(query::and);
        return toMaaraysList(query.fetch(), with);
    }

    public Collection<Maarays> getByLupaAndKohde(final Long lupaId, final String kohdeTunniste, final String... with) {
        final SelectConditionStep<Record> query = baseMaaraysQuery()
            .leftOuterJoin(KOHDE).on(KOHDE.ID.eq(MAARAYS.KOHDE_ID))
            .where(KOHDE.TUNNISTE.eq(kohdeTunniste))
            .and(LUPA.ID.eq(lupaId));
        lupaService.baseLupaFilter().ifPresent(query::and);
        return toMaaraysList(query.fetch(), with);
    }

    protected Collection<Maarays> toMaaraysList(final Result<Record> maaraysResults, final String... with) {
        if(null == maaraysResults) {
            logger.warn("Cannot build maarays list from NULL");
            return Collections.emptyList();
        }

        final Map<Long, Maarays> maaraykset = maaraysResults.stream().map(record -> record.into(Maarays.class))
            .collect(Collectors.toMap(Maarays::getId, maarays -> maarays));

        final Map<Long, Kohde> kohteet = kohdeService.mapAll();
        final Map<Long, Maaraystyyppi> maaraystyypit = maaraystyyppiService.mapAll();

        maaraykset.values().stream().forEach(maarays -> {
            maarays.setKohde(kohteet.getOrDefault(maarays.getKohdeId(), null));
            maarays.setMaaraystyyppi(maaraystyypit.getOrDefault(maarays.getMaaraystyyppiId(), null));
        });

        final Collection<Maarays> maaraysList = maaraykset.values().stream().filter(Maarays::isYlinMaarays).collect(Collectors.toList());
        maaraysList.forEach(maarays -> {
            maaraykset.values().stream().filter(maarays::isParentOf).forEach(maarays::addAliMaarays);
            with(Optional.of(maarays), with);
        });
        return maaraysList;
    }

    protected Optional<Maarays> with(final Optional<Maarays> maaraysOpt, final String... with) {
        if(withOption(KoodistoKoodi.class, with)) withKoodisto(maaraysOpt);
        return maaraysOpt;
    }

    protected Optional<Maarays> withKoodisto(final Optional<Maarays> maaraysOpt) {
        maaraysOpt.ifPresent(maarays -> {
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
            if(maarays.hasAliMaarays()) maarays.getAliMaaraykset().stream().map(Optional::ofNullable).forEach(this::withKoodisto);
        });
        return maaraysOpt;
    }

    protected boolean withOption(final Class<?> clazz, final String... with) {
        final List withOptions = null == with ? Collections.emptyList() : Arrays.asList(with).stream().map(String::toLowerCase).collect(Collectors.toList());
        final Function<Class<?>, Boolean> hasOption = targetClass -> withOptions.contains(StringUtils.lowerCase(targetClass.getSimpleName())) || withOptions.contains(With.all);
        return hasOption.apply(clazz);
    }

    public Collection<Maaraystyyppi> getAllMaaraystyyppi() {
        return dsl.select(MAARAYSTYYPPI.fields()).from(MAARAYSTYYPPI).fetchInto(Maaraystyyppi.class);
    }

}