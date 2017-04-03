package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.opintopolku.Koodisto;
import fi.minedu.oiva.backend.entity.opintopolku.Maakunta;
import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio;
import fi.minedu.oiva.backend.service.KoodistoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + KoodistoController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
public class KoodistoController {

    public static final String path = "/koodistot";

    @Autowired
    private KoodistoService service;

    @RequestMapping(value = "/maakuntakunta", method = GET)
    public CompletableFuture<Collection<Maakunta>> getMaakuntaKunnat() {
        return async(service::getMaakuntaKunnat);
    }

    @RequestMapping(value = "/koulutustoimijat", method = GET)
    public CompletableFuture<Collection<Organisaatio>> getKoulutustoimijat() {
        return async(service::getKoulutustoimijat);
    }

    @RequestMapping(value = "/maakuntajarjestajat", method = GET)
    public CompletableFuture<Collection<Maakunta>> getMaakuntaJarjestajat() {
        return async(service::getMaakuntaJarjestajat);
    }

    @RequestMapping(value = "/kunnat", method = GET)
    public CompletableFuture<Collection<Koodisto>> getKunnat() {
        return async(service::getKunnat);
    }

    @RequestMapping(value = "/kunnat/{koodi}", method = GET)
    public Koodisto getKuntaByKoodi(@PathVariable String koodi) {
        return service.getKunta(koodi);
    }

    @RequestMapping(value = "/kielet", method = GET)
    public CompletableFuture<Collection<Koodisto>> getKielet() {
        return async(service::getKielet);
    }

    @RequestMapping(value = "/kielet/{koodi}", method = GET)
    public Koodisto getKieliByKoodi(@PathVariable String koodi) {
        return service.getKieli(koodi);
    }

    @RequestMapping(value = "/opetuskielet", method = GET)
    public CompletableFuture<Collection<Koodisto>> getOpetuskielet() {
        return async(service::getOpetuskielet);
    }
}
