package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.opintopolku.Koodisto;
import fi.minedu.oiva.backend.entity.opintopolku.Maakunta;
import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KoodistoService {

    @Autowired
    private OpintopolkuService opintopolkuService;

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

    @Cacheable(value = "KoodistoService:getKunnat", key = "''")
    public List<Koodisto> getKunnat() {
        return opintopolkuService.getKunnatKoodisto();
    }

    @Cacheable(value = "KoodistoService:getKunta")
    public Koodisto getKunta(final String koodi) {
        return opintopolkuService.getKuntaKoodisto(koodi);
    }

    @Cacheable(value = "KoodistoService:getKielet", key = "''")
    public List<Koodisto> getKielet() {
        return opintopolkuService.getKieletKoodisto();
    }

    @Cacheable(value = {"KoodistoService:getKieli"})
    public Koodisto getKieli(final String koodi) {
        final Koodisto kieliKoodisto = opintopolkuService.getKieliKoodisto(koodi);

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
    public List<Koodisto> getOpetuskielet() {
        return opintopolkuService.getOppilaitoksenOpetuskieletKoodisto().stream()
            .filter(koodistoItem -> Arrays.asList("1", "2", "5").contains(koodistoItem.koodiArvo())).collect(Collectors.toList());
    }

    @Cacheable(value = "KoodistoService:getAluehallintovirastoKuntaMap", key = "''")
    public Map<String, Koodisto> getKuntaAluehallintovirastoMap() {
        final Map<String, Koodisto> map = new HashMap<>();
        opintopolkuService.getAlueHallintovirastotKoodisto().stream().forEach(
            avi -> opintopolkuService.getKunnatForAlueHallintovirastoKoodisto(avi.koodiArvo()).stream().forEach(
            kunta -> map.put(kunta.koodiArvo(), avi)));
        return map;
    }

    @Cacheable(value = "KoodistoService:getKuntaMaakuntaMap", key = "''")
    public Map<String, Koodisto> getKuntaMaakuntaMap() {
        final Map<String, Koodisto> map = new HashMap<>();
        opintopolkuService.getMaakunnatKoodisto().stream().forEach(
            maakunta -> opintopolkuService.getMaakuntaKunnatKoodisto(maakunta.koodiArvo()).stream().forEach(
            kunta -> map.put(kunta.koodiArvo(), maakunta)));
        return map;
    }
}
