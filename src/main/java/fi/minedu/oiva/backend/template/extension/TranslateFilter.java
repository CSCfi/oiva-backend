package fi.minedu.oiva.backend.template.extension;

import com.fasterxml.jackson.databind.JsonNode;
import fi.minedu.oiva.backend.entity.TranslatedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TranslateFilter extends OivaFilter {

    private static final Logger logger = LoggerFactory.getLogger(TranslateFilter.class);

    @Override
    public Object apply(final Object translationSource, final Map<String, Object> map) {
        final Optional<String> languageOpt = getContextScopeLanguage(map);
        if(languageOpt.isPresent()) {
            if(!NotEmptyTest.check(translationSource)) {
                return null;
            } else if(translationSource instanceof TranslatedString) {
                return fromTranslatedString((TranslatedString) translationSource, languageOpt);
            } else if(translationSource instanceof JsonNode) {
                return fromTranslatedString(TranslatedString.of((JsonNode) translationSource), languageOpt);
            } else {
                logger.warn("Unsupported source");
                return translationSource;
            }
        } else logger.error("Context scope was not prepared");
        return null;
    }

    private String fromTranslatedString(final TranslatedString content, final Optional<String> languageOpt) {
        return languageOpt.isPresent() ? content.getOrFirst(languageOpt.get()).orElse("") : "";
    }

    @Override
    public List<String> getArgumentNames() {
        return defaultArgumentNames();
    }
}
