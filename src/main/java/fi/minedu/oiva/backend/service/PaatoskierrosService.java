package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.entity.Paatoskierros;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

import static fi.minedu.oiva.backend.jooq.Tables.PAATOSKIERROS;

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
}