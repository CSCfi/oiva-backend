package fi.minedu.oiva.backend.template.extension;

import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.entity.Maarays;
import fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio;
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
        Luvat,
        Maaraykset
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
        final Comparator<Lupa> lupaSortByJarjestajaKunta = Comparator.comparing(lupa -> {
            final Organisaatio jarjestaja = lupa.jarjestaja();
            if(null != jarjestaja) {
                final KoodistoKoodi kuntaKoodi = jarjestaja.getKuntaKoodi();
                if(null != kuntaKoodi) {
                    return TranslateFilter.fromTranslatedString(kuntaKoodi.getNimi(), languageOpt);
                }
            }
            return "";
        });
        final Function<String, Comparator> toLupaComparators = sortBy -> {
            if (equalsIgnoreCase(sortBy, "esittelija")) return lupaSortByEsittelijaNimi;
            else if (equalsIgnoreCase(sortBy, "kunta")) return lupaSortByJarjestajaKunta;
            else if (equalsIgnoreCase(sortBy, "diaarinumero")) return lupaSortByDiaarinumero;
            else logger.warn("Unsupported sortLuvat option: {}. Using lupaSortByDiaarinumero", sortBy);
            return lupaSortByDiaarinumero;
        };

        final Comparator<Maarays> maaraysSortById = Comparator.comparing(maarays -> maarays.getId());
        final Comparator<Maarays> maaraysSortByKoodisto = Comparator.comparing(maarays -> null != maarays.getKoodisto() ? maarays.getKoodisto() : "");
        final Comparator<Maarays> maaraysSortByKoodiarvo = Comparator.comparing(maarays -> null != maarays.getKoodiarvo() ? maarays.getKoodiarvo() : "");
        final Comparator<Maarays> maaraysSortByKoodiNimi = Comparator.comparing(maarays -> {
            final KoodistoKoodi koodi = maarays.koodi();
            return null != koodi ? TranslateFilter.fromTranslatedString(koodi.getNimi(), languageOpt) : "";
        });
        final Function<String, Comparator> toMaaraysComparators = sortBy -> {
            if (equalsIgnoreCase(sortBy, "koodisto")) return maaraysSortByKoodisto;
            else if (equalsIgnoreCase(sortBy, "koodiarvo")) return maaraysSortByKoodiarvo;
            else if (equalsIgnoreCase(sortBy, "koodinimi")) return maaraysSortByKoodiNimi;
            else logger.warn("Unsupported sortMaaraykset option: {}. Using maaraysSortById", sortBy);
            return maaraysSortById;
        };

        if (sortTarget == SortTarget.Luvat) {
            final Optional<Collection<Lupa>> luvatOpt = asLupaList(obj);
            if (luvatOpt.isPresent()) {
                final List<Comparator> lupaComparators = sortBys.stream().map(toLupaComparators::apply).collect(Collectors.toList());
                if(!lupaComparators.isEmpty()) {
                    final Comparator<Lupa> comparatorChain = lupaComparators.iterator().next();
                    lupaComparators.stream().skip(1).reduce(comparatorChain, (comp, thenComp) -> comp.thenComparing(thenComp));
                    return luvatOpt.get().stream().sorted(comparatorChain).collect(Collectors.toList());
                } else logger.warn("No sort options provided");
                return luvatOpt.get();
            }
        } else if (sortTarget == SortTarget.Maaraykset) {
            final Optional<Collection<Maarays>> maarayksetOpt = asMaaraysList(obj);
            if (maarayksetOpt.isPresent()) {
                final List<Comparator> maaraysComparators = sortBys.stream().map(toMaaraysComparators::apply).collect(Collectors.toList());
                if(!maaraysComparators.isEmpty()) {
                    final Comparator<Maarays> comparatorChain = maaraysComparators.iterator().next();
                    maaraysComparators.stream().skip(1).reduce(comparatorChain, (comp, thenComp) -> comp.thenComparing(thenComp));
                    return maarayksetOpt.get().stream().sorted(comparatorChain).collect(Collectors.toList());
                } else logger.warn("No sort options provided");
                return maarayksetOpt.get();
            }
        } else logger.warn("Unsupported target");
        return Collections.emptyList();
    }

    private Optional<Collection<Lupa>> asLupaList(final Object obj) {
        if(obj instanceof Collection) {
            final Collection<Lupa> list = (Collection<Lupa>) obj;
            return Optional.ofNullable(list);
        } return Optional.empty();
    }

    private Optional<Collection<Maarays>> asMaaraysList(final Object obj) {
        if(obj instanceof Collection) {
            final Collection<Maarays> list = (Collection<Maarays>) obj;
            return Optional.ofNullable(list);
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