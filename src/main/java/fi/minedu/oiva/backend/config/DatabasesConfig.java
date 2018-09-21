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
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
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
    public void setEnvironment(final Environment environment) {
        this.env = environment;
        this.dataSourcePropertyResolver = new RelaxedPropertyResolver(environment, "spring.datasource.");
    }

    @Primary
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

        final DataSource source = new HikariDataSource(config);

        log.info("Applying database migration");

        final Flyway flyway = new Flyway();
        flyway.setDataSource(source);
        flyway.setBaselineOnMigrate(true);
        flyway.migrate();

        return source;
    }


    @Bean
    public DataSourceTransactionManager transactionManager(final DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public DSLContext dsl(final org.jooq.Configuration config) {
        return new DefaultDSLContext(config);
    }

    @Bean
    public ConnectionProvider connectionProvider(final DataSource dataSource) {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean
    public TransactionProvider transactionProvider() {
        return new SpringTransactionProvider();
    }

    @Bean
    @Primary
    public org.jooq.Configuration jooqConfig(final ConnectionProvider connectionProvider, final TransactionProvider transactionProvider) {
        return new DefaultConfiguration()
            .derive(connectionProvider)
            .derive(transactionProvider)
            .derive(SQLDialect.POSTGRES)
            .derive(new DefaultRecordListenerProvider(new AuditFieldsRecordListener()))
            .derive(new Settings().withExecuteWithOptimisticLocking(true));
    }
}
