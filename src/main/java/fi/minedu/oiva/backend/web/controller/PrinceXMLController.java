package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.service.LupaService;
import fi.minedu.oiva.backend.service.PebbleService;
import fi.minedu.oiva.backend.service.PrinceXMLService;
import fi.minedu.oiva.backend.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static fi.minedu.oiva.backend.entity.OivaTemplates.*;
import static fi.minedu.oiva.backend.entity.OivaTemplates.RenderLanguage;
import static fi.minedu.oiva.backend.service.LupaService.*;
import static fi.minedu.oiva.backend.util.ControllerUtil.get500;
import static fi.minedu.oiva.backend.util.ControllerUtil.notFound;

@Controller
@RequestMapping(
    value = "${api.url.prefix}" + PrinceXMLController.path,
    produces = { PrinceXMLController.APPLICATION_PDF })
public class PrinceXMLController {

    private static final Logger logger = LoggerFactory.getLogger(PrinceXMLController.class);
    public static final String APPLICATION_PDF = "application/pdf";

    public static final String path = "/pdf";

    @Value("${api.url.prefix}" + PrinceXMLController.path + "/")
    private String fullPath;

    @Autowired
    private PebbleService pebbleService;

    @Autowired
    private PrinceXMLService princeXMLService;

    @Autowired
    private LupaService lupaService;

    @ResponseBody
    @RequestMapping(value = "/**")
    public void renderPDF(final HttpServletResponse response, final HttpServletRequest request,
        @RequestParam(value = "debug", required = false) final String debugMode) {
        final String diaarinumero =  RequestUtils.getPathVariable(request, fullPath);
        try {
            final Lupa lupa = lupaService.get(diaarinumero, withAll).get();
            final RenderOptions options = RenderOptions.pdfOptions(RenderLanguage.fi);
            options.setDebugMode(null != debugMode);
            final Optional<String> htmlOpt = pebbleService.toHTML(lupa, options);
            if(htmlOpt.isPresent()) {
                princeXMLService.generatePDF(htmlOpt.get(), response.getOutputStream());

            } else {
                response.setStatus(get500().getStatusCode().value());
            }

        } catch (Exception e) {
            logger.error("Error while trying to toHTML public Paatos with diaarinro {}", diaarinumero, e);
            response.setStatus(notFound().getStatusCode().value());
        }
    }
}
