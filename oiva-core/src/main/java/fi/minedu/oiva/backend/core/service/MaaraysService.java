package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.model.entity.oiva.Kohde;
import fi.minedu.oiva.backend.model.entity.oiva.Maarays;
import fi.minedu.oiva.backend.model.entity.oiva.Maaraystyyppi;
import fi.minedu.oiva.backend.model.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.model.entity.opintopolku.Organisaatio;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fi.minedu.oiva.backend.model.jooq.Tables.KOHDE;
import static fi.minedu.oiva.backend.model.jooq.Tables.LUPA;
import static fi.minedu.oiva.backend.model.jooq.Tables.LUPATILA;
import static fi.minedu.oiva.backend.model.jooq.Tables.MAARAYS;
import static fi.minedu.oiva.backend.model.jooq.Tables.MAARAYSTYYPPI;

@Service
public class MaaraysService extends BaseService {

    private final static Logger logger = LoggerFactory.getLogger(MaaraysService.class);

    private final DSLContext dsl;

    private final KohdeService kohdeService;

    private final MaaraystyyppiService maaraystyyppiService;

    private final OpintopolkuService opintopolkuService;

    private final LupaService lupaService;

    private final OrganisaatioService organisaatioService;

    private final KoodistoService koodistoService;

    @Autowired
    public MaaraysService(DSLContext dsl, KohdeService kohdeService,
                          MaaraystyyppiService maaraystyyppiService,
                          OpintopolkuService opintopolkuService,
                          @Lazy LupaService lupaService,
                          OrganisaatioService organisaatioService,
                          KoodistoService koodistoService) {
        this.dsl = dsl;
        this.kohdeService = kohdeService;
        this.maaraystyyppiService = maaraystyyppiService;
        this.opintopolkuService = opintopolkuService;
        this.lupaService = lupaService;
        this.organisaatioService = organisaatioService;
        this.koodistoService = koodistoService;
    }

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
        if (null == maaraysResults) {
            logger.warn("Cannot build maarays list from NULL");
            return Collections.emptyList();
        }

        final Map<Long, Maarays> maaraykset = maaraysResults.stream().map(record -> record.into(Maarays.class))
                .collect(Collectors.toMap(Maarays::getId, maarays -> maarays));

        final Map<Long, Kohde> kohteet = kohdeService.mapAll();
        final Map<Long, Maaraystyyppi> maaraystyypit = maaraystyyppiService.mapAll();

        maaraykset.values().forEach(maarays -> {
            maarays.setKohde(kohteet.getOrDefault(maarays.getKohdeId(), null));
            maarays.setMaaraystyyppi(maaraystyypit.getOrDefault(maarays.getMaaraystyyppiId(), null));
            with(maarays, with);
            if (maarays.getParentId() != null) {
                Maarays parent = maaraykset.get(maarays.getParentId());
                if (parent != null) {
                    parent.addAliMaarays(maarays);
                }
            }
        });
        return maaraykset.values().stream().filter(Maarays::isYlinMaarays)
                .collect(Collectors.toList());
    }

    protected void with(final Maarays maarays, final String... with) {
        if (withOption(KoodistoKoodi.class, with)) withKoodisto(maarays);
        if (withOption(Organisaatio.class, with)) withOrganization(maarays);
    }

    private void withOrganization(final Maarays maarays) {
        Optional.ofNullable(maarays).ifPresent(m -> organisaatioService.getWithLocation(m.getOrgOid())
                .ifPresent(m::setOrganisaatio));
    }

    protected void withKoodisto(final Maarays maarays) {
        final Function<Maarays, Optional<KoodistoKoodi>> getKoodi = m ->
                Optional.ofNullable(opintopolkuService.getKoodi(m.getKoodisto(), m.getKoodiarvo(),
                        m.getKoodistoversio()));
        Optional.ofNullable(maarays).ifPresent(m -> {
            if (m.hasKoodistoAndKoodiArvo()) {
                getKoodi.apply(m).ifPresent(koodi -> {
                    m.setKoodi(koodi);
                    // only oiva uses koulutus koodisto
                    if (koodi.isKoodisto("koulutus")) {
                        final String koodiArvo = koodi.koodiArvo();
                        final List<String> ammatillinenKoulutustyyppiArvot = koodistoService.getAmmatillinenKoulutustyyppiArvot();
                        opintopolkuService.getKoulutustyyppiKoodiForKoulutus(koodiArvo)
                                .map(Stream::of).orElse(Stream.empty())
                                .flatMap(Collection::stream)
                                // Keep only codes we are interested in
                                .filter(tyyppi -> ammatillinenKoulutustyyppiArvot.contains(tyyppi.getKoodiArvo()))
                                .forEach(m::addYlaKoodi);
                        opintopolkuService.getKoulutusalaKoodiForKoulutus(koodiArvo).ifPresent(m::addYlaKoodi);
                    }
                });
            }
            if (m.hasAliMaarays()) m.getAliMaaraykset().forEach(this::withKoodisto);
        });
    }

    public Collection<Maaraystyyppi> getAllMaaraystyyppi() {
        return dsl.select(MAARAYSTYYPPI.fields()).from(MAARAYSTYYPPI).fetchInto(Maaraystyyppi.class);
    }

    public Optional<Maarays> getByUuid(String maaraysUuid) {
        return Optional.ofNullable(dsl.select(MAARAYS.fields()).from(MAARAYS).where(MAARAYS.UUID.eq(UUID.fromString(maaraysUuid))).fetchOneInto(Maarays.class));
    }
}