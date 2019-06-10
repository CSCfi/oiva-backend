package fi.minedu.oiva.backend.core.extension;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JsonFieldFilter extends OivaFilter {

    private static final Logger logger = LoggerFactory.getLogger(JsonFieldFilter.class);

    public enum ReturnType {
        JsonNode,
        String
    }

    private final ReturnType returnType;
    private final boolean localized;

    private static final String argFieldName = "fieldName";

    public JsonFieldFilter(final ReturnType type, final boolean localized) {
        this.returnType = type;
        this.localized = localized;
    }

    @Override
    public Object apply(final Object jsonSource, final Map<String, Object> map) {
        final Optional<String> languageOpt = getContextScopeLanguage(map);
        if(languageOpt.isPresent()) {
            if(!NotEmptyTest.check(jsonSource)) logger.warn("Blank source");
            else if(jsonSource instanceof JsonNode) {
                final JsonNode json = (JsonNode) jsonSource;
                final Optional<String> fieldOpt = getFieldName(map);
                if(fieldOpt.isPresent()) {
                    final String fieldName = fieldOpt.get() + (localized ? "_" + languageOpt.get()  : "");
                    final JsonNode valueJson = json.get(fieldName);
                    if(null != valueJson) return returnType == ReturnType.String ? valueJson.asText("") : valueJson;
                    else logger.warn("No such json-property: {}\nJson: {}", fieldName, json.toString());
                }
                else logger.warn("fieldName argument was not provided");
            } else logger.warn("Unsupported source");
        } else logger.error("Context scope was not prepared");
        return null;
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList(new String[]{argFieldName});
    }

    public static String getString(final Object source, final String fieldName) {
        return (String) new JsonFieldFilter(ReturnType.String, false).apply(source, Collections.singletonMap(argFieldName, fieldName));
    }

    private Optional<String> getFieldName(final Map<String, Object> map) {
        return argExists(map, argFieldName) ? Optional.of(String.valueOf(map.get(argFieldName))) : Optional.empty();
    }
}
