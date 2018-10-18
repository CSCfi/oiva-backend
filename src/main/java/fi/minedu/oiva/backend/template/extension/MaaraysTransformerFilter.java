package fi.minedu.oiva.backend.template.extension;

import fi.minedu.oiva.backend.entity.oiva.Maarays;
import fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        ylakoodi
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
            }
        } return toimintaAlueEiArvoa;
    }

    private boolean isValtakunnallinen(final Maarays maarays) {
        return null != maarays && maarays.isKoodi("nuts1", "FI1");
    }

    private boolean isAlueellinen(final Maarays maarays) {
        return isMaakunta(maarays) || isKunta(maarays);
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
