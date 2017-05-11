package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Asiatyyppi;
import fi.minedu.oiva.backend.entity.Esitysmalli;
import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.entity.Lupatila;
import fi.minedu.oiva.backend.entity.Maarays;
import fi.minedu.oiva.backend.entity.Paatoskierros;
import fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.entity.opintopolku.Kunta;
import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectOnConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    private PebbleService pebbleService;

    public Collection<Lupa> getAll() {
        return dsl.select(LUPA.fields()).from(LUPA).fetchInto(Lupa.class);
    }

    public String luvatLinksHtml() { // TODO REMOVEME
        return pebbleService.toLupaListHTML(baseLupaSelect().stream()
                .map(record -> entity(record, Organisaatio.class.getSimpleName(), Kunta.class.getSimpleName()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList())).orElse("");
    }

    public Optional<Lupa> get(final Long lupaId, final String... withOptions) {
        return entity(baseLupaSelect().where(LUPA.ID.eq(lupaId)).fetchOne(), withOptions);
    }

    public Optional<Lupa> get(final String diaarinumero, final String... withOptions) {
        return entity(baseLupaSelect().where(LUPA.DIAARINUMERO.eq(diaarinumero)).fetchOne(), withOptions);
    }

    private SelectOnConditionStep<Record> baseLupaSelect() {
        return dsl.select().from(LUPA)
            .leftOuterJoin(ASIATYYPPI).on(ASIATYYPPI.ID.eq(LUPA.ASIATYYPPI_ID))
            .leftOuterJoin(LUPATILA).on(LUPATILA.ID.eq(LUPA.LUPATILA_ID))
            .leftOuterJoin(PAATOSKIERROS).on(PAATOSKIERROS.ID.eq(LUPA.PAATOSKIERROS_ID))
            .leftOuterJoin(ESITYSMALLI).on(ESITYSMALLI.ID.eq(PAATOSKIERROS.ESITYSMALLI_ID));
    }

    public Optional<Lupa> entity(final Record record, final String... withOptions) {
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

    protected Optional<Lupa> with(final Optional<Lupa> lupaOpt, final String... with) {
        final List withOptions = null == with ? Collections.emptyList() : Arrays.asList(with).stream().map(String::toLowerCase).collect(Collectors.toList());
        final Function<Class<?>, Boolean> hasOption = targetClass ->
                withOptions.contains(StringUtils.lowerCase(targetClass.getSimpleName())) || withOptions.contains(withAll);

        if(hasOption.apply(Organisaatio.class)) withOrganization(lupaOpt);
        if(hasOption.apply(Kunta.class)) withKunta(lupaOpt);
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
                final KoodistoKoodi kuntakoodi = opintopolkuService.getKuntaKoodi(jarjestaja.kuntaKoodiArvo());
                if(null != kuntakoodi) jarjestaja.setKuntaKoodi(kuntakoodi);
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
            if(null != lupa.maaraykset()) lupa.maaraykset().forEach(maarays -> maaraysService.withKoodisto(maarays));
        });
        return lupaOpt;
    }
}