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

public abstract class BaseExportControllerIT extends BaseIT {

    @Test
    public void getKustannustiedot() {
        // Setup
        jdbcTemplate.batchUpdate(
                createLupaUpdateQuery("2017-01-01", null, 1),
                createLupaUpdateQuery("2017-12-31", "2018-01-01", 2),
                createLupaUpdateQuery("2018-01-01", "2018-01-31", 3),
                createLupaUpdateQuery("2018-02-01", "2018-12-31", 4),
                createLupaUpdateQuery("2019-03-01", "2020-12-31", 5)
        );

        DocumentContext doc = requestKustannustiedot("2017-01-01", "2020-12-31", HttpStatus.OK);
        assertIds("All should return", doc, 1, 2, 3, 4, 5);

        doc = requestKustannustiedot("2018-01-01", "2018-02-01", HttpStatus.OK);
        assertIds("The first, third and fourth should return", doc, 1, 3, 4);

        doc = requestKustannustiedot("2018-02-01", "2019-12-31", HttpStatus.OK);
        assertIds("The first, fourth and fifth should return", doc, 1, 4, 5);
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

    private DocumentContext requestKustannustiedot(String startDate, String endDate, HttpStatus status) {
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/api/export/kustannustiedot");
        if (StringUtils.isNotEmpty(startDate)) {
            uriBuilder.queryParam("startDate", startDate);
        }
        if (StringUtils.isNotEmpty(endDate)) {
            uriBuilder.queryParam("endDate", endDate);
        }
        final String uri = uriBuilder.build().toUriString();
        final ResponseEntity<String> response =
                makeRequest(uri, status, getBasicAuthHeaders("oiva", "oiva"));
        final DocumentContext doc = jsonPath.parse(response.getBody() == null ? "{}" : response.getBody());
        log.info(doc.jsonString());
        return doc;
    }
}