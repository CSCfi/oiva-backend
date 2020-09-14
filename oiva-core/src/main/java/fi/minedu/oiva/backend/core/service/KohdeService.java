package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.model.entity.oiva.Kohde;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static fi.minedu.oiva.backend.model.jooq.Tables.KOHDE;

@Service
public class KohdeService {

    @Autowired
    private DSLContext dsl;

    public Collection<Kohde> getAll() {
        return getAll(null);
    }

    public Collection<Kohde> getAll(String koulutustyyppi) {
        return dsl.select(KOHDE.fields()).from(KOHDE)
                .where(koulutustyyppi == null ? KOHDE.KOULUTUSTYYPPI.isNull() : KOHDE.KOULUTUSTYYPPI.eq(koulutustyyppi))
                .fetchInto(Kohde.class);
    }

    public Optional<Long> idForTunniste(final String tunniste) {
        return Optional.ofNullable(StringUtils.isNotBlank(tunniste) ? dsl.select(KOHDE.ID).from(KOHDE).where(KOHDE.TUNNISTE.eq(tunniste)).fetchOne().value1() : null);
    }

    public Map<Long, Kohde> mapAll() {
        return getAll().stream().collect(Collectors.toMap(Kohde::getId, kohde -> kohde));
    }
}