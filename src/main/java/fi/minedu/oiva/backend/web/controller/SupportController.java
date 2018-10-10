package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.security.annotations.OivaAccess_Esittelija;
import fi.minedu.oiva.backend.service.FileStorageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static fi.minedu.oiva.backend.util.ControllerUtil.notFound;
import static fi.minedu.oiva.backend.util.ControllerUtil.ok;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@RequestMapping(value = "${api.url.prefix}" + SupportController.path)
public class SupportController {

    public static final String path = "/tuki";

    @Value("${support.api.enabled}")
    private boolean supportApiEnabled;

    @Autowired
    private FileStorageService fileStorageService;

    @OivaAccess_Esittelija
    @RequestMapping(value = "/tallenna/luvat", method = PUT)
    @ApiOperation(notes = "Tuottaa ja tallentaa kaikki luvat PDF-muodossa", value = "", hidden = true)
    public ResponseEntity saveAllPDFs(final @RequestParam(value = "operation", required = false) String operation) {
        return supportApiEnabled ? ok(fileStorageService.writeAllLupaPDFs(operation)) : notFound();
    }
}
