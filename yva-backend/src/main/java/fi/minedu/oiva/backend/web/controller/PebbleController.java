package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.KujaPebbleService;
import fi.minedu.oiva.backend.core.service.LupaService;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.core.util.RequestUtils;
import fi.minedu.oiva.backend.core.util.With;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jooq.tools.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fi.minedu.oiva.backend.model.entity.OivaTemplates.*;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.get500;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.getOr404;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(value = "${api.url.prefix}" + PebbleController.path)
@Api(description = "Tietojen esittäminen HTML-muodossa")
public class PebbleController {

    private static final Logger logger = LoggerFactory.getLogger(PebbleController.class);

    public static final String path = "/pebble";

    @Autowired
    private KujaPebbleService service;

    @Autowired
    private LupaService lupaService;

    // TODO: REMOVE ME
    @OivaAccess_Public
    @RequestMapping(value = "/lista", method = GET, produces = { javax.ws.rs.core.MediaType.TEXT_HTML })
    @ApiOperation(notes = "Tuottaa lupa listan HTML-muodossa", value = "")
    public HttpEntity<String> renderList() {
        try {
            final List<Lupa> luvat = lupaService.getAll().stream()
                .map(lupa -> lupaService.getByDiaarinumero(lupa.getDiaarinumero(), With.all))
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
            return getOr404(service.toListHTML(luvat));

        } catch (Exception e) {
            logger.error("Failed to toListHTML html", e);
            return get500();
        }
    }

    @OivaAccess_Public
    @RequestMapping(value = "/{diaarinumero}/**", method = GET, produces = { javax.ws.rs.core.MediaType.TEXT_HTML })
    @ApiOperation(notes = "Tuottaa luvan HTML-muodossa", value = "")
    public HttpEntity<String> renderHTML(final @PathVariable String diaarinumero, final HttpServletRequest request, final @QueryParam("mode") String mode) {

        final String diaariNumero =  RequestUtils.getPathVariable(request, diaarinumero);
        try {
            final Lupa lupa = lupaService.getByDiaarinumero(diaariNumero, With.all).get();
            final RenderOptions options = RenderOptions.webOptions(lupaService.renderLanguageFor(lupa));
            options.setDebugMode(StringUtils.equals(mode, "debug"));
            return getOr404(service.toHTML(lupa, options));

        } catch (Exception e) {
            logger.error("Failed to toHTML html from source with diaarinro {}: {}", diaariNumero, e);
            return get500();
        }
    }

    @OivaAccess_Public
    @RequestMapping(value = "/resources/{filename}/**", method = GET)
    @ApiOperation(notes = "Palauttaa pebble-resurssin", value = "")
    public ResponseEntity<Resource> resource(final @PathVariable String filename, final HttpServletRequest request) {
        final String resourcePath = RequestUtils.getPathVariable(request, filename);
        final Optional<ByteArrayResource> resourceOpt = service.getResource(resourcePath);
        if(resourceOpt.isPresent()) {
            final ByteArrayResource bar = resourceOpt.get();
            final HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(bar.contentLength())
                    .body(bar);
        } else {
            logger.error("Failed to provide pebble resource");
            return ResponseEntity.notFound().build();
        }
    }
}
