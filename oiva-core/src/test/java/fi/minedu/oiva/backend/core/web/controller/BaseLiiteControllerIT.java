package fi.minedu.oiva.backend.core.web.controller;

import com.jayway.jsonpath.DocumentContext;
import fi.minedu.oiva.backend.core.it.BaseIT;
import org.junit.Test;
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
        ResponseEntity<String> response = makeRequest("/api/liitteet/cc3962e0-43b6-11e8-b2ef-005056aa0e66", HttpStatus.OK);
        DocumentContext doc = jsonPath.parse(response.getBody());
        log.info(doc.jsonString());
        assertEquals("Testi_liite_1", doc.read("$.nimi"));
        assertTrue(doc.read("$.salainen", Boolean.class));

        response = makeRequest("/api/liitteet/cc39666e-43b6-11e8-b2ef-005056aa0e66", HttpStatus.OK);
        doc = jsonPath.parse(response.getBody());
        log.info(doc.jsonString());
        assertEquals("Testi_liite_2", doc.read("$.nimi"));
        assertFalse(doc.read("$.salainen", Boolean.class));
    }
}
