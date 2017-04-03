package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class CacheController {

    public static final String path = "/cache";

    @Autowired
    private CacheService cacheService;

    @RequestMapping(method = GET)
    public CompletableFuture<Collection<String>> getCacheNames() {
        return cacheService.getCacheNames();
    }

    @RequestMapping(value = "/refresh", method = PUT)
    @PreAuthorize("hasAuthority('APP_KOUTE_YLLAPITAJA')")
    public ResponseEntity refresh() {
        async(() -> cacheService.refreshCache(true));
        return ok();
    }

    @RequestMapping(value="/flushall", method = PUT)
    @PreAuthorize("hasAuthority('APP_KOUTE_YLLAPITAJA')")
    public ResponseEntity flush() {
        async(() -> cacheService.flushCache(false));
        return ok();
    }
}
