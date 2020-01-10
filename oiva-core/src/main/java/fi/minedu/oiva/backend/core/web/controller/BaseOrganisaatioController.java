package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.model.entity.opintopolku.Organisaatio;
import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.core.service.OrganisaatioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.core.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.getOr404;
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
