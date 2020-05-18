package fi.minedu.oiva.backend.core.web.controller;

import com.jayway.jsonpath.DocumentContext;
import fi.minedu.oiva.backend.core.it.BaseIT;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class BaseLiiteControllerIT extends BaseIT {

    @Override
    public void beforeTest() {
        setUpDb("sql/liite_data.sql");
    }

    @Test
    public void get() {
        ResponseEntity<String> response = makeRequest("/api/liitteet/cc3962e0-43b6-11e8-b2ef-005056aa0e61", HttpStatus.OK);
        DocumentContext doc = jsonPath.parse(response.getBody());
        log.info(doc.jsonString());
        assertEquals("Testi_liite_1", doc.read("$.nimi"));
        assertTrue(doc.read("$.salainen", Boolean.class));

        response = makeRequest("/api/liitteet/cc39666e-43b6-11e8-b2ef-005056aa0e62", HttpStatus.OK);
        doc = jsonPath.parse(response.getBody());
        log.info(doc.jsonString());
        assertEquals("Testi_liite_2", doc.read("$.nimi"));
        assertFalse(doc.read("$.salainen", Boolean.class));
    }

    @Test
    public void deleteNoAccessForKayttaja() {
        loginAs("user1", okmOid, OivaAccess.Context_Kayttaja);
        makeRequest(HttpMethod.DELETE,
                "/api/liitteet/cc3962e0-43b6-11e8-b2ef-005056aa0e61", null, HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete() {
        String uuid = "cc3962e0-43b6-11e8-b2ef-005056aa0e61";
        loginAs("testEsittelija", okmOid, OivaAccess.Context_Esittelija);
        makeRequest(HttpMethod.DELETE, "/api/liitteet/" + uuid, null, HttpStatus.OK);
        makeRequest("/api/liitteet/" + uuid, HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteNotFound() {
        loginAs("testEsittelija", okmOid, OivaAccess.Context_Esittelija);
        makeRequest(HttpMethod.DELETE,
                "/api/liitteet/d190a7be-f8d4-11ea-adc1-0242ac120002", null, HttpStatus.NOT_FOUND);
    }
}
