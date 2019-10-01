package fi.minedu.oiva.backend.core.web.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.TypeRef;
import fi.minedu.oiva.backend.core.it.BaseIT;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public abstract class BaseMuutospyyntoControllerIT extends BaseIT {

    private final String lupaJarjestajaOid = "1.1.111.111.11.11111111111";

    @Override
    public void beforeTest() {
        setUpDb("sql/lupa_data.sql");
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
        assertEquals("Response code should match!", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void saveWithSuperiorRole() throws IOException {
        loginAs("testuser", lupaJarjestajaOid, OivaAccess.Context_Nimenkirjoittaja);
        final ResponseEntity<String> response = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json")));
        assertEquals("Response code should match!", HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void save() throws IOException {
        loginAs("testuser", lupaJarjestajaOid,
                OivaAccess.Context_Kayttaja, OivaAccess.Context_Katselija);

        // Create new
        ResponseEntity<String> createResponse = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json"),
                "file0", "file1", "file2", "file3", "file4", "file5"));
        assertEquals("Response status should match", HttpStatus.OK, createResponse.getStatusCode());
        DocumentContext doc = jsonPath.parse(createResponse.getBody());
        log.info("Muutospyynto createResponse: {}", doc.jsonString());
        final String uuid = doc.read("$.uuid", String.class);
        assertNotNull("Muutospyynto uuid should not be null", uuid);
        assertEquals("Liite table count should match!", 6,
                JdbcTestUtils.countRowsInTable(jdbcTemplate, "liite"));

        // Muutos with koodiarvo 334113 should have two aliMaarays
        List alimaaraykset = doc.read("$.muutokset[?(@.koodiarvo == 334113)].aliMaaraykset", List.class);
        assertEquals(1, alimaaraykset.size());
        alimaaraykset = (List) alimaaraykset.get(0);
        assertEquals(2, alimaaraykset.size());

        // Load created
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(createURLWithPort("/api/muutospyynnot/id/" + uuid), String.class);
        assertEquals("Get response status should match!", HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Create response and get response should match!", createResponse.getBody(),
                getResponse.getBody());

        // Update existing

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

        assertEquals("Update response code should match!", HttpStatus.OK, updateResponse.getStatusCode());

        assertEquals("Liite table count should match!", 4,
                JdbcTestUtils.countRowsInTable(jdbcTemplate, "liite"));

        doc = jsonPath.parse(updateResponse.getBody());
        final TypeRef<List<Boolean>> boolRef = new TypeRef<List<Boolean>>() {
        };
        final List<Boolean> list = doc.read("$.liitteet[?(@.nimi == 'muutospyynto_liite2')].salainen", boolRef);
        assertTrue("Muutospyynto liite 2 should be secret!", list.get(0));

        final TypeRef<List<String>> stringRef = new TypeRef<List<String>>() {
        };
        final List<String> names = doc.read("$..meta.liitteet[0].nimi", stringRef);
        assertEquals("Muutos liite 4 name should be match!", changeName, names.get(0));
    }

    @Test
    public void updateWithoutMuutokset() throws IOException {
        loginAs("testuser", lupaJarjestajaOid,
                OivaAccess.Context_Kayttaja, OivaAccess.Context_Katselija);

        // Create new
        ResponseEntity<String> createResponse = requestSave(prepareMultipartEntity(
                readFileToString("json/muutospyynto.json"),
                "file0", "file1", "file2", "file3", "file4", "file5"));
        assertEquals("Response status should match", HttpStatus.OK, createResponse.getStatusCode());
        DocumentContext doc = jsonPath.parse(createResponse.getBody());
        log.info("Muutospyynto createResponse: {}", doc.jsonString());
        final String uuid = doc.read("$.uuid", String.class);
        assertNotNull("Muutospyynto uuid should not be null", uuid);

        // Remove all changes from update
        doc.set("$.muutokset", Collections.EMPTY_LIST);

        final ResponseEntity<String> updateResponse = requestSave(prepareMultipartEntity(doc.jsonString()));
        assertEquals("Update response code should match!", HttpStatus.OK, updateResponse.getStatusCode());

        assertEquals("Liite table count should match!", 2,
                JdbcTestUtils.countRowsInTable(jdbcTemplate, "liite"));

        // Load updated muutospyynto
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(createURLWithPort("/api/muutospyynnot/id/" + uuid), String.class);
        assertEquals("Get response status should match!", HttpStatus.OK, getResponse.getStatusCode());

        assertEquals("Update response and get response should match!", updateResponse.getBody(),
                getResponse.getBody());
    }

    private ResponseEntity<String> requestSave(HttpEntity<MultiValueMap<String, Object>> requestEntity) {
        return restTemplate
                .postForEntity(createURLWithPort("/api/muutospyynnot/tallenna"), requestEntity, String.class);
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
