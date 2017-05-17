package fi.minedu.oiva.backend.template.extension;

import fi.minedu.oiva.backend.entity.Maarays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ToToimialaKoodiFilter extends OivaFilter {

    private static final Logger logger = LoggerFactory.getLogger(ToToimialaKoodiFilter.class);

    @Override
    public Object apply(final Object obj, final Map<String, Object> map) {
        final Optional<Collection<Maarays>> maarayksetOpt = asMaaraysList(obj);
        if(maarayksetOpt.isPresent()) {
            if(maarayksetOpt.get().stream().anyMatch(this::isValtakunnallinen)) return "33"; // Valtakunnallinen
            else if(maarayksetOpt.get().stream().anyMatch(this::isToimintaAlue)) {
                final long maakuntaCount = Math.min(maarayksetOpt.get().stream().filter(this::isMaakunta).count(), 2);
                final long kuntaCount = Math.min(maarayksetOpt.get().stream().filter(this::isKunta).count(), 2);
                return String.valueOf(maakuntaCount + "" + kuntaCount);
            }
        } return "00"; // Ei toiminta-aluetta
    }

    private boolean isValtakunnallinen(final Maarays maarays) {
        return null != maarays && maarays.isKoodisto("nuts1") && maarays.isKoodiArvo("FI1");
    }

    private boolean isToimintaAlue(final Maarays maarays) {
        return isMaakunta(maarays) || isKunta(maarays);
    }

    private boolean isMaakunta(final Maarays maarays) {
        return null != maarays && maarays.isKoodisto("maakunta");
    }

    private boolean isKunta(final Maarays maarays) {
        return null != maarays && maarays.isKoodisto("kunta");
    }

    private Optional<Collection<Maarays>> asMaaraysList(final Object obj) {
        if(null != obj && obj instanceof Collection) {
            final Collection<Maarays> list = (Collection<Maarays>) obj;
            return Optional.ofNullable(list);
        } return Optional.empty();
    }

    @Override
    public List<String> getArgumentNames() {
        return defaultArgumentNames();
    }
}
