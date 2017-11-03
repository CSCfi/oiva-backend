package fi.minedu.oiva.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.minedu.oiva.backend.entity.json.ObjectMapperSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class LocalizationService {

    private final static Logger logger = LoggerFactory.getLogger(LocalizationService.class);

    final String urlSuffix = "?category=oiva&locale=%s";

    @Value("${opintopolku.baseUrl}${opintopolku.lokalisaatio.restUrl}")
    private String localizationUrl;

    @Autowired
    private RestTemplate restTemplate;

    public Optional<JsonNode> getTranslations(final String localeStr) {
        final ObjectMapper mapper = new ObjectMapper();
        final InputStream is = getClass().getClassLoader().getResourceAsStream("languages/" + localeStr + ".json");
        try {
            return Optional.ofNullable(mapper.readTree(is));

        } catch (IOException ioe) {
            logger.error("Failed to get translation", ioe);
            return Optional.empty();
        }
    }

    @Cacheable(value = "LocalizationService:get", key = "#localeStr")
    public Map<String, String> getTranslationsWS(final String localeStr) {
        final Map<String, String> translations = new HashMap<>();
        try {
            final JsonNode json = ObjectMapperSingleton.mapper.readTree(restTemplate.getForObject(String.format(localizationUrl + urlSuffix, localeStr), String.class));
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
    public void refreshTranslations() {}
}
