package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.service.DefaultPebbleService;
import fi.minedu.oiva.backend.core.service.LupaService;
import fi.minedu.oiva.backend.core.service.OrganisaatioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "${api.url.prefix}" + BasePebbleController.path)
public class DefaultPebbleController extends BasePebbleController<DefaultPebbleService> {
    @Autowired
    public DefaultPebbleController(DefaultPebbleService service, LupaService lupaService,
                                   OrganisaatioService organisaatioService) {
        super(service, lupaService, organisaatioService);
    }
}
