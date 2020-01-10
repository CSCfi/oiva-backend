package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.model.entity.oiva.Asiatyyppi;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static fi.minedu.oiva.backend.model.jooq.Tables.ASIATYYPPI;

@Service
public class AsiatyyppiService {

    @Autowired
    private DSLContext dsl;

    protected SelectJoinStep<Record> baseQuery() {
        return dsl.select(ASIATYYPPI.fields()).from(ASIATYYPPI);
    }

    public Collection<Asiatyyppi> getAll() {
        return baseQuery().fetchInto(Asiatyyppi.class);
    }

    public Optional<Asiatyyppi> forLupa(final Lupa lupa) {
        return Optional.ofNullable(null != lupa ?
            baseQuery().where(ASIATYYPPI.ID.eq(lupa.getAsiatyyppiId())).fetchOneInto(Asiatyyppi.class) : null);
    }

    public Optional<Long> idForUuid(final UUID uuid) {
        return Optional.ofNullable(null != uuid ? dsl.select(ASIATYYPPI.ID).from(ASIATYYPPI).where(ASIATYYPPI.UUID.eq(uuid)).fetchOne().value1() : null);
    }
}