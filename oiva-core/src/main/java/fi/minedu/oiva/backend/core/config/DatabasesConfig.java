package fi.minedu.oiva.backend.core.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fi.minedu.oiva.backend.model.jooq.AuditFieldsRecordListener;
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

    @Override
    public void setEnvironment(final Environment environment) {
        this.env = environment;
    }

    @Primary
    @Profile("!test")
    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        log.debug("Configuring Datasource");
        if (!this.env.containsProperty("spring.datasource.url") && !this.env.containsProperty("spring.datasource.databaseName")) {
            log.error("Your database connection pool configuration is incorrect! The application" +
                            " cannot start. Please check your Spring profile, current profiles are: {}",
                    Arrays.toString(env.getActiveProfiles()));

            throw new ApplicationContextException("Database connection pool is not configured correctly");
        }
        final HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(this.env.getProperty("spring.datasource.dataSourceClassName"));
        config.addDataSourceProperty("url", this.env.getProperty("spring.datasource.url"));
        config.addDataSourceProperty("user", this.env.getProperty("spring.datasource.username"));
        config.addDataSourceProperty("password", this.env.getProperty("spring.datasource.password"));

        return new HikariDataSource(config);
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
