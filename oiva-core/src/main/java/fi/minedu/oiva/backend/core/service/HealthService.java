package fi.minedu.oiva.backend.core.service;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class HealthService {

    private final DSLContext dsl;
    private final CacheService cacheService;
    private final PrinceXMLService princeXMLService;
    private static final Logger logger = LoggerFactory.getLogger(HealthService.class);

    @Autowired
    HealthService(DSLContext dsl, CacheService cacheService, PrinceXMLService princeXMLService) {
        this.dsl = dsl;
        this.cacheService = cacheService;
        this.princeXMLService = princeXMLService;
    }

    public Map<String, Boolean> healthCheck() throws Exception {
        Map<String, Boolean> result = new HashMap<>();
        result.put("database", databaseHealthCheck());
        result.put("cache", cacheService.healthCheck());
        result.put("princexml", princeXMLService.healthCheck());
        return result;
    }

    private boolean databaseHealthCheck() {
        try {
            dsl.settings().withQueryTimeout(3);
            dsl.selectOne().fetch();
            return true;
        } catch(Exception e) {
            logger.error("Cannot connect to database", e);
            return false;
        }
    }
}