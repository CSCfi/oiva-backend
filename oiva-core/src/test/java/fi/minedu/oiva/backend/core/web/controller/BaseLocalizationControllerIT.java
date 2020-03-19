package fi.minedu.oiva.backend.core.web.controller;

import com.jayway.jsonpath.DocumentContext;
import fi.minedu.oiva.backend.core.it.BaseIT;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public abstract class BaseLocalizationControllerIT extends BaseIT {

    @Value("${opintopolku.lokalisaatio.restUrl}")
    private String localizationUrl;

    private String[] locales = {"fi", "en", "sv"};

    @Override
    public void beforeTest() {
        mockLocalizationRequest();
    }

    @Test
    public void getLocalizationFi() {
        getLocalizations("fi", "Testi käännös 1", "Testi käännös 2");
    }

    @Test
    public void getLocalizationEn() {
        getLocalizations("en", "Test translation 1", "Test translation 2");
    }

    @Test
    public void getLocalizationSv() {
        getLocalizations("sv", "Testet översättning 1", "Testet översättning 2");
    }

    private void getLocalizations(final String locale, String... expectedTranslations) {
        final ResponseEntity<String> response = makeRequest("/api/lokalisaatio?lang=" + locale, HttpStatus.OK);
        final DocumentContext doc = jsonPath.parse(response.getBody());
        assertEquals(expectedTranslations[0], doc.read("$.test_key_1"));
        assertEquals(expectedTranslations[1], doc.read("$.test_key_2"));
    }

    private void mockLocalizationRequest() {
        final MockServerClient mockClient = mockServerRule.getClient().reset();
        try {
            for (String locale : locales) {
                mockClient.when(request().withMethod("GET")
                        .withPath(localizationUrl)
                        .withQueryStringParameter("category", "oiva")
                        .withQueryStringParameter("locale", locale))
                        .respond(response().withBody(readFileToString("json/localization_" + locale + ".json")));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}