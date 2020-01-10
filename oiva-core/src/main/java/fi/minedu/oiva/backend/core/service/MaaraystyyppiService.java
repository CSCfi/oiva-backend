package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.model.entity.oiva.Maaraystyyppi;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static fi.minedu.oiva.backend.model.jooq.Tables.MAARAYSTYYPPI;

@Service
public class MaaraystyyppiService {

    @Autowired
    private DSLContext dsl;

    public Collection<Maaraystyyppi> getAll() {
        return dsl.select(MAARAYSTYYPPI.fields()).from(MAARAYSTYYPPI).fetchInto(Maaraystyyppi.class);
    }

    public Optional<Long> idForUuid(final UUID uuid) {
        return Optional.ofNullable(null != uuid ? dsl.select(MAARAYSTYYPPI.ID).from(MAARAYSTYYPPI).where(MAARAYSTYYPPI.UUID.eq(uuid)).fetchOne().value1() : null);
    }

    public Map<Long, Maaraystyyppi> mapAll() {
        return getAll().stream().collect(Collectors.toMap(Maaraystyyppi::getId, maaraystyyppi -> maaraystyyppi));
    }
}