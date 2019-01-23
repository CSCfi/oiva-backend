package fi.minedu.oiva.backend.web.controller;

import com.jayway.jsonpath.DocumentContext;
import fi.minedu.oiva.backend.test.BaseIT;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

public class LupaControllerIT extends BaseIT {

    @Test
    public void getAll() {
        ResponseEntity<String> response = request("/api/luvat", HttpStatus.OK);
        final DocumentContext doc = jsonPath.parse(response.getBody());
        log.info(doc.jsonString());
        assertEquals(4, doc.read("$.length()", Integer.class).intValue());
    }

    @Test
    public void getAllWithJarjestaja() {
        final ResponseEntity<String> response = request("/api/luvat/jarjestajilla", HttpStatus.OK);
        final DocumentContext doc = jsonPath.parse(response.getBody());
        log.info(doc.jsonString());
        assertEquals(4, doc.read("$.length()", Integer.class).intValue());
        assertEquals("Testi organisaatio", doc.read("$.[0].jarjestaja.nimi.fi"));
    }

    @Override
    public void beforeTest() {
        setUpDb("sql/lupa_data.sql");
    }
}