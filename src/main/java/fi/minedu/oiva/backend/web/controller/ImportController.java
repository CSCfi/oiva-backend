package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_BasicAuth;
import fi.minedu.oiva.backend.service.ImportService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fi.minedu.oiva.backend.util.ControllerUtil.badRequest;
import static fi.minedu.oiva.backend.util.ControllerUtil.ok;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + ImportController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
public class ImportController {

    public static final String path = "/import";

    @Autowired
    private ImportService service;

    @OivaAccess_BasicAuth
    @RequestMapping(method = PUT, value = "/lupa")
    @ApiOperation(notes = "Tallentaa syötetyn luvan ja sen määräykset", value = "", authorizations = @Authorization(value = "BasicAuth"))
    public HttpEntity<List<String>> importLupa(final @RequestBody Lupa lupa) {
        if(null == lupa) {
            return badRequest();
        }
        return ok(service.importEntity(lupa));
    }
}
