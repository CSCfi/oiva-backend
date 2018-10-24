package fi.minedu.oiva.backend.template.extension;

import fi.minedu.oiva.backend.entity.oiva.Muutos;
import fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MuutosTransformerFilter extends OivaFilter {

    private static final Logger logger = LoggerFactory.getLogger(MuutosTransformerFilter.class);

    private final String argOne = "argument1";

    public static final String toimintaAlueValtakunnallinen = "33";
    public static final String toimintaAlueEiArvoa = "00";

    private final Type type;

    public enum Type {
        toimintaAlueArvo,
        htmlClass,
        muutosYlakoodi
    }

    public MuutosTransformerFilter(final Type type) {
        this.type = type;
    }

    @Override
    public Object apply(final Object obj, final Map<String, Object> map) {
        final Optional<Collection<Muutos>> muutoksetOpt = asMuutosList(obj);
        if(type == Type.toimintaAlueArvo) {
            return toToimintaAlueArvo(muutoksetOpt);
        //} else if(type == Type.htmlClass) {
          //  return toHtmlClasses(muutoksetOpt);
        } else if(type == Type.muutosYlakoodi) {
            return toYlakoodi(muutoksetOpt, map);
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
     * @param muutoksetOpt list of Muutos
     * @return toiminta-alue arvo
     */
    public String toToimintaAlueArvo(final Optional<Collection<Muutos>> muutoksetOpt) {
        if(muutoksetOpt.isPresent()) {
            if(muutoksetOpt.get().stream().anyMatch(this::isValtakunnallinen)) return toimintaAlueValtakunnallinen;
            else if(muutoksetOpt.get().stream().anyMatch(this::isAlueellinen)) {
                final long maakuntaCount = Math.min(muutoksetOpt.get().stream().filter(this::isMaakunta).count(), 2);
                final long kuntaCount = Math.min(muutoksetOpt.get().stream().filter(this::isKunta).count(), 2);
                return String.valueOf(maakuntaCount + "" + kuntaCount);
            }
        } return toimintaAlueEiArvoa;
    }

    private boolean isValtakunnallinen(final Muutos muutos) {
        return null != muutos && muutos.isKoodi("nuts1", "FI1");
    }

    private boolean isAlueellinen(final Muutos muutos) {
        return isMaakunta(muutos) || isKunta(muutos);
    }

    private boolean isMaakunta(final Muutos muutos) {
        return null != muutos && muutos.isKoodisto("maakunta");
    }

    private boolean isKunta(final Muutos muutos) {
        return null != muutos && muutos.isKoodisto("kunta");
    }
/*
    public String toHtmlClasses(final Optional<Collection<Muutos>> muutoksetOpt) {
        if(muutoksetOpt.isPresent()) {
            return muutoksetOpt.get().stream().map(muutos -> StringUtils.joinWith(" ", muutos.tyyppi(), muutos.koodiUri())).collect(Collectors.joining(" "));
        } return "";
    }
    */

    public KoodistoKoodi toYlakoodi(final Optional<Collection<Muutos>> muutoksetOpt, final Map<String, Object> map) {
        final Optional argOpt = getFirstArgument(map);
        if(muutoksetOpt.isPresent() && argOpt.isPresent()) {
            final String ylaKoodiUri = (String) argOpt.get();
            return muutoksetOpt.get().stream().filter(muutos -> muutos.hasYlaKoodi(ylaKoodiUri)).map(muutos ->
                    Arrays.stream(muutos.ylaKoodit()).filter(koodi -> koodi.isKoodi(ylaKoodiUri)).findFirst()
            ).findAny().orElse(Optional.empty()).orElse(null);
        } return null;
    }

    private Optional<Collection<Muutos>> asMuutosList(final Object source) {
        if(null != source && source instanceof Collection) {
            final Collection<Muutos> list = (Collection<Muutos>) source;
            return Optional.ofNullable(list);
        } else if(null != source && source instanceof Muutos) {
            return Optional.ofNullable(Collections.singletonList((Muutos)source));
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
