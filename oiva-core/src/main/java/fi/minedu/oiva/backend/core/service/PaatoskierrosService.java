package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.oiva.Paatoskierros;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static fi.minedu.oiva.backend.model.jooq.Tables.PAATOSKIERROS;

@Service
public class PaatoskierrosService {

    @Autowired
    private DSLContext dsl;

    protected SelectJoinStep<Record> baseQuery() {
        return dsl.select(PAATOSKIERROS.fields()).from(PAATOSKIERROS);
    }

    public Collection<Paatoskierros> getAll() {
        return baseQuery().fetchInto(Paatoskierros.class);
    }

    public Optional<Paatoskierros> forLupa(final Lupa lupa) {
        return Optional.ofNullable(null != lupa ?
            baseQuery().where(PAATOSKIERROS.ID.eq(lupa.getPaatoskierrosId())).fetchOneInto(Paatoskierros.class) : null);
    }

    public Optional<Long> idForUuid(final UUID uuid) {
        return Optional.ofNullable(null != uuid ? dsl.select(PAATOSKIERROS.ID).from(PAATOSKIERROS).where(PAATOSKIERROS.UUID.eq(uuid)).fetchOne().value1() : null);
    }

    public Collection<Paatoskierros> getOpen() {
        return dsl.select(PAATOSKIERROS.fields()).from(PAATOSKIERROS)
                .where( (PAATOSKIERROS.ALKUPVM.le(DSL.currentDate()).and(PAATOSKIERROS.LOPPUPVM.greaterOrEqual(DSL.currentDate()))))
                .fetchInto(Paatoskierros.class);
    }

}