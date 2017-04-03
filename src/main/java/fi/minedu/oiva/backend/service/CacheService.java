package fi.minedu.oiva.backend.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;

@Service
public class CacheService {

    private final Logger logger = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private CacheManager cacheManager;

    @Value("${redis.userSessionPrefix}")
    private String userSessionPrefix;

    public CompletableFuture<Collection<String>> getCacheNames() {
        return async(() -> cacheManager.getCacheNames());
    }

    /**
     * Flush cache
     * Invoker is responsible checking security context
     *
     * @return duration in millseconds
     */
    public long flushCache(final boolean retainSessions) {
        final long startTime = System.currentTimeMillis();
        final RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        redisTemplate.execute((RedisCallback) connection -> {
            connection.keys(serializer.serialize("*")).stream()
                .filter(key -> !retainSessions || !StringUtils.startsWith(serializer.deserialize(key), userSessionPrefix))
                .forEach(key -> connection.del(key));
            return null;
        });
        final long duration = System.currentTimeMillis() - startTime;
        logger.info("Cache flushed in {}ms", duration);
        return duration;
    }

    /**
     * Flush and pre-populate cache
     * Invoker is responsible checking security context
     *
     * @return duration in millseconds
     */
    public long refreshCache(final boolean retainSessions) {
        flushCache(retainSessions);

        final long startTime = System.currentTimeMillis();

        cache("KoodistoService:getMaakuntaKunnat").put("", koodistoService.getMaakuntaKunnat());
        cache("KoodistoService:getKoulutustoimijat").put("", koodistoService.getKoulutustoimijat());
        cache("KoodistoService:getMaakuntaJarjestajat").put("", koodistoService.getMaakuntaJarjestajat());
        cache("KoodistoService:getKunnat").put("", koodistoService.getKunnat());
        cache("KoodistoService:getKielet").put("", koodistoService.getKielet());
        cache("KoodistoService:getOpetuskielet").put("", koodistoService.getOpetuskielet());
        cache("KoodistoService:getAluehallintovirastoKuntaMap").put("", koodistoService.getKuntaAluehallintovirastoMap());
        cache("KoodistoService:getKuntaMaakuntaMap").put("", koodistoService.getKuntaMaakuntaMap());

        final long duration = System.currentTimeMillis() - startTime;
        logger.info("Cache pre-population finished in {}ms", duration);
        return duration;
    }

    private Cache cache(final String cacheName) {
        logger.info("Pre-populating cache: {}", cacheName);
        return cacheManager.getCache(cacheName);
    }
}
