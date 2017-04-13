package fi.minedu.oiva.backend.template.extension;

import com.mitchellbosecke.pebble.extension.Filter;
import fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SortByLanguageFilter implements Filter {

    @Override
    public List apply(Object data, Map<String, Object> map) {
        if( data == null || !(data instanceof Collection )){
            return new ArrayList<>();
        }
        if( map == null || map.get("language") == null){
            List dl = new ArrayList<>();
            dl.addAll((Collection) data);
            Collections.sort(dl);
            return dl;
        }

        final String lang = ((String) map.get("language")).toLowerCase();
        final List<KoodistoKoodi> list = Arrays.asList(((Collection<KoodistoKoodi>) data).toArray(new KoodistoKoodi[]{}));
        Collections.sort(list, (KoodistoKoodi a, KoodistoKoodi b) -> a.getTeksti().get(lang).orElse("").compareTo(b.getTeksti().get(lang).orElse("")));
        return list;
    }

    @Override
    public List<String> getArgumentNames() {
        String[] params = {"language"};
        return Arrays.asList(params);
    }
}
