package fi.minedu.oiva.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service
/**
 * KeyValue interface to Redis
 */
public class KeyValueService {

    @Value("${redis.kvstore.keyPrefix}")
    private String kvKeyPrefix;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Resource(name = "redisTemplate")
    private ValueOperations<Object, Object> valueOps;

    public Optional<String> get(String key) {
        return Optional.ofNullable((String) valueOps.get(kvKeyPrefix + key));
    }

    @PreAuthorize("isSignedIn()")
    public void put(String key, String value) {
        valueOps.set(kvKeyPrefix + key, value);
    }
}
