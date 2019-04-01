package fi.minedu.oiva.backend.web.controller;

import com.jayway.jsonpath.DocumentContext;
import fi.minedu.oiva.backend.test.BaseIT;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LiiteControllerIT extends BaseIT {

    @Override
    public void beforeTest() {
        setUpDb("sql/liite_data.sql");
    }

    @Test
    public void get() {
        ResponseEntity<String> response = request("/api/liitteet/1", HttpStatus.OK);
        DocumentContext doc = jsonPath.parse(response.getBody());
        log.info(doc.jsonString());
        assertEquals("Testi_liite_1", doc.read("$.nimi"));
        assertTrue(doc.read("$.salainen", Boolean.class));

        response = request("/api/liitteet/2", HttpStatus.OK);
        doc = jsonPath.parse(response.getBody());
        log.info(doc.jsonString());
        assertEquals("Testi_liite_2", doc.read("$.nimi"));
        assertFalse(doc.read("$.salainen", Boolean.class));
    }
}