package fi.minedu.oiva.backend.task;

import fi.minedu.oiva.backend.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;

@Component
public class BuildCaches {

    private final Logger logger = LoggerFactory.getLogger(BuildCaches.class);

    @Autowired
    private Environment env;

    @Autowired
    private CacheService cacheService;

    @PostConstruct
    public void onStart() {
        final boolean isDevEnv = !Collections.disjoint(Arrays.asList(env.getActiveProfiles()), Arrays.asList("test", "dev"));
        if (!isDevEnv) {
            refreshCaches();
        }
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void refreshCaches() {
        logger.info("Scheduled cache refresh started");
        cacheService.refreshCache(false);
    }
}
