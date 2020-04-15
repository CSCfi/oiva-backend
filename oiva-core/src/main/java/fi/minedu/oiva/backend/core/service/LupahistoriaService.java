package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.model.entity.oiva.Lupahistoria;
import fi.minedu.oiva.backend.model.jooq.tables.records.LupahistoriaRecord;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static fi.minedu.oiva.backend.model.jooq.Tables.LUPAHISTORIA;

@Service
public class LupahistoriaService {

    private final DSLContext dsl;

    @Autowired
    public LupahistoriaService(DSLContext dslContext) {
        this.dsl = dslContext;
    }

    public Collection<Lupahistoria> getHistoriaByOid(String oid) {
        return dsl.select(LUPAHISTORIA.fields())
                .from(LUPAHISTORIA)
                .where(LUPAHISTORIA.OID.eq(oid)
                        .and(LUPAHISTORIA.VOIMASSAOLOLOPPUPVM.lessThan(DSL.currentDate())))
                .orderBy(LUPAHISTORIA.PAATOSPVM).fetchInto(Lupahistoria.class);
    }

    public Collection<Lupahistoria> getHistoriaByYtunnus(String ytunnus) {
        return dsl.select(LUPAHISTORIA.fields())
                .from(LUPAHISTORIA)
                .where(LUPAHISTORIA.YTUNNUS.eq(ytunnus)
                        .and(LUPAHISTORIA.VOIMASSAOLOLOPPUPVM.lessThan(DSL.currentDate())))
                .orderBy(LUPAHISTORIA.PAATOSPVM).fetchInto(Lupahistoria.class);
    }

    public Optional<LupahistoriaRecord> getByUuid(String uuid) {
        return dsl.select(LUPAHISTORIA.fields())
                .from(LUPAHISTORIA)
                .where(LUPAHISTORIA.UUID.eq(UUID.fromString(uuid)))
                .fetchOptionalInto(LupahistoriaRecord.class);
    }
}
