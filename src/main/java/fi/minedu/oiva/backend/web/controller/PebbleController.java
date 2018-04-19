package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.LupaService;
import fi.minedu.oiva.backend.service.PebbleService;
import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.util.RequestUtils;
import fi.minedu.oiva.backend.util.With;
import io.swagger.annotations.ApiOperation;
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

import java.util.Optional;

import static fi.minedu.oiva.backend.entity.OivaTemplates.*;
import static fi.minedu.oiva.backend.util.ControllerUtil.get500;
import static fi.minedu.oiva.backend.util.ControllerUtil.getOr404;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(value = "${api.url.prefix}" + PebbleController.path)
public class PebbleController {

    private static final Logger logger = LoggerFactory.getLogger(PebbleController.class);

    public static final String path = "/pebble";

    @Autowired
    private PebbleService service;

    @Autowired
    private LupaService lupaService;

    @OivaAccess_Public
    @RequestMapping(value = "/{diaarinumero}/**", method = GET, produces = { javax.ws.rs.core.MediaType.TEXT_HTML })
    @ApiOperation(notes = "Tuottaa luvan HTML-muodossa", value = "")
    public HttpEntity<String> renderHTML(final @PathVariable String diaarinumero, final HttpServletRequest request) {

        final String diaariNumero =  RequestUtils.getPathVariable(request, diaarinumero);
        try {
            final Lupa lupa = lupaService.getByDiaarinumero(diaariNumero, With.all).get();
            final RenderOptions options = RenderOptions.webOptions(lupaService.renderLanguageFor(lupa));
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
