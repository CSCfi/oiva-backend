package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.*;
import fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.*;
import fi.minedu.oiva.backend.util.RequestUtils;
import fi.minedu.oiva.backend.util.With;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static fi.minedu.oiva.backend.entity.OivaTemplates.*;
import static fi.minedu.oiva.backend.util.ControllerUtil.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

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

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private OpintopolkuService opintopolkuService;

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


    @OivaAccess_Public
    @RequestMapping(value = "/muutospyyntoObjToPdf", method = PUT)
    @ResponseBody
    @ApiOperation(notes = "Tuottaa luvan PDF-muodossa", value = "")
    public void RenderMuutospyyntoPdf(@RequestBody Muutospyynto muutospyynto,
                                  final HttpServletResponse response, final HttpServletRequest request) {

        muutospyynto.setJarjestaja(organisaatioService.getWithLocation(muutospyynto.getJarjestajaOid()).get());

        System.out.println("meta: " + muutospyynto.getMeta());

        muutospyynto.getMuutokset().stream().forEach(muutos -> {

            // jos lisätään tutkintokieliä: (TODO: UUID)
            if(null != muutos.getParentId()) {

                KoodistoKoodi koodi = opintopolkuService.getKoodi("koulutus", muutos.getParentId().toString(),null);
                if(null!=koodi) {
                    muutos.setKoodi(koodi);
                    System.out.println("kielikoodin tutkinto" + koodi.getNimi().toJson().asText());
                }
            }

            opintopolkuService.getKoodi(muutos).ifPresent(koodi -> {
                muutos.setKoodi(koodi);
                if (koodi.isKoodisto("koulutus")) {
                    final String koodiArvo = koodi.koodiArvo();
                    opintopolkuService.getKoulutustyyppiKoodiForKoulutus(koodiArvo).ifPresent(koulutustyyppiKoodit -> koulutustyyppiKoodit.stream().forEach(muutos::addYlaKoodi));
                    opintopolkuService.getKoulutusalaKoodiForKoulutus(koodiArvo).ifPresent(muutos::addYlaKoodi);
                }
            });

        });


        try {
            // TODO: kielivalinta koulutuksen järjestäjän mukaan
            final RenderOptions options = RenderOptions.pdfOptions(OivaTemplates.RenderLanguage.fi);
            final Optional<String> muutospyyntoHtml = pebbleService.muutospyyntoToHTML(muutospyynto, options);
            
            if (!princeXMLService.toPDF(muutospyyntoHtml.get(), response.getOutputStream(), options)) {
                response.setStatus(get500().getStatusCode().value());
                response.getWriter().write("Failed to generate Muutospyynto with html " + muutospyyntoHtml.get());
            }
        } catch (Exception e) {
            logger.error("Failed to generate Muutospyynto PDF with html {}", e);
            response.setStatus(get500().getStatusCode().value());
        }
    }

}
