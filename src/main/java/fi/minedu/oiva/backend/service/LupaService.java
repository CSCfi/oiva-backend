package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Asiatyyppi;
import fi.minedu.oiva.backend.entity.Esitysmalli;
import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.entity.Lupatila;
import fi.minedu.oiva.backend.entity.LupatilaValue;
import fi.minedu.oiva.backend.entity.Maarays;
import fi.minedu.oiva.backend.entity.OivaTemplates;
import fi.minedu.oiva.backend.entity.Paatoskierros;
import fi.minedu.oiva.backend.entity.Liite;
import fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.entity.opintopolku.Kunta;
import fi.minedu.oiva.backend.entity.opintopolku.Maakunta;
import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio;
import fi.minedu.oiva.backend.security.OivaPermission;
import fi.minedu.oiva.backend.security.annotations.OivaAccess;
import fi.minedu.oiva.backend.template.extension.MaaraysListFilter;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import static fi.minedu.oiva.backend.jooq.Tables.*;

@Service
public class LupaService implements RecordMapping<Lupa> {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private MaaraysService maaraysService;

    @Autowired
    private OpintopolkuService opintopolkuService;

    @Autowired
    private AuthService authService;

    protected SelectOnConditionStep<Record> baseLupaSelect() {
        return dsl.select(LUPA.fields()).from(LUPA)
            .leftOuterJoin(LUPATILA).on(LUPATILA.ID.eq(LUPA.LUPATILA_ID));
    }

    protected SelectOnConditionStep<Record> lupaSelect() {
        return baseLupaSelect()
            .leftOuterJoin(ASIATYYPPI).on(ASIATYYPPI.ID.eq(LUPA.ASIATYYPPI_ID))
            .leftOuterJoin(PAATOSKIERROS).on(PAATOSKIERROS.ID.eq(LUPA.PAATOSKIERROS_ID))
            .leftOuterJoin(ESITYSMALLI).on(ESITYSMALLI.ID.eq(PAATOSKIERROS.ESITYSMALLI_ID));
    }

    protected Optional<Condition> baseLupaFilter() {
        final OivaPermission accessPermission = authService.lupaAccessPermission();
        final Condition valmisLupaCondition = LUPATILA.TUNNISTE.eq(LupatilaValue.VALMIS);
        if(accessPermission.is(OivaAccess.Type.OnlyPublic)) {
            return Optional.of(valmisLupaCondition);
        } else if(accessPermission.is(OivaAccess.Type.OrganizationAndPublic)) {
            if(accessPermission.oids.isEmpty()) {
                return Optional.of(valmisLupaCondition);
            } else {
                return Optional.of(valmisLupaCondition.or(LUPA.JARJESTAJA_OID.in(accessPermission.oids)));
            }
        } else {
            return Optional.empty();
        }
    }

    public Collection<Lupa> getAll() { // TODO: Implement filter
        final SelectJoinStep<Record> query = baseLupaSelect();
        baseLupaFilter().ifPresent(query::where);
        return query.fetchInto(Lupa.class);
    }

    public Optional<Lupa> get(final Long lupaId, final String... withOptions) {
        final SelectConditionStep<Record> query = lupaSelect().where(LUPA.ID.eq(lupaId));
        baseLupaFilter().ifPresent(query::and);
        return entity(query.fetchOne(), withOptions);
    }

    public Optional<Lupa> get(final String diaarinumero, final String... withOptions) {
        final SelectConditionStep<Record> query = lupaSelect().where(LUPA.DIAARINUMERO.eq(diaarinumero));
        baseLupaFilter().ifPresent(query::and);
        return entity(query.fetchOne(), withOptions);
    }

    protected Optional<Lupa> entity(final Record record, final String... withOptions) {
        if(null != record) {
            final Lupa lupa = convertFieldsTo(record, LUPA.fields(), Lupa.class);

            final Paatoskierros paatoskierros = convertFieldsTo(record, PAATOSKIERROS.fields(), Paatoskierros.class);
            paatoskierros.setEsitysmalli(convertFieldsTo(record, ESITYSMALLI.fields(), Esitysmalli.class));
            lupa.setPaatoskierros(paatoskierros);

            lupa.setAsiatyyppi(convertFieldsTo(record, ASIATYYPPI.fields(), Asiatyyppi.class));
            lupa.setLupatila(convertFieldsTo(record, LUPATILA.fields(), Lupatila.class));
            return with(Optional.of(lupa), withOptions);
        }
        return Optional.empty();
    }

    protected String[] withOptions(final Class<?>... classes) {
        if(null == classes) return new String[] {};
        else return Arrays.asList(classes).stream().map(Class::getSimpleName).toArray(size -> new String[size]);
    }

    protected Optional<Lupa> with(final Optional<Lupa> lupaOpt, final String... with) {
        final List withOptions = null == with ? Collections.emptyList() : Arrays.asList(with).stream().map(String::toLowerCase).collect(Collectors.toList());
        final Function<Class<?>, Boolean> hasOption = targetClass ->
            withOptions.contains(StringUtils.lowerCase(targetClass.getSimpleName())) || withOptions.contains(withAll);

        if(hasOption.apply(Organisaatio.class)) withOrganization(lupaOpt);
        if(hasOption.apply(Kunta.class)) withKunta(lupaOpt);
        if(hasOption.apply(Maakunta.class)) withMaakunta(lupaOpt);
        if(hasOption.apply(Maarays.class)) withMaaraykset(lupaOpt);
        if(hasOption.apply(KoodistoKoodi.class)) withKoodisto(lupaOpt);
        return lupaOpt;
    }

    protected Optional<Lupa> withOrganization(final Optional<Lupa> lupaOpt) {
        lupaOpt.ifPresent(lupa -> lupa.setJarjestaja(opintopolkuService.getBlockingOrganisaatio(lupa.getJarjestajaOid())));
        return lupaOpt;
    }

    protected Optional<Lupa> withKunta(final Optional<Lupa> lupaOpt) {
        lupaOpt.ifPresent(lupa -> {
            final Organisaatio jarjestaja = lupa.jarjestaja();
            if(null != jarjestaja && StringUtils.isNotBlank(jarjestaja.kuntaKoodiArvo())) {
                opintopolkuService.getKuntaKoodi(jarjestaja.kuntaKoodiArvo()).ifPresent(jarjestaja::setKuntaKoodi);
            }
        });
        return lupaOpt;
    }

    protected Optional<Lupa> withMaakunta(final Optional<Lupa> lupaOpt) {
        lupaOpt.ifPresent(lupa -> {
            final Organisaatio jarjestaja = lupa.jarjestaja();
            if(null != jarjestaja && null != jarjestaja.kuntaKoodi()) {
                opintopolkuService.getMaakuntaKoodiForKunta(jarjestaja.kuntaKoodiArvo()).ifPresent(jarjestaja::setMaakuntaKoodi);
            }
        });
        return lupaOpt;
    }

    protected Optional<Lupa> withMaaraykset(final Optional<Lupa> lupaOpt) {
        lupaOpt.ifPresent(lupa -> lupa.setMaaraykset(maaraysService.getByLupa(lupa.getId())));
        return lupaOpt;
    }

    protected Optional<Lupa> withKoodisto(final Optional<Lupa> lupaOpt) {
        lupaOpt.ifPresent(lupa -> {
            if(null != lupa.maaraykset()) lupa.maaraykset().stream().map(Optional::ofNullable).forEach(maaraysService::withKoodisto);
        });
        return lupaOpt;
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
}