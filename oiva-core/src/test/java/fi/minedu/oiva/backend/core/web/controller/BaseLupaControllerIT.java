package fi.minedu.oiva.backend.core.web.controller;

import com.jayway.jsonpath.DocumentContext;
import fi.minedu.oiva.backend.core.it.BaseIT;
import fi.minedu.oiva.backend.model.entity.AsiatyyppiValue;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public abstract class BaseLupaControllerIT extends BaseIT {

    private static String PARAM_KOULUTUSTYYPPI = "koulutustyyppi";
    private static String PARAM_OPPILAITOSTYYPPI = "oppilaitostyyppi";

    @Test
    public void getAll() {
        ResponseEntity<String> response = makeRequest("/api/luvat", HttpStatus.OK);
        final DocumentContext doc = jsonPath.parse(response.getBody());
        log.info(doc.jsonString());
        assertEquals(5, doc.read("$.length()", Integer.class).intValue());
    }

    @Test
    public void getAllWithJarjestaja() {
        getLuvat(null, 4);
    }

    @Test
    public void getAllWithJarjestajaParameters() {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        // Without parameters
        getLuvat(params, 4);

        // With koulutustyyppi 1
        params.put(PARAM_KOULUTUSTYYPPI, Collections.singletonList("1"));
        getLuvat(params, 2);

        // With koulutustyyppi 1 and oppilaitostyyppi 1
        params.put(PARAM_OPPILAITOSTYYPPI, Collections.singletonList("1"));
        getLuvat(params, 1);

        // With oppilaitostyyppi 1
        params.remove(PARAM_KOULUTUSTYYPPI);
        getLuvat(params, 2);
    }

    @Test
    public void getByYtunnusAndKoulutustyyppi() {
        setUpDb("sql/extra_lupa_data.sql");
        final ResponseEntity<String> response = makeRequest("/api/luvat/jarjestaja/1111111-1/koulutustyyppi/2/oppilaitostyyppi/1", HttpStatus.OK);
        final DocumentContext doc = jsonPath.parse(response.getBody());
        assertEquals("11/111/2020", doc.read("$.diaarinumero"));
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