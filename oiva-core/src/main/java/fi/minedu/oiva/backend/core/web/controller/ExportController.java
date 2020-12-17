package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_BasicAuth;
import fi.minedu.oiva.backend.core.service.ExportService;
import fi.minedu.oiva.backend.model.entity.export.KoulutusLupa;
import fi.minedu.oiva.backend.model.entity.export.Koulutustarjonta;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.core.util.AsyncUtil.async;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(
        value = "${api.url.prefix}" + ExportController.path,
        produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(description = "Tietojen ulosvienti")
public class ExportController extends BaseExportController {

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
    @ApiOperation(notes = "Palauttaa kaikkien ammatillistenlupien alkupäivämäärä, loppupäivämäärä-, koulutusjärjestäjä- ja koulutustiedot", value = "", authorizations = @Authorization(value = "BasicAuth"))
    public CompletableFuture<Collection<KoulutusLupa>> getKoulutusLuvat() {
        return async(service::getKoulutusLuvat);
    }

    @OivaAccess_BasicAuth
    @RequestMapping(method = GET, value = "/koulutustarjonta")
    @ApiOperation(notes = "Palauttaa julkisten ammatillistenlupien koulutustarjonta tiedot", value = "", authorizations = @Authorization(value = "BasicAuth"))
    public CompletableFuture<Collection<Koulutustarjonta>> getKoulutustarjonta() {
        return async(service::getKoulutustarjonta);
    }

    @OivaAccess_BasicAuth
    @RequestMapping(method = GET, value = "/kustannustiedot")
    @ApiOperation(notes = "Tarjoaa kustannustiedot kaikista luvista jotka ovat olleet / ovat voimassa annetulla aikavälillä.", value = "",
            authorizations = @Authorization(value = "BasicAuth"))
    public CompletableFuture<ResponseEntity<Collection<Lupa>>> getKustannustiedot(
            @ApiParam(value = "Koulutustyyppi (jos ei annettu haetaan ammatilliset koulutuksen kustannustiedot")
            @RequestParam(required = false) String koulutustyyppi,
            @ApiParam(value = "Aloituspäivämäärä ISO formaatissa. Esim. 2018-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @ApiParam(value = "Loppupäivämäärä ISO formaatissa. Esim. 2018-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            return async(() -> new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST));
        }
        return async(() -> new ResponseEntity<>(service.getKustannusTiedot(koulutustyyppi, startDate, endDate), HttpStatus.OK));
    }
}
