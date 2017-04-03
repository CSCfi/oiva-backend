package fi.minedu.oiva.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableCaching
@Profile({"!test"})
public class CacheConfig {
    @Autowired
    RedisTemplate redisTemplate;

    @Bean
    CacheManager cacheManager() {
        RedisCacheManager cm = new RedisCacheManager(redisTemplate);
        cm.setUsePrefix(true);
        return cm;
    }
}
