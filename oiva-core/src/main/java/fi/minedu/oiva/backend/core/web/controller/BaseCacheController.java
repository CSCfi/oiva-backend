package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Yllapitaja;
import fi.minedu.oiva.backend.core.service.CacheService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.core.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

public abstract class BaseCacheController {

    public static final String path = "/cache";

    @Autowired
    private CacheService cacheService;

    @OivaAccess_Yllapitaja
    @RequestMapping(method = GET)
    @ApiOperation(notes = "Palauttaa välimuistissa olevat avaimet", value = "", authorizations = @Authorization(value = "CAS"))
    public CompletableFuture<Collection<String>> getCacheNames() {
        return async(cacheService::getCacheNames);
    }

    @OivaAccess_Yllapitaja
    @RequestMapping(value = "/refresh", method = PUT)
    @ApiOperation(notes = "Tyhjentää ja alustaa koko välimuistin", value = "", authorizations = @Authorization(value = "CAS"))
    public ResponseEntity refresh() {
        async(() -> cacheService.refreshCache(true));
        return ok();
    }
}
