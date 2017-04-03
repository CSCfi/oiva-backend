package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.dto.Henkilo;
import fi.minedu.oiva.backend.service.HenkiloService;
import fi.minedu.oiva.backend.service.OpintopolkuService;
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

    @Autowired
    public OpintopolkuService opService;

    @ApiOperation(notes = "Palauttaa henkilön oidin perusteella.", value = "")
    @RequestMapping(method = GET, value = "/{oid:.+}")
    public CompletableFuture<HttpEntity<Henkilo>> get(@PathVariable String oid) {
        return getOr404(service.getByOidWithOrganizationAsync(oid));
    }

    @RequestMapping(method = GET)
    public CompletableFuture<Collection<Henkilo>> getMany(@RequestParam String[] oids) {
        return service.getHenkilosByOidsAsync(oids);
    }
}
