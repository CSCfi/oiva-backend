package fi.minedu.oiva.backend.template.extension;

import fi.minedu.oiva.backend.entity.oiva.Muutos;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.isDigits;

public class MuutosListFilter extends OivaFilter {

    private static final Logger logger = LoggerFactory.getLogger(MuutosListFilter.class);

    private static final String argTarget = "targetName";

    private final BiFunction<Object, Function<String, Boolean>, Boolean> filterer = (filterTarget, filter) -> {
        final Collection<String> filterTargets = filterTarget instanceof Collection ? ((Collection<String>) filterTarget) : Collections.singletonList((String) filterTarget);
        return filterTargets.stream().anyMatch(filter::apply);
    };
    private final BiFunction<Muutos, Object, Boolean> muutosKohdeFilter = (muutos, filter) -> filterer.apply(filter, value -> xor(startsWith(value, "~"), muutos.isKohde(removeStart(value, "~"))));
    private final BiFunction<Muutos, Object, Boolean> muutosKoodistoFilter = (muutos, filter) -> filterer.apply(filter, value -> xor(startsWith(value, "~"), muutos.isKoodisto(removeStart(value, "~"))));
    private final BiFunction<Muutos, Object, Boolean> muutosKoodiArvoFilter = (muutos, filter) -> filterer.apply(filter, value -> xor(startsWith(value, "~"), muutos.isKoodiArvo(removeStart(value, "~"))));
    private final BiFunction<Muutos, Object, Boolean> muutosYlaKoodiFilter = (muutos, filter) -> filterer.apply(filter, value -> xor(startsWith(value, "~"), muutos.hasYlaKoodi(removeStart(value, "~"))));

    private final BiFunction<Muutos, Object, Boolean> muutosArvoFilter = (muutos, filter) -> filterer.apply(filter, value -> {
        if (startsWith(value, ">") || startsWith(value, "<")) {
            final String filterValue = substring(value, 1);
            if (isDigits(muutos.getArvo()) && isDigits(filterValue)) {
                final Integer numberArvo = NumberUtils.toInt(muutos.getArvo());
                final Integer numberFilter = NumberUtils.toInt(filterValue);
                return equalsIgnoreCase(">", substring(value, 0, 1)) ? numberArvo > numberFilter : numberArvo < numberFilter;
            } return false;
        } else return xor(startsWith(value, "~"), equalsIgnoreCase(muutos.getArvo(), removeStart(value, "~")));
    });
    private final BiFunction<Muutos, Object, Boolean> chainFilter = (muutos, filterSource) -> {
        final Collection<String> filters = filterSource instanceof Collection ? (Collection<String>) filterSource : Collections.singletonList((String)filterSource);
        return filters.stream().allMatch(filter -> applyFilters(muutos, filter));
    };
    private final BiFunction<Muutos, Object, Boolean> alimaaraysChainFilter = (muutos, filter) ->
            muutos.hasAliMaarays() ? muutos.aliMaaraykset().stream().anyMatch(alimaarays -> chainFilter.apply(alimaarays, filter)) : false;

    @Override
    public Object apply(final Object obj, final Map<String, Object> map) {
        final Optional<Collection<Muutos>> muutoksetOpt = asMuutosList(obj);
        final Optional targetOpt = getTarget(map);

        if(muutoksetOpt.isPresent() && targetOpt.isPresent()) {
            return muutoksetOpt.get().stream().filter(muutos -> chainFilter.apply(muutos, targetOpt.get())).collect(toList());
        } return Collections.emptyList();
    }

    private boolean applyFilters(final Muutos muutos, final String filter) {
        final String filterType = trim(substringBefore(filter, ":"));
        final Collection<String> filterTarget = Arrays.asList(split(trim(substringAfter(filter, ":")), "|"));

        if(equalsIgnoreCase(filterType, "kohde")) return muutosKohdeFilter.apply(muutos, filterTarget);
        else if(equalsIgnoreCase(filterType, "koodisto")) return muutosKoodistoFilter.apply(muutos, filterTarget);
        else if(equalsIgnoreCase(filterType, "koodiarvo")) return muutosKoodiArvoFilter.apply(muutos, filterTarget);
        else if(equalsIgnoreCase(filterType, "ylakoodi")) return muutosYlaKoodiFilter.apply(muutos, filterTarget);
        else if(equalsIgnoreCase(filterType, "arvo")) return muutosArvoFilter.apply(muutos, filterTarget);
        else if(equalsIgnoreCase(filterType, "alimaarays")) return alimaaraysChainFilter.apply(muutos, filterTarget);
        else return false;
    }

    private boolean xor(boolean... booleans) {
        return BooleanUtils.xor(booleans);
    }

    private Optional<Collection<Muutos>> asMuutosList(final Object obj) {
        if(null != obj && obj instanceof Collection) {
            final Collection<Muutos> list = (Collection<Muutos>) obj;
            return Optional.ofNullable(list);
        } return Optional.empty();
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList(new String[]{argTarget});
    }

    private Optional getTarget(final Map<String, Object> map) {
        if(argExists(map, argTarget)) {
            final Object obj = map.get(argTarget);
            return Optional.of((obj instanceof Collection) ? (Collection) obj : (String) obj);
        } else return Optional.empty();
    }

    public static Collection apply(final Collection<Muutos> muutokset, final String... filtersArg) {
        final Map<String, Object> map = new HashMap();
        if(null != filtersArg) {
            final Collection<String> filters = new ArrayList();
            for(final String filter : filtersArg) {
                filters.add(filter);
            }
            map.put(argTarget, filters);
        }
        return (Collection) new MuutosListFilter().apply(muutokset, map);
    }
}
