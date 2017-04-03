package fi.minedu.oiva.backend.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fi.minedu.oiva.backend.jooq.AuditFieldsRecordListener;
import fi.minedu.oiva.backend.jooq.SpringTransactionProvider;
import org.flywaydb.core.Flyway;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.TransactionProvider;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultRecordListenerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableTransactionManagement
public class DatabasesConfig implements EnvironmentAware {

    private final Logger log = LoggerFactory.getLogger(DatabasesConfig.class);

    private Environment env;

    private RelaxedPropertyResolver dataSourcePropertyResolver;

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
        this.dataSourcePropertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");
    }

    @Profile("!test")
    @Bean(destroyMethod = "shutdown")
    public DataSource dataSource() {
        log.debug("Configuring Datasource");
        if (dataSourcePropertyResolver.getProperty("url") == null && dataSourcePropertyResolver.getProperty("databaseName") == null) {
            log.error("Your database connection pool configuration is incorrect! The application" +
                    " cannot start. Please check your Spring profile, current profiles are: {}",
                Arrays.toString(env.getActiveProfiles()));

            throw new ApplicationContextException("Database connection pool is not configured correctly");
        }
        final HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(dataSourcePropertyResolver.getProperty("dataSourceClassName"));
        config.addDataSourceProperty("url", dataSourcePropertyResolver.getProperty("url"));
        config.addDataSourceProperty("user", dataSourcePropertyResolver.getProperty("username"));
        config.addDataSourceProperty("password", dataSourcePropertyResolver.getProperty("password"));
        DataSource source = new HikariDataSource(config);

        log.info(" RUNNING DATABASE MIGRATION NOW");

        final Flyway flyway = new Flyway();
        flyway.setDataSource(source);
        flyway.migrate();

        log.info("DATABASE MIGRATION DONE");

        return source;
    }


    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public DSLContext dsl(org.jooq.Configuration config) {
        return new DefaultDSLContext(config);
    }

    @Bean
    public ConnectionProvider connectionProvider(DataSource dataSource) {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean
    public TransactionProvider transactionProvider() {
        return new SpringTransactionProvider();
    }

    @Bean
    public org.jooq.Configuration jooqConfig(ConnectionProvider connectionProvider,
                                             TransactionProvider transactionProvider) {
        return new DefaultConfiguration()
            .derive(connectionProvider)
            .derive(transactionProvider)
            .derive(SQLDialect.POSTGRES)
            .derive(new DefaultRecordListenerProvider(new AuditFieldsRecordListener()))
            .derive(new Settings().withExecuteWithOptimisticLocking(true));
    }

    @Configuration
    public static class RedisConfig {

        private @Value("${redis.host}") String redisHost;
        private @Value("${redis.port}") int redisPort;

        @Bean
        public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }

        @Bean
        JedisConnectionFactory jedisConnectionFactory() {
            JedisConnectionFactory factory = new JedisConnectionFactory();
            factory.setHostName(redisHost);
            factory.setPort(redisPort);
            factory.setUsePool(true);
            return factory;
        }

        @Bean
        RedisTemplate<Object, Object> redisTemplate() {
            final RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(jedisConnectionFactory());
            final RedisSerializer<Object> keySerializer =
                new Jackson2JsonRedisSerializer<>(Object.class);
            redisTemplate.setKeySerializer(keySerializer);
            redisTemplate.setHashKeySerializer(keySerializer);
            return redisTemplate;
        }
    }
}
