package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.LupaService;
import fi.minedu.oiva.backend.service.PebbleService;
import fi.minedu.oiva.backend.service.PrinceXMLService;
import fi.minedu.oiva.backend.util.RequestUtils;
import fi.minedu.oiva.backend.util.With;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static fi.minedu.oiva.backend.entity.OivaTemplates.*;
import static fi.minedu.oiva.backend.util.ControllerUtil.get500;
import static fi.minedu.oiva.backend.util.ControllerUtil.notFound;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(
    value = "${api.url.prefix}" + PrinceXMLController.path,
    produces = { PrinceXMLController.APPLICATION_PDF })
public class PrinceXMLController {

    private static final Logger logger = LoggerFactory.getLogger(PrinceXMLController.class);

    public static final String APPLICATION_PDF = "application/pdf";

    public static final String path = "/pdf";

    @Autowired
    private PebbleService pebbleService;

    @Autowired
    private PrinceXMLService princeXMLService;

    @Autowired
    private LupaService lupaService;

    @OivaAccess_Public
    @RequestMapping(value = "/{diaarinumero}/**", method = GET)
    @ResponseBody
    @ApiOperation(notes = "Tuottaa luvan PDF-muodossa", value = "")
    public void renderPDF(final @PathVariable String diaarinumero,
        final HttpServletResponse response, final HttpServletRequest request) {

        final String diaariNumero =  RequestUtils.getPathVariable(request, diaarinumero);
        try {
            final Optional<Lupa> lupaOpt = lupaService.getByDiaarinumero(diaariNumero, With.all);
            if(lupaOpt.isPresent()) {
                final Lupa lupa = lupaOpt.get();
                final RenderOptions options = RenderOptions.pdfOptions(lupaService.renderLanguageFor(lupa));
                final Optional<String> htmlOpt = pebbleService.toHTML(lupa, options);
                if (htmlOpt.isPresent()) {

                    lupaService.getAttachments(lupa.getId()).stream().forEach(attachment -> {
                        if(AttachmentType.convert(attachment.getTyyppi()) != null) {
                            options.addAttachment(AttachmentType.convert(attachment.getTyyppi()), attachment.getPolku());
                        }

                    });

                    // TODO: lisää pdf kantaan tarvittaville luville
                    if (lupaService.hasTutkintoNimenMuutos(lupa)) options.addAttachment(AttachmentType.tutkintoNimenMuutos, "LIITE-tutkintojen_nimien_muutokset.pdf");

                    response.setContentType(APPLICATION_PDF);
                    response.setHeader("Content-Disposition", "inline; filename=lupa-" + StringUtils.replaceAll(diaariNumero, "/", "-") + ".pdf");
                    if (!princeXMLService.toPDF(htmlOpt.get(), response.getOutputStream(), options)) {
                        response.setStatus(get500().getStatusCode().value());
                        response.getWriter().write("Failed to generate Lupa with diaarinumero " + diaariNumero);
                    }
                }
            } else {
                response.setStatus(notFound().getStatusCode().value());
                response.getWriter().write("No such Lupa with diaarinumero " + diaariNumero);
            }
        } catch (Exception e) {
            logger.error("Failed to generate Lupa PDF with diaarinumero {}", diaariNumero, e);
            response.setStatus(get500().getStatusCode().value());
        }
    }
}
