package fi.minedu.oiva.backend.core.service;

import com.google.common.collect.Lists;
import fi.minedu.oiva.backend.model.entity.opintopolku.Koodisto;
import fi.minedu.oiva.backend.model.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.model.entity.opintopolku.KoulutusKoodi;
import fi.minedu.oiva.backend.model.entity.opintopolku.Maakunta;
import fi.minedu.oiva.backend.model.entity.opintopolku.Organisaatio;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class KoodistoService {

    private final OpintopolkuService opintopolkuService;

    @Value("#{'${koulutustyyppi.ammatillinen.koodiarvot:}'.split(',')}")
    private List<String> ammatillinenKoulutustyyppiKoodiArvot;

    @Autowired
    public KoodistoService(OpintopolkuService opintopolkuService) {
        this.opintopolkuService = opintopolkuService;
    }

    public List<String> getAmmatillinenKoulutustyyppiArvot() {
        return ammatillinenKoulutustyyppiKoodiArvot;
    }

    @Value("${tutkintotyyppi.ammatillinen.koodiarvot}")
    private String ammatillinenTutkintotyyppiKoodiArvot;

    private List<String> getAmmatillinenTutkintotyyppiArvot() {
        return StringUtils.isNotBlank(ammatillinenTutkintotyyppiKoodiArvot) ? Arrays.asList(StringUtils.split(ammatillinenTutkintotyyppiKoodiArvot, ",")) : Collections.emptyList();
    }

    /**
     * Hae koodisto koodistoUrin ja koodistoVersion perusteella. Välimuistitetaan OpintopolkuService -palvelussa
     *
     * @param koodistoUri    koodisto uri
     * @param koodistoVersio koodisto versio
     * @return koodisto
     */
    public Koodisto getKoodisto(final String koodistoUri, final Integer koodistoVersio) {
        return opintopolkuService.getKoodisto(koodistoUri, koodistoVersio);
    }

    /**
     * Hae kaikki koodiston koodit koodistoUrin ja koodistoVersion perusteella. Välimuistitetaan OpintopolkuService -palvelussa
     *
     * @param koodistoUri    koodisto uri
     * @param koodistoVersio koodisto versio
     * @param includeExpired palautetaanko myös vanhentuneet koodit
     * @return Koodiston koodit
     */
    public List<KoodistoKoodi> getKoodit(final String koodistoUri, final Integer koodistoVersio, final Boolean includeExpired) {
        return opintopolkuService.getKoodit(koodistoUri, koodistoVersio, includeExpired);
    }

    /**
     * Hae koodi koodistoUrin, koodin ja koodistoVersion perusteella. Välimuistitetaan OpintopolkuService -palvelussa
     *
     * @param koodistoUri    koodisto uri
     * @param koodiArvo      koodiarvo
     * @param koodistoVersio koodisto versio
     * @return Koodi
     */
    public KoodistoKoodi getKoodi(final String koodistoUri, final String koodiArvo, final Integer koodistoVersio) {
        return opintopolkuService.getKoodi(koodistoUri, koodiArvo, koodistoVersio);
    }

    /**
     * Hae uusin koodiston versio koodistopalvelusta.
     *
     * @param koodisto Koodiston nimi
     * @return Koodiston versionumero
     */
    @Cacheable(value = "KoodistoService:getLatestKoodistoVersio")
    public Integer getLatestKoodistoVersio(String koodisto) {
        return Optional.ofNullable(getKoodisto(koodisto, null))
                .map(Koodisto::getVersio)
                .orElse(null);
    }

    /**
     * Hae maakunta koodit. Välimuistitetaan OpintopolkuService -palvelussa
     *
     * @return maakunta koodit
     */
    public List<KoodistoKoodi> getMaakunnat() {
        return opintopolkuService.getMaakuntaKoodit();
    }

    /**
     * Hae kunta koodit. Välimuistitetaan OpintopolkuService -palvelussa
     *
     * @return kunta koodit
     */
    public List<KoodistoKoodi> getKunnat() {
        return opintopolkuService.getKuntaKoodit();
    }

    /**
     * Hae kieli koodit. Välimuistitetaan OpintopolkuService -palvelussa
     *
     * @return kieli koodit
     */
    public List<KoodistoKoodi> getKielet() {
        return opintopolkuService.getKieliKoodit();
    }

    @Cacheable(value = "KoodistoService:getMaakuntaKunnat", key = "''")
    public List<Maakunta> getMaakuntaKunnat() {
        return new ArrayList<>(opintopolkuService.getMaakuntaKunnat(getLatestKoodistoVersio("kunta")));
    }

    @Cacheable(value = "KoodistoService:getKoulutustoimijat", key = "''")
    public List<Organisaatio> getKoulutustoimijat() {
        return new ArrayList<>(opintopolkuService.getKoulutustoimijat());
    }

    @Cacheable(value = "KoodistoService:getMaakuntaJarjestajat", key = "''")
    public List<Maakunta> getMaakuntaJarjestajat() {
        return new ArrayList<>(opintopolkuService.getMaakuntaJarjestajat(getLatestKoodistoVersio("kunta")));
    }

    @Cacheable(value = "KoodistoService:getKunta")
    public KoodistoKoodi getKunta(final String koodi) {
        return opintopolkuService.getKuntaKoodi(koodi).orElse(null);
    }

    @Cacheable(value = {"KoodistoService:getKieli"})
    public KoodistoKoodi getKieli(final String koodi) {
        final KoodistoKoodi kieliKoodisto = opintopolkuService.getKieliKoodi(koodi);

        // AM-308 fix usable until koodisto is fixed
        if (StringUtils.equalsIgnoreCase(kieliKoodisto.koodiArvo(), "SE")) {
            kieliKoodisto.getMetadataList().forEach(m -> m.setNimi("saame"));
        }
        return kieliKoodisto;
    }

    /**
     * Käytetään vain seuraavia opetuskieliä
     * - suomi (koodiArvo: 1)
     * - ruotsi (koodiArvo: 2)
     * - saame (koodiArvi: 5)
     */
    @Cacheable(value = "KoodistoService:getOpetuskielet", key = "''")
    public List<KoodistoKoodi> getOpetuskielet() {
        return opintopolkuService.getOppilaitoksenOpetuskieliKoodit().stream()
                .filter(koodistoItem -> Arrays.asList("1", "2", "5").contains(koodistoItem.koodiArvo())).collect(Collectors.toList());
    }

    @Cacheable(value = "KoodistoService:getAluehallintovirastoKuntaMap", key = "''")
    public Map<String, KoodistoKoodi> getKuntaAluehallintovirastoMap() { // TODO: NOT USED, REMOVE?
        final Map<String, KoodistoKoodi> map = new HashMap<>();
        opintopolkuService.getAlueHallintovirastoKoodit().forEach(
                avi -> opintopolkuService.getKuntaKooditForAlueHallintovirasto(avi.koodiArvo()).forEach(
                        kunta -> map.put(kunta.koodiArvo(), avi)));
        return map;
    }

    @Cacheable(value = "KoodistoService:getKuntaMaakuntaMap", key = "''")
    public Map<String, KoodistoKoodi> getKuntaMaakuntaMap() { // TODO: NOT USED, REMOVE?
        final Map<String, KoodistoKoodi> map = new HashMap<>();
        opintopolkuService.getMaakuntaKoodit().forEach(
                maakunta -> opintopolkuService.getKuntaKooditForMaakunta(maakunta.koodiArvo()).forEach(
                        kunta -> map.put(kunta.koodiArvo(), maakunta)));
        return map;
    }

    @Cacheable(value = "KoodistoService:getKoulutusalat", key = "''")
    public List<KoodistoKoodi> getKoulutusalat() {
        return new ArrayList<>(opintopolkuService.getKoulutusalaKoodit());
    }

    @Cacheable(value = "KoodistoService:getKoulutusala", key = "#koodi")
    public KoodistoKoodi getKoulutusala(final String koodi) {
        return opintopolkuService.getKoulutusalaKoodi(koodi);
    }

    @Cacheable(value = "KoodistoService:getKoulutusalaKoulutukset", key = "#koodi")
    public List<KoodistoKoodi> getKoulutusalaKoulutukset(final String koodi) {
        return new ArrayList<>(opintopolkuService.getKoulutusKooditForKoulutusala(koodi));
    }

    @Cacheable(value = "KoodistoService:getKoulutusToKoulutusalaRelation", key = "''")
    public Map<String, String> getKoulutusToKoulutusalaRelation() {
        final Map<String, String> map = new HashMap<>();
        getKoulutusalat().forEach(koulutusalaKoodi ->
                getKoulutusalaKoulutukset(koulutusalaKoodi.koodiArvo()).forEach(koulutusKoodi ->
                        map.put(koulutusKoodi.koodiArvo(), koulutusalaKoodi.koodiArvo()))
        );
        return map;
    }

    @Cacheable(value = "KoodistoService:getKoulutustyypit", key = "''")
    public List<KoodistoKoodi> getKoulutustyypit() {
        return new ArrayList<>(opintopolkuService.getKoulutustyyppiKoodit());
    }

    @Cacheable(value = "KoodistoService:getKoulutustyyppi", key = "#koodi")
    public KoodistoKoodi getKoulutustyyppi(final String koodi) {
        return opintopolkuService.getKoulutustyyppiKoodi(koodi);
    }

    @Cacheable(value = "KoodistoService:getKoulutustyyppiKoulutukset", key = "#koodi")
    public List<KoodistoKoodi> getKoulutustyyppiKoulutukset(final String koodi) {
        return new ArrayList<>(opintopolkuService.getKoulutusKooditForKoulutustyyppi(koodi));
    }

    @Cacheable(value = "KoodistoService:getKoulutusToKoulutustyyppiRelation", key = "''")
    public Map<String, String> getKoulutusToKoulutustyyppiRelation() {
        final Map<String, String> map = new HashMap<>();

        getKoulutustyypit().stream()
                .filter(koodi -> getAmmatillinenKoulutustyyppiArvot().contains(koodi.koodiArvo()))
                .forEach(koulutustyyppi ->
                        getKoulutustyyppiKoulutukset(koulutustyyppi.koodiArvo())
                                .forEach(koulutusKoodi -> map.put(koulutusKoodi.koodiArvo(), koulutustyyppi.koodiArvo())));
        return map;
    }

    @Cacheable(value = "KoodistoService:getOsaamisalat", key = "''")
    public List<KoodistoKoodi> getOsaamisalat() {
        final List<KoodistoKoodi> osaamisala = new ArrayList<>();
        // Asetuksen mukaiset osaamisalat -> koodisto ei näitä erottele
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "1728", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "1733", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "1734", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "1758", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "1762", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "1773", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2283", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2309", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2311", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2315", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2316", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2317", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2318", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2332", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2387", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2347", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2423", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "3123", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "3137", null));
        return osaamisala;
    }

    @Cacheable(value = "KoodistoService:getOsaamisalaKoulutukset", key = "#koodi")
    public List<KoodistoKoodi> getOsaamisalaKoulutukset(final String koodi) {
        return new ArrayList<>(opintopolkuService.getKoulutusKooditForOsaamisala(koodi));
    }

    private Map<String, Set<KoodistoKoodi>> getKoulutusToOsaamisalatRelation() {
        final Map<String, Set<KoodistoKoodi>> map = new HashMap<>();
        getOsaamisalat().forEach(osaamisalaKoodi ->
                getOsaamisalaKoulutukset(osaamisalaKoodi.getKoodiArvo())
                        .forEach(koulutusKoodi -> {
                            Set<KoodistoKoodi> osaamisalat = map.get(koulutusKoodi.koodiArvo());
                            if (osaamisalat == null) {
                                osaamisalat = new HashSet<>();
                            }
                            osaamisalat.add(osaamisalaKoodi);
                            map.put(koulutusKoodi.koodiArvo(), osaamisalat);
                        }));
        return map;
    }

    @Cacheable(value = "KoodistoService:getAmmatillinenTutkinnot", key = "''")
    public List<KoulutusKoodi> getAmmatillinenTutkinnot() {
        final Map<String, KoulutusKoodi> koulutukset = new HashMap<>();
        final Map<String, String> koulutusToKoulutusala = getKoulutusToKoulutusalaRelation();
        final Map<String, String> koulutusToKoulutustyyppi = getKoulutusToKoulutustyyppiRelation();
        final Map<String, Set<KoodistoKoodi>> koulutusToOsaamisala = getKoulutusToOsaamisalatRelation();

        final Consumer<KoodistoKoodi> includeKoulutus = koulutuskoodi -> {
            final String koulutusalaKoodiArvo = koulutusToKoulutusala.getOrDefault(koulutuskoodi.koodiArvo(), null);
            final String koulutustyyppiKoodiArvo = koulutusToKoulutustyyppi.getOrDefault(koulutuskoodi.koodiArvo(), null);
            final Set<KoodistoKoodi> osaamisalat = koulutusToOsaamisala.getOrDefault(koulutuskoodi.koodiArvo(), new HashSet<>());

            // Voimassaolon päättyminen ja koodisto
            if (null == koulutuskoodi.voimassaLoppuPvm() && koulutuskoodi.koodisto().getKoodistoUri().equals("koulutus")) {

                // Erikoisammattitutkinnoilta kolmosalkuiset pois
                if (!(koulutuskoodi.getKoodiArvo().startsWith("3") && koulutustyyppiKoodiArvo.equals("12"))) {

                    // Lisätään ja päivitetään vanhat uudemmilla koodeilla
                    KoulutusKoodi existing = koulutukset.get(koulutuskoodi.getKoodiArvo());
                    if (existing == null || existing.getVersio() < koulutuskoodi.getVersio()) {
                        koulutukset.put(koulutuskoodi.getKoodiArvo(), new KoulutusKoodi(koulutuskoodi, koulutusalaKoodiArvo, koulutustyyppiKoodiArvo, osaamisalat));
                    }
                }
            }
        };

        getAmmatillinenKoulutustyyppiArvot().forEach(koulutustyyppiKoodiArvo -> getKoulutustyyppiKoulutukset(koulutustyyppiKoodiArvo).forEach(includeKoulutus));
        return Lists.newLinkedList(koulutukset.values());
    }

    @Cacheable(value = "KoodistoService:getTutkintotyypit", key = "''")
    public List<KoodistoKoodi> getTutkintotyypit() {
        return new ArrayList<>(opintopolkuService.getTutkintotyyppiKoodit());
    }

    @Cacheable(value = "KoodistoService:getTutkintotyyppi", key = "#koodi")
    public KoodistoKoodi getTutkintotyyppi(final String koodi) {
        return opintopolkuService.getTutkintotyyppiKoodi(koodi);
    }

    @Cacheable(value = "KoodistoService:getTutkintotyyppiKoulutukset", key = "#koodi")
    public List<KoodistoKoodi> getTutkintotyyppiKoulutukset(final String koodi) {
        return new ArrayList<>(opintopolkuService.getKoulutusKooditForTutkintotyyppi(koodi));
    }

    @Cacheable(value = "KoodistoService:getKoulutusToTutkintotyyppiRelation", key = "''")
    public Map<String, String> getKoulutusToTutkintotyyppiRelation() {
        final Map<String, String> map = new HashMap<>();

        getTutkintotyypit().stream()
                .filter(koodi -> getAmmatillinenTutkintotyyppiArvot().contains(koodi.koodiArvo()))
                .forEach(tutkintotyyppi ->
                        getTutkintotyyppiKoulutukset(tutkintotyyppi.koodiArvo())
                                .forEach(koulutusKoodi -> map.put(koulutusKoodi.koodiArvo(), tutkintotyyppi.koodiArvo())));

        return map;
    }
}
