package fi.minedu.oiva.backend.template.extension;

import com.mitchellbosecke.pebble.extension.Test;
import fi.minedu.oiva.backend.entity.TranslatedString;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HasTranslationTest implements Test {

    @Override
    public boolean apply(Object translatedString, Map<String, Object> map) {
        if( translatedString == null){
            return false;
        }

        if( map == null || map.get("language") == null){
            return false;
        }
        String lang = ((String) map.get("language")).toLowerCase();
        Boolean value = ((TranslatedString) translatedString).get((String) map.get("language")).isPresent();
        return value;
    }

    @Override
    public List<String> getArgumentNames() {
        String[] params = {"language"};
        return Arrays.asList(params);
    }
}
