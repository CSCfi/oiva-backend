package fi.minedu.oiva.backend.template.extension;

import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.ScopeChain;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public abstract class OivaExtension {

    public static final String languageArg = "language";
    private final String argContext = "_context";

    public boolean hasLanguage(final Map<String, Object> map) {
        return argExists(map, languageArg);
    }

    public boolean argExists(final Map<String, Object> map, String key) {
        return map != null && map.get(key) != null;
    }

    public boolean isArgType(final Map<String, Object> map, String key, Class<?> clazz) {
        return argExists(map, key) ? clazz.isInstance(map.get(key)) : false;
    }

    public Date getArgAsDate(final Map<String, Object> map, String key) {
        return argExists(map, key) ? (Date) map.get(key) : null;
    }

    public Long getArgAsLong(final Map<String, Object> map, String key) {
        return argExists(map, key) && NumberUtils.isDigits(String.valueOf(map.get(key))) ? NumberUtils.toLong(String.valueOf(map.get(key))) : null;
    }

    public String getArgAsString(final Map<String, Object> map, String key) {
        return argExists(map, key) ? (String) map.get(key) : null;
    }

    public boolean getArgAsBoolean(final Map<String, Object> map, String key) {
        return argExists(map, key) ? (boolean) map.get(key) : false;
    }

    public String getLanguage(final Map<String, Object> map) {
        return StringUtils.lowerCase(getArgAsString(map, languageArg));
    }

    public List<String> defaultArgumentNames() {
        return Arrays.asList(new String[]{languageArg});
    }

    public List<String> emptyArgumentNames() {
        return Arrays.asList(new String[0]);
    }

    public Optional<ScopeChain> getContextScope(final Map<String, Object> map) {
        final Optional<EvaluationContext> contextOpt = getContext(map);
        if(contextOpt.isPresent()) {
            final ScopeChain scope = contextOpt.get().getScopeChain();
            return Optional.ofNullable(scope);
        } else {
            return Optional.empty();
        }
    }

    public Optional<String> getContextScopeLanguage(final Map<String, Object> map) {
        final Optional<ScopeChain> scopeOpt = getContextScope(map);
        if(scopeOpt.isPresent()) {
            final ScopeChain scope = scopeOpt.get();
            return Optional.of(scope.containsKey(languageArg) ? String.valueOf(scope.get(languageArg)) : "");
        } else return Optional.empty();
    }

    public Optional<Locale> getContextScopeLocale(final Map<String, Object> map) {
        final Optional<String> languageOpt = getContextScopeLanguage(map);
        if(languageOpt.isPresent()) {
            if(StringUtils.equalsIgnoreCase(languageOpt.get(), "fi")) {
                return Optional.of(new Locale("fi", "FI"));
            } else if(StringUtils.equalsIgnoreCase(languageOpt.get(), "en")) {
                return Optional.of(Locale.UK);
            } else if(StringUtils.equalsIgnoreCase(languageOpt.get(), "sv")) {
                return Optional.of(new Locale("sv", "SE"));
            }
        } return Optional.empty();
    }

    public String getScopeArgumentAsString(final ScopeChain scope, final String argName) {
        return null != argName && scope.containsKey(argName) ? String.valueOf(scope.get(argName)) : "";
    }

    public Object getScopeArgument(final ScopeChain scope, final String argName) {
        return null != argName && scope.containsKey(argName) ? scope.get(argName) : null;
    }

    private Optional<EvaluationContext> getContext(final Map<String, Object> map) {
        return argExists(map, argContext) ? Optional.ofNullable((EvaluationContext) map.get(argContext)) : Optional.empty();
    }
}
