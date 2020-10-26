package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.core.extension.MaaraysListFilter;
import fi.minedu.oiva.backend.core.security.OivaPermission;
import fi.minedu.oiva.backend.model.entity.AsiatyyppiValue;
import fi.minedu.oiva.backend.model.entity.LupatilaValue;
import fi.minedu.oiva.backend.model.entity.OivaTemplates;
import fi.minedu.oiva.backend.model.entity.oiva.Liite;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.oiva.Maarays;
import fi.minedu.oiva.backend.model.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.model.entity.opintopolku.Organisaatio;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fi.minedu.oiva.backend.model.jooq.Tables.ASIATYYPPI;
import static fi.minedu.oiva.backend.model.jooq.Tables.LIITE;
import static fi.minedu.oiva.backend.model.jooq.Tables.LUPA;
import static fi.minedu.oiva.backend.model.jooq.Tables.LUPATILA;
import static fi.minedu.oiva.backend.model.jooq.Tables.LUPA_LIITE;
import static fi.minedu.oiva.backend.model.jooq.Tables.MAARAYS;

@Service
public class LupaService extends BaseService {

    private final static Logger logger = LoggerFactory.getLogger(LupaService.class);

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

    @Autowired
    private KoodistoService koodistoservice;

    @Autowired
    private OpintopolkuService opintopolkuService;

    protected SelectOnConditionStep<Record> baseLupaSelect() {
        return dsl.select(LUPA.fields()).from(LUPA)
                .leftOuterJoin(LUPATILA).on(LUPATILA.ID.eq(LUPA.LUPATILA_ID));
    }

    public Optional<Lupa> getById(final Long id) {
        return Optional.ofNullable(null != id ? dsl.select(LUPA.fields()).from(LUPA).where(LUPA.ID.eq(id))
                .fetchOneInto(Lupa.class) : null);
    }

    public Optional<Lupa> getById(final Long id, String... options) {
        final SelectConditionStep<Record> query = dsl.select(LUPA.fields()).from(LUPA).where(LUPA.ID.eq(id));
        return entity(query.fetchOne(), options);
    }

    protected Optional<Condition> baseLupaFilter() {
        final OivaPermission accessPermission = authService.accessPermission();
        final Condition valmisLupaCondition = LUPATILA.TUNNISTE.eq(LupatilaValue.VALMIS);
        if (accessPermission.is(OivaAccess.Type.OnlyPublic)) {
            return Optional.of(valmisLupaCondition);
        } else if (accessPermission.is(OivaAccess.Type.OrganizationAndPublic)) {
            if (accessPermission.oids.isEmpty()) {
                return Optional.of(valmisLupaCondition);
            } else return Optional.of(valmisLupaCondition.or(LUPA.JARJESTAJA_OID.in(accessPermission.oids)));
        } else return Optional.empty();
    }

    public Collection<Lupa> getAllWithJarjestaja(final String koulutustyyppi,
                                                 final String oppilaitostyyppi,
                                                 final String... options) {
        final SelectJoinStep<Record> query = getAllQuery(ASIATYYPPI.TUNNISTE.ne(AsiatyyppiValue.PERUUTUS));
        Optional.ofNullable(koulutustyyppi).ifPresent(t -> {
            query.where(LUPA.KOULUTUSTYYPPI.eq(t));
            if (koulutustyyppi.equals("3")) {
                // Fetch oppilaitos maarays for VST
                query.getSelect().add(MAARAYS.KOODISTO);
                query.getSelect().add(MAARAYS.ORG_OID);
                query.leftJoin(MAARAYS).on(LUPA.ID.eq(MAARAYS.LUPA_ID));
                query.where(MAARAYS.KOODISTO.eq("oppilaitos"));
            }
        });
        Optional.ofNullable(oppilaitostyyppi).ifPresent(t -> query.where(LUPA.OPPILAITOSTYYPPI.eq(t)));
        query.leftJoin(ASIATYYPPI).on(ASIATYYPPI.ID.eq(LUPA.ASIATYYPPI_ID));
        return query.fetchGroups(r -> r.into(LUPA.fields()).into(Lupa.class), r -> r.into(MAARAYS.KOODISTO, MAARAYS.ORG_OID).into(Maarays.class))
                .entrySet()
                .stream()
                .map(e -> {
                    Lupa l = e.getKey();
                    l.setMaaraykset(e.getValue().stream()
                            .filter(m -> m.getOrgOid() != null)
                            .peek(m -> m.setOrganisaatio(organisaatioService.getWithLocation(m.getOrgOid()).orElse(null)))
                            .collect(Collectors.toList()));
                    return with(Optional.of(l), options);
                }).filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toList());
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
        query.where(LUPA.JARJESTAJA_YTUNNUS.notIn("0763403-0", "0986820-1", "0108023-3", "0188756-3", "0950895-1",
                "0206976-5", "0151534-8", "0112038-9", "0201789-3", "0210311-8", "1524361-1", "1099221-8", "0215382-8",
                "1041090-0", "0195032-3", "0773744-3"));
        return fetch(query, withOptions);
    }

    public Optional<Lupa> getLatestByYtunnus(String ytunnus, boolean useKoodistoVersions, String[] withOptions) {
        final SelectConditionStep<Record> query = baseLupaSelect().where(LUPA.JARJESTAJA_YTUNNUS.eq(ytunnus));
        query.orderBy(LUPA.LUONTIPVM.desc()).limit(1);
        return get(query, useKoodistoVersions, withOptions);
    }

    public Collection<Lupa> getFutureByYtunnus(String ytunnus, String koulutustyyppi, String[] options) {
        final SelectOnConditionStep<Record> query = baseLupaSelect();
        baseLupaFilter().ifPresent(query::where);
        query.where(LUPA.JARJESTAJA_YTUNNUS.eq(ytunnus)
                .and(LUPA.ALKUPVM.ge(DSL.currentDate()))
                .and(koulutustyyppi == null ? LUPA.KOULUTUSTYYPPI.isNull() : LUPA.KOULUTUSTYYPPI.eq(koulutustyyppi)));
        return fetch(query, options);
    }

    public Optional<Lupa> getByYtunnus(final String ytunnus, final String[] withOptions) {
        return getByYtunnus(ytunnus, null, null, true, withOptions );
    }

    public Optional<Lupa> getByYtunnus(final String ytunnus, final Boolean useKoodistoVersions, final String[] withOptions) {
        return getByYtunnus(ytunnus, null, null, useKoodistoVersions, withOptions);
    }

    public Optional<Lupa> getByYtunnus(final String ytunnus, final String koulutustyyppi, final String oppilaitostyyppi,
                                       final Boolean useKoodistoVersions, final String[] withOptions) {
        final SelectConditionStep<Record> query = baseLupaSelect().where(LUPA.JARJESTAJA_YTUNNUS.eq(ytunnus)
                .and(LUPA.ALKUPVM.le(DSL.currentDate()))
                .and(LUPA.LOPPUPVM.isNull().or(LUPA.LOPPUPVM.ge(DSL.currentDate()))));
        Optional.ofNullable(koulutustyyppi).ifPresent(tyyppi -> query.and(LUPA.KOULUTUSTYYPPI.eq(tyyppi)));
        Optional.ofNullable(oppilaitostyyppi).ifPresent(tyyppi -> query.and(LUPA.OPPILAITOSTYYPPI.eq(tyyppi)));
        return get(query, useKoodistoVersions, withOptions);
    }

    public Optional<Lupa> getByUuid(final String uuid, final String... withOptions) {
        return Optional.ofNullable(uuid).map(UUID::fromString)
                .flatMap(u -> get(baseLupaSelect().where(LUPA.UUID.equal(u)), withOptions));
    }

    protected Optional<Lupa> get(final SelectConditionStep<Record> query, final String... withOptions) {
        baseLupaFilter().ifPresent(query::and);
        return entity(query.fetchOne(), true, withOptions);
    }

    protected Optional<Lupa> get(final SelectConditionStep<Record> query, final Boolean useKoodistoVersions, final String... withOptions) {
        baseLupaFilter().ifPresent(query::and);
        return entity(query.fetchOne(), useKoodistoVersions, withOptions);
    }

    protected Optional<Lupa> entity(final Record record, final String... with) {
        return entity(record, true, with);
    }

    protected Optional<Lupa> entity(final Record record, final Boolean useKoodistoVersions, final String... with) {
        if (null != record) {
            final Lupa lupa = record.into(Lupa.class);
            paatoskierrosService.forLupa(lupa).ifPresent(paatoskierros -> {
                esitysmalliService.forPaatoskierros(paatoskierros).ifPresent(paatoskierros::setEsitysmalli);
                lupa.setPaatoskierros(paatoskierros);
            });
            asiatyyppiService.forLupa(lupa).ifPresent(lupa::setAsiatyyppi);
            lupatilaService.forLupa(lupa).ifPresent(lupa::setLupatila);
            return with(Optional.of(lupa), useKoodistoVersions, with);
        }
        return Optional.empty();
    }

    protected Optional<Lupa> with(final Optional<Lupa> lupaOpt, final String... with) {
        return with(lupaOpt, true, with);
    }

    protected Optional<Lupa> with(final Optional<Lupa> lupaOpt, final Boolean useKoodistoVersions, final String... with) {
        if (withOption(Organisaatio.class, with)) withOrganization(lupaOpt);
        if (withOption(Maarays.class, with)) withMaaraykset(lupaOpt, with);
        if (withOption(KoodistoKoodi.class, with)) withKoodisto(lupaOpt, useKoodistoVersions);
        if (withOption(Liite.class, with)) withLiitteet(lupaOpt);
        return lupaOpt;
    }

    protected void withOrganization(final Optional<Lupa> lupaOpt) {
        lupaOpt.ifPresent(lupa -> organisaatioService.getWithLocation(lupa.getJarjestajaOid()).ifPresent(lupa::setJarjestaja));
    }

    protected void withMaaraykset(final Optional<Lupa> lupaOpt, String... with) {
        lupaOpt.ifPresent(lupa -> lupa.setMaaraykset(maaraysService.getByLupa(lupa.getId(), with)));
    }

    protected void withKoodisto(final Optional<Lupa> lupaOpt, final Boolean useKoodistoVersions) {
        lupaOpt.ifPresent(lupa -> {
            if (null != lupa.maaraykset()) lupa.maaraykset().forEach(m -> maaraysService.withKoodisto(m, useKoodistoVersions));
        });
    }

    protected void withLiitteet(final Optional<Lupa> lupaOpt) {
        lupaOpt.ifPresent(lupa -> lupa.setLiitteet(getAttachments(lupaOpt.get().getId())));
    }

    public boolean hasTutkintoNimenMuutos(final Lupa lupa) {
        return !MaaraysListFilter.apply(lupa.getMaaraykset(), "kohde:tutkinnotjakoulutukset", "koodisto:koulutus",
                "koodiarvo:384201|487203|331101|351407|351703|458103|361201|374115|477103|384501|384202|371113|477102|354102|364902|467905|374111|477101|384112|487102|487103|487202").isEmpty();
    }

    public OivaTemplates.RenderLanguage renderLanguageFor(final Lupa lupa) {
        final BiFunction<Collection<Maarays>, String, Boolean> isOpetuskieliKoodiArvo = (maaraykset, koodiarvo) -> maaraykset.stream().anyMatch(maarays -> maarays.isKoodiArvo(koodiarvo));
        final Collection<Maarays> maaraykset = MaaraysListFilter.apply(lupa.getMaaraykset(), "kohde:opetusjatutkintokieli", "koodisto:oppilaitoksenopetuskieli");
        if (isOpetuskieliKoodiArvo.apply(maaraykset, "1") ||
                isOpetuskieliKoodiArvo.apply(maaraykset, "3") ||
                isOpetuskieliKoodiArvo.apply(maaraykset, "9")) return OivaTemplates.RenderLanguage.fi;
        else if (isOpetuskieliKoodiArvo.apply(maaraykset, "2")) return OivaTemplates.RenderLanguage.sv;
            // else if(isOpetuskieliKoodiArvo.apply(maaraykset, "4")) return OivaTemplates.RenderLanguage.en; // TODO: After english support is added
            // else if(isOpetuskieliKoodiArvo.apply(maaraykset, "5")) return OivaTemplates.RenderLanguage.se; // TODO: After saame support is added
        else return OivaTemplates.RenderLanguage.fi;
    }

    public Collection<Liite> getAttachments(final long lupaId) { // TODO: Add baseLupaFilter here
        return dsl.select(LIITE.POLKU, LIITE.NIMI, LIITE.TYYPPI, LIITE.UUID).from(LIITE, LUPA_LIITE)
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

    /**
     * Ammattilisten tutkintojen ja koulutuksen järjestämisluvissa määrätyt tutkinnot, osaamisalarajoitukset sekä opetuskielet koulutuksen järjestäjän mukaan
     *
     * @return csv file of query result
     */
    public String getReport() {
        // All except TELMA and VALMA
        List<String> except = Arrays.asList("999901", "999903");

        java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());

        SelectConditionStep<Record> query = dsl.select(ArrayUtils.addAll(LUPA.fields(), MAARAYS.fields()))
                .from(LUPA)
                .join(LUPATILA).on(LUPA.LUPATILA_ID.eq(LUPATILA.ID))
                .join(MAARAYS).on(MAARAYS.LUPA_ID.eq(LUPA.ID))
                .where(MAARAYS.KOODISTO.eq("koulutus")
                        .and(MAARAYS.KOODIARVO.in(except)).not()
                        .and(LUPA.ALKUPVM.lessOrEqual(currentDate).or(LUPA.ALKUPVM.isNull()))
                        .and(LUPA.LOPPUPVM.greaterOrEqual(currentDate).or(LUPA.LOPPUPVM.isNull()))
                        .and(LUPATILA.TUNNISTE.eq(LupatilaValue.VALMIS)));

        logger.debug(query.getSQL());

        StringBuilder builder = new StringBuilder();
        builder.append("Y-tunnus;Koulutuksen järjestäjä;Järjestäjän toiminta-alue;Opetuskieli tai -kielet;tutkintotyyppi;tutkintokoodi;tutkinnon nimi;lisätiedot;tutkintokieli lisäksi;\n");

        Collector<KoodistoKoodi, ?, Map<String, KoodistoKoodi>> toKoodistoMap = Collectors.toMap(KoodistoKoodi::getKoodiArvo, v -> v);

        final Map<String, KoodistoKoodi> koulutukset = koodistoservice.getAmmatillinenTutkinnot().stream().collect(toKoodistoMap);
        // Combine osaamisalakielet (e.g. "1", "2") and kielet (e.g. "fi", "sv")
        final Map<String, KoodistoKoodi> kielet = Stream.concat(
                koodistoservice.getOpetuskielet().stream(),
                koodistoservice.getKielet().stream()).collect(toKoodistoMap);
        final Map<String, KoodistoKoodi> osaamisalat = koodistoservice.getOsaamisalat().stream().collect(toKoodistoMap);

        Result<Record> result = dsl.fetch(query);
        result.stream().collect(Collectors.groupingBy(r -> r.get(LUPA.JARJESTAJA_YTUNNUS)))
                .entrySet().stream()
                .flatMap(entry -> generateRows(koulutukset, kielet, osaamisalat, entry))
                .forEach(r -> builder.append(r).append("\n"));

        return builder.toString();
    }

    private String getNimi(Map<String, KoodistoKoodi> koodisto, String koodiarvo) {
        return Optional.ofNullable(koodisto.get(koodiarvo))
                .map(k -> k.getNimi().getFirstOfOrEmpty("fi", "sv"))
                .orElse("");
    }

    private Stream<String> generateRows(Map<String, KoodistoKoodi> koulutukset, Map<String, KoodistoKoodi> kielet, Map<String, KoodistoKoodi> osaamisalat, Map.Entry<String, List<Record>> orgRecord) {
        String ytunnus = orgRecord.getKey();
        List<Record> values = orgRecord.getValue();

        final Organisaatio organisaatio = opintopolkuService.getBlockingOrganisaatio(values.get(0).get(LUPA.JARJESTAJA_OID));

        String kielinimet = values.stream()
                .filter(v -> "oppilaitoksenopetuskieli".equals(v.get(MAARAYS.KOODISTO)) && v.get(MAARAYS.PARENT_ID) == null)
                .map(k -> k.get(MAARAYS.KOODIARVO))
                .map(k -> getNimi(kielet, k))
                .collect(Collectors.joining(","));

        String maakuntanimi = opintopolkuService.getMaakuntaKoodiForKunta(organisaatio.kuntaKoodiArvo())
                .map(k -> k.getNimi().getFirstOfOrEmpty("fi", "sv")).orElse("");

        final Map<String, String> tutkintolyhenteet = new HashMap<>();
        tutkintolyhenteet.put("erikoisammattitutkinto", "EAT");
        tutkintolyhenteet.put("perustutkinto", "PT");
        tutkintolyhenteet.put("ammattitutkinto", "AT");
        tutkintolyhenteet.put("specialyrkesexamen", "EAT");
        tutkintolyhenteet.put("grundexamen", "PT");
        tutkintolyhenteet.put("yrkesexamen", "AT");

        // Loop through koulutukset
        return values.stream()
                .filter(v -> "koulutus".equals(v.get(MAARAYS.KOODISTO)) && koulutukset.get(v.get(MAARAYS.KOODIARVO)) != null && v.get(MAARAYS.PARENT_ID) == null)
                .map(r -> {
                    Optional<KoodistoKoodi> koulutusKoodi = Optional.ofNullable(koulutukset.get(r.get(MAARAYS.KOODIARVO)));
                    String koulutus = koulutusKoodi.map(k -> k.getNimi().getFirstOfOrEmpty("fi", "sv")).orElse("");

                    if (koulutus.length() == 0) {
                        logger.warn("Koulutuksella ei ole nimeä. id=" + r.get(MAARAYS.KOODIARVO) + ", koodi=" + koulutusKoodi);
                    }

                    String tutkintolyhenne = tutkintolyhenteet.entrySet().stream()
                            .filter(entry -> koulutus.contains(entry.getKey()))
                            .findFirst()
                            .map(Map.Entry::getValue)
                            .orElse("");

                    // extra info
                    List<Record> lisatiedot = values.stream().filter(v -> r.get(MAARAYS.ID).equals(v.get(MAARAYS.PARENT_ID))).collect(Collectors.toList());

                    String lisakielet = lisatiedot.stream()
                            .filter(v -> "kieli".equals(v.get(MAARAYS.KOODISTO)))
                            .map(k -> k.get(MAARAYS.KOODIARVO).toUpperCase())
                            .map(k -> getNimi(kielet, k))
                            .collect(Collectors.joining(","));

                    String lukuunottamatta = lisatiedot.stream()
                            .filter(v -> "osaamisala".equals(v.get(MAARAYS.KOODISTO)))
                            .map(k -> k.get(MAARAYS.KOODIARVO))
                            .map(k -> k + " " + getNimi(osaamisalat, k))
                            .collect(Collectors.joining(", "));

                    if (lukuunottamatta.length() > 0) {
                        lukuunottamatta = "lukuun ottamatta: " + lukuunottamatta;
                    }

                    return String.join(";",
                            ytunnus,
                            organisaatio.getNimi().getFirstOfOrEmpty("fi", "sv"),
                            maakuntanimi,
                            kielinimet,
                            tutkintolyhenne,
                            r.get(MAARAYS.KOODIARVO),
                            koulutus,
                            lukuunottamatta,
                            lisakielet);
                });
    }

    public List<Organisaatio> getLupaorganisaatiot() {
        SelectConditionStep<Record1<String>> query = dsl.selectDistinct(LUPA.JARJESTAJA_OID)
                .from(LUPA)
                .where(LUPA.ALKUPVM.le(DSL.currentDate()).
                        and(LUPA.LOPPUPVM.isNull().or(LUPA.LOPPUPVM.ge(DSL.currentDate()))));

        return dsl.fetch(query).stream()
                .map(result -> result.get(LUPA.JARJESTAJA_OID))
                .map(oid -> organisaatioService.getWithLocation(oid))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}