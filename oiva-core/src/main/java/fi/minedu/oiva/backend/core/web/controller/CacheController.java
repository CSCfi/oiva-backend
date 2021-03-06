package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Application;
import fi.minedu.oiva.backend.core.service.OivaCacheService;
import fi.minedu.oiva.backend.core.util.RequestUtils;
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

import static fi.minedu.oiva.backend.core.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.ok;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + CacheController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
@Api(description = "Välimuistin hallinta")
public class CacheController extends BaseCacheController {

    @Autowired
    private OivaCacheService cacheService;

    @OivaAccess_Application
    @RequestMapping(value="/refresh/lupa/{uuid}", method = PUT)
    @ApiOperation(notes = "Uudistaa lupaan liittyvän välimuistin", value = "", authorizations = @Authorization(value = "CAS"))
    public ResponseEntity refreshLupa(final @PathVariable String uuid) {
        async(() -> cacheService.refreshLupa(uuid));
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
