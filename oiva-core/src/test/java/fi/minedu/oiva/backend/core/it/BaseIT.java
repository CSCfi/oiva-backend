package fi.minedu.oiva.backend.core.it;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import fi.minedu.oiva.backend.core.EmbeddedRedisConfig;
import fi.minedu.oiva.backend.core.service.OpintopolkuService;
import fi.minedu.oiva.backend.core.task.BuildCaches;
import fi.minedu.oiva.backend.model.entity.TranslatedString;
import fi.minedu.oiva.backend.model.entity.opintopolku.KayttajaKayttooikeus;
import fi.minedu.oiva.backend.model.entity.opintopolku.Kayttooikeus;
import fi.minedu.oiva.backend.model.entity.opintopolku.Organisaatio;
import fi.minedu.oiva.backend.model.entity.opintopolku.OrganisaatioKayttooikeus;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
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
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(EmbeddedRedisConfig.class)
@ActiveProfiles("it")
abstract public class BaseIT {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    protected JdbcTemplate jdbcTemplate;
    @LocalServerPort
    private Integer port = null;
    protected TestRestTemplate restTemplate;
    /* Do not build caches on application startup */
    @MockBean
    private BuildCaches mockCacheBuilder;
    @MockBean
    private OpintopolkuService opintopolkuService;
    protected ParseContext jsonPath;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 8888);

    @Before
    public void setUp() {
        restTemplate = createRestTemplate();
        // Setup database
        setUpDb("sql/asiatyyppi_data.sql", "sql/esitysmalli_data.sql", "sql/lupatila_data.sql",
                "sql/paatoskierros_data.sql", "sql/kohde_data.sql", "sql/maaraystyyppi_data.sql");
        // Mock opintopolkuService
        mockOpintopolkuService();
        // Setup JsonPath
        setupJsonPath();
        beforeTest();
    }

    protected TestRestTemplate createRestTemplate() {
        // Enable cookies for rest template (Authentication needs this).
        return new TestRestTemplate(TestRestTemplate.HttpClientOption.ENABLE_COOKIES);
    }

    protected void resetRestTemplate() {
        restTemplate = createRestTemplate();
    }

    /**
     * Override for test specific setup.
     */
    abstract public void beforeTest();

    @After
    public void tearDown() {
        cleanDb();
        logout();
    }

    protected ResponseEntity<String> makeRequest(String uri, HttpStatus status) {
        return makeRequest(uri, status, null);
    }

    protected ResponseEntity<String> makeRequest(String uri, HttpStatus status, HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>(null, Optional.ofNullable(headers).
                orElse(new HttpHeaders()));
        final ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(uri),
                HttpMethod.GET, entity, String.class);
        assertEquals(status, response.getStatusCode());
        return response;
    }

    protected String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    protected void loginAs(String username, String orgOid, String... roles) {
        // First mock Opintopolku kayttooikeus query and CAS authentication.
        mockLogin(username, orgOid, roles);
        // Make login request to mock CAS authentication.
        final ResponseEntity<String> loginResponse =
                restTemplate.postForEntity(
                        "http://localhost:" + mockServerRule.getPort() + "/mock-cas/cas/login",
                        "", String.class);
        assertEquals("Login should success!", HttpStatus.FOUND, loginResponse.getStatusCode());
        // Follow ticket validation
        restTemplate.getForEntity(loginResponse.getHeaders().getLocation(), String.class);
    }

    protected HttpHeaders getBasicAuthHeaders(String username, String password) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        httpHeaders.set("Authorization", authHeader);
        return httpHeaders;
    }

    private void mockLogin(String username, String orgOid, String... roles) {
        mockCAS(username);
        List<OrganisaatioKayttooikeus> orgKayttooikeusList = new ArrayList<>();
        List<Kayttooikeus> kayttooikeusList = new ArrayList<>();
        Stream.of(roles).forEach(role -> kayttooikeusList.add(new Kayttooikeus(OivaAccess.Context_Oiva, role)));

        orgKayttooikeusList.add(new OrganisaatioKayttooikeus(orgOid, kayttooikeusList));
        final KayttajaKayttooikeus kayttajaKayttooikeus = new KayttajaKayttooikeus("1.1.1.1.1", orgKayttooikeusList);
        when(opintopolkuService.getKayttajaKayttooikeus(username)).thenReturn(Optional.of(kayttajaKayttooikeus));
    }

    private void mockCAS(String username) {
        final String casTicket = "ST-59710-PDKr53jQ3HGF-4CyKl5IbD-8Gqg68ea1e16ad92";
        final MockServerClient mockClient = mockServerRule.getClient().reset();
        // Login mock response
        mockClient.when(request().withMethod("POST").withPath("/mock-cas/cas/login.*"))
                .respond(response().withStatusCode(HttpStatus.FOUND.value())
                        .withCookie("TGC", "TGC-12354kjdfkdjf-3232343; Path=/mock-cas; Secure; HttpOnly")
                        .withHeader("Location", createURLWithPort("/login/cas?ticket=" + casTicket)));
        // Ticket validation mock response
        mockClient.when(request().withMethod("GET").withPath("/mock-cas/cas/serviceValidate.*"))
                .respond(response().withBody("<cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>\n" +
                        "    <cas:authenticationSuccess>\n" +
                        "        <cas:user>" + username + "</cas:user>\n" +
                        "    </cas:authenticationSuccess>\n" +
                        "</cas:serviceResponse>"));
    }

    private void logout() {
        restTemplate.getForEntity(createURLWithPort("/api/auth/logout"), String.class);
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
        return IOUtils.toString(getResource(file).getInputStream(), Charset.forName("utf-8"));
    }
}
