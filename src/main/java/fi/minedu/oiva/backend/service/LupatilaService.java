package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.entity.Lupatila;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

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
}