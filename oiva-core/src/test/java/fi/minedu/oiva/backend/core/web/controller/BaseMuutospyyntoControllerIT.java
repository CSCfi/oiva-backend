package fi.minedu.oiva.backend.core.web.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.TypeRef;
import fi.minedu.oiva.backend.core.it.BaseIT;
import fi.minedu.oiva.backend.core.service.MuutospyyntoService;
import fi.minedu.oiva.backend.model.entity.OivaTemplates;
import fi.minedu.oiva.backend.model.entity.oiva.Liite;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.oiva.Maarays;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public abstract class BaseMuutospyyntoControllerIT extends BaseIT {

    private final String lupaJarjestajaOid = "1.1.111.111.11.11111111111";

    @Override
    public void beforeTest() {
        setUpDb("sql/lupa_data.sql", "sql/maarays_data.sql");
    }

    @Test
    public void getMuutospyynnot() {
        setUpDb("sql/muutospyynto_data.sql");
        loginAs("testuser", lupaJarjestajaOid, OivaAccess.Context_Esittelija);
        String response = requestBody("/api/muutospyynnot?tilat=LUONNOS", GET);
        DocumentContext doc = jsonPath.parse(response);
        assertEquals(Integer.valueOf(1), doc.read("$.length()"));
        assertEquals("VN/1234/123456", doc.read("$[0].asianumero", String.class));

        // Get with koulutustyyppi
        response = requestBody("/api/muutospyynnot?tilat=LUONNOS&koulutustyyppi=2", GET);
        doc = jsonPath.parse(response);
        assertEquals(Integer.valueOf(1), doc.read("$.length()"));
        assertEquals("VN/1234/123457", doc.read("$[0].asianumero", String.class));
    }

    @Test
    public void saveWithoutLogin() throws IOException {
        final ResponseEntity<String> response = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json")), "/api/muutospyynnot/tallenna");
        assertEquals("Response code should match!", HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void saveWithoutProperRole() throws IOException {
        loginAs("testuser", lupaJarjestajaOid, OivaAccess.Context_Katselija);
        final ResponseEntity<String> response = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json")), "/api/muutospyynnot/tallenna");
        assertEquals("Response code should match!", HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void saveWithoutProperOrganisation() throws IOException {
        loginAs("testuser", "1.2.3.4", OivaAccess.Context_Kayttaja);
        final ResponseEntity<String> response = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json")), "/api/muutospyynnot/tallenna");
        assertEquals("Response code should match! Response was\n" + response, HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void saveWithSuperiorRole() throws IOException {
        loginAs("testuser", lupaJarjestajaOid, OivaAccess.Context_Nimenkirjoittaja);
        final ResponseEntity<String> response = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json")), "/api/muutospyynnot/tallenna");
        assertEquals("Response code should match!", OK, response.getStatusCode());
    }

    @Test
    public void saveWithoutYtunnus() throws IOException {
        loginAs("testuser", lupaJarjestajaOid, OivaAccess.Context_Kayttaja);
        final String json = readFileToString("json/muutospyynto.json");
        final DocumentContext doc = jsonPath.parse(json);
        doc.set("$.jarjestajaYtunnus", null);
        final ResponseEntity<String> response = requestSave(prepareMultipartEntity(
                doc.jsonString()), "/api/muutospyynnot/tallenna");
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
                "file0", "file1", "file2", "file3", "file4", "file5"), "/api/muutospyynnot/tallenna");
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
        // Remove lupa id from request #CSCOIVA-1648
        doc.delete("$.lupaId");

        final ResponseEntity<String> updateResponse = requestSave(prepareMultipartEntity(doc.jsonString()), "/api/muutospyynnot/tallenna");

        // Check that lupa id is not null #CSCOIVA-1648
        final Long lupaId = jdbcTemplate.queryForObject("select lupa_id from muutospyynto where uuid = '"+ uuid +"'", Long.class);
        assertNotNull("LupaId should not be null!", lupaId);

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
        jdbcTemplate.update("UPDATE lupa SET kieli = 'sv' WHERE id = 1");
        loginAs("testuser", lupaJarjestajaOid,
                OivaAccess.Context_Kayttaja, OivaAccess.Context_Kayttaja);
        // Create new muutospyynto without ytunnus
        final String json = readFileToString("json/muutospyynto.json");
        DocumentContext doc = jsonPath.parse(json);
        doc.set("$.jarjestajaYtunnus", null);
        ResponseEntity<String> createResponse = requestSave(prepareMultipartEntity(
                doc.jsonString(),
                "file0", "file1", "file2", "file3", "file4", "file5"), "/api/muutospyynnot/tallenna");
        assertEquals("Response status should match", OK, createResponse.getStatusCode());
        doc = jsonPath.parse(createResponse.getBody());
        final String uuid = doc.read("$.uuid", String.class);
        logout();
        // Change muutospyynto tila to "KASITTELYSSA"
        loginAs("testNimenkirjoittaja", lupaJarjestajaOid, OivaAccess.Context_Nimenkirjoittaja);
        makeRequest(POST,
                "/api/muutospyynnot/tila/avoin/" + uuid,
                null, OK);
        logout();
        // Change muutospyynto tila to "VALMISTELUSSA"
        loginAs("testEsittelija", okmOid, OivaAccess.Context_Esittelija);
        makeRequest(POST,
                "/api/muutospyynnot/tila/valmistelussa/" + uuid,
                null, OK);

        // Change tila to "ESITTELYSSA"
        makeRequest(POST,
                "/api/muutospyynnot/tila/esittelyssa/" + uuid,
                null, OK);

        // Add paatoskirje
        Map<String, String> paatosKirje = new HashMap<>();
        paatosKirje.put("nimi", "paatoskirje");
        paatosKirje.put("tiedostoId", "paatoskirje");
        paatosKirje.put("tyyppi", "paatosKirje");
        paatosKirje.put("kieli", "fi");
        doc.add("$.liitteet", paatosKirje);
        final ResponseEntity<String> paatoskirjeResponse = requestSave(prepareMultipartEntity(doc.jsonString(), "paatoskirje"),
                "api/muutospyynnot/" + uuid + "/liitteet/paatoskirje");

        assertEquals("Paatoskirje save should have been success!", OK, paatoskirjeResponse.getStatusCode());

        // Change muutospyynto tila to "PAATETTY"
        final String response = makeRequest(POST,
                "/api/muutospyynnot/tila/paatetty/" + uuid,
                null, OK).getBody();
        assertEquals("\"" + uuid + "\"", response);

        // Fetch the latest lupa for jarjestaja
        final TypeRef<List<String>> stringRef = new TypeRef<List<String>>() {
        };
        final ResponseEntity<String> lupaJson = makeRequest("/api/luvat/jarjestaja/1.1.111.111.11.11111111111?with=all", OK);
        doc = jsonPath.parse(lupaJson.getBody());
        final String oldLupaUuid = doc.read("$.uuid", String.class);
        final String asianumero = doc.read("$.asianumero", String.class);
        assertEquals("Lupa kieli should match!", "sv", doc.read("$.kieli", String.class));
        assertEquals("Lupa diaarinumero and asianumero should be equal!", doc.read("$.diaarinumero", String.class), asianumero);
        final String oldAsianumero = "VN/123456/1234";
        assertEquals("Lupa asianumero should match!", oldAsianumero, asianumero);

        // Check that created lupa id is in muutospyynto table
        final Long lupa_id = jdbcTemplate.queryForObject("select luotu_lupa_id from muutospyynto where uuid = ?",
                new Object[] {UUID.fromString(uuid)}, Long.class);
        assertNotNull("Created lupa id in muutospyynto should not be null!", lupa_id);

        assertEquals("Maarays count should match!", 5, doc.read("$.maaraykset.length()",
                Integer.class).intValue());
        assertEquals("AliMaarays count should match!", 5,
                doc.read("$..aliMaaraykset[*].uuid", stringRef).size());
        final List<String> koodiarvoList = doc.read("$.maaraykset[*].koodiarvo", stringRef);
        assertFalse("There should not be removed maarays.", koodiarvoList.contains("124"));

        final TypeRef<List<Maarays>> maaraysRef = new TypeRef<List<Maarays>>() {
        };
        final List<Maarays> aliMaaraykset = doc.read("$.maaraykset[?(@.koodiarvo==123)].aliMaaraykset[*]", maaraysRef);
        assertEquals("Koulutus 123 should have 2 alimaarays", 2, aliMaaraykset.size());
        // Check that tutkintokieli is within alimaaraykset
        final Optional<Maarays> tutkintoKieli = aliMaaraykset.stream().filter(m -> m.getKoodisto().equals("kieli")).findFirst();
        assertTrue("There should be tutkinto kieli maarays", tutkintoKieli.isPresent());
        assertEquals("Tutkintokieli is english", "en", tutkintoKieli.get().getKoodiarvo());

        // Check history
        assertEquals("There should be history row for old lupa", 1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "lupahistoria", "lupa_id = 1"));
        final String kieli = jdbcTemplate.queryForObject("SELECT kieli FROM lupahistoria WHERE lupa_id = 1", String.class);
        assertEquals("Lupahistoria kieli should match!", "sv", kieli);

        // Change muutospyynto tila to "KORJAUKSESSA"
        makeRequest(POST,
                "/api/muutospyynnot/tila/korjauksessa/" + uuid,
                null, OK);
        final ResponseEntity<String> muutospyyntoJson = makeRequest("/api/muutospyynnot/id/" + uuid, OK);
        doc = jsonPath.parse(muutospyyntoJson.getBody());
        // Change asianumero
        final String changedAsianumero = "VN/1/2021";
        doc.set("$.asianumero", changedAsianumero);
        ResponseEntity<String> fixResponse = requestSave(prepareMultipartEntity(
                doc.jsonString(),
                "file0", "file1", "file2", "file3", "file4", "file5"), "/api/muutospyynnot/esittelija/tallenna");
        assertEquals("Response status should match", OK, fixResponse.getStatusCode());
        doc = jsonPath.parse(fixResponse.getBody());
        assertEquals("Fixed asianumero should match!", changedAsianumero, doc.read("$.asianumero", String.class));

        // Change muutospyynto tila to "PAATETTY" after fixing.
        final String fixedUuid = makeRequest(POST,
                "/api/muutospyynnot/tila/paatetty/" + uuid,
                null, OK).getBody();
        assertEquals("\"" + uuid + "\"", fixedUuid);

        // Fetch the latest lupa for jarjestaja
        final ResponseEntity<String> fixedLupaJson = makeRequest("/api/luvat/jarjestaja/1.1.111.111.11.11111111111?with=all", OK);
        doc = jsonPath.parse(fixedLupaJson.getBody());
        assertEquals("Fixed asianumero in lupa should match!", changedAsianumero, doc.read("$.asianumero", String.class));

        // Check that old lupa is deleted
        final Boolean oldLupaExists = jdbcTemplate.queryForObject("select exists(select id from lupa where uuid = ?)",
                new Object[] {UUID.fromString(oldLupaUuid)}, Boolean.class);
        assertFalse("Old lupa should not exists!", oldLupaExists);
    }

    @Test
    public void updateWithoutMuutokset() throws IOException {
        loginAs("testuser", lupaJarjestajaOid,
                OivaAccess.Context_Kayttaja, OivaAccess.Context_Katselija);

        // Create new
        ResponseEntity<String> createResponse = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json"),
                "file0", "file1", "file2", "file3", "file4", "file5"), "/api/muutospyynnot/tallenna");
        assertEquals("Response status should match", OK, createResponse.getStatusCode());
        DocumentContext doc = jsonPath.parse(createResponse.getBody());
        log.info("Muutospyynto createResponse: {}", doc.jsonString());
        final String uuid = doc.read("$.uuid", String.class);
        assertNotNull("Muutospyynto uuid should not be null", uuid);

        // Remove all changes from update
        doc.set("$.muutokset", Collections.EMPTY_LIST);

        final ResponseEntity<String> updateResponse = requestSave(prepareMultipartEntity(doc.jsonString()), "/api/muutospyynnot/tallenna");
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
                readFileToString("json/muutospyynto.json")), "/api/muutospyynnot/tallenna");

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
    public void nimenkirjoittajaCanDelete() throws IOException {
        String username = "testuser";
        loginAs(username, lupaJarjestajaOid,
                OivaAccess.Context_Kayttaja, OivaAccess.Context_Nimenkirjoittaja);

        final ResponseEntity<String> response = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json")), "/api/muutospyynnot/tallenna");

        DocumentContext doc = jsonPath.parse(response.getBody());
        log.info("Muutospyynto createResponse: {}", doc.jsonString());
        final String uuid = doc.read("$.uuid", String.class);

        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                createURLWithPort("/api/muutospyynnot/" + uuid),
                DELETE, null, String.class);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND,
                restTemplate.getForEntity(createURLWithPort("/api/muutospyynnot/id/" + uuid), String.class).getStatusCode());
    }

    @Test
    public void esittelijaCanHandleMuutospyynto() throws IOException {
        String username = "testuser";
        loginAs(username, lupaJarjestajaOid,
                OivaAccess.Context_Kayttaja, OivaAccess.Context_Nimenkirjoittaja);

        ResponseEntity<String> response = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json")), "/api/muutospyynnot/tallenna");

        DocumentContext doc = jsonPath.parse(response.getBody());
        log.info("Muutospyynto createResponse: {}", doc.jsonString());
        final String uuid = doc.read("$.uuid", String.class);

        // Vie käsittelyyn esittelijälle
        super.requestBody("/api/muutospyynnot/tila/avoin/" + uuid, POST);

        // Login as esittelijä
        loginAs("esittelija", lupaJarjestajaOid, OivaAccess.Context_Esittelija);
        response = makeRequest("api/muutospyynnot?tilat=AVOIN", OK);
        doc = jsonPath.parse(response.getBody());
        assertEquals(1, doc.read("$.length()", Integer.class).intValue());

        // Ota käsittelyyn
        super.requestBody("/api/muutospyynnot/tila/valmistelussa/" + uuid, POST);

        doc = requestJSONData("/api/muutospyynnot?tilat=AVOIN");
        assertEquals(0, doc.read("$.length()", Integer.class).intValue());

        doc = requestJSONData("/api/muutospyynnot?tilat=VALMISTELUSSA");
        assertEquals(1, doc.read("$.length()", Integer.class).intValue());

        // Test api with multiple values for the same parameter
        assertEquals(1, requestJSONData("/api/muutospyynnot?tilat=VALMISTELUSSA,AVOIN").read("$.length()", Integer.class).intValue());
        assertEquals(1, requestJSONData("/api/muutospyynnot?tilat=VALMISTELUSSA&tilat=AVOIN").read("$.length()", Integer.class).intValue());

        doc = requestJSONData("/api/muutospyynnot?tilat=VALMISTELUSSA&vainOmat=true");
        assertEquals("Esittelija has one own muutospyynto", 1, doc.read("$.length()", Integer.class).intValue());

        loginAs("esittelija_2", lupaJarjestajaOid, OivaAccess.Context_Esittelija);
        doc = requestJSONData("/api/muutospyynnot?tilat=VALMISTELUSSA&vainOmat=true");
        assertEquals("Another esittelijä has no own muutospyynot", 0, doc.read("$.length()", Integer.class).intValue());
    }

    @Test
    public void esittelijaCreateSaveAndDelete() throws IOException {
        loginAs("elli esittelija", "", OivaAccess.Context_Esittelija);

        // ----- CREATE NEW -----
        ResponseEntity<String> createResponse =
                requestSave(prepareMultipartEntity(readFileToString("json/muutospyynto.json"),
                        "file0", "file1", "file2", "file3", "file4", "file5"), "/api/muutospyynnot/esittelija/tallenna");

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

        // ----- LOAD CREATED -----
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(createURLWithPort("/api/muutospyynnot/id/" + uuid), String.class);
        assertEquals("Get response status should match!", HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Create response and get response should match!", createResponse.getBody(),
                getResponse.getBody());

        // ----- UPDATE EXISTING -----
        final String newEndDate = "2020-02-20";
        final String asianumero = "VN/123/2020";
        doc.put("$", "voimassaalkupvm", newEndDate)
                .put("$", "paatospvm", newEndDate)
                .put("$", "asianumero", asianumero);

        final ResponseEntity<String> updateResponse =
                requestSave(prepareMultipartEntity(doc.jsonString()), "/api/muutospyynnot/esittelija/tallenna");

        assertEquals("Update response code should match!", HttpStatus.OK, updateResponse.getStatusCode());
        doc = jsonPath.parse(updateResponse.getBody());
        assertEquals(newEndDate, doc.read("$.voimassaalkupvm"));
        assertEquals(newEndDate, doc.read("$.paatospvm"));
        assertEquals(asianumero, doc.read("$.asianumero"));

        // ----- ESITTELE -----
        makeRequest(POST, "/api/muutospyynnot/tila/esittelyssa/" + uuid, null, OK);

        // ----- REVERSE TO VALMISTELU -----
        makeRequest(POST, "/api/muutospyynnot/tila/valmistelussa/" + uuid, null, OK);

        // ----- DELETE DRAFT -----
        makeRequest(DELETE, "/api/muutospyynnot/" + uuid, null, OK);
        makeRequest(GET, "/api/muutospyynnot/id/" + uuid, null, NOT_FOUND);
    }

    @Test
    public void esittelijaCreatesNewOrganizationLupa() throws IOException {
        loginAs("elli esittelija", "", OivaAccess.Context_Esittelija);
        DocumentContext doc = jsonPath.parse(readFileToString("json/muutospyynto.json"));
        // Remove lupa uuid and change jarjesta information for KJ that has no existing lupa.
        doc.delete("$.lupaUuid");
        doc.set("$.jarjestajaOid", "1.2.3.4.111111");
        doc.set("$.jarjestajaYtunnus", "12345-1");
        final ResponseEntity<String> createdResponse = requestSave(prepareMultipartEntity(
                doc.jsonString()), "/api/muutospyynnot/esittelija/tallenna");
        assertEquals("Response code should match!", OK, createdResponse.getStatusCode());
        doc = jsonPath.parse(createdResponse.getBody());
        final String uuid = doc.read("$.uuid", String.class);

        // Change tila to "ESITTELYSSA"
        makeRequest(POST,
                "/api/muutospyynnot/tila/esittelyssa/" + uuid,
                null, OK).getBody();

        // Add paatoskirje
        Map<String, String> paatosKirje = new HashMap<>();
        paatosKirje.put("nimi", "paatoskirje");
        paatosKirje.put("tiedostoId", "paatoskirje");
        paatosKirje.put("tyyppi", "paatosKirje");
        paatosKirje.put("kieli", "fi");
        doc.add("$.liitteet", paatosKirje);
        final ResponseEntity<String> paatoskirjeResponse = requestSave(prepareMultipartEntity(doc.jsonString(), "paatoskirje"),
                "api/muutospyynnot/" + uuid + "/liitteet/paatoskirje");

        assertEquals("Paatoskirje save should have been success!", OK, paatoskirjeResponse.getStatusCode());

        // Change muutospyynto tila to "PAATETTY"
        final String response = makeRequest(POST,
                "/api/muutospyynnot/tila/paatetty/" + uuid,
                null, OK).getBody();
        assertEquals("\"" + uuid + "\"", response);

        // Fetch the latest lupa for jarjestaja
        final ResponseEntity<String> lupaJson = makeRequest("/api/luvat/jarjestaja/1.2.3.4.111111?with=all", OK);
        doc = jsonPath.parse(lupaJson.getBody());
        final String asianumero = doc.read("$.asianumero", String.class);
        assertEquals("Lupa diaarinumero and asianumero should be equal!", doc.read("$.diaarinumero", String.class), asianumero);
        assertEquals("Lupa asianumero should match!", "VN/123456/1234", asianumero);
    }

    /**
     * Test generated lupa object based on existing lupa and muutospyynto.
     * Templates used to generate html files are in another repository so html and pdf generation are mocked
     */
    @Test
    public void getLupaFromMuutospyynto() {
        final String muutospyyntoUuid = "2b02c730-ebef-11e9-8d25-0242ac110023";
        setUpDb("sql/muutospyynto_data.sql", "sql/muutos_data_lisays_poisto.sql");

        when(pebbleService.toHTML(any(Lupa.class), any(OivaTemplates.RenderOptions.class))).thenReturn(Optional.of(""));
        when(princeXMLService.toPDF(anyString(), any(OutputStream.class), any(OivaTemplates.RenderOptions.class)))
                .thenReturn(true);

        loginAs("testuser", "esittelija_org", OivaAccess.Context_Esittelija);
        ResponseEntity<Object> responseEntity = restTemplate.getForEntity(createURLWithPort("/api/muutospyynnot/pdf/esikatsele/" + muutospyyntoUuid), Object.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ArgumentCaptor<Lupa> lupaCaptor = ArgumentCaptor.forClass(Lupa.class);
        verify(pebbleService).toHTML(lupaCaptor.capture(), any(OivaTemplates.RenderOptions.class));

        // Verify correct lupa is generated from muutospyynto
        Lupa lupa = lupaCaptor.getValue();
        assertNotNull(lupa);

        // Case: new muutos (kieli: sv)
        Maarays kieli = lupa.getMaaraykset().stream()
                .filter(m -> "kieli".equals(m.getKoodisto()) && "sv".equals(m.getKoodiarvo()))
                .findFirst().orElse(null);
        assertNotNull(kieli);

        // Case: new muutos, alimaarays and alialimaarays are added to kielikoodisto (fi__ -> fi_lisatty -> fi_alimaarays)
        kieli = lupa.getMaaraykset().stream()
                .filter(m -> "kieli".equals(m.getKoodisto()) && "fi__".equals(m.getKoodiarvo()))
                .findFirst().orElse(null);
        assertNotNull(kieli);
        assertEquals(1, kieli.getAliMaaraykset().size());
        Maarays alimaarays = kieli.getAliMaaraykset().iterator().next();
        assertEquals("kieli", alimaarays.getKoodisto());
        assertEquals("fi_lisatty", alimaarays.getKoodiarvo());
        assertEquals(1, alimaarays.getAliMaaraykset().size());
        alimaarays = alimaarays.getAliMaaraykset().iterator().next();
        assertEquals("kieli", alimaarays.getKoodisto());
        assertEquals("fi_alimaarays", alimaarays.getKoodiarvo());

        // Case: maarays and alimaarays are removed (koulutus 123 -> koulutus 1231)
        assertEquals(0, lupa.getMaaraykset().stream().filter(m -> "koodisto".equals(m.getKoodisto()) && "123".equals(m.getKoodiarvo())).count());
        assertEquals(0, lupa.getMaaraykset().stream().filter(m -> "koodisto".equals(m.getKoodisto()) && "1231".equals(m.getKoodiarvo())).count());
    }

    @Test
    public void testDuplikaattiasianumero() throws IOException {
        // Saves muutospyyntö with asianumero VN/1234/123456
        setUpDb("sql/muutospyynto_data.sql");
        loginAs("testEsittelija", okmOid, OivaAccess.Context_Esittelija);

        // Asianumero exists with same uuid -> not duplicate
        String resp = requestDuplikaattiasianumero(prepareMultipartForDuplikaattiAsianumero("2b02c730-ebef-11e9-8d25-0242ac110023", "VN/1234/123456"));
        assertEquals("false", resp);

        // Asianumero exists with different uuid -> duplicate
        resp = requestDuplikaattiasianumero(prepareMultipartForDuplikaattiAsianumero("d2a86c5b-0721-4dfa-adf9-f6e4f3f9f084", "VN/1234/123456"));
        assertEquals("true", resp);

        // Asianumero exists when uuid is null -> duplicate
        resp = requestDuplikaattiasianumero(prepareMultipartForDuplikaattiAsianumero(null, "VN/1234/123456"));
        assertEquals("true", resp);

        // Asianumero does not exist -> not duplicate
        resp = requestDuplikaattiasianumero(prepareMultipartForDuplikaattiAsianumero(null, "VN/1/123456"));
        assertEquals("false", resp);
    }

    private ResponseEntity<String> requestSave(HttpEntity<MultiValueMap<String, Object>> requestEntity, String uri) {
        return restTemplate
                .postForEntity(createURLWithPort(uri), requestEntity, String.class);
    }

    private ResponseEntity<String> requestLiitteet(String uuid) {
        return restTemplate
                .getForEntity(createURLWithPort("/api/muutospyynnot/" + uuid + "/liitteet/"), String.class);
    }

    private String requestDuplikaattiasianumero(HttpEntity<MultiValueMap<String, Object>> requestEntity) {
        return restTemplate.postForObject(createURLWithPort("/api/muutospyynnot/duplikaattiasianumero"), requestEntity, String.class);
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

    private HttpEntity<MultiValueMap<String, Object>> prepareMultipartForDuplikaattiAsianumero(String uuid, String asianumero) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<String> uuidEntity = new HttpEntity<>(uuid, jsonHeaders);
        final HttpEntity<String> asianumeroEntity = new HttpEntity<>(asianumero, jsonHeaders);
        if (uuid != null) {
            parts.add("uuid", uuidEntity);
        }
        parts.add("asianumero", asianumeroEntity);

        final HttpHeaders multipartHeaders = new HttpHeaders();
        multipartHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        multipartHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<>(parts, multipartHeaders);
    }

}
