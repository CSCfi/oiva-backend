package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.service.LupaService;
import fi.minedu.oiva.backend.util.RequestUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + LupaController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
public class LupaController {

    private static final Logger logger = LoggerFactory.getLogger(LupaController.class);

    public static final String path = "/luvat";

    @Value("${api.url.prefix}" + LupaController.path + "/")
    private String fullPath;

    @Autowired
    private LupaService service;

    @ApiOperation(notes = "Palauttaa kaikki luvat", value = "")
    @RequestMapping(method = GET)
    public CompletableFuture<Collection<Lupa>> getAll() {
        return async(() -> service.getAll());
    }

    @ApiOperation(notes = "Palauttaa luvan tietokantatunnuksen perusteella", value = "")
    @RequestMapping(method = GET, value = "/{lupaId:[0-9]+}")
    public HttpEntity<Lupa> getById(@PathVariable long lupaId,
        @RequestParam(value = "with", required = false) String with) {
        return getOr404(service.get(lupaId, StringUtils.split(with, ",")));
    }

    @ApiOperation(notes = "Palauttaa luvan diaarinumeron perusteella", value = "")
    @RequestMapping(method = GET, value = "/**")
    public HttpEntity<Lupa> getByDiaarinumero(final HttpServletResponse response, final HttpServletRequest request,
        @RequestParam(value = "with", required = false) String with) {
        return getOr404(service.get(RequestUtils.getPathVariable(request, fullPath), with));
    }
}
