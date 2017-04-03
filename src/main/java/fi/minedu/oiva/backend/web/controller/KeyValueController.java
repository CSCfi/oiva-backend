package fi.minedu.oiva.backend.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import fi.minedu.oiva.backend.service.KeyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static fi.minedu.oiva.backend.util.ControllerUtil.created;
import static fi.minedu.oiva.backend.util.ControllerUtil.getOr404;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + KeyValueController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
public class KeyValueController {

    public static final String path = "/kv";

    @Autowired
    private KeyValueService keyValueService;

    @Autowired
    private ServletContext context;

    @RequestMapping("{key}")
    public HttpEntity<String> get(@PathVariable String key) {
        return getOr404(keyValueService.get(key));
    }

    @RequestMapping(value="{key}", method = RequestMethod.POST)
    public void post(@PathVariable String key, @RequestBody @Valid JsonNode value, HttpServletResponse response) {
        keyValueService.put(key, value.toString());
        response.setStatus(created().getStatusCode().value());
        response.addHeader("Location", context.getContextPath() + path + '/' + key);
    }

    @RequestMapping(value="{key}", method = RequestMethod.PUT)
    public void put(@PathVariable String key, @RequestBody @Valid JsonNode value) {
        keyValueService.put(key, value.toString());
    }
}
