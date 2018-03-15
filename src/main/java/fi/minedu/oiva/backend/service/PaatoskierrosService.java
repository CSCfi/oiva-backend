package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Paatoskierros;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;

import static fi.minedu.oiva.backend.jooq.Tables.PAATOSKIERROS;

@Service
public class PaatoskierrosService {

    @Autowired
    private DSLContext dsl;

    public Collection<Paatoskierros> getAll() { 
        return dsl.select(PAATOSKIERROS.fields()).from(PAATOSKIERROS).fetchInto(Paatoskierros.class);
    }

    public Collection<Paatoskierros> getOpen() {
        return dsl.select(PAATOSKIERROS.fields()).from(PAATOSKIERROS)
                .where( (PAATOSKIERROS.ALKUPVM.le(DSL.currentDate()).and(PAATOSKIERROS.LOPPUPVM.greaterOrEqual(DSL.currentDate()))))
                .fetchInto(Paatoskierros.class);
    }

}