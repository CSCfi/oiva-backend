package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.dto.Henkilo;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Application;
import fi.minedu.oiva.backend.service.HenkiloService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.util.ControllerUtil.getOr404;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + HenkiloController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
public class HenkiloController {

    public static final String path = "/henkilot";

    @Autowired
    private HenkiloService service;

    @OivaAccess_Application
    @RequestMapping(method = GET, value = "/{oid:.+}")
    @ApiOperation(notes = "Palauttaa henkilön oidin perusteella.", value = "")
    public CompletableFuture<HttpEntity<Henkilo>> get(final @PathVariable String oid) {
        return getOr404(async(() -> service.getByOidWithOrganization(oid)));
    }

    @OivaAccess_Application
    @RequestMapping(method = GET)
    @ApiOperation(notes = "Palauttaa henkilöt oidien perusteella.", value = "")
    public CompletableFuture<Collection<Henkilo>> getMany(final @RequestParam String[] oids) {
        return async(() -> service.getHenkilosByOids(oids));
    }
}
