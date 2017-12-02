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
    @RequestMapping("/{oid:.+}/raw")
    public CompletionStage<String> get(final @PathVariable String oid) {
        return opintopolkuService.getOrganisaatioAsRaw(oid);
    }

    @OivaAccess_Public
    @RequestMapping("/{oid:.+}")
    public CompletionStage<Organisaatio> getAsEntity(final @PathVariable String oid) {
        return opintopolkuService.getOrganisaatio(oid);
    }

    @OivaAccess_Public
    @RequestMapping("/block/{oid:.+}")
    public Organisaatio getAsEntityBlocking(final @PathVariable String oid) {
        return opintopolkuService.getBlockingOrganisaatio(oid);
    }

    @OivaAccess_Public
    @RequestMapping(method = GET)
    public CompletionStage<String> getMany(final @RequestParam String[] oids) {
        return opintopolkuService.getOrganisaatiosAsCSString(oids);
    }
}
