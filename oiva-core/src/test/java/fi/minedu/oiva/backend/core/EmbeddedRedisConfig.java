package fi.minedu.oiva.backend.core;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.IfProfileValue;
import redis.embedded.RedisServer;

@TestConfiguration
@IfProfileValue(name = "it")
public class EmbeddedRedisConfig implements DisposableBean {

    @Value("${redis.port}")
    private int redisPort;

    private RedisServer redis;

    @Bean
    public RedisServer redisEmbeddedServer() {
        redis = new RedisServer(redisPort);
        redis.start();
        return redis;
    }

    @Override
    public void destroy() {
        if (redis != null) {
            redis.stop();
        }
    }
}
