package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.core.service.BasePebbleService;
import fi.minedu.oiva.backend.core.service.LupaService;
import fi.minedu.oiva.backend.core.util.RequestUtils;
import fi.minedu.oiva.backend.core.util.With;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import java.util.Optional;

import static fi.minedu.oiva.backend.model.entity.OivaTemplates.RenderOptions;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.get500;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.getOr404;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Api(description = "Tietojen esittäminen HTML-muodossa")
public abstract class BasePebbleController<T extends BasePebbleService> {

    protected static final Logger logger = LoggerFactory.getLogger(BasePebbleController.class);

    public static final String path = "/pebble";

    protected final T service;

    protected final LupaService lupaService;

    public BasePebbleController(T service, LupaService lupaService) {
        this.service = service;
        this.lupaService = lupaService;
    }

    @OivaAccess_Public
    @RequestMapping(value = "/{diaarinumero}/**", method = GET, produces = {javax.ws.rs.core.MediaType.TEXT_HTML})
    @ApiOperation(notes = "Tuottaa luvan HTML-muodossa", value = "")
    public HttpEntity<String> renderHTML(final @PathVariable String diaarinumero, final HttpServletRequest request, final @QueryParam("mode") String mode) {

        final String diaariNumero = RequestUtils.getPathVariable(request, diaarinumero);
        try {
            return lupaService.getByDiaarinumero(diaariNumero, With.all).map(lupa -> {
                final RenderOptions options = RenderOptions.webOptions(lupaService.renderLanguageFor(lupa));
                options.setDebugMode(StringUtils.equals(mode, "debug"));
                return getOr404(service.toHTML(lupa, options));
            }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
        if (resourceOpt.isPresent()) {
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
