package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.service.DefaultPebbleService;
import fi.minedu.oiva.backend.model.entity.OivaTemplates;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.oiva.Muutospyynto;
import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Esittelija;
import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.core.service.FileStorageService;
import fi.minedu.oiva.backend.core.service.LupaRenderService;
import fi.minedu.oiva.backend.core.service.LupaService;
import fi.minedu.oiva.backend.core.service.MuutospyyntoService;
import fi.minedu.oiva.backend.core.service.BasePebbleService;
import fi.minedu.oiva.backend.core.service.PrinceXMLService;
import fi.minedu.oiva.backend.core.util.RequestUtils;
import fi.minedu.oiva.backend.core.util.With;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import static fi.minedu.oiva.backend.model.entity.OivaTemplates.RenderOptions;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.get500;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.notFound;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@RequestMapping(value = "${api.url.prefix}" + PrinceXMLController.path)
@Api(description = "Tietojen esittäminen PDF-muodossa")
public class PrinceXMLController {

    private static final Logger logger = LoggerFactory.getLogger(PrinceXMLController.class);

    private static final String APPLICATION_PDF = "application/pdf";

    public static final String path = "/pdf";

    private final DefaultPebbleService pebbleService;

    private final PrinceXMLService princeXMLService;

    private final LupaService lupaService;

    private final LupaRenderService lupaRenderService;

    private final FileStorageService fileStorageService;

    private final MuutospyyntoService muutospyyntoService;

    @Autowired
    public PrinceXMLController(DefaultPebbleService pebbleService, PrinceXMLService princeXMLService,
                               LupaService lupaService, LupaRenderService lupaRenderService,
                               FileStorageService fileStorageService, MuutospyyntoService muutospyyntoService) {
        this.pebbleService = pebbleService;
        this.princeXMLService = princeXMLService;
        this.lupaService = lupaService;
        this.lupaRenderService = lupaRenderService;
        this.fileStorageService = fileStorageService;
        this.muutospyyntoService = muutospyyntoService;
    }

    @OivaAccess_Public
    @RequestMapping(value = "/{uuid}", method = GET)
    @ApiOperation(notes = "Tarjoaa luvan PDF-muodossa", value = "")
    public ResponseEntity<Resource> providePdf(final @PathVariable String uuid) {
        try {
            final Optional<Lupa> lupaOpt = lupaService.getByUuid(uuid, With.all);
            if (lupaOpt.isPresent()) {
                final Path lupaPath = Paths.get(fileStorageService.getLupaPDFFilePath(lupaOpt.get()).orElseThrow(IllegalArgumentException::new));
                if (Files.exists(lupaPath)) {
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
                final String lupaHtml = pebbleService.toHTML(lupaOpt.get(), renderOptions).orElseThrow(IllegalStateException::new);
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
                final Optional<File> writtenFile = fileStorageService.writeLupaPDF(lupaOpt.get());
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
    @RequestMapping(value = "/esikatsele/muutospyynto/{uuid}", method = GET)
    @ResponseBody
    @ApiOperation(notes = "Tuottaa muutospyynnön PDF-muodossa", value = "")
    public void previewMuutospyynto(@PathVariable String uuid, HttpServletResponse response) {
        try {
            final Optional<Muutospyynto> muutospyyntoOpt = muutospyyntoService.getByUuid(uuid);
            if (!muutospyyntoOpt.isPresent()) {
                response.setStatus(notFound().getStatusCode().value());
                response.getWriter().write("No such muutospyynto with uuid " + uuid);
                return;
            }

            final RenderOptions options = RenderOptions.pdfOptions(OivaTemplates.RenderLanguage.fi);
            final Muutospyynto muutospyynto = muutospyyntoOpt.get();
            // Set temporal date to hakupvm field for pebble template header.
            muutospyynto.setHakupvm(Date.valueOf(LocalDate.now()));
            final Optional<String> muutospyyntoHtml = pebbleService.muutospyyntoToHTML(muutospyynto, options);
            if (muutospyyntoHtml.isPresent()) {
                response.setContentType(APPLICATION_PDF);
                response.setHeader("Content-Disposition", "inline; filename=" + muutospyynto.getPDFFileName());
                if (!princeXMLService.toPDF(muutospyyntoHtml.get(), response.getOutputStream(), options)) {
                    response.setStatus(get500().getStatusCode().value());
                    response.getWriter().write("Failed to generate Muutospyynto with html " + muutospyyntoHtml.get());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to generate muutospyynto PDF with uuid {}", uuid, e);
            response.setStatus(get500().getStatusCode().value());
        }
    }

}
