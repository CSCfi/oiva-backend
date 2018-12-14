package fi.minedu.oiva.backend.config;

import com.mitchellbosecke.pebble.extension.Filter;
import fi.minedu.oiva.backend.template.extension.MaaraysListFilter;
import fi.minedu.oiva.backend.template.extension.MaaraysTransformerFilter;
import fi.minedu.oiva.backend.template.extension.MuutosListFilter;
import fi.minedu.oiva.backend.template.extension.MuutosTransformerFilter;
import fi.minedu.oiva.backend.template.extension.OivaTemplateExtension;
import fi.minedu.oiva.backend.template.extension.SortListFilter;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static fi.minedu.oiva.backend.template.extension.MaaraysTransformerFilter.Type.htmlClass;
import static fi.minedu.oiva.backend.template.extension.MaaraysTransformerFilter.Type.toimintaAlueArvo;
import static fi.minedu.oiva.backend.template.extension.MaaraysTransformerFilter.Type.ylakoodi;
import static fi.minedu.oiva.backend.template.extension.MuutosTransformerFilter.Type.muutosYlakoodi;
import static fi.minedu.oiva.backend.template.extension.SortListFilter.SortTarget.luvat;
import static fi.minedu.oiva.backend.template.extension.SortListFilter.SortTarget.maaraykset;

@Configuration
public class AmosPebbleConfig extends PebbleConfig {

    @Override
    public OivaTemplateExtension getTemplateExtension() {
        return new OivaTemplateExtension() {
            @Override
            public Map<String, Filter> getFilters() {
                final Map<String, Filter> filters = super.getFilters();
                filters.put("filterMaarays", new MaaraysListFilter());
                filters.put("toimintaAlueArvo", new MaaraysTransformerFilter(toimintaAlueArvo));
                filters.put("ylakoodi", new MaaraysTransformerFilter(ylakoodi));
                filters.put("htmlClass", new MaaraysTransformerFilter(htmlClass));
                filters.put("sortLupa", new SortListFilter(luvat));
                filters.put("sortMaarays", new SortListFilter(maaraykset));
                filters.put("filterMuutos", new MuutosListFilter());
                filters.put("muutosYlakoodi", new MuutosTransformerFilter(muutosYlakoodi));
                return filters;
            }
        };
    }
}