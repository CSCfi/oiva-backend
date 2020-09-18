package fi.minedu.oiva.backend.core.config;

import com.mitchellbosecke.pebble.extension.Filter;
import fi.minedu.oiva.backend.core.extension.MaaraysListFilter;
import fi.minedu.oiva.backend.core.extension.MaaraysTransformerFilter;
import fi.minedu.oiva.backend.core.extension.MuutosListFilter;
import fi.minedu.oiva.backend.core.extension.MuutosTransformerFilter;
import fi.minedu.oiva.backend.core.extension.OivaTemplateExtension;
import fi.minedu.oiva.backend.core.extension.SortByOrganisationFilter;
import fi.minedu.oiva.backend.core.extension.SortListFilter;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static fi.minedu.oiva.backend.core.extension.MaaraysTransformerFilter.Type.htmlClass;
import static fi.minedu.oiva.backend.core.extension.MaaraysTransformerFilter.Type.toimintaAlueArvo;
import static fi.minedu.oiva.backend.core.extension.MaaraysTransformerFilter.Type.tutkintokieliGroup;
import static fi.minedu.oiva.backend.core.extension.MaaraysTransformerFilter.Type.ylakoodi;
import static fi.minedu.oiva.backend.core.extension.MuutosTransformerFilter.Type.muutosYlakoodi;
import static fi.minedu.oiva.backend.core.extension.SortListFilter.SortTarget.luvat;
import static fi.minedu.oiva.backend.core.extension.SortListFilter.SortTarget.maaraykset;
import static fi.minedu.oiva.backend.core.extension.SortListFilter.SortTarget.muutokset;

@Configuration
public class OivaPebbleConfig extends PebbleConfig {

    @Override
    public OivaTemplateExtension getTemplateExtension() {
        return new OivaTemplateExtension() {
            @Override
            public Map<String, Filter> getFilters() {
                final Map<String, Filter> filters = super.getFilters();
                filters.put("filterMaarays", new MaaraysListFilter());
                filters.put("toimintaAlueArvo", new MaaraysTransformerFilter(toimintaAlueArvo));
                filters.put("muutosToimintaAlueArvo", new MuutosTransformerFilter(MuutosTransformerFilter.Type.toimintaAlueArvo));
                filters.put("ylakoodi", new MaaraysTransformerFilter(ylakoodi));
                filters.put("htmlClass", new MaaraysTransformerFilter(htmlClass));
                filters.put("groupTutkintokielet", new MaaraysTransformerFilter(tutkintokieliGroup));
                filters.put("sortLupa", new SortListFilter(luvat));
                filters.put("sortMaarays", new SortListFilter(maaraykset));
                filters.put("sortMuutos", new SortListFilter(muutokset));
                filters.put("filterMuutos", new MuutosListFilter());
                filters.put("muutosYlakoodi", new MuutosTransformerFilter(muutosYlakoodi));
                filters.put("sortByOrganizationName", new SortByOrganisationFilter());
                return filters;
            }
        };
    }
}