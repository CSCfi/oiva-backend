package fi.minedu.oiva.backend.template.extension;

import com.mitchellbosecke.pebble.template.ScopeChain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Filter to apply separator character to loop entries in case of non-last loop.
 */
public class SeparatorFilter extends OivaFilter {

    private final String separator;

    public SeparatorFilter(final String separator) {
        this.separator = separator;
    }

    @Override
    public Object apply(final Object obj, final Map<String, Object> map) {
        return NotEmptyTest.check(obj) ? obj + (applySeparator(map) ? this.separator : "") : "";
    }

    private boolean applySeparator(final Map<String, Object> map) {
        final Optional<Map<String, Integer>> loopOpt = getLoop(map);
        if(loopOpt.isPresent()) {
            final Map<String, Integer> loop = loopOpt.get();
            return loop.get("length") - loop.get("index") > 1;
        } else return false;
    }

    @Override
    public List<String> getArgumentNames() {
        return defaultArgumentNames();
    }

    private Optional<Map<String, Integer>> getLoop(final Map<String, Object> map) {
        final Optional<ScopeChain> scopeOpt = getContextScope(map);
        if(scopeOpt.isPresent()) {
            return Optional.ofNullable((HashMap<String, Integer>) getScopeArgument(scopeOpt.get(), "loop"));
        } else return Optional.empty();
    }
}
