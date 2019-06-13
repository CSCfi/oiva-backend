package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.core.service.HealthService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.getHealthCheckStatus;

public abstract class BaseHealthController {

    private static final Logger logger = LoggerFactory.getLogger(BaseHealthController.class);

    public static final String path = "/health";

    @Autowired
    private HealthService service;

    @OivaAccess_Public
    @RequestMapping(method = GET)
    @ApiOperation(notes = "Testaa sovelluksen toimivuustilaa", value = "")
    public HttpEntity<String> check() throws Exception {
        try {
            service.healthCheck();
            logger.debug("Health-check: OK");
            return getHealthCheckStatus(HttpStatus.OK, "OK");

        } catch(Exception e) {
            logger.error("Health-check: " + e.getMessage());
            return getHealthCheckStatus(HttpStatus.INTERNAL_SERVER_ERROR, "Failed");
        }
    }
}
