package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_BasicAuth;
import fi.minedu.oiva.backend.core.service.ImportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fi.minedu.oiva.backend.model.util.ControllerUtil.badRequest;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.ok;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + ImportController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
@Api(description = "Tietojen sisääntuonti")
public class ImportController extends BaseImportController {

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
