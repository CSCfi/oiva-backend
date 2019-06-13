package fi.minedu.oiva.backend.core.service;

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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class KoodistoService {

    private final OpintopolkuService opintopolkuService;

    @Value("${koulutustyyppi.ammatillinen.koodiarvot}")
    private String ammatillinenKoulutustyyppiKoodiArvot;

    @Autowired
    public KoodistoService(OpintopolkuService opintopolkuService) {
        this.opintopolkuService = opintopolkuService;
    }

    private List<String> getAmmatillinenKoulutustyyppiArvot() {
        return StringUtils.isNotBlank(ammatillinenKoulutustyyppiKoodiArvot) ? Arrays.asList(StringUtils.split(ammatillinenKoulutustyyppiKoodiArvot, ",")) : Collections.emptyList();
    }

    @Value("${tutkintotyyppi.ammatillinen.koodiarvot}")
    private String ammatillinenTutkintotyyppiKoodiArvot;

    private List<String> getAmmatillinenTutkintotyyppiArvot() {
        return StringUtils.isNotBlank(ammatillinenTutkintotyyppiKoodiArvot) ? Arrays.asList(StringUtils.split(ammatillinenTutkintotyyppiKoodiArvot, ",")) : Collections.emptyList();
    }

    /**
     * Hae koodisto koodistoUrin ja koodistoVersion perusteella. Välimuistitetaan OpintopolkuService -palvelussa
     *
     * @param koodistoUri koodisto uri
     * @param koodistoVersio koodisto versio
     * @return koodisto
     */
    public Koodisto getKoodisto(final String koodistoUri, final Integer koodistoVersio) {
        return opintopolkuService.getKoodisto(koodistoUri, koodistoVersio);
    }

    /**
     * Hae kaikki koodiston koodit koodistoUrin ja koodistoVersion perusteella. Välimuistitetaan OpintopolkuService -palvelussa
     *
     * @param koodistoUri koodisto uri
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
     * @param koodistoUri koodisto uri
     * @param koodiArvo koodiarvo
     * @param koodistoVersio koodisto versio
     * @return Koodi
     */
    public KoodistoKoodi getKoodi(final String koodistoUri, final String koodiArvo, final Integer koodistoVersio) {
        return opintopolkuService.getKoodi(koodistoUri, koodiArvo, koodistoVersio);
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
        return opintopolkuService.getMaakuntaKunnat();
    }

    @Cacheable(value = "KoodistoService:getKoulutustoimijat", key = "''")
    public List<Organisaatio> getKoulutustoimijat() {
        return opintopolkuService.getKoulutustoimijat();
    }

    @Cacheable(value = "KoodistoService:getMaakuntaJarjestajat", key = "''")
    public List<Maakunta> getMaakuntaJarjestajat() {
        return opintopolkuService.getMaakuntaJarjestajat();
    }

    @Cacheable(value = "KoodistoService:getKunta")
    public KoodistoKoodi getKunta(final String koodi) {
        return opintopolkuService.getKuntaKoodi(koodi).orElseGet(null);
    }

    @Cacheable(value = {"KoodistoService:getKieli"})
    public KoodistoKoodi getKieli(final String koodi) {
        final KoodistoKoodi kieliKoodisto = opintopolkuService.getKieliKoodi(koodi);

        // AM-308 fix usable until koodisto is fixed
        if(StringUtils.equalsIgnoreCase(kieliKoodisto.koodiArvo(), "SE")) {
            kieliKoodisto.getMetadataList().forEach(m -> m.setNimi("saame"));
        }
        return kieliKoodisto;
    }

    /**
     * Käytetään vain seuraavia opetuskieliä
     *  - suomi (koodiArvo: 1)
     *  - ruotsi (koodiArvo: 2)
     *  - saame (koodiArvi: 5)
     */
    @Cacheable(value = "KoodistoService:getOpetuskielet", key = "''")
    public List<KoodistoKoodi> getOpetuskielet() {
        return opintopolkuService.getOppilaitoksenOpetuskieliKoodit().stream()
            .filter(koodistoItem -> Arrays.asList("1", "2", "5").contains(koodistoItem.koodiArvo())).collect(Collectors.toList());
    }

    @Cacheable(value = "KoodistoService:getAluehallintovirastoKuntaMap", key = "''")
    public Map<String, KoodistoKoodi> getKuntaAluehallintovirastoMap() { // TODO: NOT USED, REMOVE?
        final Map<String, KoodistoKoodi> map = new HashMap<>();
        opintopolkuService.getAlueHallintovirastoKoodit().stream().forEach(
            avi -> opintopolkuService.getKuntaKooditForAlueHallintovirasto(avi.koodiArvo()).stream().forEach(
            kunta -> map.put(kunta.koodiArvo(), avi)));
        return map;
    }

    @Cacheable(value = "KoodistoService:getKuntaMaakuntaMap", key = "''")
    public Map<String, KoodistoKoodi> getKuntaMaakuntaMap() { // TODO: NOT USED, REMOVE?
        final Map<String, KoodistoKoodi> map = new HashMap<>();
        opintopolkuService.getMaakuntaKoodit().stream().forEach(
            maakunta -> opintopolkuService.getKuntaKooditForMaakunta(maakunta.koodiArvo()).stream().forEach(
            kunta -> map.put(kunta.koodiArvo(), maakunta)));
        return map;
    }

    @Cacheable(value = "KoodistoService:getKoulutusalat", key = "''")
    public List<KoodistoKoodi> getKoulutusalat() {
        return opintopolkuService.getKoulutusalaKoodit();
    }

    @Cacheable(value = "KoodistoService:getKoulutusala", key = "#koodi")
    public KoodistoKoodi getKoulutusala(final String koodi) {
        return opintopolkuService.getKoulutusalaKoodi(koodi);
    }

    @Cacheable(value = "KoodistoService:getKoulutusalaKoulutukset", key = "#koodi")
    public List<KoodistoKoodi> getKoulutusalaKoulutukset(final String koodi) {
        return opintopolkuService.getKoulutusKooditForKoulutusala(koodi);
    }

    @Cacheable(value = "KoodistoService:getKoulutusToKoulutusalaRelation", key = "''")
    public Map<String, String> getKoulutusToKoulutusalaRelation() {
        final Map<String, String> map = new HashMap<>();
        getKoulutusalat().stream().forEach(koulutusalaKoodi ->
            getKoulutusalaKoulutukset(koulutusalaKoodi.koodiArvo()).stream().forEach(koulutusKoodi ->
                map.put(koulutusKoodi.koodiArvo(), koulutusalaKoodi.koodiArvo()))
        );
        return map;
    }

    @Cacheable(value = "KoodistoService:getKoulutustyypit", key = "''")
    public List<KoodistoKoodi> getKoulutustyypit() {
        return opintopolkuService.getKoulutustyyppiKoodit();
    }

    @Cacheable(value = "KoodistoService:getKoulutustyyppi", key = "#koodi")
    public KoodistoKoodi getKoulutustyyppi(final String koodi) {
        return opintopolkuService.getKoulutustyyppiKoodi(koodi);
    }

    @Cacheable(value = "KoodistoService:getKoulutustyyppiKoulutukset", key = "#koodi")
    public List<KoodistoKoodi> getKoulutustyyppiKoulutukset(final String koodi) {
        return opintopolkuService.getKoulutusKooditForKoulutustyyppi(koodi);
    }

    @Cacheable(value = "KoodistoService:getKoulutusToKoulutustyyppiRelation", key = "''")
    public Map<String, String> getKoulutusToKoulutustyyppiRelation() {
        final Map<String, String> map = new HashMap<>();
        getKoulutustyypit().stream().forEach(koulutustyyppiKoodi ->
            getKoulutustyyppiKoulutukset(koulutustyyppiKoodi.koodiArvo()).stream().forEach(koulutusKoodi -> {
                if(getAmmatillinenKoulutustyyppiArvot().contains(koulutustyyppiKoodi.koodiArvo())) {
                    map.put(koulutusKoodi.koodiArvo(), koulutustyyppiKoodi.koodiArvo());
                }
            })
        );
        return map;
    }

    @Cacheable(value = "KoodistoService:getOsaamisalat", key = "''")
    public List<KoodistoKoodi> getOsaamisalat() {
        final List<KoodistoKoodi> osaamisala = new ArrayList<>();
        // Asetuksen mukaiset osaamisalat -> koodisto ei näitä erottele
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "1728", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "1531", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "1617", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "1588", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "1505", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "1647", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2332", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2315", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2316", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2317", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2318", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2283", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "2227", null));
        osaamisala.add(opintopolkuService.getKoodi("osaamisala", "3137", null));
        return osaamisala;
    }

    @Cacheable(value = "KoodistoService:getOsaamisalaKoulutukset", key = "#koodi")
    public List<KoodistoKoodi> getOsaamisalaKoulutukset(final String koodi) {
        return opintopolkuService.getKoulutusKooditForOsaamisala(koodi);
    }

    @Cacheable(value = "KoodistoService:getKoulutusToOsaamisalaRelation", key = "''")
    public Map<String, KoodistoKoodi> getKoulutusToOsaamisalaRelation() {
        final Map<String, KoodistoKoodi> map = new HashMap<>();
        getOsaamisalat().stream().forEach(osaamisalaKoodi -> {
                getOsaamisalaKoulutukset(osaamisalaKoodi.getKoodiArvo()).stream().forEach(koulutusKoodi -> {
                   map.put(koulutusKoodi.koodiArvo(), osaamisalaKoodi);
                });
            }
        );
        return map;
    }

    @Cacheable(value = "KoodistoService:getAmmatillinenTutkinnot", key = "''")
    public List<KoulutusKoodi> getAmmatillinenTutkinnot() {
        final List<KoulutusKoodi> koulutukset = new ArrayList<>();
        final Map<String, String> koulutusToKoulutusala = getKoulutusToKoulutusalaRelation();
        final Map<String, String> koulutusToKoulutustyyppi = getKoulutusToKoulutustyyppiRelation();
        final Map<String, KoodistoKoodi> koulutusToOsaamisala = getKoulutusToOsaamisalaRelation();

        final Consumer<KoodistoKoodi> includeKoulutus = koodistoKoodi -> {
            final String koulutusalaKoodiArvo = koulutusToKoulutusala.getOrDefault(koodistoKoodi.koodiArvo(), null);
            final String koulutustyyppiKoodiArvo = koulutusToKoulutustyyppi.getOrDefault(koodistoKoodi.koodiArvo(), null);
            final KoodistoKoodi osaamisala = koulutusToOsaamisala.getOrDefault(koodistoKoodi.koodiArvo(), null);

            // Voimassaolon päättyminen ja koodisto
            if(null == koodistoKoodi.voimassaLoppuPvm() && koodistoKoodi.koodisto().getKoodistoUri().equals("koulutus")) {

                // Erikoisammattitutkinnoilta kolmosalkuiset pois
                if(!(koodistoKoodi.getKoodiArvo().startsWith("3") && koulutustyyppiKoodiArvo.equals("12"))) {

                    // Tarkistetaan versio-duplikaatit
                    if(null == koulutukset.stream().filter(koulutusKoodi -> koulutusKoodi.getKoodiArvo().contains(koodistoKoodi.getKoodiArvo())).findFirst().orElse(null)) {

                        koulutukset.add(new KoulutusKoodi(koodistoKoodi, koulutusalaKoodiArvo, koulutustyyppiKoodiArvo, osaamisala));
                    }
                }
            }
        };
        getAmmatillinenKoulutustyyppiArvot().stream().forEach(koulutustyyppiKoodiArvo -> getKoulutustyyppiKoulutukset(koulutustyyppiKoodiArvo).forEach(includeKoulutus::accept));
        return koulutukset;
    }

    @Cacheable(value = "KoodistoService:getTutkintotyypit", key = "''")
    public List<KoodistoKoodi> getTutkintotyypit() {
        return opintopolkuService.getTutkintotyyppiKoodit();
    }

    @Cacheable(value = "KoodistoService:getTutkintotyyppi", key = "#koodi")
    public KoodistoKoodi getTutkintotyyppi(final String koodi) {
        return opintopolkuService.getTutkintotyyppiKoodi(koodi);
    }

    @Cacheable(value = "KoodistoService:getTutkintotyyppiKoulutukset", key = "#koodi")
    public List<KoodistoKoodi> getTutkintotyyppiKoulutukset(final String koodi) {
        return opintopolkuService.getKoulutusKooditForTutkintotyyppi(koodi);
    }

    @Cacheable(value = "KoodistoService:getKoulutusToTutkintotyyppiRelation", key = "''")
    public Map<String, String> getKoulutusToTutkintotyyppiRelation() {
        final Map<String, String> map = new HashMap<>();
        getTutkintotyypit().stream().forEach(tutkintotyyppiKoodi -> {
                getTutkintotyyppiKoulutukset(tutkintotyyppiKoodi.koodiArvo()).stream().forEach(koulutusKoodi -> {
                    if(getAmmatillinenTutkintotyyppiArvot().contains(tutkintotyyppiKoodi.koodiArvo())) {
                    map.put(koulutusKoodi.koodiArvo(), tutkintotyyppiKoodi.koodiArvo());
                    }
                });
            }
        );
        return map;
    }
}
