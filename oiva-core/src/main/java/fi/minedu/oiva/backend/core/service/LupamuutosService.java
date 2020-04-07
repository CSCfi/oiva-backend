package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.core.exception.ForbiddenException;
import fi.minedu.oiva.backend.core.exception.ResourceNotFoundException;
import fi.minedu.oiva.backend.model.entity.oiva.Muutospyynto;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import fi.minedu.oiva.backend.core.service.MuutospyyntoService.Action;
import fi.minedu.oiva.backend.core.service.MuutospyyntoService.Muutospyyntotila;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

/**
 * LupamuutosService handles muutospyynnöt created and approved by esittelijä. This differs from normal process where
 * muutospyyntö is created by KJ and send to esittelijä for approval.
 */
@Service
public class LupamuutosService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private final MuutospyyntoService muutospyyntoService;
    private final AuthService authService;

    @Autowired
    public LupamuutosService(MuutospyyntoService muutospyyntoService, AuthService authService) {
        this.muutospyyntoService = muutospyyntoService;
        this.authService = authService;
    }

    // Complimentary method for päättäminen
    public Optional<Muutospyynto> executeAction(String uuid, Action action) {
        return executeAction(uuid, action, null, null);
    }

    public Optional<Muutospyynto> executeAction(String uuid, Action action, Muutospyynto muutospyynto, Map<String, MultipartFile> fileMap) {
        try {
            logger.info("Executing muutospyynto action " + action);

            switch (action) {
                case LUO:
                    return luo(muutospyynto, fileMap);
                case TALLENNA:
                    return tallenna(muutospyynto, fileMap);
                // case PAATA:   implementation here
                default:
                    throw new UnsupportedOperationException("Action " + action + " is not supported for muutospyynto");
            }
        } catch (Exception e) {
            logger.warn("Error executing muutospyynto " + action, e);
            throw e;
        }
    }

    private Optional<Muutospyynto> luo(final Muutospyynto muutospyynto, final Map<String, MultipartFile> fileMap) {
//        muutospyynto.setTyyppi(Tyyppi.ESITTELIJA.toString());
        assertValid(muutospyynto);
        if (!authService.hasAnyRole(OivaAccess.Role_Esittelija)) {
            throw new ForbiddenException("User has no right");
        }
        muutospyynto.setTila(Muutospyyntotila.VALMISTELUSSA.name());
        return muutospyyntoService.save(muutospyynto, fileMap).flatMap(m -> muutospyyntoService.getById(m.getId()));
    }

    private Optional<Muutospyynto> tallenna(final Muutospyynto muutospyynto, final Map<String, MultipartFile> fileMap) {
        assertValid(muutospyynto);
        Muutospyynto existing = muutospyyntoService.getByUuid(muutospyynto.getUuid().toString()).orElseThrow(() -> new ResourceNotFoundException("Muutospyynto is not found with uuid " + muutospyynto.getUuid()));
        if (!Muutospyyntotila.VALMISTELUSSA.toString().equals(existing.getTila())) {
            throw new ForbiddenException("Action is not allowed");
        }
        if (!authService.hasAnyRole(OivaAccess.Role_Esittelija)) {
            throw new ForbiddenException("User has no right");
        }
        return muutospyyntoService.update(muutospyynto, fileMap).flatMap(m -> muutospyyntoService.getById(m.getId()));
    }

    protected void assertValid(Muutospyynto muutospyynto) {
//        if (muutospyynto.getTyyppi()) {
//            throw new ValidationException("Invalid object");
//        }

        muutospyyntoService.assertValid(muutospyynto);
    }
}
