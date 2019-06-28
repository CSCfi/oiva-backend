package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.model.entity.opintopolku.Organisaatio;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganisaatioService {

    private final OpintopolkuService opintopolkuService;

    @Autowired
    public OrganisaatioService(OpintopolkuService opintopolkuService) {
        this.opintopolkuService = opintopolkuService;
    }

    @Cacheable(value = {"OrganisaatioService:getWithLocation"}, condition = "#oid != null", key = "#oid")
    public Optional<Organisaatio> getWithLocation(final String oid) {
        return Optional.ofNullable(oid).map(o -> {
            final Organisaatio organisaatio = opintopolkuService.getBlockingOrganisaatio(o);
            withKunta(organisaatio);
            withMaakunta(organisaatio);
            return organisaatio;
        });
    }

    private void withKunta(final Organisaatio organisaatio) {
        Optional.ofNullable(organisaatio).ifPresent(o -> {
            if(StringUtils.isNotBlank(o.kuntaKoodiArvo())) {
                opintopolkuService.getKuntaKoodi(o.kuntaKoodiArvo()).ifPresent(o::setKuntaKoodi);
            }
        });
    }

    private void withMaakunta(final Organisaatio organisaatio) {
        Optional.ofNullable(organisaatio).ifPresent(o -> {
            if(null != o.kuntaKoodi()) {
                opintopolkuService.getMaakuntaKoodiForKunta(o.kuntaKoodiArvo())
                        .ifPresent(o::setMaakuntaKoodi);
            }
        });
    }
}