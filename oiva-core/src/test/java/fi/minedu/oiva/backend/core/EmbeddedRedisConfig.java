package fi.minedu.oiva.backend.core;

import org.apache.log4j.Logger;
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
    private Logger logger = Logger.getLogger(this.getClass());

    @Bean
    public RedisServer redisEmbeddedServer() {
        logger.info("Starting redis server in port " + redisPort);
        redis = new RedisServer(redisPort);
        redis.start();
        return redis;
    }

    @Override
    public void destroy() {
        if (redis != null) {
            logger.debug("Stoppping redis cache");
            redis.stop();
        }
    }
}
