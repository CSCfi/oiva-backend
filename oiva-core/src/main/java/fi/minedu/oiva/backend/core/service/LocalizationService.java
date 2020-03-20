package fi.minedu.oiva.backend.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import fi.minedu.oiva.backend.model.entity.json.ObjectMapperSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
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
        final Map<String, String> translations = new HashMap<>();
        try {
            final JsonNode json = ObjectMapperSingleton.mapper
                    .readTree(restTemplate.getForObject(String.
                            format(localizationUrl + urlSuffix, lang), String.class));
            if (json.isArray()) {
                for (final JsonNode translation : json) {
                    translations.put(translation.get("key").asText(), translation.get("value").asText());
                }
            }
        } catch (IOException ioe) {
            logger.error("Failed to get translation", ioe);
        }
        return translations;
    }

    @CacheEvict(value = "LocalizationService", allEntries = true)
    public void refreshTranslations() {
    }
}
