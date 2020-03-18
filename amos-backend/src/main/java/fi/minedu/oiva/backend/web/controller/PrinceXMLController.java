package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.*;
import fi.minedu.oiva.backend.entity.oiva.Lupa;
import fi.minedu.oiva.backend.entity.oiva.Muutos;
import fi.minedu.oiva.backend.entity.oiva.Muutospyynto;
import fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Esittelija;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.*;
import fi.minedu.oiva.backend.util.With;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

import static fi.minedu.oiva.backend.entity.OivaTemplates.RenderOptions;
import static fi.minedu.oiva.backend.util.ControllerUtil.get500;
import static fi.minedu.oiva.backend.util.ControllerUtil.notFound;
import static fi.minedu.oiva.backend.util.ControllerUtil.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@RequestMapping(value = "${api.url.prefix}" + PrinceXMLController.path)
@Api(description = "Tietojen esittäminen PDF-muodossa")
public class PrinceXMLController {

    private static final Logger logger = LoggerFactory.getLogger(PrinceXMLController.class);

    public static final String APPLICATION_PDF = "application/pdf";

    public static final String path = "/pdf";

    @Value("${api.url.prefix}")
    private String apiPrefix;

    @Autowired
    private PebbleService pebbleService;

    @Autowired
    private PrinceXMLService princeXMLService;

    @Autowired
    private LupaService lupaService;

    @Autowired
    private LupaRenderService lupaRenderService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private OpintopolkuService opintopolkuService;

    @Autowired
    private LupahistoriaService lupahistoriaService;

    @OivaAccess_Public
    @RequestMapping(value = "/{uuid}", method = GET)
    @ApiOperation(notes = "Tarjoaa luvan PDF-muodossa", value = "")
    public ResponseEntity<Resource> providePdf(final @PathVariable String uuid) {
        try {
            final Optional<Lupa> lupaOpt = lupaService.getByUuid(uuid, With.all);
            if(lupaOpt.isPresent()) {
                final Path lupaPath = Paths.get(fileStorageService.getLupaPDFFilePath(lupaOpt).orElseThrow(IllegalArgumentException::new));
                if(Files.exists(lupaPath)) {
                    final ByteArrayResource lupaBar = new ByteArrayResource(Files.readAllBytes(lupaPath));
                    return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header("Content-Disposition", "inline; filename=" + lupaOpt.get().getPDFFileName())
                        .contentLength(lupaBar.contentLength())
                        .body(lupaBar);
                } else {
                    logger.error("No such Lupa PDF with uuid " + uuid);
                    return ResponseEntity.notFound().build();
                }
            } else {
                logger.error("No such Lupa with uuid " + uuid);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Failed to provide Lupa PDF with uuid {}", uuid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @OivaAccess_Public
    @RequestMapping(value = "/history/{uuid}", method = GET)
    @ApiOperation(notes = "Tarjoaa ei-voimassa olevan luvan PDF-muodossa ohjaamalla pyynnön oikeaan urliin", value = "")
    public ResponseEntity<?> provideHistoryPdf(final @PathVariable String uuid) {
        return lupahistoriaService.getByUuid(uuid)
                .map(historia -> {
                    HttpHeaders headers = new HttpHeaders();
                    if (historia.getLupaId() != null) {
                        Optional<Lupa> lupa = lupaService.getById(historia.getLupaId());
                        if (!lupa.isPresent()) {
                            logger.error("No lupa found (id={}) by lupa history item (uuid={})", historia.getLupaId(), uuid);
                            return ResponseEntity.notFound().build();
                        }

                        headers.add("Location",
                                apiPrefix + PrinceXMLController.path + "/" + lupa.get().getUUIDValue());
                    } else {
                        headers.add("Location",
                                apiPrefix + PebbleController.path + "/resources/liitteet/lupahistoria/" + historia.getFilename());
                    }
                    return new ResponseEntity<>(headers, HttpStatus.FOUND);
                })
                .orElseGet(() -> {
                    logger.error("No lupa history with uuid {}", uuid);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @OivaAccess_Esittelija
    @RequestMapping(value = "/esikatsele/{uuid}", method = GET)
    @Produces({ PrinceXMLController.APPLICATION_PDF })
    @ResponseBody
    @ApiOperation(notes = "Tuottaa luvan PDF-muodossa", value = "")
    public void previewPdf(final @PathVariable String uuid, final HttpServletResponse response, final HttpServletRequest request) {
        try {
            final Optional<Lupa> lupaOpt = lupaService.getByUuid(uuid, With.all);
            if(lupaOpt.isPresent()) {
                final RenderOptions renderOptions = lupaRenderService.getLupaRenderOptions(lupaOpt).orElseThrow(IllegalStateException::new);
                final String lupaHtml = pebbleService.toHTML(lupaOpt, renderOptions).orElseThrow(IllegalStateException::new);
                response.setContentType(APPLICATION_PDF);
                response.setHeader("Content-Disposition", "inline; filename=" + lupaOpt.get().getPDFFileName());
                if (!princeXMLService.toPDF(lupaHtml, response.getOutputStream(), renderOptions)) {
                    response.setStatus(get500().getStatusCode().value());
                    response.getWriter().write("Failed to generate Lupa with uuid " + uuid);
                }
            } else {
                response.setStatus(notFound().getStatusCode().value());
                response.getWriter().write("No such Lupa with uuid " + uuid);
            }
        } catch (Exception e) {
            logger.error("Failed to generate Lupa PDF with uuid {}", uuid, e);
            response.setStatus(get500().getStatusCode().value());
        }
    }

    @OivaAccess_Esittelija
    @RequestMapping(value = "/tallenna/{uuid}", method = PUT)
    @ApiOperation(notes = "Tuottaa ja tallentaa luvan PDF-muodossa", value = "")
    public ResponseEntity savePDF(final @PathVariable String uuid) {
        try {
            final Optional<Lupa> lupaOpt = lupaService.getByUuid(uuid, With.all);
            if(lupaOpt.isPresent()) {
                final Optional<File> writtenFile = fileStorageService.writeLupaPDF(lupaOpt);
                if(writtenFile.isPresent()) {
                    return ok();
                } else {
                    logger.error("Failed to generate Lupa with uuid " + uuid);
                    return get500();
                }
            } else {
                logger.error("No such Lupa with uuid " + uuid);
                return notFound();
            }
        } catch (Exception e) {
            logger.error("Failed to generate Lupa PDF with uuid {}", uuid, e);
            return get500();
        }
    }

    @OivaAccess_Public
    @RequestMapping(value = "/muutospyyntoObjToPdf", method = PUT)
    @ResponseBody
    @ApiOperation(notes = "Tuottaa muutospyynnön PDF-muodossa", value = "")
    public void RenderMuutospyyntoPdf(@RequestBody Muutospyynto muutospyynto, final HttpServletResponse response, final HttpServletRequest request) {

        muutospyynto.setJarjestaja(organisaatioService.getWithLocation(muutospyynto.getJarjestajaOid()).get());

        System.out.println("meta: " + muutospyynto.getMeta());

        final Function<Muutos, Optional<KoodistoKoodi>> getKoodi = muutos ->
            Optional.ofNullable(opintopolkuService.getKoodi(muutos.getKoodisto(), muutos.getKoodiarvo(), null));

        muutospyynto.getMuutokset().stream().forEach(muutos -> {

            // jos lisätään tutkintokieliä: (TODO: UUID)
            if(null != muutos.getParentId()) {

                KoodistoKoodi koodi = opintopolkuService.getKoodi("koulutus", muutos.getParentId().toString(),null);
                if(null!=koodi) {
                    muutos.setKoodi(koodi);
                    System.out.println("kielikoodin tutkinto" + koodi.getNimi().toJson().asText());
                }
            }

            getKoodi.apply(muutos).ifPresent(koodi -> {
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
