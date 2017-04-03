package fi.minedu.oiva.backend.web.controller;

import springfox.documentation.annotations.ApiIgnore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@ApiIgnore
public class RootController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getRoot() {
        return "redirect:/swagger-ui.html";
    }
}
