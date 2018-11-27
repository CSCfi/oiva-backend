package fi.minedu.oiva.backend.service;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private PrinceXMLService princeXMLService;

    public void healthCheck() throws Exception {

        // check database connection
        databaseHealthCheck();

        // check redis connection
        cacheService.healthCheck();

        // check princexml
        princeXMLService.healthCheck();
    }

    private void databaseHealthCheck() throws Exception {
        try {
            dsl.settings().withQueryTimeout(3);
            dsl.selectOne().fetch();
        } catch(Exception e) {
            throw new IllegalStateException("Database Failure");
        }
    }
}