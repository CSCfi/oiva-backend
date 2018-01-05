package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.entity.opintopolku.Kunta;
import fi.minedu.oiva.backend.entity.opintopolku.Maakunta;
import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.LupaService;
import fi.minedu.oiva.backend.util.RequestUtils;
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
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.util.ControllerUtil.getOr404;
import static fi.minedu.oiva.backend.util.ControllerUtil.options;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + LupaController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
public class LupaController {

    public static final String path = "/luvat";

    @Autowired
    private LupaService service;

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
        return async(() -> service.getAll(options(Organisaatio.class, Kunta.class, Maakunta.class)));
    }

    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/{lupaId:[0-9]+}")
    @ApiOperation(notes = "Palauttaa luvan tietokantatunnuksen perusteella", value = "")
    public CompletableFuture<HttpEntity<Lupa>> getById(final @PathVariable long lupaId, final @RequestParam(value = "with", required = false) String with) {
        return getOr404(async(() -> service.get(lupaId, options(with))));
    }

    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/{diaarinumero}/**")
    @ApiOperation(notes = "Palauttaa luvan diaarinumeron perusteella", value = "")
    public CompletableFuture<HttpEntity<Lupa>> getByDiaarinumero(final @PathVariable String diaarinumero,
        final HttpServletResponse response, final HttpServletRequest request,
        final @RequestParam(value = "with", required = false) String with) {
        return getOr404(async(() -> service.get(RequestUtils.getPathVariable(request, diaarinumero), options(with))));
    }
}
