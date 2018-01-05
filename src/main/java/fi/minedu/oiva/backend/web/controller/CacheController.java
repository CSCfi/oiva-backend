package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.security.annotations.OivaAccess_Yllapitaja;
import fi.minedu.oiva.backend.service.CacheService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.util.ControllerUtil.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + CacheController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
@ApiIgnore
public class CacheController {

    public static final String path = "/cache";

    @Autowired
    private CacheService cacheService;

    @OivaAccess_Yllapitaja
    @RequestMapping(method = GET)
    @ApiOperation(notes = "Palauttaa välimuistissa olevat avaimet", value = "/")
    public CompletableFuture<Collection<String>> getCacheNames() {
        return async(cacheService::getCacheNames);
    }

    @OivaAccess_Yllapitaja
    @RequestMapping(value = "/refresh", method = PUT)
    @ApiOperation(notes = "Tyhjentää ja alustaa välimuistin", value = "/refresh")
    public ResponseEntity refresh() {
        async(() -> cacheService.refreshCache(true));
        return ok();
    }

    @OivaAccess_Yllapitaja
    @RequestMapping(value="/flushall", method = PUT)
    @ApiOperation(notes = "Tyhjentää välimuistin", value = "/flushall")
    public ResponseEntity flush() {
        async(() -> cacheService.flushCache(false));
        return ok();
    }
}
