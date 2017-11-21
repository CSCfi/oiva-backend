package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.dto.Henkilo;
import fi.minedu.oiva.backend.repo.LdapHenkiloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HenkiloService {

    @Autowired
    private LdapHenkiloRepository ldapAccess;

    @Autowired
    private OpintopolkuService opintopolkuService;

    public List<Henkilo> getHenkilosByOids(final String[] oids) {
        return ldapAccess.getHenkilosByOids(oids);
    }

    public Optional<Henkilo> getByOidWithOrganization(final String oid) {
        return Optional.ofNullable(this.getHenkiloByOid(oid)).map(henkilo -> {
            henkilo.setOrganisaatioInfo(opintopolkuService.getOrganisaatiohenkilo(henkilo.getOid()));
            return henkilo;
        });
    }

    private Henkilo getHenkiloByOid(final String oid) {
        return ldapAccess.getHenkiloByOid(oid);
    }
}
