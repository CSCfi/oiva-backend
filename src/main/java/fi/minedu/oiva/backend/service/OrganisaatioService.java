package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganisaatioService {

    @Autowired
    private OpintopolkuService opintopolkuService;

    public Organisaatio get(final String oid) {
        return opintopolkuService.getBlockingOrganisaatio(oid);
    }

    @Cacheable(value = {"OrganisaatioService:getWithLocation"}, key = "#oid")
    public Optional<Organisaatio> getWithLocation(final String oid) {
        final Optional<Organisaatio> organisaatioOpt = Optional.ofNullable(opintopolkuService.getBlockingOrganisaatio(oid));
        withKunta(organisaatioOpt);
        withMaakunta(organisaatioOpt);
        return organisaatioOpt;
    }

    protected Optional<Organisaatio> withKunta(final Optional<Organisaatio> organisaatioOpt) {
        organisaatioOpt.ifPresent(organisaatio -> {
            if(StringUtils.isNotBlank(organisaatio.kuntaKoodiArvo())) {
                opintopolkuService.getKuntaKoodi(organisaatio.kuntaKoodiArvo()).ifPresent(organisaatio::setKuntaKoodi);
            }
        });
        return organisaatioOpt;
    }

    protected Optional<Organisaatio> withMaakunta(final Optional<Organisaatio> organisaatioOpt) {
        organisaatioOpt.ifPresent(jarjestaja -> {
            if(null != jarjestaja.kuntaKoodi()) {
                opintopolkuService.getMaakuntaKoodiForKunta(jarjestaja.kuntaKoodiArvo()).ifPresent(jarjestaja::setMaakuntaKoodi);
            }
        });
        return organisaatioOpt;
    }
}