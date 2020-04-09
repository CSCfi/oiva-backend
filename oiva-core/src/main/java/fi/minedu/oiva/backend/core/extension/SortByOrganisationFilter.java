package fi.minedu.oiva.backend.core.extension;

import fi.minedu.oiva.backend.model.entity.oiva.Maarays;
import fi.minedu.oiva.backend.model.entity.opintopolku.KoodistoKoodi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SortByOrganisationFilter extends OivaFilter {

    private static final Logger logger = LoggerFactory.getLogger(SortByOrganisationFilter.class);

    public SortByOrganisationFilter() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object apply(final Object obj, final Map<String, Object> map) {
        final Optional<String> languageOpt = getContextScopeLanguage(map);

        if (obj instanceof List && !((List<?>)obj).isEmpty()) {
            Object item = ((List<?>) obj).get(0);;

            if (item instanceof Maarays) {
                ((List<Maarays>) obj).sort(Comparator.comparing(maarays -> TranslateFilter.fromTranslatedString(maarays.getKoodi().getNimi(), languageOpt)));
            } else if (item instanceof KoodistoKoodi) {
                ((List<KoodistoKoodi>) obj).sort(Comparator.comparing(koodi -> TranslateFilter.fromTranslatedString(koodi.getNimi(), languageOpt)));
            } else {
                logger.warn("Skipping sort. Unknown item type {} in collection", item.getClass());
            }

        } else {
            logger.debug("Skipping sorting of {}", obj);
        }

        return obj;
    }

    @Override
    public List<String> getArgumentNames() {
        return super.emptyArgumentNames();
    }
}