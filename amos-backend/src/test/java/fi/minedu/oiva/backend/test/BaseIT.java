package fi.minedu.oiva.backend.test;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import fi.minedu.oiva.backend.entity.TranslatedString;
import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio;
import fi.minedu.oiva.backend.service.OpintopolkuService;
import fi.minedu.oiva.backend.task.BuildCaches;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
abstract public class BaseIT {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @LocalServerPort
    private Integer port = null;
    @Autowired
    protected TestRestTemplate restTemplate;
    /* Do not build caches on application startup */
    @MockBean
    private BuildCaches mockCacheBuilder;
    @MockBean
    private OpintopolkuService opintopolkuService;
    protected ParseContext jsonPath;

    @Before
    public void setUp() {
        // Setup database
        setUpDb("sql/asiatyyppi_data.sql", "sql/esitysmalli_data.sql", "sql/lupatila_data.sql",
                "sql/paatoskierros_data.sql", "sql/kohde_data.sql", "sql/maaraystyyppi_data.sql");
        // Mock opintopolkuService
        mockOpintopolkuService();
        // Setup JsonPath
        setupJsonPath();
        beforeTest();
    }

    /**
     * Override for test specific setup.
     */
    abstract public void beforeTest();

    @After
    public void tearDown() {
        cleanDb();
    }

    protected ResponseEntity<String> request(String uri, HttpStatus status) {
        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
        final ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(uri),
                HttpMethod.GET, entity, String.class);
        assertEquals(status, response.getStatusCode());
        return response;
    }

    protected String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private void setupJsonPath() {
        Configuration conf = Configuration.defaultConfiguration()
                .jsonProvider(new JacksonJsonProvider())
                .mappingProvider(new JacksonMappingProvider());
        conf.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
        jsonPath = JsonPath.using(conf);
    }

    private void mockOpintopolkuService() {
        final Map<String, String> lang = new HashMap<>();
        lang.put("fi", "Testi organisaatio");
        final Organisaatio o = new Organisaatio();
        o.setNimi(TranslatedString.of(lang));
        when(opintopolkuService.getBlockingOrganisaatio(anyString()))
                .thenReturn(o);
    }

    private void cleanDb() {
        executeDbScript(getResource("sql/truncate.sql"));
    }

    protected void setUpDb(String... scripts) {
        for (String s : scripts) {
            executeDbScript(getResource(s));
        }
    }

    protected Resource getResource(String s) {
        return resourceLoader.getResource("classpath:" + s);
    }

    private void executeDbScript(Resource script) {
        try (Connection con = jdbcTemplate.getDataSource().getConnection()) {
            ScriptUtils.executeSqlScript(con,
                    script);
        } catch (SQLException e) {
            log.error("Could not execute sql script " + script, e);
        }
    }

    protected String readFileToString(String file) throws IOException {
        return Files.lines(Paths.get(getResource(file).getURI()))
                .collect(Collectors.joining("\n"));
    }
}
