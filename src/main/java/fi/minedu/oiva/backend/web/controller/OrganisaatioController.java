package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.OrganisaatioService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.util.ControllerUtil.getOr404;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + OrganisaatioController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
@Api(description = "Opintopolun organisaatiot")
public class OrganisaatioController {

    public static final String path = "/organisaatiot";

    @Autowired
    private OrganisaatioService service;

    @OivaAccess_Public
    @RequestMapping(value = "/{oid:.+}", method = GET)
    public CompletableFuture<HttpEntity<Organisaatio>> getWithLocation(final @PathVariable String oid) {
        return getOr404(async(() -> service.getWithLocation(oid)));
    }
}
