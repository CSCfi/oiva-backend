package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.service.LupaService;
import fi.minedu.oiva.backend.service.PebbleService;
import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

import static fi.minedu.oiva.backend.entity.OivaTemplates.*;
import static fi.minedu.oiva.backend.service.LupaService.*;
import static fi.minedu.oiva.backend.util.ControllerUtil.get500;
import static fi.minedu.oiva.backend.util.ControllerUtil.getOr404;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(value = "${api.url.prefix}" + PebbleController.path)
public class PebbleController {

    private static final Logger logger = LoggerFactory.getLogger(PebbleController.class);

    public static final String path = "/pebble";

    @Value("${api.url.prefix}" + PebbleController.path + "/")
    private String fullPath;

    @Autowired
    private PebbleService service;

    @Autowired
    private LupaService lupaService;

    @RequestMapping(value = "/**", method = GET, produces = { javax.ws.rs.core.MediaType.TEXT_HTML })
    public HttpEntity<String> renderHTML(final HttpServletRequest request,
        @RequestParam(value = "debug", required = false) final String debugMode) {
        final String diaarinumero =  RequestUtils.getPathVariable(request, fullPath);
        try {
            final Lupa lupa = lupaService.get(diaarinumero, withAll).get();
            final RenderOptions options = RenderOptions.webOptions(lupaService.renderLanguageFor(lupa));
            options.setDebugMode(null != debugMode);
            return getOr404(service.toHTML(lupa, options));

        } catch (Exception e) {
            logger.error("Failed to toHTML html from source with diaarinro {}: {}", diaarinumero, e);
            return get500();
        }
    }

    @RequestMapping(value = "/resources/**", method = GET)
    public ResponseEntity<Resource> resource(final HttpServletRequest request) {
        final String resourcePath =  RequestUtils.getPathVariable(request, path + "/resources/");
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
