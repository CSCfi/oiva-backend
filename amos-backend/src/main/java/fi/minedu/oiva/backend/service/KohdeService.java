package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.oiva.Kohde;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static fi.minedu.oiva.backend.jooq.Tables.KOHDE;

@Service
public class KohdeService {

    @Autowired
    private DSLContext dsl;

    public Collection<Kohde> getAll() {
        return dsl.select(KOHDE.fields()).from(KOHDE).fetchInto(Kohde.class);
    }

    public Optional<Long> idForTunniste(final String tunniste) {
        return Optional.ofNullable(StringUtils.isNotBlank(tunniste) ? dsl.select(KOHDE.ID).from(KOHDE).where(KOHDE.TUNNISTE.eq(tunniste)).fetchOne().value1() : null);
    }

    public Map<Long, Kohde> mapAll() {
        return getAll().stream().collect(Collectors.toMap(Kohde::getId, kohde -> kohde));
    }
}