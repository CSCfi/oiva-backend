package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.core.service.HealthService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

import static fi.minedu.oiva.backend.model.util.ControllerUtil.getHealthCheckStatus;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

public abstract class BaseHealthController {

    public static final String path = "/health";

    @Autowired
    private HealthService service;

    @OivaAccess_Public
    @RequestMapping(method = GET)
    @ApiOperation(notes = "Testaa sovelluksen toimivuustilaa", value = "")
    public HttpEntity<Map<?, ?>> check() throws Exception {
        Map<String, Boolean> result = service.healthCheck();
        if (result.values().stream().allMatch(Boolean.TRUE::equals)) {
            return ok(result);
        }
        else {
            return getHealthCheckStatus(HttpStatus.INTERNAL_SERVER_ERROR, result);
        }
    }
}
