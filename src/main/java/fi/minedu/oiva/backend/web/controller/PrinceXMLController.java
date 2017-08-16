package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.service.LupaService;
import fi.minedu.oiva.backend.service.PebbleService;
import fi.minedu.oiva.backend.service.PrinceXMLService;
import fi.minedu.oiva.backend.util.RequestUtils;
import org.apache.commons.lang3.StringUtils;
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
            final Optional<Lupa> lupaOpt = lupaService.get(diaarinumero, withAll);
            if(lupaOpt.isPresent()) {
                final Lupa lupa = lupaOpt.get();
                final RenderOptions options = RenderOptions.pdfOptions(lupaService.renderLanguageFor(lupa));
                options.setDebugMode(null != debugMode);
                final Optional<String> htmlOpt = pebbleService.toHTML(lupa, options);
                if (htmlOpt.isPresent()) {
                    if (lupaService.hasTutkintoNimenMuutos(lupa)) options.addAttachment(Attachment.tutkintoNimenMuutos);
                    response.setContentType(APPLICATION_PDF);
                    response.setHeader("Content-Disposition", "inline; filename=lupa-" + StringUtils.replaceAll(diaarinumero, "/", "-") + ".pdf");
                    if (!princeXMLService.toPDF(htmlOpt.get(), response.getOutputStream(), options)) {
                        response.setStatus(get500().getStatusCode().value());
                        response.getWriter().write("Failed to generate Lupa with diaarinumero " + diaarinumero);
                    }
                }
            } else {
                response.setStatus(notFound().getStatusCode().value());
                response.getWriter().write("No such Lupa with diaarinumero " + diaarinumero);
            }
        } catch (Exception e) {
            logger.error("Failed to generate Lupa PDF with diaarinumero {}", diaarinumero, e);
            response.setStatus(get500().getStatusCode().value());
        }
    }
}
