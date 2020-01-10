package fi.minedu.oiva.backend.yva.web.controller;

import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.core.service.LupaService;
import fi.minedu.oiva.backend.core.service.MaaraysService;
import fi.minedu.oiva.backend.core.service.OrganisaatioService;
import fi.minedu.oiva.backend.core.web.controller.BasePebbleController;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.model.entity.opintopolku.Organisaatio;
import fi.minedu.oiva.backend.yva.service.KujaPebbleService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

import static fi.minedu.oiva.backend.model.util.ControllerUtil.get500;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.getOr404;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.options;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value = "${api.url.prefix}" + BasePebbleController.path)
public class KujaPebbleController extends BasePebbleController<KujaPebbleService> {

    private MaaraysService maaraysService;

    @Autowired
    public KujaPebbleController(KujaPebbleService service, LupaService lupaService,
                                OrganisaatioService organisaatioService, MaaraysService maaraysService) {
        super(service, lupaService, organisaatioService);
        this.maaraysService = maaraysService;
    }

    // TODO: REMOVE ME
    @OivaAccess_Public
    @RequestMapping(value = "/lista", method = GET, produces = {javax.ws.rs.core.MediaType.TEXT_HTML})
    @ApiOperation(notes = "Tuottaa lupa listan HTML-muodossa", value = "")
    public HttpEntity<String> renderList() {
        try {
            final List<Lupa> luvat = lupaService.getAll(options(Organisaatio.class, KoodistoKoodi.class))
                    .stream()
                    .peek(l -> l.setMaaraykset(maaraysService.getByLupa(l.getId())))
                    .collect(Collectors.toList());
            return getOr404(service.toListHTML(luvat));
        } catch (Exception e) {
            logger.error("Failed to toListHTML html", e);
            return get500();
        }
    }
}
