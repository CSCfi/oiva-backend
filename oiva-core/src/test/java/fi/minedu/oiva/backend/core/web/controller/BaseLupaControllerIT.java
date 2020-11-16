package fi.minedu.oiva.backend.core.web.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import fi.minedu.oiva.backend.core.it.BaseIT;
import fi.minedu.oiva.backend.model.entity.AsiatyyppiValue;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public abstract class BaseLupaControllerIT extends BaseIT {

    private static String PARAM_KOULUTUSTYYPPI = "koulutustyyppi";
    private static String PARAM_OPPILAITOSTYYPPI = "oppilaitostyyppi";

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    protected JdbcTemplate jdbcTemplate;

    @Test
    public void getAll() {
        ResponseEntity<String> response = makeRequest("/api/luvat", HttpStatus.OK);
        final DocumentContext doc = jsonPath.parse(response.getBody());
        log.info(doc.jsonString());
        assertEquals(7, doc.read("$.length()", Integer.class).intValue());
    }

    @Test
    public void getAllWithJarjestaja() {
        getLuvat(null, 2);
    }

    @Test
    public void getAllWithJarjestajaParameters() {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        // Without parameters
        getLuvat(params, 2);

        // With koulutustyyppi 1
        params.put(PARAM_KOULUTUSTYYPPI, Collections.singletonList("1"));
        getLuvat(params, 2);

        // With koulutustyyppi 1 and oppilaitostyyppi 1
        params.put(PARAM_OPPILAITOSTYYPPI, Collections.singletonList("1"));
        getLuvat(params, 1);
    }

    @Test
    public void getLatestByYtunnus() {
        setUpDb("sql/extra_lupa_data.sql");
        loginAs("testEsittelija", okmOid, OivaAccess.Context_Esittelija);
        final ResponseEntity<String> response = makeRequest("/api/luvat/jarjestaja/1111111-1/viimeisin", HttpStatus.OK);
        final DocumentContext doc = jsonPath.parse(response.getBody());
        final String diaarinumero = "23/223/2020";
        assertEquals(diaarinumero, doc.read("$.diaarinumero"));

        // If latest lupa is already ended, response should be 404 not found.
        jdbcTemplate.update("update lupa set loppupvm = ? where diaarinumero = ?", LocalDate.now().minusDays(2), diaarinumero);
        makeRequest("/api/luvat/jarjestaja/1111111-1/viimeisin", HttpStatus.NOT_FOUND);
    }

    @Test
    public void getLatestByYtunnusAndKoulutustyyppi() {
        setUpDb("sql/extra_lupa_data.sql");
        loginAs("testEsittelija", okmOid, OivaAccess.Context_Esittelija);
        ResponseEntity<String> response = makeRequest("/api/luvat/jarjestaja/1111111-1/viimeisin?koulutustyyppi=2", HttpStatus.OK);
        DocumentContext doc = jsonPath.parse(response.getBody());
        final String diaarinumero = "22/222/2020";
        assertEquals(diaarinumero, doc.read("$.diaarinumero"));

        // Get latest not ended lupa.
        jdbcTemplate.update("update lupa set loppupvm = ? where diaarinumero = ?", LocalDate.now().minusDays(2), diaarinumero);
        response = makeRequest("/api/luvat/jarjestaja/1111111-1/viimeisin?koulutustyyppi=2", HttpStatus.OK);
        doc = jsonPath.parse(response.getBody());
        assertEquals("11/111/2020", doc.read("$.diaarinumero"));

        // If all lupas are already ended, response should be 404 not found.
        jdbcTemplate.update("update lupa set loppupvm = ? where diaarinumero = ?", LocalDate.now().minusDays(2), "11/111/2020");
        makeRequest("/api/luvat/jarjestaja/1111111-1/viimeisin?koulutustyyppi=2", HttpStatus.NOT_FOUND);
    }

    @Test
    public void getByYtunnusAndKoulutustyyppi() {
        setUpDb("sql/extra_lupa_data.sql");
        final ResponseEntity<String> response = makeRequest("/api/luvat/jarjestaja/1111111-1?koulutustyyppi=2&oppilaitostyyppi=1", HttpStatus.OK);
        final DocumentContext doc = jsonPath.parse(response.getBody());
        assertEquals("11/111/2020", doc.read("$.diaarinumero"));
    }

    @Test
    public void getFutureByYtunnus() {
        setUpDb("sql/extra_lupa_data.sql");
        jdbcTemplate.update("update lupa set alkupvm = ?, koulutustyyppi = null, oppilaitostyyppi = null where diaarinumero = ?",
                LocalDate.now().plusDays(3), "22/222/2020");
        ResponseEntity<String> response = makeRequest("/api/luvat/jarjestaja/1111111-1/tulevaisuus", HttpStatus.OK);
        DocumentContext doc = jsonPath.parse(response.getBody());
        List<String> diaariList = doc.read("$.[*].diaarinumero");
        assertEquals(2, diaariList.size());
        assertTrue(diaariList.containsAll(Arrays.asList("22/222/2020", "23/223/2020")));

        // Should not return lupa which is started in the same day.
        jdbcTemplate.update("update lupa set alkupvm = ?, koulutustyyppi = null, oppilaitostyyppi = null where diaarinumero = ?",
                LocalDate.now(), "22/222/2020");
        response = makeRequest("/api/luvat/jarjestaja/1111111-1/tulevaisuus", HttpStatus.OK);
        doc = jsonPath.parse(response.getBody());
        diaariList = doc.read("$.[*].diaarinumero");
        assertEquals(1, diaariList.size());
        assertTrue(diaariList.contains("23/223/2020"));
    }

    @Test
    public void getFutureByYtunnusAndKoulutustyyppi() {
        setUpDb("sql/extra_lupa_data.sql");
        jdbcTemplate.update("update lupa set alkupvm = ? where diaarinumero = ?",
                LocalDate.now().plusDays(3), "22/222/2020");
        final ResponseEntity<String> response = makeRequest("/api/luvat/jarjestaja/1111111-1/tulevaisuus?koulutustyyppi=2", HttpStatus.OK);
        final DocumentContext doc = jsonPath.parse(response.getBody());
        final List<String> diaariList = doc.read("$.[*].diaarinumero");
        assertEquals(1, diaariList.size());
        assertTrue(diaariList.contains("22/222/2020"));
    }

    @Test
    public void getAllLupaOrganizations() {
        ResponseEntity<String> response = makeRequest("/api/luvat/organisaatiot", HttpStatus.OK);
        DocumentContext doc = jsonPath.parse(response.getBody());
        final Integer organizations = 2;
        log.debug("Response was " + doc.jsonString());
        assertEquals("Result should have " + organizations + " items", organizations, doc.read("$.length()"));
        assertTrue("Result item should have oid field", (doc.read("$[0].oid") + "").length() > 0);
        assertTrue("Result item should have nimi field", (doc.read("$[0].nimi") + "").length() > 0);

        // Add new lupa to existing organization, add old lupa which is not valid anymore
        setUpDb("sql/organisaatio_lupa_data.sql");
        response = makeRequest("/api/luvat/organisaatiot", HttpStatus.OK);
        doc = jsonPath.parse(response.getBody());
        // Result should be same as before
        assertEquals(organizations, doc.read("$.length()"));
    }

    @Test
    public void getLupahistoria() {
        setUpDb("sql/lupahistoria_data.sql");

        // Set one historia lupa loppu pvm to distant future
        LocalDate future = LocalDate.now().plusDays(2);
        int updateCount = jdbcTemplate.update("update lupahistoria set voimassaololoppupvm = ? where id=1;", future);
        assertEquals(1, updateCount);

        DocumentContext doc = jsonPath.parse(makeRequest("/api/luvat/historia/1.1.111.111.11.11111111111", HttpStatus.OK).getBody());
        DocumentContext doc2 = jsonPath.parse(makeRequest("/api/luvat/historiaytunnuksella/1111111-1", HttpStatus.OK).getBody());
        assertEquals(new Integer(1), doc.read("$.length()"));
        assertEquals("1.1.111.111.11.11111111111", doc.read("$[0].oid"));
        doc = JsonPath.using(doc.configuration().addOptions(Option.SUPPRESS_EXCEPTIONS)).parse(doc.jsonString());
        assertNull(doc.read("$[0].id"));
        assertNull(doc.read("$[0].lupaId"));
        assertEquals(doc.jsonString(), doc2.jsonString());
    }

    @Test
    public void printLupahistoriaLupa() {
        setUpDb("sql/lupahistoria_data.sql");
        ResponseEntity<String> response = makeRequest("/api/pdf/historia/cc3c1d00-43b6-11e8-b2ef-005056aa0e61", HttpStatus.FOUND);

        // case: redirect to lupa printing api  /api/pdf/<uuid>
        List<String> locations = response.getHeaders().get("Location");
        assertEquals(1, locations.size());
        assertTrue(locations.get(0).matches("/api/pdf/[a-z0-9-]+"));

        // case: redirect to direct file download  /api/pebble/resouces/liitteet...
        response = makeRequest("/api/pdf/historia/cc3c1d00-43b6-11e8-b2ef-005056aa0e63", HttpStatus.FOUND);
        locations = response.getHeaders().get("Location");
        assertEquals(1, locations.size());
        assertEquals("/api/pebble/resources/liitteet/lupahistoria/a+b+c.pdf", locations.get(0));
    }

    private void getLuvat(MultiValueMap<String, String> queryParams, int expected) {
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("/api/luvat/jarjestajilla")
                .queryParams(queryParams);
        final ResponseEntity<String> response = makeRequest(uriBuilder.build().toUriString(), HttpStatus.OK);
        final DocumentContext doc = jsonPath.parse(response.getBody());
        log.info(doc.jsonString());
        assertEquals(expected, doc.read("$.length()", Integer.class).intValue());
        assertEquals("Testi organisaatio", doc.read("$.[0].jarjestaja.nimi.fi"));
        List<String> tyypit = doc.read("$.[*].asiatyyppi.tunniste");
        assertFalse(tyypit.contains(AsiatyyppiValue.PERUUTUS.name()));
    }

    @Override
    public void beforeTest() {
        setUpDb("sql/lupa_data.sql");
    }
}