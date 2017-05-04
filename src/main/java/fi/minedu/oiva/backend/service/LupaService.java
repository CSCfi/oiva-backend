package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Asiatyyppi;
import fi.minedu.oiva.backend.entity.Esitysmalli;
import fi.minedu.oiva.backend.entity.Kohde;
import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.entity.Lupatila;
import fi.minedu.oiva.backend.entity.Maarays;
import fi.minedu.oiva.backend.entity.Maaraystyyppi;
import fi.minedu.oiva.backend.entity.Paatoskierros;
import fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectOnConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import static fi.minedu.oiva.backend.jooq.Tables.*;

@Service
public class LupaService {

    public static final String withAll = "all";

    @Autowired
    private DSLContext dsl;

    @Autowired
    private OpintopolkuService opintopolkuService;

    public Collection<Lupa> getAll() {
        return dsl.select(LUPA.fields()).from(LUPA).fetchInto(Lupa.class);
    }

    public Optional<Lupa> get(final Long lupaId, final String... withOptions) {
        return asLupa(baseLupaSelect().where(LUPA.ID.eq(lupaId)).fetchOne(), withOptions);
    }

    public Optional<Lupa> get(final String diaarinumero, final String... withOptions) {
        return asLupa(baseLupaSelect().where(LUPA.DIAARINUMERO.eq(diaarinumero)).fetchOne(), withOptions);
    }

    private SelectOnConditionStep<Record> baseLupaSelect() {
        return dsl.select().from(LUPA)
            .leftOuterJoin(ASIATYYPPI).on(ASIATYYPPI.ID.eq(LUPA.ASIATYYPPI_ID))
            .leftOuterJoin(LUPATILA).on(LUPATILA.ID.eq(LUPA.LUPATILA_ID))
            .leftOuterJoin(PAATOSKIERROS).on(PAATOSKIERROS.ID.eq(LUPA.PAATOSKIERROS_ID))
            .leftOuterJoin(ESITYSMALLI).on(ESITYSMALLI.ID.eq(PAATOSKIERROS.ESITYSMALLI_ID));
    }

    protected Optional<Lupa> asLupa(final Record record, final String... withOptions) {
        if(null != record) {
            final Lupa lupa = asEntity(record, LUPA.fields(), Lupa.class);

            final Paatoskierros paatoskierros = asEntity(record, PAATOSKIERROS.fields(), Paatoskierros.class);
            paatoskierros.setEsitysmalli(asEntity(record, ESITYSMALLI.fields(), Esitysmalli.class));
            lupa.setPaatoskierros(paatoskierros);

            lupa.setAsiatyyppi(asEntity(record, ASIATYYPPI.fields(), Asiatyyppi.class));
            lupa.setLupatila(asEntity(record, LUPATILA.fields(), Lupatila.class));
            return with(Optional.of(lupa), withOptions);
        }
        return Optional.empty();
    }

    protected Optional<Lupa> with(final Optional<Lupa> lupaOpt, final String... with) {
        final List withOptions = null == with ? Collections.emptyList() : Arrays.asList(with);
        final Function<Class<?>, Boolean> hasOption = (targetClass) ->
                withOptions.contains(StringUtils.lowerCase(targetClass.getSimpleName())) || withOptions.contains(withAll);

        if(hasOption.apply(Organisaatio.class)) withOrganization(lupaOpt);
        if(hasOption.apply(Maarays.class)) withMaaraykset(lupaOpt);
        if(hasOption.apply(KoodistoKoodi.class)) withKoodisto(lupaOpt);
        return lupaOpt;
    }

    protected Optional<Lupa> withOrganization(final Optional<Lupa> lupaOpt) {
        lupaOpt.ifPresent(lupa -> lupa.setJarjestaja(opintopolkuService.getBlockingOrganisaatio(lupa.getJarjestajaOid())));
        return lupaOpt;
    }

    protected Optional<Lupa> withMaaraykset(final Optional<Lupa> lupaOpt) {
        lupaOpt.ifPresent(lupa -> {
            final Map<Long, Maarays> maaraykset = new HashMap<>();
            final Map<Long, Kohde> kohteet = new HashMap<>();
            final Map<Long, Maaraystyyppi> maaraystyypit = new HashMap<>();

            final Result<Record> result = dsl.select().from(LUPA)
                .leftOuterJoin(MAARAYS).on(MAARAYS.LUPA_ID.eq(LUPA.ID))
                .leftOuterJoin(KOHDE).on(KOHDE.ID.eq(MAARAYS.KOHDE_ID))
                .leftOuterJoin(MAARAYSTYYPPI).on(MAARAYSTYYPPI.ID.eq(MAARAYS.MAARAYSTYYPPI_ID))
                .where(LUPA.ID.eq(lupa.getId()))
                .fetch();

            result.forEach(record -> {
                putToMap(maaraykset, record, MAARAYS.fields(), Maarays.class);
                putToMap(kohteet, record, KOHDE.fields(), Kohde.class);
                putToMap(maaraystyypit, record, MAARAYSTYYPPI.fields(), Maaraystyyppi.class);
            });
            lupa.setMaaraykset(maaraykset.values());
            lupa.maaraykset().forEach(maarays -> {
                maarays.setKohde(kohteet.getOrDefault(maarays.getKohdeId(), null));
                maarays.setMaaraystyyppi(maaraystyypit.getOrDefault(maarays.getMaaraystyyppiId(), null));
            });
        });
        return lupaOpt;
    }

    protected Optional<Lupa> withKoodisto(final Optional<Lupa> lupaOpt) {
        lupaOpt.ifPresent(lupa -> {
            if(null != lupa.maaraykset()) lupa.maaraykset().forEach(maarays -> {
                if(maarays.hasKoodistoKoodiBind()) {
                    final KoodistoKoodi koodi = opintopolkuService.getKoodi(maarays);
                    maarays.setKoodi(koodi);
                    if(null != koodi && (koodi.isKoodisto("koulutus"))) {
                        final List<KoodistoKoodi> koulutustyyppiKoodit = opintopolkuService.getKoulutustyyppiKoodiForKoulutus(koodi.koodiArvo());
                        if(null != koulutustyyppiKoodit) {
                            koulutustyyppiKoodit.stream().forEach(maarays::addYlaKoodi);

                            koulutustyyppiKoodit.stream().forEach(maarays2 -> {
                                System.out.println("maarays: " + maarays2);
                            });

                        }

                        final KoodistoKoodi koulutusalaKoodi = opintopolkuService.getKoulutusalaKoodiForKoulutus(koodi.koodiArvo());
                        if(null != koulutusalaKoodi) {

                            maarays.addYlaKoodi(koulutusalaKoodi);

                            System.out.println("koulutusalaKoodi: " + koulutusalaKoodi);
                        }

                        //System.out.println("maarays: " + maarays.getYlaKoodit());
                    }
                }
            });
        });
        return lupaOpt;
    }

    private <T> T asEntity(final Record r, final Field<?>[] fields, final Class<T> clazz) {
        return r.into(fields).into(clazz);
    }

    private <T, V> void putToMap(Map<V, T> map, Record r, Field<?>[] fields, Class<T> clazz) {
        final Record intermediateRecord = r.into(fields);
        final V id = (V) intermediateRecord.getValue("id");
        if (id != null && !map.containsKey(id)) {
            map.put(id, intermediateRecord.into(clazz));
        }
    }
}