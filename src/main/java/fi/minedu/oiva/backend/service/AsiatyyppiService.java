package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Asiatyyppi;
import fi.minedu.oiva.backend.entity.Lupa;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

import static fi.minedu.oiva.backend.jooq.Tables.ASIATYYPPI;

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
}