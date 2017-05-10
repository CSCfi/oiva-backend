package fi.minedu.oiva.backend.template.extension;

import fi.minedu.oiva.backend.entity.Maarays;
import fi.minedu.oiva.backend.entity.MaaraystyyppiValue;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.isDigits;

public class MaaraysListFilter extends OivaFilter {

    public enum Method {
        combo,
        byKohdeTunniste,
        byKoodistoUri,
    }

    private static final Logger logger = LoggerFactory.getLogger(MaaraysListFilter.class);

    private final Method method;
    private final Collection<MaaraystyyppiValue> targetTypes;

    public MaaraysListFilter(final Method method, final MaaraystyyppiValue... types) {
        this.method = method;
        this.targetTypes = null != types ? Arrays.asList(types) : Collections.emptyList();
    }

    private final String argTarget = "targetName";

    @Override
    public Object apply(final Object obj, final Map<String, Object> map) {
        final BiFunction<Object, Function<String, Boolean>, Boolean> filterer = (filterTarget, filter) -> {
            final Collection<String> filterTargets = filterTarget instanceof Collection ? ((Collection<String>) filterTarget) : Collections.singletonList((String) filterTarget);
            return filterTargets.stream().anyMatch(filter::apply);
        };
        final BiFunction<Maarays, Object, Boolean> maaraystyyppiFilter = (maarays, filter) -> filterer.apply(filter, value -> xor(startsWith(value, "~"), maarays.isMaaraystyyppi(removeStart(value, "~"))));
        final Function<Maarays, Boolean> targetTypeFilter = maarays -> this.targetTypes.isEmpty() || maaraystyyppiFilter.apply(maarays, getTargetTypeStrings());
        final BiFunction<Maarays, Object, Boolean> maaraysKohdeFilter = (maarays, filter) -> filterer.apply(filter, value -> xor(startsWith(value, "~"), maarays.isKohde(removeStart(value, "~"))));
        final BiFunction<Maarays, Object, Boolean> maaraysKoodistoFilter = (maarays, filter) -> filterer.apply(filter, value -> xor(startsWith(value, "~"), maarays.isKoodisto(removeStart(value, "~"))));
        final BiFunction<Maarays, Object, Boolean> maaraysYlaKoodiFilter = (maarays, filter) -> filterer.apply(filter, value -> xor(startsWith(value, "~"), maarays.hasYlaKoodi(removeStart(value, "~"))));
        final BiFunction<Maarays, Object, Boolean> maaraysArvoFilter = (maarays, filter) -> filterer.apply(filter, value -> {
            if (startsWith(value, ">") || startsWith(value, "<")) {
                final String filterValue = substring(value, 1);
                if (isDigits(maarays.getArvo()) && isDigits(filterValue)) {
                    final Integer numberArvo = NumberUtils.toInt(maarays.getArvo());
                    final Integer numberFilter = NumberUtils.toInt(filterValue);
                    return equalsIgnoreCase(">", substring(value, 0, 1)) ? numberArvo > numberFilter : numberArvo < numberFilter;
                } return false;
            } else return xor(startsWith(value, "~"), equalsIgnoreCase(maarays.getArvo(), removeStart(value, "~")));
        });

        final BiFunction<Maarays, Object, Boolean> comboFilter = (maarays, filterSource) -> {
            final Collection<String> filters = filterSource instanceof Collection ? (Collection<String>) filterSource : Collections.singletonList((String)filterSource);
            return filters.stream().allMatch(filter -> {
                final String filterType = trim(substringBefore(filter, ":"));
                final Collection<String> filterTarget = Arrays.asList(split(trim(substringAfter(filter, ":")), "|"));
                if(equalsIgnoreCase(filterType, "tyyppi")) return maaraystyyppiFilter.apply(maarays, filterTarget);
                else if(equalsIgnoreCase(filterType, "kohde")) return maaraysKohdeFilter.apply(maarays, filterTarget);
                else if(equalsIgnoreCase(filterType, "koodisto")) return maaraysKoodistoFilter.apply(maarays, filterTarget);
                else if(equalsIgnoreCase(filterType, "ylakoodi")) return maaraysYlaKoodiFilter.apply(maarays, filterTarget);
                else if(equalsIgnoreCase(filterType, "arvo")) return maaraysArvoFilter.apply(maarays, filterTarget);
                else return false;
            });
        };
        final Optional<Collection<Maarays>> maarayksetOpt = asMaaraysList(obj);
        if(maarayksetOpt.isPresent()) {
            final Optional targetOpt = getTarget(map);
            if(targetOpt.isPresent() && method == Method.combo) {
                return maarayksetOpt.get().stream()
                    .filter(maarays -> targetTypeFilter.apply(maarays))
                    .filter(maarays -> comboFilter.apply(maarays, targetOpt.get()))
                    .collect(toList());
            } else if(targetOpt.isPresent() && method == Method.byKohdeTunniste) {
                return maarayksetOpt.get().stream()
                    .filter(maarays -> targetTypeFilter.apply(maarays))
                    .filter(maarays -> maaraysKohdeFilter.apply(maarays, targetOpt.get()))
                    .collect(toList());
            } else if(targetOpt.isPresent() && method == Method.byKoodistoUri) {
                return maarayksetOpt.get().stream()
                    .filter(maarays -> targetTypeFilter.apply(maarays))
                    .filter(maarays -> maaraysKoodistoFilter.apply(maarays, targetOpt.get()))
                    .collect(toList());
            }
        } else logger.warn("Unsupported source");
        return Collections.emptyList();
    }

    private boolean xor(boolean... booleans) {
        return BooleanUtils.xor(booleans);
    }

    private Collection<String> getTargetTypeStrings() {
        final List<String> targets = new ArrayList<>();
        this.targetTypes.stream().forEach(maaraystyyppi -> targets.add(maaraystyyppi.name()));
        return targets;
    }

    private Optional<Collection<Maarays>> asMaaraysList(final Object obj) {
        if(obj instanceof Collection) {
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
}
