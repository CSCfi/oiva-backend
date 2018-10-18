package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.oiva.Lupa;
import fi.minedu.oiva.backend.entity.export.KoulutusLupa;
import fi.minedu.oiva.backend.entity.export.Koulutustarjonta;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_BasicAuth;
import fi.minedu.oiva.backend.service.ExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.util.ControllerUtil.getOr404;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + ExportController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
@Api(description = "Tietojen ulosvienti")
public class ExportController {

    public static final String path = "/export";

    @Autowired
    private ExportService service;

    @OivaAccess_BasicAuth
    @RequestMapping(method = GET, value = "/jarjestysluvat")
    @ApiOperation(notes = "Palauttaa kaikki uusimmat jarjestysluvat", value = "", authorizations = @Authorization(value = "BasicAuth"))
    public CompletableFuture<Collection<Lupa>> getJarjestysluvat() {
        return async(service::getJarjestysluvat);
    }

    @OivaAccess_BasicAuth
    @RequestMapping(method = GET, value = "/koulutusluvat")
    @ApiOperation(notes = "Palauttaa kaikkien lupien alkupäivämäärä, loppupäivämäärä-, koulutusjärjestäjä- ja koulutustiedot", value = "", authorizations = @Authorization(value = "BasicAuth"))
    public CompletableFuture<Collection<KoulutusLupa>> getKoulutusLuvat() {
        return async(service::getKoulutusLuvat);
    }

    @OivaAccess_BasicAuth
    @RequestMapping(method = GET, value = "/koulutustarjonta")
    @ApiOperation(notes = "Palauttaa julkisten lupien koulutustarjonta tiedot", value = "", authorizations = @Authorization(value = "BasicAuth"))
    public CompletableFuture<Collection<Koulutustarjonta>> getKoulutustarjonta() {
        return async(service::getKoulutustarjonta);
    }
}
