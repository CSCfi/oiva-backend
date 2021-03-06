package fi.minedu.oiva.backend.core.extension;

import fi.minedu.oiva.backend.model.entity.oiva.Maarays;
import fi.minedu.oiva.backend.model.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.model.entity.opintopolku.Metadata;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MaaraysTransformerFilter extends OivaFilter {

    private static final Logger logger = LoggerFactory.getLogger(MaaraysTransformerFilter.class);

    private final String argOne = "argument1";

    public static final String toimintaAlueValtakunnallinen = "33";
    public static final String toimintaAlueEiArvoa = "00";

    private final Type type;

    public enum Type {
        toimintaAlueArvo,
        htmlClass,
        ylakoodi,
        tutkintokieliGroup
    }

    public MaaraysTransformerFilter(final Type type) {
        this.type = type;
    }

    @Override
    public Object apply(final Object obj, final Map<String, Object> map) {
        final Optional<Collection<Maarays>> maarayksetOpt = asMaaraysList(obj);
        if(type == Type.toimintaAlueArvo) {
            return toToimintaAlueArvo(maarayksetOpt);
        } else if(type == Type.htmlClass) {
            return toHtmlClasses(maarayksetOpt);
        } else if(type == Type.ylakoodi) {
            return toYlakoodi(maarayksetOpt, map);
        } else if(type == Type.tutkintokieliGroup) {
            return groupTutkintokielet(maarayksetOpt, map);
        } else return "";
    }

    /**
     * Return toiminta-alue arvo which can be one of the following:
     *  - 00: has no toiminta-alue määräyksiä
     *  - 01: has one town
     *  - 02: has more than one town
     *  - 10: has one region
     *  - 11: has one region and one town
     *  - 12: has one region and more than one town
     *  - 20: has more than one region
     *  - 21: has more than one region and one town
     *  - 22: has more than one region and town
     *  - 33: whole Finland
     *
     * @param maarayksetOpt list of Maarays
     * @return toiminta-alue arvo
     */
    public String toToimintaAlueArvo(final Optional<Collection<Maarays>> maarayksetOpt) {
        if(maarayksetOpt.isPresent()) {
            if(maarayksetOpt.get().stream().anyMatch(this::isValtakunnallinen)) return toimintaAlueValtakunnallinen;
            else if(maarayksetOpt.get().stream().anyMatch(this::isAlueellinen)) {
                final long maakuntaCount = Math.min(maarayksetOpt.get().stream().filter(this::isMaakunta).count(), 2);
                final long kuntaCount = Math.min(maarayksetOpt.get().stream().filter(this::isKunta).count(), 2);
                return String.valueOf(maakuntaCount + "" + kuntaCount);
            } else if (maarayksetOpt.get().stream().anyMatch(this::isNotDefined)) {
                return toimintaAlueEiArvoa;
            }
        }
        return null;
    }

    public Map<String, List<String>> groupTutkintokielet(final Optional<Collection<Maarays>> tutkintokieliMaaraykset, final Map<String, Object> map) {
        if (tutkintokieliMaaraykset.isPresent()) {
            String langCode = getContextScopeLanguage(map).map(String::toUpperCase).orElse("FI");
            Map<String, KoodistoKoodi> koodistoKooditMap = (HashMap<String, KoodistoKoodi>) map.get(argOne);
            // Sort maaraykset by tutkintokoodi
            List<Maarays> maaraykset = new ArrayList<>(tutkintokieliMaaraykset.get());
            maaraykset.sort(Comparator.comparing(a -> a.getKoodi().getKoodiArvo()));
            // Find all tutkintokielilanguages used in maaraykset
            final List<String> kielet = new ArrayList<>();
            maaraykset.forEach(m -> m.aliMaaraykset().stream()
                    .filter(alimaarays -> "kieli".equals(alimaarays.getKoodisto()))
                    .forEach(alimaarays -> kielet.add(alimaarays.getKoodiarvo().toUpperCase())));
            ArrayList<String> distinctKielet = kielet.stream().distinct().collect(Collectors.toCollection(ArrayList::new));

            // Group tutkinnot by kieli
            Map<String, List<String>> groupedByKieli = new HashMap<>(Collections.emptyMap());
            distinctKielet.forEach(kieli -> {
                KoodistoKoodi kieliObj = koodistoKooditMap.get(kieli);
                List<String> tutkinnot = getTutkinnotByTutkintokieliLanguage(maaraykset, kieli, langCode);
                for (int i = 0; i < kieliObj.getMetadata().length; i++) {
                    if (kieliObj.getMetadata()[i].getKieli().equals(langCode)) {
                        groupedByKieli.put(kieliObj.getMetadata()[i].getNimi(), tutkinnot);
                        break;
                    }
                }
            });
            // Sort by key
            return new TreeMap<>(groupedByKieli);
        }
        return null;
    }

    private List<String> getTutkinnotByTutkintokieliLanguage(List<Maarays> maaraykset, String kieli, String templateLang) {
        List<String> tutkinnot = new ArrayList<>();
        maaraykset.forEach(m -> {
            if (m.aliMaaraykset().stream().anyMatch(alimaarays -> alimaarays.getKoodiarvo().toUpperCase().equals(kieli))) {
                Metadata metadata = new Metadata();
                for (int i = 0; i < m.getKoodi().metadata().length; i++) {
                    if (m.getKoodi().metadata()[i].getKieli().toUpperCase().equals(templateLang)) {
                        metadata = m.getKoodi().metadata()[i];
                        break;
                    }
                }
                tutkinnot.add(m.getKoodiarvo() + " " + metadata.getNimi());
            }
        });
        return tutkinnot;
    }

    private boolean isValtakunnallinen(final Maarays maarays) {
        return null != maarays && maarays.isKoodi("nuts1", "FI1");
    }

    private boolean isAlueellinen(final Maarays maarays) {
        return isMaakunta(maarays) || isKunta(maarays);
    }

    private boolean isNotDefined(final Maarays maarays) {
        return null != maarays && maarays.isKoodi("nuts1", "FI2");
    }

    private boolean isMaakunta(final Maarays maarays) {
        return null != maarays && maarays.isKoodisto("maakunta");
    }

    private boolean isKunta(final Maarays maarays) {
        return null != maarays && maarays.isKoodisto("kunta");
    }

    public String toHtmlClasses(final Optional<Collection<Maarays>> maarayksetOpt) {
        if(maarayksetOpt.isPresent()) {
            return maarayksetOpt.get().stream().map(maarays -> StringUtils.joinWith(" ", maarays.tyyppi(), maarays.koodiUri())).collect(Collectors.joining(" "));
        } return "";
    }

    public KoodistoKoodi toYlakoodi(final Optional<Collection<Maarays>> maarayksetOpt, final Map<String, Object> map) {
        final Optional argOpt = getFirstArgument(map);
        if(maarayksetOpt.isPresent() && argOpt.isPresent()) {
            final String ylaKoodiUri = (String) argOpt.get();
            return maarayksetOpt.get().stream().filter(maarays -> maarays.hasYlaKoodi(ylaKoodiUri)).map(maarays ->
                Arrays.stream(maarays.ylaKoodit()).filter(koodi -> koodi.isKoodi(ylaKoodiUri)).findFirst()
            ).findAny().orElse(Optional.empty()).orElse(null);
        } return null;
    }

    private Optional<Collection<Maarays>> asMaaraysList(final Object source) {
        if(null != source && source instanceof Collection) {
            final Collection<Maarays> list = (Collection<Maarays>) source;
            return Optional.ofNullable(list);
        } else if(null != source && source instanceof Maarays) {
            return Optional.ofNullable(Collections.singletonList((Maarays)source));
        } return Optional.empty();
    }

    private Optional<Object> getFirstArgument(final Map<String, Object> map) {
        return argExists(map, argOne) ? Optional.of(map.get(argOne)) : Optional.empty();
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList(new String[]{argOne});
    }
}
