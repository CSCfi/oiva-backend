package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.OrganisaatioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.util.ControllerUtil.getOr404;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

public abstract class BaseOrganisaatioController {

    public static final String path = "/organisaatiot";

    @Autowired
    private OrganisaatioService service;

    @OivaAccess_Public
    @RequestMapping(value = "/{oid:.+}", method = GET)
    public CompletableFuture<HttpEntity<Organisaatio>> getWithLocation(final @PathVariable String oid) {
        return getOr404(async(() -> service.getWithLocation(oid)));
    }
}
