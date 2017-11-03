package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.dto.Henkilo;
import fi.minedu.oiva.backend.repo.LdapHenkiloRepository;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;

@Service
public class HenkiloService {

    @Autowired
    private LdapHenkiloRepository henkiloRepository;

    @Autowired
    private OpintopolkuService opintopolkuService;

    @OivaAccess_Application
    public Henkilo getHenkiloByOid(String oid) {
        return henkiloRepository.getHenkiloByOid(oid);
    }

    @OivaAccess_Application
    public CompletableFuture<Collection<Henkilo>> getHenkilosByOidsAsync(final String[] oids) {
        return async(() -> this.getHenkilosByOids(oids));
    }

    @OivaAccess_Application
    public CompletableFuture<Optional<Henkilo>> getByOidWithOrganizationAsync(final String oid) {
        return async(() -> getByOidWithOrganization(oid));
    }

    private List<Henkilo> getHenkilosByOids(final String[] oids) {
        return henkiloRepository.getHenkilosByOids(oids);
    }

    private Optional<Henkilo> getByOidWithOrganization(final String oid) {
        return Optional.ofNullable(this.getHenkiloByOid(oid)).map(henkilo -> {
                henkilo.setOrganisaatioInfo(opintopolkuService.getOrganisaatiohenkilo(henkilo.getOid()));
                return henkilo;
            }
        );
    }
}
