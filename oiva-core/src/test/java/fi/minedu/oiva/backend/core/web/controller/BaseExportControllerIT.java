package fi.minedu.oiva.backend.core.web.controller;

import com.jayway.jsonpath.DocumentContext;
import fi.minedu.oiva.backend.core.it.BaseIT;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpMethod.GET;

public abstract class BaseExportControllerIT extends BaseIT {

    @Test
    public void getKustannustiedot() {
        // Setup
        jdbcTemplate.update("UPDATE lupa SET koulutustyyppi = null WHERE id in (1, 3, 4, 5, 6, 7)");
        jdbcTemplate.batchUpdate(
                createLupaUpdateQuery("2017-01-01", null, 1),
                createLupaUpdateQuery("2017-12-31", "2018-01-01", 2),
                createLupaUpdateQuery("2018-01-01", "2018-01-31", 3),
                createLupaUpdateQuery("2018-02-01", "2018-12-31", 4),
                createLupaUpdateQuery("2019-03-01", "2020-12-31", 5),
                createLupaUpdateQuery("2017-01-01", null, 6),
                createLupaUpdateQuery("2017-01-01", null, 7)
        );

        DocumentContext doc = requestKustannustiedot("2017-01-01", "2020-12-31", HttpStatus.OK);
        assertIds("All except 2 should return", doc, 1, 3, 4, 5, 6, 7);

        doc = requestKustannustiedot("2018-01-01", "2018-02-01", HttpStatus.OK);
        assertIds("The first, third, fourth, sixth and seventh should return", doc, 1, 3, 4, 6, 7);

        doc = requestKustannustiedot("2018-02-01", "2019-12-31", HttpStatus.OK);
        assertIds("The first, fourth, fifth, sixth and seventh should return", doc, 1, 4, 5, 6, 7);

        // Get with koulutustyyppi
        doc = requestKustannustiedot("1", "2017-01-01", "2020-12-31", HttpStatus.OK);
        assertIds("Only second should return", doc, 2);
    }

    @Test
    public void getKustannustiedotInvalidParameters() {
        requestKustannustiedot(null, "2019-01-01", HttpStatus.BAD_REQUEST);
        requestKustannustiedot("2018-01-01", null, HttpStatus.BAD_REQUEST);
        requestKustannustiedot("2019-01-01", "2018-01-01", HttpStatus.BAD_REQUEST);
    }

    private void assertIds(String message, DocumentContext doc, int... ids) {
        final List<String> jsonUuids = doc.read("$.*.uuid");
        jsonUuids.sort(String::compareTo);
        Assert.assertArrayEquals(message, Arrays.stream(ids).mapToObj(id ->
                        jdbcTemplate.queryForObject("SELECT uuid FROM lupa WHERE id = " + id, String.class))
                        .sorted().toArray(),
                jsonUuids.toArray());
    }

    @Override
    public void beforeTest() {
        setUpDb("sql/lupa_data.sql");
    }

    private String createLupaUpdateQuery(final String startDate, final String endDate, final long id) {
        return String.format("UPDATE lupa SET alkupvm = %s, loppupvm = %s WHERE id = %d",
                Optional.ofNullable(startDate).map(s -> "'" + s + "'").orElse("NULL"),
                Optional.ofNullable(endDate).map(s -> "'" + s + "'").orElse("NULL"),
                id);
    }

    @Test
    public void getJarjestysluvat() {
        setUpDb("sql/lupa_data_lisakouluttajat.sql");
        final String uri = UriComponentsBuilder.fromPath("/api/export/jarjestysluvat").build().toUriString();
        final ResponseEntity<String> response =
                makeRequest(GET, uri, getBasicAuthHeaders("oiva", "oiva"), null, HttpStatus.OK);
        final DocumentContext doc = jsonPath.parse(response.getBody() == null ? "{}" : response.getBody());
        log.info(doc.jsonString());
        assertIds("All except 5 (Asiatyyppi Peruutus) and 9, 11, 13 (Lisäkouluttajat ammatillinen lupa) should return",
                doc, 1, 2, 3, 4, 6, 7, 8, 10, 12, 14, 15);

    }

    private DocumentContext requestKustannustiedot(String startDate, String endDate, HttpStatus status) {
        return requestKustannustiedot(null, startDate, endDate, status);
    }

    private DocumentContext requestKustannustiedot(String koulutustyyppi, String startDate, String endDate, HttpStatus status) {
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/api/export/kustannustiedot");
        if (StringUtils.isNotEmpty(koulutustyyppi)) {
            uriBuilder.queryParam("koulutustyyppi", koulutustyyppi);
        }
        if (StringUtils.isNotEmpty(startDate)) {
            uriBuilder.queryParam("startDate", startDate);
        }
        if (StringUtils.isNotEmpty(endDate)) {
            uriBuilder.queryParam("endDate", endDate);
        }
        final String uri = uriBuilder.build().toUriString();
        final ResponseEntity<String> response =
                makeRequest(GET, uri, getBasicAuthHeaders("oiva", "oiva"), null, status);
        final DocumentContext doc = jsonPath.parse(response.getBody() == null ? "{}" : response.getBody());
        log.info(doc.jsonString());
        return doc;
    }
}
