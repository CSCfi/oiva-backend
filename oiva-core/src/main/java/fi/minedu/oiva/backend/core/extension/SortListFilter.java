package fi.minedu.oiva.backend.core.extension;

import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.oiva.Maarays;
import fi.minedu.oiva.backend.model.entity.oiva.Muutos;
import fi.minedu.oiva.backend.model.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.model.entity.opintopolku.Organisaatio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public class SortListFilter extends OivaFilter {

    public enum SortTarget {
        luvat,
        maaraykset,
        muutokset
    }

    private static final Logger logger = LoggerFactory.getLogger(SortListFilter.class);

    private final SortTarget sortTarget;

    public SortListFilter(final SortTarget sortTarget) {
        this.sortTarget = sortTarget;
    }

    private final String argTarget = "targetName";

    @Override
    public Object apply(final Object obj, final Map<String, Object> map) {
        final Optional<String> languageOpt = getContextScopeLanguage(map);
        final Collection<String> sortBys = getTargets(map).orElse(Collections.emptyList());

        final Comparator<Lupa> lupaSortByDiaarinumero = Comparator.comparing(lupa -> lupa.getDiaarinumero());
        final Comparator<Lupa> lupaSortByEsittelijaNimi = Comparator.comparing(lupa -> JsonFieldFilter.getString(lupa.getMeta(), "esittelija_nimi"));
        final Comparator<Lupa> lupaSortByJarjestaja = Comparator.comparing(lupa -> {
            final Organisaatio jarjestaja = lupa.jarjestaja();
            return null != jarjestaja ? TranslateFilter.fromTranslatedString(jarjestaja.getNimi(), languageOpt) : "";
        });
        final Function<KoodistoKoodi, String> koodistoKoodiNimi = koodi -> null != koodi ? TranslateFilter.fromTranslatedString(koodi.getNimi(), languageOpt) : "";
        final Comparator<Lupa> lupaSortByJarjestajaMaakunta = Comparator.comparing(lupa -> {
            final Organisaatio jarjestaja = lupa.jarjestaja();
            return null != jarjestaja ? koodistoKoodiNimi.apply(jarjestaja.getMaakuntaKoodi()) : "";
        });
        final Comparator<Lupa> lupaSortByJarjestajaKunta = Comparator.comparing(lupa -> {
            final Organisaatio jarjestaja = lupa.jarjestaja();
            return null != jarjestaja ? koodistoKoodiNimi.apply(jarjestaja.getKuntaKoodi()) : "";
        });
        final Function<String, Comparator<Lupa>> toLupaComparators = sortBy -> {
            if (equalsIgnoreCase(sortBy, "esittelija")) return lupaSortByEsittelijaNimi;
            else if (equalsIgnoreCase(sortBy, "jarjestaja")) return lupaSortByJarjestaja;
            else if (equalsIgnoreCase(sortBy, "maakunta")) return lupaSortByJarjestajaMaakunta;
            else if (equalsIgnoreCase(sortBy, "kunta")) return lupaSortByJarjestajaKunta;
            else if (equalsIgnoreCase(sortBy, "diaarinumero")) return lupaSortByDiaarinumero;
            else logger.warn("Unsupported sortLupa option: {}. Using lupaSortByDiaarinumero", sortBy);
            return lupaSortByDiaarinumero;
        };

        final Comparator<Maarays> maaraysSortById = Comparator.comparing(Maarays::getId);
        final Comparator<Maarays> maaraysSortByKoodisto = Comparator.comparing(maarays -> null != maarays.getKoodisto() ? maarays.getKoodisto() : "");
        final Comparator<Maarays> maaraysSortByKoodiarvo = Comparator.comparing(maarays -> null != maarays.getKoodiarvo() ? maarays.getKoodiarvo() : "");
        final Comparator<Maarays> maaraysSortByKoodiNimi = Comparator.comparing(maarays -> {
            final KoodistoKoodi koodi = maarays.koodi();
            return null != koodi ? TranslateFilter.fromTranslatedString(koodi.getNimi(), languageOpt) : "";
        });
        final Function<String, Comparator<Maarays>> toMaaraysComparators = sortBy -> {
            if (equalsIgnoreCase(sortBy, "koodisto")) return maaraysSortByKoodisto;
            else if (equalsIgnoreCase(sortBy, "koodiarvo")) return maaraysSortByKoodiarvo;
            else if (equalsIgnoreCase(sortBy, "koodinimi")) return maaraysSortByKoodiNimi;
            else logger.warn("Unsupported sortMaarays option: {}. Using maaraysSortById", sortBy);
            return maaraysSortById;
        };

        final Comparator<Muutos> muutosSortById = Comparator.comparing(Muutos::getId);
        final Comparator<Muutos> muutosSortByKoodisto = Comparator.comparing(muutos -> muutos.getKoodisto() + "");
        final Comparator<Muutos> muutosSortByKoodiarvo = Comparator.comparing(muutos -> muutos.getKoodiarvo() + "");
        final Comparator<Muutos> muutosSortByKoodiNimi = Comparator.comparing(muutos -> {
            final KoodistoKoodi koodi = muutos.koodi();
            return null != koodi ? TranslateFilter.fromTranslatedString(koodi.getNimi(), languageOpt) : "";
        });
        final Function<String, Comparator<Muutos>> toMuutosComparators = sortBy -> {
            if (equalsIgnoreCase(sortBy, "koodisto")) return muutosSortByKoodisto;
            else if (equalsIgnoreCase(sortBy, "koodiarvo")) return muutosSortByKoodiarvo;
            else if (equalsIgnoreCase(sortBy, "koodinimi")) return muutosSortByKoodiNimi;
            else logger.warn("Unsupported sortMaarays option: {}. Using maaraysSortById", sortBy);
            return muutosSortById;
        };

        if (sortTarget == SortTarget.luvat) {
            return sort(obj, toLupaComparators, sortBys);
        } else if (sortTarget == SortTarget.maaraykset) {
            return sort(obj, toMaaraysComparators, sortBys);
        } else if (sortTarget == SortTarget.muutokset) {
            return sort(obj, toMuutosComparators, sortBys);
        } else {
            logger.warn("Unsupported target");
        }
        return Collections.emptyList();
    }

    private <T> Collection<T> sort(Object obj, Function<String, Comparator<T>> comparator, Collection<String> sortBys) {
        final Optional<Collection<T>> opt = asTypedList(obj);
        if (opt.isPresent()) {
            final List<Comparator<T>> comparators = sortBys.stream().map(comparator).collect(Collectors.toList());
            if(!comparators.isEmpty()) {
                final Comparator<T> comparatorChain = comparators.iterator().next();
                comparators.stream().skip(1).reduce(comparatorChain, Comparator::thenComparing);
                return opt.get().stream().sorted(comparatorChain).collect(Collectors.toList());
            } else {
                logger.warn("No sort options provided");
                return opt.get();
            }
        }
        return Collections.emptyList();
    }

    private <T> Optional<Collection<T>> asTypedList(final Object obj) {
        if(obj instanceof Collection) {
            final Collection<T> list = (Collection<T>) obj;
            return Optional.of(list);
        } return Optional.empty();
    }

    private Optional<Collection> getTargets(final Map<String, Object> map) {
        if(argExists(map, argTarget)) {
            final Object obj = map.get(argTarget);
            return Optional.of((obj instanceof Collection) ? (Collection) obj : Collections.singletonList(obj));
        } else return Optional.empty();
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList(new String[]{argTarget});
    }
}