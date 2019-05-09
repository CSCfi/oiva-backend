package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.AsiatyyppiValue;
import fi.minedu.oiva.backend.entity.oiva.Lupa;
import fi.minedu.oiva.backend.entity.LupatilaValue;
import fi.minedu.oiva.backend.entity.oiva.Maarays;
import fi.minedu.oiva.backend.entity.OivaTemplates;
import fi.minedu.oiva.backend.entity.oiva.Liite;
import fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio;
import fi.minedu.oiva.backend.security.OivaPermission;
import fi.minedu.oiva.backend.security.annotations.OivaAccess;
import fi.minedu.oiva.backend.template.extension.MaaraysListFilter;
import fi.minedu.oiva.backend.util.With;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import static fi.minedu.oiva.backend.jooq.Tables.*;
import static fi.minedu.oiva.backend.jooq.tables.Lupa.LUPA;

@Service
public class LupaService {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private MaaraysService maaraysService;

    @Autowired
    private PaatoskierrosService paatoskierrosService;

    @Autowired
    private EsitysmalliService esitysmalliService;

    @Autowired
    private AsiatyyppiService asiatyyppiService;

    @Autowired
    private LupatilaService lupatilaService;

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private AuthService authService;

    protected SelectOnConditionStep<Record> baseLupaSelect() {
        return dsl.select(LUPA.fields()).from(LUPA)
            .leftOuterJoin(LUPATILA).on(LUPATILA.ID.eq(LUPA.LUPATILA_ID));
    }

    protected Optional<Lupa> getById(final Long id) {
        return Optional.ofNullable(null != id ? dsl.select(LUPA.fields()).from(LUPA).where(LUPA.ID.eq(id))
                .fetchOneInto(Lupa.class) : null);
    }

    protected Optional<Condition> baseLupaFilter() {
        final OivaPermission accessPermission = authService.accessPermission();
        final Condition valmisLupaCondition = LUPATILA.TUNNISTE.eq(LupatilaValue.VALMIS);
        if(accessPermission.is(OivaAccess.Type.OnlyPublic)) {
            return Optional.of(valmisLupaCondition);
        } else if(accessPermission.is(OivaAccess.Type.OrganizationAndPublic)) {
            if(accessPermission.oids.isEmpty()) {
                return Optional.of(valmisLupaCondition);
            } else return Optional.of(valmisLupaCondition.or(LUPA.JARJESTAJA_OID.in(accessPermission.oids)));
        } else return Optional.empty();
    }

    public Collection<Lupa> getAllWithJarjestaja(final String... options) {
        final SelectJoinStep<Record> query = getAllQuery(ASIATYYPPI.TUNNISTE.ne(AsiatyyppiValue.PERUUTUS));
        query.leftJoin(ASIATYYPPI).on(ASIATYYPPI.ID.eq(LUPA.ASIATYYPPI_ID));
        return fetch(query, options);
    }

    public Collection<Lupa> getAll(final String... withOptions) {
        return getAll(null, withOptions);
    }

    public Collection<Lupa> getAll(final Condition filter, final String... withOptions) {
        final SelectJoinStep<Record> query = getAllQuery(filter);
        return fetch(query, withOptions);
    }

    public Collection<Lupa> getAllJarjestamisluvat(final String... withOptions) {
        return getAllJarjestamisluvat(null, withOptions);
    }

    public Collection<Lupa> getAllJarjestamisluvat(final Condition filter, final String... withOptions) {
        final SelectJoinStep<Record> query = baseLupaSelect();
        baseLupaFilter().ifPresent(query::where);
        Optional.ofNullable(filter).ifPresent(query::where);
        // filteröidään pois lisäkouluttajat
        query.where(LUPA.JARJESTAJA_YTUNNUS.notIn("0763403-0","0986820-1","0108023-3","0188756-3","0950895-1",
                "0206976-5","0151534-8","0112038-9","0201789-3","0210311-8","1524361-1","1099221-8","0215382-8",
                "1041090-0","0195032-3","0773744-3"));
        return fetch(query, withOptions);
    }

    public Optional<Lupa> getByDiaarinumero(final String diaarinumero, final String... withOptions) {
        return get(baseLupaSelect().where(LUPA.DIAARINUMERO.eq(diaarinumero)), withOptions);
    }

    public Optional<Lupa> getByYtunnus(final String ytunnus, final String... withOptions) {
        return get(baseLupaSelect().where(LUPA.JARJESTAJA_YTUNNUS.eq(ytunnus)
                .and(LUPA.ALKUPVM.le(DSL.currentDate()))
                .and(LUPA.LOPPUPVM.isNull().or(LUPA.LOPPUPVM.ge(DSL.currentDate())))
        ), withOptions);
    }

    public Optional<Lupa> getByUuid(final String uuid, final String... withOptions) {
        return get(baseLupaSelect().where(LUPA.UUID.equal(UUID.fromString(uuid))), withOptions);
    }

    protected Optional<Lupa> get(final SelectConditionStep<Record> query, final String... withOptions) {
        baseLupaFilter().ifPresent(query::and);
        return entity(query.fetchOne(), withOptions);
    }

    protected Optional<Lupa> entity(final Record record, final String... with) {
        if(null != record) {
            final Lupa lupa = record.into(Lupa.class);
            paatoskierrosService.forLupa(lupa).ifPresent(paatoskierros -> {
                esitysmalliService.forPaatoskierros(paatoskierros).ifPresent(paatoskierros::setEsitysmalli);
                lupa.setPaatoskierros(paatoskierros);
            });
            asiatyyppiService.forLupa(lupa).ifPresent(lupa::setAsiatyyppi);
            lupatilaService.forLupa(lupa).ifPresent(lupa::setLupatila);
            return with(Optional.of(lupa), with);
        }
        return Optional.empty();
    }

    protected Optional<Lupa> with(final Optional<Lupa> lupaOpt, final String... with) {
        final List withOptions = null == with ? Collections.emptyList() : Arrays.asList(with).stream().map(String::toLowerCase).collect(Collectors.toList());
        final Function<Class<?>, Boolean> hasOption = targetClass ->
            withOptions.contains(StringUtils.lowerCase(targetClass.getSimpleName())) || withOptions.contains(With.all);

        if(hasOption.apply(Organisaatio.class)) withOrganization(lupaOpt);
        if(hasOption.apply(Maarays.class)) withMaaraykset(lupaOpt);
        if(hasOption.apply(KoodistoKoodi.class)) withKoodisto(lupaOpt);
        return lupaOpt;
    }

    protected void withOrganization(final Optional<Lupa> lupaOpt) {
        lupaOpt.ifPresent(lupa -> organisaatioService.getWithLocation(lupa.getJarjestajaOid()).ifPresent(lupa::setJarjestaja));
    }

    protected void withMaaraykset(final Optional<Lupa> lupaOpt) {
        lupaOpt.ifPresent(lupa -> lupa.setMaaraykset(maaraysService.getByLupa(lupa.getId())));
    }

    protected void withKoodisto(final Optional<Lupa> lupaOpt) {
        lupaOpt.ifPresent(lupa -> {
            if(null != lupa.maaraykset()) lupa.maaraykset().stream().map(Optional::ofNullable).forEach(maaraysService::withKoodisto);
        });
    }

    public boolean hasTutkintoNimenMuutos(final Lupa lupa) {
        return !MaaraysListFilter.apply(lupa.getMaaraykset(), "kohde:tutkinnotjakoulutukset", "koodisto:koulutus",
            "koodiarvo:384201|487203|331101|351407|351703|458103|361201|374115|477103|384501|384202|371113|477102|354102|364902|467905|374111|477101|384112|487102|487103|487202").isEmpty();
    }

    public OivaTemplates.RenderLanguage renderLanguageFor(final Lupa lupa) {
        final BiFunction<Collection<Maarays>, String, Boolean> isOpetuskieliKoodiArvo = (maaraykset, koodiarvo) -> maaraykset.stream().anyMatch(maarays -> maarays.isKoodiArvo(koodiarvo));
        final Collection<Maarays> maaraykset = MaaraysListFilter.apply(lupa.getMaaraykset(), "kohde:opetusjatutkintokieli", "koodisto:oppilaitoksenopetuskieli");
        if(isOpetuskieliKoodiArvo.apply(maaraykset, "1") ||
           isOpetuskieliKoodiArvo.apply(maaraykset, "3") ||
           isOpetuskieliKoodiArvo.apply(maaraykset, "9")) return OivaTemplates.RenderLanguage.fi;
        else if(isOpetuskieliKoodiArvo.apply(maaraykset, "2")) return OivaTemplates.RenderLanguage.sv;
        // else if(isOpetuskieliKoodiArvo.apply(maaraykset, "4")) return OivaTemplates.RenderLanguage.en; // TODO: After english support is added
        // else if(isOpetuskieliKoodiArvo.apply(maaraykset, "5")) return OivaTemplates.RenderLanguage.se; // TODO: After saame support is added
        else return OivaTemplates.RenderLanguage.fi;
    }

    public Collection<Liite> getAttachments(final long lupaId) { // TODO: Add baseLupaFilter here
       return dsl.select(LIITE.POLKU, LIITE.NIMI, LIITE.TYYPPI).from(LIITE, LUPA_LIITE)
        .where((LUPA_LIITE.LIITE_ID.eq((LIITE.ID))).and(LUPA_LIITE.LUPA_ID.eq(lupaId))).fetchInto(Liite.class);
    }

    private SelectJoinStep<Record> getAllQuery(Condition filter) {
        final SelectJoinStep<Record> query = baseLupaSelect();
        baseLupaFilter().ifPresent(query::where);
        Optional.ofNullable(filter).ifPresent(query::where);
        // filteröidään tulevat luvat (ja varmaan kohta vanhatkin)
        query.where(LUPA.ALKUPVM.le(DSL.currentDate()).
                and(LUPA.LOPPUPVM.isNull().or(LUPA.LOPPUPVM.ge(DSL.currentDate()))));
        return query;
    }

    private List<Lupa> fetch(SelectJoinStep<Record> query, String[] withOptions) {
        return query.fetchInto(Lupa.class).stream()
                .map(lupa -> with(Optional.ofNullable(lupa), withOptions))
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
    }
}