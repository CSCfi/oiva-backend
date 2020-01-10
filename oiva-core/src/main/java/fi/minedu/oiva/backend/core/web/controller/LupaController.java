package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_BasicAuth;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.opintopolku.Organisaatio;
import fi.minedu.oiva.backend.model.entity.oiva.Lupahistoria;
import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.core.service.LupaService;
import fi.minedu.oiva.backend.core.service.LupahistoriaService;
import fi.minedu.oiva.backend.core.util.RequestUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import static fi.minedu.oiva.backend.core.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.getOr404;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.options;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + LupaController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
@Api(description = "Lupien hallinta")
public class LupaController {

    @Value("${templates.base.path}")
    private String templateBasePath;

    public static final String path = "/luvat";

    @Autowired
    private LupaService service;

    @Autowired
    private LupahistoriaService lhservice;

    @OivaAccess_Public
    @RequestMapping(method = GET)
    @ApiOperation(notes = "Palauttaa kaikki luvat", value = "")
    public CompletableFuture<Collection<Lupa>> getAll() {
        return async(service::getAll);
    }

    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/jarjestajilla")
    @ApiOperation(notes = "Palauttaa kaikki luvat järjestäjän tiedoilla", value = "")
    public CompletableFuture<Collection<Lupa>> getAllWithJarjestaja() {
        return async(() -> service.getAllWithJarjestaja(options(Organisaatio.class)));
    }

    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/{diaarinumero}/**")
    @ApiOperation(notes = "Palauttaa luvan diaarinumeron perusteella", value = "")
    public CompletableFuture<HttpEntity<Lupa>> getByDiaarinumero(final @PathVariable String diaarinumero, final HttpServletRequest request,
        final @RequestParam(value = "with", required = false) String with) {
        return getOr404(async(() -> service.getByDiaarinumero(RequestUtils.getPathVariable(request, diaarinumero), options(with))));
    }

    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/jarjestaja/{ytunnus}")
    @ApiOperation(notes = "Palauttaa luvan järjestäjän ytunnuksen perusteella", value = "")
    public CompletableFuture<HttpEntity<Lupa>> getByYtunnus(final @PathVariable String ytunnus, final @RequestParam(value = "with", required = false) String with) {
        return getOr404(async(() -> service.getByYtunnus(ytunnus, options(with))));
    }

    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/historia/{oid}/**")
    @ApiOperation(notes = "Palauttaa lupahistorian koulutuksen järjestäjän oid:n perusteella", value = "")
    public CompletableFuture<Collection<Lupahistoria>> getLupahistoriaByOid(final @PathVariable String oid, final HttpServletRequest request) {
        return async(() -> lhservice.getHistoriaByOid(RequestUtils.getPathVariable(request, oid)));
    }

    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/historiaytunnuksella/{ytunnus}/**")
    @ApiOperation(notes = "Palauttaa lupahistorian koulutuksen järjestäjän ytunnuksen perusteella", value = "")
    public CompletableFuture<Collection<Lupahistoria>> getLupahistoriaByYtunnus(final @PathVariable String ytunnus, final HttpServletRequest request) {
        return async(() -> lhservice.getHistoriaByYtunnus(RequestUtils.getPathVariable(request, ytunnus)));
    }

    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/lupa/{uuid}")
    @ApiOperation(notes = "Palauttaa luvan uuid:n perusteella", value = "")
    public CompletableFuture<HttpEntity<Lupa>> getByUuid(final @PathVariable String uuid, final @RequestParam(value = "with", required = false) String with) {
        return getOr404(async(() -> service.getByUuid(uuid, options(with))));
    }

    @OivaAccess_BasicAuth
    @RequestMapping(method = GET, value = "/listaus.csv", produces = "text/csv; charset=utf-8")
    @ApiOperation(notes = "Palauttaa ajantasaisen listauksen järjestelmälupien tutkinnoista, osamisalarajoituksista ja tutkintokielistä", value = "")
    public CompletableFuture<String> getListaus() {
        return async(() -> service.getReport());
    }
}
