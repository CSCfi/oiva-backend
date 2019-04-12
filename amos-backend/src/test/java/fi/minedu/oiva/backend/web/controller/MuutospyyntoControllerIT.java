package fi.minedu.oiva.backend.web.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.TypeRef;
import fi.minedu.oiva.backend.test.BaseIT;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MuutospyyntoControllerIT extends BaseIT {
    @Override
    public void beforeTest() {
        setUpDb("sql/lupa_data.sql");
    }

    @Test
    public void save() throws IOException {
        // Create new
        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                prepareMultipartEntity(readFileToString("json/muutospyynto.json"),
                "file0", "file1", "file2", "file3", "file4", "file5");

        ResponseEntity<String> createResponse = requestSave(requestEntity);
        assertEquals("Response status should match", 200, createResponse.getStatusCodeValue());
        DocumentContext doc = jsonPath.parse(createResponse.getBody());
        log.info("Muutospyynto createResponse: {}", doc.jsonString());
        final String uuid = doc.read("$.uuid", String.class);
        assertNotNull("Muutospyynto uuid should not be null", uuid);
        assertEquals("Liite table count should match!", 6,
                JdbcTestUtils.countRowsInTable(jdbcTemplate, "liite"));

        // Load created
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(createURLWithPort("/api/muutospyynnot/id/" + uuid), String.class);
        assertEquals("Get response status should match!", 200, getResponse.getStatusCodeValue());
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

        final HttpEntity<MultiValueMap<String, Object>> updateEntity = prepareMultipartEntity(doc.jsonString());
        final ResponseEntity<String> updateResponse = requestSave(updateEntity);

        assertEquals("Update response code should match!", 200, updateResponse.getStatusCodeValue());

        assertEquals("Liite table count should match!", 4,
                JdbcTestUtils.countRowsInTable(jdbcTemplate, "liite"));

        doc = jsonPath.parse(updateResponse.getBody());
        final TypeRef<List<Boolean>> boolRef = new TypeRef<List<Boolean>>() {};
        final List<Boolean> list = doc.read("$.liitteet[?(@.nimi == 'muutospyynto_liite2')].salainen", boolRef);
        assertTrue("Muutospyynto liite 2 should be secret!", list.get(0));

        final TypeRef<List<String>> stringRef = new TypeRef<List<String>>() {};
        final List<String> names = doc.read("$..meta.liitteet[0].nimi", stringRef);
        assertEquals("Muutos liite 4 name should be match!", changeName, names.get(0));
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
        multipartHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<>(parts, multipartHeaders);
    }

}
