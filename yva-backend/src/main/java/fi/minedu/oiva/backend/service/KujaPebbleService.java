package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.core.config.PebbleConfig;
import fi.minedu.oiva.backend.core.service.PebbleService;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.OivaTemplates;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class KujaPebbleService extends PebbleService {
    public KujaPebbleService(PebbleConfig pebble) {
        super(pebble);
    }

    // TODO: REMOVE ME
    public Optional<String> toListHTML(final List<Lupa> luvat) {
        final Map<String, Object> context = defaultContext(OivaTemplates.RenderOptions.webOptions(OivaTemplates.RenderLanguage.fi), "", "/luvat");
        context.put("luvat", luvat);
        return writeHTML((String) context.get("template"), context);
    }
}
