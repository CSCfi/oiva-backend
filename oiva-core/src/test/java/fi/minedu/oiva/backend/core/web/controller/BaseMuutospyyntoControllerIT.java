package fi.minedu.oiva.backend.core.web.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.TypeRef;
import fi.minedu.oiva.backend.core.it.BaseIT;
import fi.minedu.oiva.backend.core.service.MuutospyyntoService;
import fi.minedu.oiva.backend.model.entity.oiva.Liite;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

public abstract class BaseMuutospyyntoControllerIT extends BaseIT {

    private final String lupaJarjestajaOid = "1.1.111.111.11.11111111111";
    private final String okmOid = "1.1.111.111.11.22222222";

    @Override
    public void beforeTest() {
        setUpDb("sql/lupa_data.sql", "sql/maarays_data.sql");
    }

    @Test
    public void saveWithoutLogin() throws IOException {
        final ResponseEntity<String> response = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json")));
        assertEquals("Response code should match!", HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void saveWithoutProperRole() throws IOException {
        loginAs("testuser", lupaJarjestajaOid, OivaAccess.Context_Katselija);
        final ResponseEntity<String> response = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json")));
        assertEquals("Response code should match!", HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void saveWithoutProperOrganisation() throws IOException {
        loginAs("testuser", "1.2.3.4", OivaAccess.Context_Kayttaja);
        final ResponseEntity<String> response = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json")));
        assertEquals("Response code should match! Response was\n" + response, HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void saveWithSuperiorRole() throws IOException {
        loginAs("testuser", lupaJarjestajaOid, OivaAccess.Context_Nimenkirjoittaja);
        final ResponseEntity<String> response = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json")));
        assertEquals("Response code should match!", OK, response.getStatusCode());
    }

    @Test
    public void save() throws IOException {
        final TypeRef<List<String>> stringRef = new TypeRef<List<String>>() {
        };
        loginAs("testuser", lupaJarjestajaOid,
                OivaAccess.Context_Kayttaja, OivaAccess.Context_Katselija);

        // Create new
        ResponseEntity<String> createResponse = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json"),
                "file0", "file1", "file2", "file3", "file4", "file5"));
        assertEquals("Response status should match", OK, createResponse.getStatusCode());
        DocumentContext doc = jsonPath.parse(createResponse.getBody());
        log.info("Muutospyynto createResponse: {}", doc.jsonString());
        final String uuid = doc.read("$.uuid", String.class);
        assertNotNull("Muutospyynto uuid should not be null", uuid);
        assertEquals("Liite table count should match!", 6,
                JdbcTestUtils.countRowsInTable(jdbcTemplate, "liite"));

        // Check that liitteet has uuid in response
        List<String> liiteUuids = doc.read("$..liitteet[*].uuid", stringRef);
        assertEquals("Every saved liite should have uuid!", 6, liiteUuids.size());

        // Check that liitteet has tiedostoId in response
        List<String> tiedostoIds = doc.read("$..liitteet[*].tiedostoId", stringRef);
        assertEquals("Every saved liite should have tiedostoId!", 6, tiedostoIds.size());

        // Muutos with koodiarvo 334113 should have two aliMaarays
        List alimaaraykset = doc.read("$.muutokset[?(@.koodiarvo == 334113)].aliMaaraykset", List.class);
        assertEquals(1, alimaaraykset.size());
        alimaaraykset = (List) alimaaraykset.get(0);
        assertEquals(2, alimaaraykset.size());

        // First alimaarays has its own alimaarays
        List<Map<String, Object>> dbcontent = jdbcTemplate.queryForList("select * from muutos where koodiarvo='1531_1_1';");
        assertEquals("Ali-alimaarays is not saved to database correctly. DB-content: " + dbcontent, 1, dbcontent.size());
        List aliali = doc.read("$.muutokset[?(@.koodiarvo == 334113)].aliMaaraykset[0].aliMaaraykset[0].koodiarvo", List.class);
        assertEquals("There should be one ali-alimaarays with koodiarvo 1531_1_1, was " + aliali,
                Collections.singletonList("1531_1_1"), aliali);

        // Load created
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(createURLWithPort("/api/muutospyynnot/id/" + uuid), String.class);
        assertEquals("Get response status should match!", OK, getResponse.getStatusCode());
        assertEquals("Create response and get response should match!", createResponse.getBody(),
                getResponse.getBody());

        // ----- UPDATE EXISTING -----

        // Mark muutospyynto_liite to be removed
        doc.set("$.liitteet[?(@.nimi == 'muutospyynto_liite')].removed", true);
        // Change muutospyynto_liite2 to secret
        doc.set("$.liitteet[?(@.nimi == 'muutospyynto_liite2')].salainen", true);
        // Mark meta muutos_liite3 to be removed
        doc.set("$..meta.liitteet[?(@.nimi == 'muutos_liite3')].removed", true);
        // Change meta muutos_liite4 name
        final String changeName = "muutos_liite4_changed";
        doc.set("$..meta.liitteet[?(@.nimi == 'muutos_liite4')].nimi", changeName);

        final ResponseEntity<String> updateResponse = requestSave(prepareMultipartEntity(doc.jsonString()));

        assertEquals("Update response code should match!", OK, updateResponse.getStatusCode());

        assertEquals("Liite table count should match!", 4,
                JdbcTestUtils.countRowsInTable(jdbcTemplate, "liite"));

        doc = jsonPath.parse(updateResponse.getBody());
        final TypeRef<List<Boolean>> boolRef = new TypeRef<List<Boolean>>() {
        };
        final List<Boolean> list = doc.read("$.liitteet[?(@.nimi == 'muutospyynto_liite2')].salainen", boolRef);
        assertTrue("Muutospyynto liite 2 should be secret!", list.get(0));
        
        final List<String> names = doc.read("$..meta.liitteet[?(@.nimi == '" + changeName + "')].nimi", stringRef);
        assertEquals("Muutos liite 4 name should be match!", changeName, names.get(0));

        // Check that liitteet has uuid in response
        liiteUuids = doc.read("$..liitteet[*].uuid", stringRef);
        assertEquals("Every saved liite should have uuid!", 4, liiteUuids.size());

        // Check that liitteet has tiedostoId in response
        tiedostoIds = doc.read("$..liitteet[*].tiedostoId", stringRef);
        assertEquals("Every saved liite should have tiedostoId!", 4, tiedostoIds.size());

        // Muutos with koodiarvo 334113 should have two aliMaarays after some muutos have been changed
        alimaaraykset = doc.read("$.muutokset[?(@.koodiarvo == 334113)].aliMaaraykset", List.class);
        assertEquals(1, alimaaraykset.size());
        alimaaraykset = (List) alimaaraykset.get(0);
        assertEquals(2, alimaaraykset.size());

        // First alimaarays has 1 alimaarays
        aliali = doc.read("$.muutokset[?(@.koodiarvo == 334113)].aliMaaraykset[0].aliMaaraykset[0].koodiarvo", List.class);
        assertEquals("There should be one ali-alimaarays with koodiarvo 1531_1_1, was " + aliali,
                Collections.singletonList("1531_1_1"), aliali);
    }

    @Test
    public void testHappyPath() throws IOException {
        loginAs("testuser", lupaJarjestajaOid,
                OivaAccess.Context_Kayttaja, OivaAccess.Context_Kayttaja);
        // Create new muutospyynto
        ResponseEntity<String> createResponse = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json"),
                "file0", "file1", "file2", "file3", "file4", "file5"));
        assertEquals("Response status should match", OK, createResponse.getStatusCode());
        DocumentContext doc = jsonPath.parse(createResponse.getBody());
        final String uuid = doc.read("$.uuid", String.class);
        logout();
        // Change muutospyynto tila to "KASITTELYSSA"
        loginAs("testNimenkirjoittaja", lupaJarjestajaOid, OivaAccess.Context_Nimenkirjoittaja);
        makeRequest(POST,
                "/api/muutospyynnot/tila/avoin/" + uuid,
                null, OK).getBody();
        logout();
        // Change muutospyynto tila to "VALMISTELUSSA"
        loginAs("testEsittelija", okmOid, OivaAccess.Context_Esittelija);
        makeRequest(POST,
                "/api/muutospyynnot/tila/valmistelussa/" + uuid,
                null, OK).getBody();

        // Change tila to "ESITTELYSSA"
        makeRequest(POST,
                "/api/muutospyynnot/tila/esittelyssa/" + uuid,
                null, OK).getBody();

        // Change muutospyynto tila to "PAATETTY"
        final String response = makeRequest(POST,
                "/api/muutospyynnot/tila/paatetty/" + uuid,
                null, OK).getBody();
        assertEquals("\"" + uuid + "\"", response);

        // Fetch the latest lupa for jarjestaja
        final TypeRef<List<String>> stringRef = new TypeRef<List<String>>() {
        };
        final ResponseEntity<String> lupaJson = makeRequest("/api/luvat/jarjestaja/1111111-1?with=all", OK);
        doc = jsonPath.parse(lupaJson.getBody());
        assertEquals("Lupa diaarinumero should match.", "20/531/2018", doc.read("$.diaarinumero"));
        assertEquals("Maarays count should match!", 6, doc.read("$.maaraykset.length()",
                Integer.class).intValue());
        assertEquals("AliMaarays count should match!", 4,
                doc.read("$..aliMaaraykset[*].uuid", stringRef).size());
        final List<String> koodiarvoList = doc.read("$.maaraykset[*].koodiarvo", stringRef);
        assertFalse("There should not be removed maarays.", koodiarvoList.contains("124"));

        // Check history
        assertEquals("There should be history row for old lupa", 1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "lupahistoria", "lupa_id = 1"));
    }

    @Test
    public void updateWithoutMuutokset() throws IOException {
        loginAs("testuser", lupaJarjestajaOid,
                OivaAccess.Context_Kayttaja, OivaAccess.Context_Katselija);

        // Create new
        ResponseEntity<String> createResponse = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json"),
                "file0", "file1", "file2", "file3", "file4", "file5"));
        assertEquals("Response status should match", OK, createResponse.getStatusCode());
        DocumentContext doc = jsonPath.parse(createResponse.getBody());
        log.info("Muutospyynto createResponse: {}", doc.jsonString());
        final String uuid = doc.read("$.uuid", String.class);
        assertNotNull("Muutospyynto uuid should not be null", uuid);

        // Remove all changes from update
        doc.set("$.muutokset", Collections.EMPTY_LIST);

        final ResponseEntity<String> updateResponse = requestSave(prepareMultipartEntity(doc.jsonString()));
        assertEquals("Update response code should match!", OK, updateResponse.getStatusCode());

        assertEquals("Liite table count should match!", 2,
                JdbcTestUtils.countRowsInTable(jdbcTemplate, "liite"));

        // Load updated muutospyynto
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(createURLWithPort("/api/muutospyynnot/id/" + uuid), String.class);
        assertEquals("Get response status should match!", OK, getResponse.getStatusCode());

        assertEquals("Update response and get response should match!", updateResponse.getBody(),
                getResponse.getBody());
    }

    @Test
    public void getMuutospyyntoLiitteet() {
        setUpDb("sql/liite_data.sql", "sql/muutospyynto_data.sql", "sql/muutos_data.sql",
                "sql/muutospyynto_liite_data.sql", "sql/muutos_liite_data.sql");
        loginAs("testuser", lupaJarjestajaOid,
                OivaAccess.Context_Kayttaja, OivaAccess.Context_Katselija);
        final ResponseEntity<String> liiteResponse = requestLiitteet("2b02c730-ebef-11e9-8d25-0242ac110023");
        assertEquals("Response status should match!", liiteResponse.getStatusCode(), OK);
        final DocumentContext doc = jsonPath.parse(liiteResponse.getBody());
        final TypeRef<List<Liite>> liiteRef = new TypeRef<List<Liite>>() {
        };
        final List<Liite> liiteList = doc.read("$.*", liiteRef);
        assertEquals("Liite count should match!", 8, liiteList.size());
    }

    @Test
    public void stateChangeIsLogged() throws IOException {
        String username = "testuser";
        loginAs(username, lupaJarjestajaOid,
                OivaAccess.Context_Kayttaja, OivaAccess.Context_Nimenkirjoittaja);

        final ResponseEntity<String> response = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json")));

        DocumentContext doc = jsonPath.parse(response.getBody());
        log.info("Muutospyynto createResponse: {}", doc.jsonString());
        final String uuid = doc.read("$.uuid", String.class);

        // Vie käsittelyyn esittelijälle
        super.requestBody("/api/muutospyynnot/tila/avoin/" + uuid, POST);

        List<Map<String, Object>> muutokset = jdbcTemplate.queryForList("select * from asiatilamuutos");
        assertEquals("Should have 2: creation \"\" -> luonnos and luonnos -> avoin",2, muutokset.size());
        Map<String, Object> row = muutokset.get(1);
        assertEquals("LUONNOS", row.get("alkutila"));
        assertEquals("AVOIN", row.get("lopputila"));
        assertEquals(username, row.get("kayttajatunnus"));
    }

    @Test
    public void esittelijaCanHandleMuutospyynto() throws IOException {
        String username = "testuser";
        loginAs(username, lupaJarjestajaOid,
                OivaAccess.Context_Kayttaja, OivaAccess.Context_Nimenkirjoittaja);

        ResponseEntity<String> response = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json")));

        DocumentContext doc = jsonPath.parse(response.getBody());
        log.info("Muutospyynto createResponse: {}", doc.jsonString());
        final String uuid = doc.read("$.uuid", String.class);

        // Vie käsittelyyn esittelijälle
        super.requestBody("/api/muutospyynnot/tila/avoin/" + uuid, POST);

        // Login as esittelijä
        loginAs("esittelija", lupaJarjestajaOid, OivaAccess.Context_Esittelija);
        response = makeRequest("api/muutospyynnot/avoimet", OK);
        doc = jsonPath.parse(response.getBody());
        assertEquals(1, doc.read("$.length()", Integer.class).intValue());

        // Ota käsittelyyn
        super.requestBody("/api/muutospyynnot/tila/valmistelussa/" + uuid, POST);

        doc = requestJSONData("/api/muutospyynnot/avoimet");
        assertEquals(0, doc.read("$.length()", Integer.class).intValue());

        doc = requestJSONData("/api/muutospyynnot/valmistelussa");
        assertEquals(1, doc.read("$.length()", Integer.class).intValue());

        doc = requestJSONData("/api/muutospyynnot/valmistelussa?vainOmat=true");
        assertEquals("Esittelija has one own muutospyynto", 1, doc.read("$.length()", Integer.class).intValue());

        loginAs("esittelija_2", lupaJarjestajaOid, OivaAccess.Context_Esittelija);
        doc = requestJSONData("/api/muutospyynnot/valmistelussa?vainOmat=true");
        assertEquals("Another esittelijä has no own muutospyynot", 0, doc.read("$.length()", Integer.class).intValue());
    }

    @Test
    public void esittelijaCreateAndSave() throws IOException {
        loginAs("elli esittelija", "", OivaAccess.Context_Esittelija);

        // Create new
        ResponseEntity<String> createResponse =
                restTemplate.postForEntity(
                        createURLWithPort("/api/muutospyynnot/esittelija/tallenna"),
                        prepareMultipartEntity(readFileToString("json/muutospyynto.json"),
                                "file0", "file1", "file2", "file3", "file4", "file5"),
                        String.class);

        assertEquals("Response status should match", HttpStatus.OK, createResponse.getStatusCode());
        DocumentContext doc = jsonPath.parse(createResponse.getBody());
        log.info("Muutospyynto createResponse: {}", doc.jsonString());
        final String uuid = doc.read("$.uuid", String.class);
        assertNotNull("Muutospyynto uuid should not be null", uuid);
        assertEquals("Liite table count should match!", 6,
                JdbcTestUtils.countRowsInTable(jdbcTemplate, "liite"));
        assertEquals("Tila should be VALMISTELUSSA", MuutospyyntoService.Muutospyyntotila.VALMISTELUSSA.toString(),
                doc.read("$.tila", String.class));
        assertEquals("Alkupera should be ESITTELIJA", MuutospyyntoService.Tyyppi.ESITTELIJA.toString(),
                doc.read("$.alkupera", String.class));

        // Load created
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(createURLWithPort("/api/muutospyynnot/id/" + uuid), String.class);
        assertEquals("Get response status should match!", HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Create response and get response should match!", createResponse.getBody(),
                getResponse.getBody());

        // ----- UPDATE EXISTING -----
        final String newEndDate = "2020-02-20";
        final String asianumero = "VNK/123/2020";
        doc.put("$", "voimassaalkupvm", newEndDate)
                .put("$", "paatospvm", newEndDate)
                .put("$", "asianumero", asianumero);

        final ResponseEntity<String> updateResponse =
                restTemplate.postForEntity(
                        createURLWithPort("/api/muutospyynnot/esittelija/tallenna"),
                        prepareMultipartEntity(doc.jsonString()),
                        String.class);

        assertEquals("Update response code should match!", HttpStatus.OK, updateResponse.getStatusCode());
        doc = jsonPath.parse(updateResponse.getBody());
        assertEquals(newEndDate, doc.read("$.voimassaalkupvm"));
        assertEquals(newEndDate, doc.read("$.paatospvm"));
        assertEquals(asianumero, doc.read("$.asianumero"));
    }


    private ResponseEntity<String> requestSave(HttpEntity<MultiValueMap<String, Object>> requestEntity) {
        return restTemplate
                .postForEntity(createURLWithPort("/api/muutospyynnot/tallenna"), requestEntity, String.class);
    }

    private ResponseEntity<String> requestLiitteet(String uuid) {
        return restTemplate
                .getForEntity(createURLWithPort("/api/muutospyynnot/" + uuid + "/liitteet/"), String.class);
    }

    private HttpEntity<MultiValueMap<String, Object>> prepareMultipartEntity(String json, String... fileIds) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<String> jsonEntity = new HttpEntity<>(json, jsonHeaders);
        parts.add("muutospyynto", jsonEntity);

        final HttpHeaders pdfHeader = new HttpHeaders();
        pdfHeader.setContentType(MediaType.APPLICATION_PDF);
        Arrays.stream(fileIds)
                .forEachOrdered(i ->
                        parts.add(i, new HttpEntity<>(getResource("attachments/test.pdf"), pdfHeader)));

        final HttpHeaders multipartHeaders = new HttpHeaders();
        multipartHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        multipartHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<>(parts, multipartHeaders);
    }

}
