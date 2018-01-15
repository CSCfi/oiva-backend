package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.opintopolku.Koodisto;
import fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.entity.opintopolku.Maakunta;
import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.KoodistoService;
import fi.minedu.oiva.backend.service.OpintopolkuService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    @OivaAccess_Public
    @RequestMapping(value = "/koodisto/{koodistoUri}", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun uusimman koodiston koodistoUrin perusteella", value = "")
    public CompletableFuture<Koodisto> getKoodisto(final @PathVariable String koodistoUri) {
        return async(() -> service.getKoodisto(koodistoUri, null));
    }

    @OivaAccess_Public
    @RequestMapping(value = "/koodisto/{koodistoUri}/{koodistoVersio:[0-9]+}", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun koodiston koodistoUrin ja koodistoVersion perusteella", value = "")
    public CompletableFuture<Koodisto> getKoodisto(final @PathVariable String koodistoUri, final @PathVariable Integer koodistoVersio) {
        return async(() -> service.getKoodisto(koodistoUri, koodistoVersio));
    }

    @OivaAccess_Public
    @RequestMapping(value = "/koodit/{koodistoUri}", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun uusimmat koodiston koodit koodistoUrin perusteella", value = "")
    public CompletableFuture<List<KoodistoKoodi>> getKoodit(final @PathVariable String koodistoUri) {
        return async(() -> service.getKoodit(koodistoUri, null));
    }

    @OivaAccess_Public
    @RequestMapping(value = "/koodit/{koodistoUri}/{koodistoVersio:[0-9]+}", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun koodiston koodit koodistoUrin ja koodistoVersion perusteella", value = "")
    public CompletableFuture<List<KoodistoKoodi>> getKoodit(final @PathVariable String koodistoUri, final @PathVariable Integer koodistoVersio) {
        return async(() -> service.getKoodit(koodistoUri, koodistoVersio));
    }

    @OivaAccess_Public
    @RequestMapping(value = "/koodi/{koodisto}/{koodi}", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun uusimman koodistoversion koodin koodiston ja kooriarvon perusteella", value = "")
    public CompletableFuture<KoodistoKoodi> getKoodi(final @PathVariable String koodisto, final @PathVariable String koodi) {
        return async(() -> service.getKoodi(koodisto, koodi, null));
    }

    @OivaAccess_Public
    @RequestMapping(value = "/koodi/{koodisto}/{koodi}/{koodistoVersio:[0-9]+}", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun koodin koodiston, kooriarvon ja version perusteella", value = "")
    public CompletableFuture<KoodistoKoodi> getKoodi(
        final @PathVariable String koodisto, final @PathVariable String koodi, final @PathVariable Integer koodistoVersio) {
        return async(() -> service.getKoodi(koodisto, koodi, koodistoVersio));
    }

    @OivaAccess_Public
    @RequestMapping(value = "/maakuntakunta", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun maakunnat kunnilla", value = "")
    public CompletableFuture<Collection<Maakunta>> getMaakuntaKunnat() {
        return async(service::getMaakuntaKunnat);
    }

    @OivaAccess_Public
    @RequestMapping(value = "/koulutustoimijat", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun koulutustoimijat", value = "")
    public CompletableFuture<Collection<Organisaatio>> getKoulutustoimijat() {
        return async(service::getKoulutustoimijat);
    }

    @OivaAccess_Public
    @RequestMapping(value = "/maakuntajarjestajat", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun maakunnat koulutuksenjärjestäjillä", value = "")
    public CompletableFuture<Collection<Maakunta>> getMaakuntaJarjestajat() {
        return async(service::getMaakuntaJarjestajat);
    }

    @OivaAccess_Public
    @RequestMapping(value = "/maakunnat", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun maakunnat", value = "")
    public CompletableFuture<Collection<KoodistoKoodi>> getMaakunnat() {
        return async(service::getMaakunnat);
    }

    @OivaAccess_Public
    @RequestMapping(value = "/kunnat", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun kunnat", value = "")
    public CompletableFuture<Collection<KoodistoKoodi>> getKunnat() {
        return async(service::getKunnat);
    }

    @OivaAccess_Public
    @RequestMapping(value = "/kunnat/{koodiArvo}", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun kunnan koodiarvon perusteela", value = "")
    public CompletableFuture<KoodistoKoodi> getKunta(final @PathVariable String koodiArvo) {
        return async(() -> service.getKunta(koodiArvo));
    }

    @OivaAccess_Public
    @RequestMapping(value = "/kielet", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun kielet", value = "")
    public CompletableFuture<Collection<KoodistoKoodi>> getKielet() {
        return async(service::getKielet);
    }

    @OivaAccess_Public
    @RequestMapping(value = "/kielet/{koodiArvo}", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun kielen koodiarvon perusteela", value = "")
    public CompletableFuture<KoodistoKoodi> getKieli(final @PathVariable String koodiArvo) {
        return async(() -> service.getKieli(koodiArvo));
    }

    @OivaAccess_Public
    @RequestMapping(value = "/opetuskielet", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun opetuskielet koodit", value = "")
    public CompletableFuture<Collection<KoodistoKoodi>> getOpetuskielet() {
        return async(service::getOpetuskielet);
    }

    @OivaAccess_Public
    @RequestMapping(value = "/koulutusalat", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun koulutusalat", value = "")
    public CompletableFuture<Collection<KoodistoKoodi>> getKoulutusalat() {
        return async(service::getKoulutusalat);
    }

    @OivaAccess_Public
    @RequestMapping(value = "/koulutusalat/{koodiArvo}", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun koulutusalan koodiarvon perusteela", value = "")
    public CompletableFuture<KoodistoKoodi> getKoulutusala(final @PathVariable String koodiArvo) {
        return async(() -> service.getKoulutusala(koodiArvo));
    }

    @OivaAccess_Public
    @RequestMapping(value = "/koulutusalat/{koodiArvo}/koulutukset", method = GET)
    @ApiOperation(notes = "Palauttaa opintopolun koulutukset koulutusalan perusteella", value = "")
    public CompletableFuture<List<KoodistoKoodi>> getKoulutusalaKoulutukset(final @PathVariable String koodiArvo) {
        return async(() -> service.getKoulutusalaKoulutukset(koodiArvo));
    }

    @OivaAccess_Public
    @RequestMapping(value = "/koulutus-koulutusala-relaatio", method = GET)
    @ApiOperation(notes = "Palauttaa koulutusten koulutusala relaatiot", value = "")
    public CompletableFuture<Map<String, String>> getKoulutusToKoulutusalaRelation() {
        return async(service::getKoulutusToKoulutusalaRelation);
    }
}
