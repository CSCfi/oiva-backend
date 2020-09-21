package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.model.entity.oiva.Liite;
import fi.minedu.oiva.backend.model.entity.oiva.Lupahistoria;
import fi.minedu.oiva.backend.model.jooq.tables.records.LupahistoriaRecord;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static fi.minedu.oiva.backend.model.jooq.Tables.LIITE;
import static fi.minedu.oiva.backend.model.jooq.Tables.LUPAHISTORIA;
import static fi.minedu.oiva.backend.model.jooq.Tables.LUPA_LIITE;

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
                .orderBy(LUPAHISTORIA.PAATOSPVM).fetchInto(Lupahistoria.class).stream()
                .map(this::withPaatoskirje)
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    public Collection<Lupahistoria> getHistoriaByYtunnus(String ytunnus) {
        return dsl.select(LUPAHISTORIA.fields())
                .from(LUPAHISTORIA)
                .where(LUPAHISTORIA.YTUNNUS.eq(ytunnus)
                        .and(LUPAHISTORIA.VOIMASSAOLOLOPPUPVM.lessThan(DSL.currentDate())))
                .orderBy(LUPAHISTORIA.PAATOSPVM).fetchInto(Lupahistoria.class).stream()
                .map(this::withPaatoskirje)
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    public Optional<LupahistoriaRecord> getByUuid(String uuid) {
        return dsl.select(LUPAHISTORIA.fields())
                .from(LUPAHISTORIA)
                .where(LUPAHISTORIA.UUID.eq(UUID.fromString(uuid)))
                .fetchOptionalInto(LupahistoriaRecord.class);
    }

    private Optional <Lupahistoria> withPaatoskirje(Lupahistoria lupahistoria) {
        Optional.ofNullable(lupahistoria)
                .ifPresent(l -> getPaatoskirje(l.getLupaId()).ifPresent(l::setPaatoskirje));
        return Optional.ofNullable(lupahistoria);
    }

    private Optional<Liite> getPaatoskirje(Long lupaId) {
        return dsl.select(LIITE.fields()).from(LUPA_LIITE).leftJoin(LIITE)
                .on(LUPA_LIITE.LIITE_ID.eq(LIITE.ID)).where(LUPA_LIITE.LUPA_ID.eq(lupaId).and(LIITE.TYYPPI.eq("paatosKirje")))
                .fetchOptionalInto(Liite.class);
    }
}
