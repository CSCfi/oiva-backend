package fi.minedu.oiva.backend.core.task;

import fi.minedu.oiva.backend.core.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

@Component
public class BuildCaches implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = LoggerFactory.getLogger(BuildCaches.class);

    @Value("${redis.refreshOnStartup}")
    private boolean refreshOnStartup;

    @Autowired
    private Environment env;

    @Autowired
    private CacheService cacheService;

    @Scheduled(cron = "0 0 4 * * *")
    public void refreshCaches() {
        logger.info("Scheduled cache refresh started");
        cacheService.refreshCache(false);
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent contextRefreshedEvent) {
        final boolean isDevEnv = !Collections.disjoint(Arrays.asList(env.getActiveProfiles()), Arrays.asList("test", "dev"));
        if (!isDevEnv && refreshOnStartup) {
            refreshCaches();
        }
    }
}
