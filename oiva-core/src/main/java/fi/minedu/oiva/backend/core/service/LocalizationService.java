package fi.minedu.oiva.backend.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import fi.minedu.oiva.backend.model.entity.json.ObjectMapperSingleton;
import fi.minedu.oiva.backend.model.entity.opintopolku.Localization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LocalizationService {

    private final static Logger logger = LoggerFactory.getLogger(LocalizationService.class);

    final String urlSuffix = "?category=oiva&locale=%s";

    @Value("${opintopolku.baseUrl}${opintopolku.lokalisaatio.restUrl}")
    private String localizationUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public LocalizationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "LocalizationService:get", key = "#lang")
    public Map<String, String> getTranslations(final String lang) {
        final String body = restTemplate.getForObject(String.
                format(localizationUrl + urlSuffix, lang), String.class);
        return parseTranslations(body);
    }

    @CacheEvict(value = "LocalizationService", allEntries = true)
    public void refreshTranslations() {
    }

    public ResponseEntity<Map<String, String>> saveTranslations(Map<String, String> translations, String lang) {
        List<Localization> data = new ArrayList<>();
        translations.forEach((key, value) -> {
            final Localization localization = new Localization("oiva", lang, key, value);
            data.add(localization);
        });
        try {
            final ResponseEntity<String> response = restTemplate.postForEntity(localizationUrl + "/update", data,
                    String.class);
            return new ResponseEntity<>(parseTranslations(response.getBody()), response.getStatusCode());
        } catch (HttpClientErrorException e) {
            logger.error("Could not save localizations to opintopolku!", e);
            return new ResponseEntity<>(e.getStatusCode());
        }
    }

    private Map<String, String> parseTranslations(String body) {
        Map<String, String> translations = new HashMap<>();
        final JsonNode json;
        try {
            json = ObjectMapperSingleton.mapper.readTree(body);
            if (json.isArray()) {
                for (final JsonNode translation : json) {
                    translations.put(translation.get("key").asText(), translation.get("value").asText());
                }
            }
        } catch (IOException e) {
            logger.error("Failed to parse translation", e);
        }
        return translations;
    }
}
