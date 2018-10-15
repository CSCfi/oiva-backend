package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.oiva.Lupa;
import fi.minedu.oiva.backend.entity.oiva.Lupatila;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static fi.minedu.oiva.backend.jooq.Tables.LUPATILA;

@Service
public class LupatilaService {

    @Autowired
    private DSLContext dsl;

    protected SelectJoinStep<Record> baseQuery() {
        return dsl.select(LUPATILA.fields()).from(LUPATILA);
    }

    public Collection<Lupatila> getAll() {
        return baseQuery().fetchInto(Lupatila.class);
    }

    public Optional<Lupatila> forLupa(final Lupa lupa) {
        return Optional.ofNullable(null != lupa ?
            baseQuery().where(LUPATILA.ID.eq(lupa.getLupatilaId())).fetchOneInto(Lupatila.class) : null);
    }

    public Optional<Long> idForUuid(final UUID uuid) {
        return Optional.ofNullable(null != uuid ? dsl.select(LUPATILA.ID).from(LUPATILA).where(LUPATILA.UUID.eq(uuid)).fetchOne().value1() : null);
    }
}