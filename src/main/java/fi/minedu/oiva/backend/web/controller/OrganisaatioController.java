package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.OpintopolkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletionStage;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + OrganisaatioController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
public class OrganisaatioController {

    public static final String path = "/organisaatiot";

    @Autowired
    private OpintopolkuService opintopolkuService;

    @OivaAccess_Public
    @RequestMapping(value = "/{oid:.+}", method = GET)
    public CompletionStage<Organisaatio> get(final @PathVariable String oid) {
        return opintopolkuService.getOrganisaatio(oid);
    }
}
