package fi.minedu.oiva.backend.core.web.controller;

import com.google.common.collect.Lists;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.TypeRef;
import fi.minedu.oiva.backend.core.it.BaseIT;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.Times;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;

public abstract class BaseLocalizationControllerIT extends BaseIT {

    @Value("${opintopolku.lokalisaatio.restUrl}")
    private String localizationUrl;

    @Override
    public void beforeTest() {
        try {
            mockLocalizationGetRequests();
        } catch (IOException e) {
            throw new RuntimeException("Init failed", e);
        }
    }

    @Test
    public void getLocalizationFi() throws IOException {
        getLocalizations("fi", getExpectations("Testi käännös 1", "Testi käännös 2"));
    }

    @Test
    public void getLocalizationSv() throws IOException {
        getLocalizations("sv", getExpectations("Testet översättning 1", "Testet översättning 2"));
    }

    @Test
    public void saveLocalizationsFi() throws IOException {
        saveLocalizations("fi", getExpectations("Testi käännös 1", "Testi käännös 2"));
    }

    @Test
    public void saveLocalizationsEn() throws IOException {
        saveLocalizations("en", getExpectations("Test translation 1", "Test translation 2"));
    }

    @Test
    public void saveLocalizationsSv() throws IOException {
        saveLocalizations("sv", getExpectations("Testet översättning 1", "Testet översättning 2"));
    }

    private void saveLocalizations(String locale, Map<String, String> expectations) throws IOException {
        loginAs("admin", "1.1.1.1111", OivaAccess.Context_Yllapitaja);
        mockLocalizationUpdateRequests(locale, expectations);
        final ResponseEntity<String> response = makeRequest(PUT, "/api/lokalisaatio/tallenna/" + locale, expectations, OK);
        Map<String, String> localizations = jsonPath.parse(response.getBody()).read("$", new TypeRef<Map<String, String>>() {});
        assertLocalizations(localizations, expectations);
    }

    private Map<String, String> getExpectations(String... expectations) {
        Map<String, String> expectedLocalizations = new HashMap<>();
        for (int i = 0; i < expectations.length; i++) {
            expectedLocalizations.put("test_key_" + (i + 1), expectations[i]);
        }
        return expectedLocalizations;
    }

    private void getLocalizations(final String locale, Map<String, String> expectations) throws IOException {
        final ResponseEntity<String> response = makeRequest("/api/lokalisaatio", OK);
        Map<String, String> localizations = jsonPath.parse(response.getBody()).read("$." + locale, new TypeRef<Map<String, String>>() {});
        assertLocalizations(localizations, expectations);
    }

    private void assertLocalizations(Map<String, String> response, Map<String, String> expectations) {
        final DocumentContext doc = jsonPath.parse(response);
        expectations.forEach((key, value) -> {
            assertEquals(value, doc.read("$." + key));
        });
    }

    private void mockLocalizationUpdateRequests(String locale, Map<String, String> expectedLocalizations) throws IOException {
        final MockServerClient mockClient = mockServerRule.getClient();
        final StringBuilder json = new StringBuilder("[");
        expectedLocalizations.forEach((key, value) -> {
            json.append("{\"category\":\"oiva\",\"locale\":\"")
                    .append(locale)
                    .append("\",\"key\":\"")
                    .append(key).append("\",\"value\":\"")
                    .append(value).append("\"},");
        });
        json.deleteCharAt(json.lastIndexOf(","));
        json.append("]");

        mockClient.when(request()
                .withMethod("POST")
                .withPath(localizationUrl + "/update")
                .withBody(json.toString()), Times.once())
                .respond(response().withBody(readFileToString("json/localization_" + locale + ".json")));
    }

    private void mockLocalizationGetRequests() throws IOException {
        final MockServerClient mockClient = mockServerRule.getClient().reset();
        for (String kieli : Lists.newArrayList("fi", "sv")) {
            mockClient.when(request().withMethod("GET")
                    .withPath(localizationUrl)
                    .withQueryStringParameter("category", "oiva")
                    .withQueryStringParameter("locale", kieli))
                    .respond(response().withBody(readFileToString("json/localization_" + kieli + ".json")));
        }
    }
}