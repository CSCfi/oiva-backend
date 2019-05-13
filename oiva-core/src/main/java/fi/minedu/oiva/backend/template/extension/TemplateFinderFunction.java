package fi.minedu.oiva.backend.template.extension;

import com.mitchellbosecke.pebble.template.ScopeChain;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TemplateFinderFunction extends OivaFunction {

    private static final Logger logger = LoggerFactory.getLogger(TemplateFinderFunction.class);

    private final String scopeArgPath;
    private final String argTemplateName = "templateName";

    public TemplateFinderFunction(final String argName) {
        this.scopeArgPath = argName;
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList(new String[]{ argTemplateName });
    }

    @Override
    public Object execute(final Map<String, Object> args) {
        final Optional<ScopeChain> scopeOpt = getContextScope(args);
        final Optional<String> languageOpt = getContextScopeLanguage(args);
        if(scopeOpt.isPresent() && languageOpt.isPresent()) {
            final ScopeChain scope = scopeOpt.get();
            return getPath(scope) + StringUtils.removeStart(getTemplateName(args), "/") + getLanguageSuffix(languageOpt);
        } else logger.error("Failed to process templateFinder function");
        return "";
    }

    private String getTemplateName(final Map<String, Object> map) {
        return argExists(map, argTemplateName) ? getArgAsString(map, argTemplateName) : "";
    }

    private String getPath(final ScopeChain scope) {
        final String path = getScopeArgumentAsString(scope, scopeArgPath);
        return org.apache.commons.lang3.StringUtils.isNoneBlank(path) ? path + "/" : "";
    }

    private String getLanguageSuffix(final Optional<String> languageOpt) {
        return languageOpt.isPresent() ? "_" + languageOpt.get() : "";
    }
}
