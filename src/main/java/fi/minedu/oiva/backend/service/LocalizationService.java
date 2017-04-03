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

    public Optional<JsonNode> getTranslations(String localeStr) {
        final ObjectMapper mapper = new ObjectMapper();
        final InputStream is = getClass().getClassLoader().getResourceAsStream("languages/" + localeStr + ".json");
        try {
            return Optional.ofNullable(mapper.readTree(is));

        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            return Optional.empty();
        }
    }

    @Cacheable(value = "translationsCache", key = "#localeStr")
    public Map<String, String> getTranslationsREST(String localeStr) {
        Map<String, String> transMap = new HashMap<>();
        try {
            JsonNode json = ObjectMapperSingleton.mapper.readTree(restTemplate.getForObject(String.format(localizationUrl + urlSuffix, localeStr), String.class));
            if (json.isArray()) {
                for (final JsonNode translation : json) {
                    transMap.put(translation.get("key").asText(), translation.get("value").asText());
                }
            }

        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return transMap;
    }

    @CacheEvict(value = "translationsCache", allEntries = true)
    public void refreshTranslations() {}
}
