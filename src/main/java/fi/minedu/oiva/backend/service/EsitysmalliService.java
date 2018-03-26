package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Esitysmalli;
import fi.minedu.oiva.backend.entity.Paatoskierros;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

import static fi.minedu.oiva.backend.jooq.Tables.ESITYSMALLI;

@Service
public class EsitysmalliService {

    @Autowired
    private DSLContext dsl;

    protected SelectJoinStep<Record> baseQuery() {
        return dsl.select(ESITYSMALLI.fields()).from(ESITYSMALLI);
    }

    public Collection<Esitysmalli> getAll() {
        return baseQuery().fetchInto(Esitysmalli.class);
    }

    public Optional<Esitysmalli> forPaatoskierros(final Paatoskierros paatoskierros) {
        return Optional.ofNullable(null != paatoskierros ?
            baseQuery().where(ESITYSMALLI.ID.eq(paatoskierros.getEsitysmalliId())).fetchOneInto(Esitysmalli.class) : null);
    }
}