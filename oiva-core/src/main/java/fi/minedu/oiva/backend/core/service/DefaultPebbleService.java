package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.core.config.PebbleConfig;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
public class DefaultPebbleService extends BasePebbleService {
    public DefaultPebbleService(PebbleConfig pebble) {
        super(pebble);
    }
}
