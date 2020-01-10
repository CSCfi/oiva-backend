package fi.minedu.oiva.backend.core.extension;

import com.mitchellbosecke.pebble.template.ScopeChain;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class OivaFormatterFilter extends OivaFilter {

    private static final Logger logger = LoggerFactory.getLogger(OivaFormatterFilter.class);

    public enum Type {
        number,
        capitalize,
        wording,
        pick
    }

    private final Type type;

    private final String argOne = "argument1";

    public OivaFormatterFilter(final Type type) {
        this.type = type;
    }

    @Override
    public Object apply(final Object source, final Map<String, Object> map) {
        final Optional<Locale> localeOpt = getContextScopeLocale(map);

        if(!NotEmptyTest.check(source)) {
            return "";
        } else if(type == Type.number && NumberUtils.isDigits(String.valueOf(source))) {
            final Long value = NumberUtils.toLong(String.valueOf(source));
            if(localeOpt.isPresent()) {
                return NumberFormat.getNumberInstance(localeOpt.get()).format(value);
            }
        } else if(type == Type.capitalize) {
            return StringUtils.capitalize(String.valueOf(source));
        } else if(type == Type.wording) {
            final Optional opt = getFirstArgument(map);
            if(opt.isPresent()) {
                final Object arg = opt.get();
                if(arg instanceof Boolean) return chooseWording(source, BooleanUtils.isTrue((Boolean) arg));
                else if(arg instanceof Collection) return chooseWording(source, ((Collection) arg).size() == 1);
            } else {
                final Optional<Collection> maarayksetOpt = getContextScopeMaaraykset(map);
                if(maarayksetOpt.isPresent()) return chooseWording(source, maarayksetOpt.get().size() == 1);
            }
        } else if(type == Type.pick) {
            final Optional opt = getFirstArgument(map);
            if(opt.isPresent()) {
                return pick(source, String.valueOf(opt.get()));
            }
            return "";
        }
        return source;
    }

    private String chooseWording(final Object source, final boolean singular) {
        final String[] options = StringUtils.split(String.valueOf(source), "|");
        if(null == options) return String.valueOf(source);
        else if(!singular && options.length > 1) return options[1];
        else return options[0];
    }

    private String pick(final Object source, final String picker) {
        final String[] options = getPickOptions(source);
        if(null == options) return String.valueOf(source);
        else {
            final Map<String, String> map = new HashMap<>();
            for(final String option : options) {
                final String[] parts = StringUtils.split(option, "::");
                if(null != parts && parts.length > 1) map.put(StringUtils.trim(parts[0]), StringUtils.trim(parts[1]));
            }
            return map.get(picker);
        }
    }

    private String[] getPickOptions(final Object source) {
        if(source instanceof String) return StringUtils.split(String.valueOf(source), "|");
        else if(source instanceof Collection) {
            final Collection list = (Collection) source;
            return (String[]) list.toArray(new String[list.size()]);
        } else return new String[] {};
    }

    private Optional<Object> getFirstArgument(final Map<String, Object> map) {
        return argExists(map, argOne) ? Optional.of(map.get(argOne)) : Optional.empty();
    }

    public Optional<Collection> getContextScopeMaaraykset(final Map<String, Object> map) {
        final String argMaaraykset = "maaraykset";
        final Optional<ScopeChain> scopeOpt = getContextScope(map);
        if(scopeOpt.isPresent()) {
            final ScopeChain scope = scopeOpt.get();
            return Optional.of(scope.containsKey(argMaaraykset) ? (Collection) scope.get(argMaaraykset) : Collections.emptyList());
        } else return Optional.empty();
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList(new String[]{ argOne });
    }
}
