package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.model.entity.AsiatyyppiValue;
import fi.minedu.oiva.backend.model.entity.LupatilaValue;
import fi.minedu.oiva.backend.model.entity.MaaraystyyppiValue;
import fi.minedu.oiva.backend.model.entity.export.KoulutusLupa;
import fi.minedu.oiva.backend.model.entity.export.Koulutustarjonta;
import fi.minedu.oiva.backend.model.entity.export.KoulutustarjontaKoulutus;
import fi.minedu.oiva.backend.model.entity.oiva.Kohde;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.oiva.Maarays;
import fi.minedu.oiva.backend.model.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.model.jooq.tables.pojos.Maaraystyyppi;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fi.minedu.oiva.backend.model.jooq.Tables.ASIATYYPPI;
import static fi.minedu.oiva.backend.model.jooq.Tables.KOHDE;
import static fi.minedu.oiva.backend.model.jooq.Tables.LUPA;
import static fi.minedu.oiva.backend.model.jooq.Tables.LUPATILA;
import static fi.minedu.oiva.backend.model.jooq.Tables.MAARAYS;
import static fi.minedu.oiva.backend.model.jooq.Tables.MAARAYSTYYPPI;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.options;

@Service
public class ExportService {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private LupaService lupaService;

    @Autowired
    private KohdeService kohdeService;

    @Autowired
    private KoodistoService koodistoService;

    /**
     * Tarjoaa kaikki lupatiedot
     * Käyttäjä: Vipunen
     *
     * @return Lista kaikista luvista
     */
    public Collection<Lupa> getJarjestysluvat() {
        final Condition filter = ASIATYYPPI.TUNNISTE.ne(AsiatyyppiValue.PERUUTUS);
        return lupaService.getAllJarjestamisluvat(filter, options(Maarays.class));
    }

    /**
     * Tarjoaa kustannustiedot kaikista luvista jotka ovat olleet / ovat voimassa annetulla aikavälillä.
     * Käyttäjä: OPH
     *
     * @return Lista kaikista luvista
     */
    public Collection<Lupa> getKustannusTiedot(String koulutustyyppi, LocalDate start, LocalDate end) {
        final Condition filter = LUPA.ALKUPVM.lessOrEqual(Date.valueOf(end))
                .and(koulutustyyppi == null ? LUPA.KOULUTUSTYYPPI.isNull() : LUPA.KOULUTUSTYYPPI.eq(koulutustyyppi))
                .and(LUPA.LOPPUPVM.isNull().or(LUPA.LOPPUPVM.greaterThan(Date.valueOf(start))));
        return lupaService.getAllJarjestamisluvat(filter, options(Maarays.class, KoodistoKoodi.class));
    }

    /**
     * Tarjoaa kaikki ammatillisenkoulutuksen koulutustiedot
     * Käyttäjä: Arvo
     *
     * @return Lista kaikista ammatillisistakoulutusluvista
     */
    public Collection<KoulutusLupa> getKoulutusLuvat() {
        final Function<Long, Collection<String>> koulutusKoodiArvot = (lupaId) -> dsl.select(MAARAYS.KOODIARVO).from(LUPA)
            .leftOuterJoin(MAARAYS).on(MAARAYS.LUPA_ID.eq(LUPA.ID))
            .leftOuterJoin(KOHDE).on(KOHDE.ID.eq(MAARAYS.KOHDE_ID))
            .where(LUPA.ID.eq(lupaId)).and(LUPA.KOULUTUSTYYPPI.isNull())
            .and(KOHDE.TUNNISTE.eq("tutkinnotjakoulutukset"))
            .and(MAARAYS.KOODISTO.eq("koulutus"))
            .fetchInto(String.class);

        final Function<Long, Optional<String>> oppisopimuskoulutus = (lupaId) -> dsl.select(MAARAYS.KOODIARVO).from(LUPA)
                .leftOuterJoin(MAARAYS).on(MAARAYS.LUPA_ID.eq(LUPA.ID))
                .leftOuterJoin(KOHDE).on(KOHDE.ID.eq(MAARAYS.KOHDE_ID))
                .where(LUPA.ID.eq(lupaId))
                .and(KOHDE.TUNNISTE.eq("muut"))
                .and(MAARAYS.KOODISTO.eq("oivamuutoikeudetvelvollisuudetehdotjatehtavat"))
                .and(MAARAYS.KOODIARVO.eq("1"))
                .fetchOptionalInto(String.class);

        return dsl.select(LUPA.ID, LUPA.JARJESTAJA_YTUNNUS, LUPA.ALKUPVM, LUPA.LOPPUPVM).from(LUPA)
                .leftJoin(ASIATYYPPI).on(LUPA.ASIATYYPPI_ID.eq(ASIATYYPPI.ID))
                .where(LUPA.KOULUTUSTYYPPI.isNull())
                .and(ASIATYYPPI.TUNNISTE.ne(AsiatyyppiValue.PERUUTUS))
                .orderBy(LUPA.JARJESTAJA_YTUNNUS, LUPA.ALKUPVM).fetchInto(KoulutusLupa.class)
                .stream().peek(koulutusLupa -> {
                koulutusLupa.setKoulutukset(koulutusKoodiArvot.apply(koulutusLupa.getId()));
                if(oppisopimuskoulutus.apply(koulutusLupa.getId()).isPresent()) {
                    koulutusLupa.setLaajaOppisopimuskoulutus(oppisopimuskoulutus.apply(koulutusLupa.getId()).get());
                }
                }).collect(Collectors.toList());
    }

    /**
     * Tarjoaa julkiset ammatillisetkoulutustiedot
     * Käyttäjä: OPH
     *
     * @return Lista ammatillisestakoulutustarjonnasta
     */
    public Collection<Koulutustarjonta> getKoulutustarjonta() {

        final Function<String, String> oppilaitoksenopetuskieliToKieli = koodiUri -> {
            switch (koodiUri) {
                case "oppilaitoksenopetuskieli_1": return "fi";
                case "oppilaitoksenopetuskieli_2": return "sv";
                case "oppilaitoksenopetuskieli_5": return "se";
                default: return "";
            }
        };

        final Function<Long, Collection<Maarays>> maaraykset = lupaId ->
            dsl.select(MAARAYS.ID, MAARAYS.PARENT_ID, MAARAYS.KOHDE_ID, MAARAYS.MAARAYSTYYPPI_ID, MAARAYS.KOODISTO, MAARAYS.KOODIARVO).from(MAARAYS)
            .where(MAARAYS.LUPA_ID.eq(lupaId)).fetchInto(Maarays.class);

        final Function<Long, Collection<String>> oppilaitoksenopetuskielet = lupaId ->
            dsl.select(MAARAYS.KOODISTO, MAARAYS.KOODIARVO).from(MAARAYS)
            .leftOuterJoin(KOHDE).on(KOHDE.ID.eq(MAARAYS.KOHDE_ID))
            .where(MAARAYS.LUPA_ID.eq(lupaId))
                .and(KOHDE.TUNNISTE.eq("opetusjatutkintokieli"))
                .and(MAARAYS.KOODISTO.eq("oppilaitoksenopetuskieli"))
            .fetchInto(Maarays.class).stream().filter(Maarays::hasKoodistoAndKoodiArvo)
            .map(Maarays::koodiUri)
            .map(oppilaitoksenopetuskieliToKieli::apply).filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());

        final Map<String, Long> kohdeIdMap = kohdeService.getAll().stream().collect(Collectors.toMap(Kohde::getTunniste, Kohde::getId));
        final BiFunction<Maarays, String, Boolean> isKohde = (maarays, kohdeTunniste) -> maarays.getKohdeId() == kohdeIdMap.get(kohdeTunniste);

        final Map<MaaraystyyppiValue, Long> maaraystyyppiIdMap = dsl.select(MAARAYSTYYPPI.fields()).from(MAARAYSTYYPPI).fetchInto(Maaraystyyppi.class)
            .stream().collect(Collectors.toMap(Maaraystyyppi::getTunniste, Maaraystyyppi::getId));
        final BiFunction<Maarays, MaaraystyyppiValue, Boolean> isMaaraysyyppi = (maarays, maaraystyyppi) -> maarays.getMaaraystyyppiId() == maaraystyyppiIdMap.get(maaraystyyppi);

        final Function<Long, Collection<KoulutustarjontaKoulutus>> koulutukset = lupaId -> {
            final List<KoulutustarjontaKoulutus> koulutusMaaraykset = new ArrayList<>();

            final Map<Long, List<Maarays>> aliMaaraysMap = new HashMap<>();
            final BiConsumer<Long, Maarays> addAliMaarays = (parentMaaraysId, maarays) -> {
                final List<Maarays> aliMaaraykset = aliMaaraysMap.getOrDefault(parentMaaraysId, new ArrayList<>());
                aliMaaraykset.add(maarays);
                aliMaaraysMap.put(parentMaaraysId, aliMaaraykset);
            };

            final Collection<String> oppilaitoksenkielet = oppilaitoksenopetuskielet.apply(lupaId);
            final Map<String, String> koulutusToKoulutusalaMap = koodistoService.getKoulutusToKoulutusalaRelation();

            // määräykset
            maaraykset.apply(lupaId).stream().forEach(maarays -> {

                // alimääräys
                if (null != maarays.getParentId()) addAliMaarays.accept(maarays.getParentId(), maarays);

                // koulutus
                else if (isKohde.apply(maarays, "tutkinnotjakoulutukset") && maarays.isKoodisto("koulutus")) {
                    final KoulutustarjontaKoulutus koulutus = new KoulutustarjontaKoulutus(maarays);

                    // oppilaitoksen opetuskieli
                    oppilaitoksenkielet.stream().forEach(koulutus::addKieli);

                    // koulutusala
                    koulutus.setKoulutusala(koulutusToKoulutusalaMap.get(maarays.getKoodiarvo()));

                    koulutusMaaraykset.add(koulutus);
                }
            });

            // koulutuksen alimääräykset
            koulutusMaaraykset.stream().forEach(koulutus ->
                aliMaaraysMap.getOrDefault(koulutus.getMaaraysId(), new ArrayList<>()).stream().forEach(aliMaarays -> {

                    // koulutus rajoitteet
                    if(isMaaraysyyppi.apply(aliMaarays, MaaraystyyppiValue.RAJOITE) && aliMaarays.isKoodisto("osaamisala")) {
                        koulutus.addRajoitus(aliMaarays.koodiUri());

                    // koulutus kielet
                    } else if(aliMaarays.isKoodisto("kieli")) {
                        koulutus.addKieli(aliMaarays.getKoodiarvo());
                    }
                })
            );
            return koulutusMaaraykset;
        };

        return dsl.select(LUPA.ID, LUPA.JARJESTAJA_YTUNNUS, LUPA.JARJESTAJA_OID, LUPA.DIAARINUMERO, LUPA.ALKUPVM, LUPA.LOPPUPVM, LUPA.PAATOSPVM).from(LUPA)
            .leftOuterJoin(LUPATILA).on(LUPATILA.ID.eq(LUPA.LUPATILA_ID))
            .where(LUPATILA.TUNNISTE.eq(LupatilaValue.VALMIS)).and(LUPA.KOULUTUSTYYPPI.isNull())
            .orderBy(LUPA.JARJESTAJA_YTUNNUS, LUPA.PAATOSPVM)
            .fetchInto(Koulutustarjonta.class).stream().map(koulutustarjonta -> {
                final Long lupaId = koulutustarjonta.getId();
                oppilaitoksenopetuskielet.apply(lupaId).forEach(koulutustarjonta::addOpetuskieli);
                koulutukset.apply(lupaId).forEach(koulutustarjonta::addKoulutus);
                return koulutustarjonta;
            }).collect(Collectors.toList());
    }
}