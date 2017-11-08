package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.service.LocalizationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + LocalizationController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
public class LocalizationController {

    public static final String path = "/lokalisaatio";

    @Autowired
    private LocalizationService service;

    @ApiOperation(notes = "Proxyttää lokalisaation Opintopolun lokalisaatiopalvelusta.", value = "")
    @RequestMapping(method = GET)
    public CompletableFuture<Map<String, String>> getTranslations(
        @RequestParam(defaultValue = "fi") String lang,
        @RequestParam(defaultValue = "false") Boolean refresh) {
        return async(() -> {
            if(refresh) service.refreshTranslations();
            return service.getTranslationsWS(lang);
        });
    }
}
