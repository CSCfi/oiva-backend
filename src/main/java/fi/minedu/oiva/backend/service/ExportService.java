package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Kohde;
import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.entity.LupatilaValue;
import fi.minedu.oiva.backend.entity.Maarays;
import fi.minedu.oiva.backend.entity.Maaraystyyppi;
import fi.minedu.oiva.backend.entity.MaaraystyyppiValue;
import fi.minedu.oiva.backend.entity.export.KoulutusLupa;
import fi.minedu.oiva.backend.entity.export.Koulutustarjonta;
import fi.minedu.oiva.backend.entity.export.KoulutustarjontaKoulutus;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

import static fi.minedu.oiva.backend.jooq.Tables.KOHDE;
import static fi.minedu.oiva.backend.jooq.Tables.LUPA;
import static fi.minedu.oiva.backend.jooq.Tables.LUPATILA;
import static fi.minedu.oiva.backend.jooq.Tables.MAARAYS;
import static fi.minedu.oiva.backend.jooq.Tables.MAARAYSTYYPPI;
import static fi.minedu.oiva.backend.util.ControllerUtil.options;

@Service
public class ExportService implements RecordMapping<Lupa> {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private LupaService lupaService;

    @Autowired
    private KohdeService kohdeService;

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private OpintopolkuService opintopolkuService;

    /**
     * Tarjoaa kaikki lupatiedot
     * Käyttäjä: Vipunen
     *
     * @return Lista kaikista luvista
     */
    public Collection<Lupa> getJarjestysluvat() {
        return lupaService.getAll(options(Maarays.class));
    }

    /**
     * Tarjoaa kaikki koulutustiedot
     * Käyttäjä: Arvo
     *
     * @return Lista kaikista koulutusluvista
     */
    public Collection<KoulutusLupa> getKoulutusLuvat() {
        final Function<Long, Collection<String>> koulutusKoodiArvot = (lupaId) -> dsl.select(MAARAYS.KOODIARVO).from(LUPA)
            .leftOuterJoin(MAARAYS).on(MAARAYS.LUPA_ID.eq(LUPA.ID))
            .leftOuterJoin(KOHDE).on(KOHDE.ID.eq(MAARAYS.KOHDE_ID))
            .where(LUPA.ID.eq(lupaId))
            .and(KOHDE.TUNNISTE.eq("tutkinnotjakoulutukset"))
            .and(MAARAYS.KOODISTO.eq("koulutus"))
            .fetchInto(String.class);

        return dsl.select(LUPA.ID, LUPA.JARJESTAJA_YTUNNUS, LUPA.ALKUPVM, LUPA.LOPPUPVM).from(LUPA)
            .orderBy(LUPA.JARJESTAJA_YTUNNUS, LUPA.ALKUPVM).fetchInto(KoulutusLupa.class).stream().map(koulutusLupa -> {
                koulutusLupa.setKoulutukset(koulutusKoodiArvot.apply(koulutusLupa.getId()));
                return koulutusLupa;
            }).collect(Collectors.toList());
    }

    /**
     * Tarjoaa julkiset koulutustiedot
     * Käyttäjä: OPH
     *
     * @return Lista koulutustarjonnasta
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
            final Map<String, String> koulutusToKoulutusalaMap = koodistoService.getKoulutusToKoulutusalaMap();

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
            .where(LUPATILA.TUNNISTE.eq(LupatilaValue.VALMIS))
            .orderBy(LUPA.JARJESTAJA_YTUNNUS, LUPA.PAATOSPVM)
            .fetchInto(Koulutustarjonta.class).stream().map(koulutustarjonta -> {
                final Long lupaId = koulutustarjonta.getId();
                oppilaitoksenopetuskielet.apply(lupaId).forEach(koulutustarjonta::addOpetuskieli);
                koulutukset.apply(lupaId).forEach(koulutustarjonta::addKoulutus);
                return koulutustarjonta;
            }).collect(Collectors.toList());
    }
}