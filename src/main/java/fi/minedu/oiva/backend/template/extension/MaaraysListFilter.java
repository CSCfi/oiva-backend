package fi.minedu.oiva.backend.template.extension;

import fi.minedu.oiva.backend.entity.Maarays;
import fi.minedu.oiva.backend.entity.MaaraystyyppiValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

public class MaaraysListFilter extends OivaFilter {

    public enum Method {
        byKohdeTunniste,
        byHasKoodiAndArvo,
        byHasOnlyArvo,
        byKoodistoUri,
    }

    private static final Logger logger = LoggerFactory.getLogger(TranslateFilter.class);
    private final Method method;
    private final Collection<MaaraystyyppiValue> targetTypes;

    public MaaraysListFilter(final Method method, final MaaraystyyppiValue... types) {
        this.method = method;
        this.targetTypes = null != types ? Arrays.asList(types) : Collections.emptyList();
    }

    private final String argTarget = "targetName";

    @Override
    public Object apply(final Object obj, final Map<String, Object> map) {
        final Function<Maarays, Boolean> maaraystyyppiFilter = maarays -> this.targetTypes.isEmpty() || targetTypes.stream().anyMatch(tyyppi -> maarays.isMaaraystyyppi(tyyppi));
        final BiFunction<Maarays, Object, Boolean> maaraysKohdeFilter = (maarays, kohdeObj) -> {
            if(kohdeObj instanceof Collection) {
                return ((Collection<String>) kohdeObj).stream().anyMatch(kohde -> maarays.isKohde(kohde));
            } else return maarays.isKohde((String) kohdeObj);
        };
        final BiFunction<Maarays, Object, Boolean> maaraysKoodistoFilter = (maarays, koodistoObj) -> {
            if(koodistoObj instanceof Collection) {
                return ((Collection<String>) koodistoObj).stream().anyMatch(kohde -> maarays.isKoodisto(kohde));
            } else return maarays.isKoodisto((String) koodistoObj);
        };
        final Optional<Collection<Maarays>> maarayksetOpt = asMaaraysList(obj);
        if(maarayksetOpt.isPresent()) {
            final Optional targetOpt = getTarget(map);
            if(targetOpt.isPresent() && method == Method.byKohdeTunniste) {
                return maarayksetOpt.get().stream()
                    .filter(maarays -> maaraystyyppiFilter.apply(maarays))
                    .filter(maarays -> maaraysKohdeFilter.apply(maarays, targetOpt.get()))
                    .collect(toList());
            } else if(targetOpt.isPresent() && method == Method.byKoodistoUri) {
                return maarayksetOpt.get().stream()
                    .filter(maarays -> maaraystyyppiFilter.apply(maarays))
                    .filter(maarays -> maaraysKoodistoFilter.apply(maarays, targetOpt.get()))
                    .collect(toList());
            } else if (method == Method.byHasKoodiAndArvo) {
                return maarayksetOpt.get().stream()
                    .filter(maarays -> maaraystyyppiFilter.apply(maarays))
                    .filter(maarays -> maarays.hasKoodistoKoodiAssociation() && StringUtils.isNotBlank(maarays.getArvo()))
                    .collect(toList());
            } else if (method == Method.byHasOnlyArvo) {
                return maarayksetOpt.get().stream()
                    .filter(maarays -> maaraystyyppiFilter.apply(maarays))
                    .filter(maarays -> !maarays.hasKoodistoKoodiAssociation() && StringUtils.isNotBlank(maarays.getArvo()))
                    .collect(toList());
            }
        } else logger.warn("Unsupported source");
        return Collections.emptyList();
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
