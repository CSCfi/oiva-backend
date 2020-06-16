package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Yllapitaja;
import fi.minedu.oiva.backend.core.service.LocalizationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.core.util.AsyncUtil.async;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

public abstract class BaseLocalizationController {

    public static final String path = "/lokalisaatio";

    @Autowired
    private LocalizationService service;

    @OivaAccess_Public
    @RequestMapping(method = GET)
    @ApiOperation(notes = "Palauttaa kielikäännökset suomeksi ja ruotsiksi opintopolun lokalisaatiopalvelusta", value = "")
    public CompletableFuture<Map<String, Map<String, String>>> getTranslations(
            final @RequestParam(defaultValue = "false") Boolean refresh) {
        return async(() -> {
            if (refresh) {
                service.refreshTranslations();
            }
            Map<String,Map<String,String>> translations = new HashMap<>();
            translations.put("fi", service.getTranslations("fi"));
            translations.put("sv", service.getTranslations("sv"));
            return translations;
        });
    }

    @OivaAccess_Yllapitaja
    @ApiOperation(notes = "Tallentaa kielikäännökset opintopolun lokalisaatiopalveluun", value = "")
    @RequestMapping(method = PUT, value = "/tallenna/{lang}")
    public ResponseEntity<Map<String, String>> save(@PathVariable(value = "lang") String lang, @RequestBody Map<String, String> translations) {
        return service.saveTranslations(translations, lang);
    }
}
