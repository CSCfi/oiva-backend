package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.security.annotations.OivaAccess_Application;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Yllapitaja;
import fi.minedu.oiva.backend.service.CacheService;
import fi.minedu.oiva.backend.util.RequestUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
@Api(description = "Välimuistin hallinta")
public class CacheController {

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

    @OivaAccess_Application
    @RequestMapping(value="/refresh/lupa/{diaarinumero}/**", method = PUT)
    @ApiOperation(notes = "Uudistaa lupaan liittyvän välimuistin", value = "", authorizations = @Authorization(value = "CAS"))
    public ResponseEntity refreshLupa(final @PathVariable String diaarinumero, final HttpServletRequest request) {
        async(() -> cacheService.refreshLupa(RequestUtils.getPathVariable(request, diaarinumero)));
        return ok();
    }

    @OivaAccess_Application
    @RequestMapping(value="/refresh/koulutus", method = PUT)
    @ApiOperation(notes = "Uudistaa koulutuskoodistoon liittyvän välimuistin", value = "", authorizations = @Authorization(value = "CAS"))
    public ResponseEntity refreshKoulutus() {
        async(() -> cacheService.refreshKoulutus(true));
        return ok();
    }
}
