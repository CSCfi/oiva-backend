package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Public;
import springfox.documentation.annotations.ApiIgnore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@ApiIgnore
public class RootController {

    @OivaAccess_Public
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getRoot() {
        return "redirect:/swagger-ui.html";
    }
}
