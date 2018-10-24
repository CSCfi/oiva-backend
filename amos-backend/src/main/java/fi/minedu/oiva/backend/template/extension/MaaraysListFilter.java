package fi.minedu.oiva.backend.template.extension;

import fi.minedu.oiva.backend.entity.oiva.Maarays;
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

public class MaaraysListFilter extends OivaFilter {

    private static final Logger logger = LoggerFactory.getLogger(MaaraysListFilter.class);

    private static final String argTarget = "targetName";

    private final BiFunction<Object, Function<String, Boolean>, Boolean> filterer = (filterTarget, filter) -> {
        final Collection<String> filterTargets = filterTarget instanceof Collection ? ((Collection<String>) filterTarget) : Collections.singletonList((String) filterTarget);
        return filterTargets.stream().anyMatch(filter::apply);
    };
    private final BiFunction<Maarays, Object, Boolean> maaraysTyyppiFilter = (maarays, filter) -> filterer.apply(filter, value -> xor(startsWith(value, "~"), maarays.isMaaraystyyppi(removeStart(value, "~"))));
    private final BiFunction<Maarays, Object, Boolean> maaraysKohdeFilter = (maarays, filter) -> filterer.apply(filter, value -> xor(startsWith(value, "~"), maarays.isKohde(removeStart(value, "~"))));
    private final BiFunction<Maarays, Object, Boolean> maaraysKoodistoFilter = (maarays, filter) -> filterer.apply(filter, value -> xor(startsWith(value, "~"), maarays.isKoodisto(removeStart(value, "~"))));
    private final BiFunction<Maarays, Object, Boolean> maaraysKoodiArvoFilter = (maarays, filter) -> filterer.apply(filter, value -> xor(startsWith(value, "~"), maarays.isKoodiArvo(removeStart(value, "~"))));
    private final BiFunction<Maarays, Object, Boolean> maaraysYlaKoodiFilter = (maarays, filter) -> filterer.apply(filter, value -> xor(startsWith(value, "~"), maarays.hasYlaKoodi(removeStart(value, "~"))));
    private final BiFunction<Maarays, Object, Boolean> maaraysArvoFilter = (maarays, filter) -> filterer.apply(filter, value -> {
        if (startsWith(value, ">") || startsWith(value, "<")) {
            final String filterValue = substring(value, 1);
            if (isDigits(maarays.getArvo()) && isDigits(filterValue)) {
                final Integer numberArvo = NumberUtils.toInt(maarays.getArvo());
                final Integer numberFilter = NumberUtils.toInt(filterValue);
                return equalsIgnoreCase(">", substring(value, 0, 1)) ? numberArvo > numberFilter : numberArvo < numberFilter;
            } return false;
        } else return xor(startsWith(value, "~"), equalsIgnoreCase(maarays.getArvo(), removeStart(value, "~")));
    });
    private final BiFunction<Maarays, Object, Boolean> chainFilter = (maarays, filterSource) -> {
        final Collection<String> filters = filterSource instanceof Collection ? (Collection<String>) filterSource : Collections.singletonList((String)filterSource);
        return filters.stream().allMatch(filter -> applyFilters(maarays, filter));
    };
    private final BiFunction<Maarays, Object, Boolean> alimaaraysChainFilter = (maarays, filter) ->
        maarays.hasAliMaarays() ? maarays.aliMaaraykset().stream().anyMatch(alimaarays -> chainFilter.apply(alimaarays, filter)) : false;

    @Override
    public Object apply(final Object obj, final Map<String, Object> map) {
        final Optional<Collection<Maarays>> maarayksetOpt = asMaaraysList(obj);
        final Optional targetOpt = getTarget(map);
        if(maarayksetOpt.isPresent() && targetOpt.isPresent()) {
            return maarayksetOpt.get().stream().filter(maarays -> chainFilter.apply(maarays, targetOpt.get())).collect(toList());
        } return Collections.emptyList();
    }

    private boolean applyFilters(final Maarays maarays, final String filter) {
        final String filterType = trim(substringBefore(filter, ":"));
        final Collection<String> filterTarget = Arrays.asList(split(trim(substringAfter(filter, ":")), "|"));
        if(equalsIgnoreCase(filterType, "tyyppi")) return maaraysTyyppiFilter.apply(maarays, filterTarget);
        else if(equalsIgnoreCase(filterType, "kohde")) return maaraysKohdeFilter.apply(maarays, filterTarget);
        else if(equalsIgnoreCase(filterType, "koodisto")) return maaraysKoodistoFilter.apply(maarays, filterTarget);
        else if(equalsIgnoreCase(filterType, "koodiarvo")) return maaraysKoodiArvoFilter.apply(maarays, filterTarget);
        else if(equalsIgnoreCase(filterType, "ylakoodi")) return maaraysYlaKoodiFilter.apply(maarays, filterTarget);
        else if(equalsIgnoreCase(filterType, "arvo")) return maaraysArvoFilter.apply(maarays, filterTarget);
        else if(equalsIgnoreCase(filterType, "alimaarays")) return alimaaraysChainFilter.apply(maarays, filterTarget);
        else return false;
    }

    private boolean xor(boolean... booleans) {
        return BooleanUtils.xor(booleans);
    }

    private Optional<Collection<Maarays>> asMaaraysList(final Object obj) {
        if(null != obj && obj instanceof Collection) {
            final Collection<Maarays> list = (Collection<Maarays>) obj;
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

    public static Collection apply(final Collection<Maarays> maaraykset, final String... filtersArg) {
        final Map<String, Object> map = new HashMap();
        if(null != filtersArg) {
            final Collection<String> filters = new ArrayList();
            for(final String filter : filtersArg) {
                filters.add(filter);
            }
            map.put(argTarget, filters);
        }
        return (Collection) new MaaraysListFilter().apply(maaraykset, map);
    }
}
