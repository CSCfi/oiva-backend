package fi.minedu.oiva.backend.core.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public abstract class CacheService {

    private final Logger logger = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Value("${redis.userSessionPrefix}")
    private String userSessionPrefix;

    public abstract long refreshCache(final boolean retainSessions);

    /**
     * Return all existing cache keys
     *
     * @return cache keys
     */
    public Collection<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }

    /**
     * Flush cache
     * Invoker is responsible checking security context
     *
     * @return duration in millseconds
     */
    protected long flushCache(final boolean retainSessions) {
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

    protected void flushCacheKeys(final Collection<String> cacheKeys) {
        final RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        redisTemplate.execute((RedisCallback) connection -> {
            cacheKeys.stream().forEach(key -> connection.del(serializer.serialize(key)));
            return null;
        });
    }

    /**
     * Throws exception if cannot connect to redis
     */
    protected void healthCheck() {
        try {
            redisTemplate.getClientList();
        } catch(Exception e) {
            throw new IllegalStateException("Redis failure");
        }
    }
}