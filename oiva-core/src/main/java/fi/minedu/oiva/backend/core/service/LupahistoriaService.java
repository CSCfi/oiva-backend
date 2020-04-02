package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.model.entity.oiva.Lupahistoria;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static fi.minedu.oiva.backend.model.jooq.Tables.LUPAHISTORIA;

@Service
public class LupahistoriaService {

    @Autowired
    private DSLContext dsl;

    public Collection<Lupahistoria> getHistoriaByOid(String oid) {
        return dsl.select(LUPAHISTORIA.ID, LUPAHISTORIA.YTUNNUS, LUPAHISTORIA.OID, LUPAHISTORIA.VOIMASSAOLOALKUPVM,
            LUPAHISTORIA.VOIMASSAOLOLOPPUPVM, LUPAHISTORIA.DIAARINUMERO, LUPAHISTORIA.MAAKUNTA,
            LUPAHISTORIA.FILENAME, LUPAHISTORIA.PAATOSPVM, LUPAHISTORIA.UUID, LUPAHISTORIA.KUMOTTUPVM).from(LUPAHISTORIA)
            .where(LUPAHISTORIA.OID.eq(oid))
            .orderBy(LUPAHISTORIA.PAATOSPVM).fetchInto(Lupahistoria.class);
    }

    public Collection<Lupahistoria> getHistoriaByYtunnus(String ytunnus) {
        return dsl.select(LUPAHISTORIA.ID, LUPAHISTORIA.YTUNNUS, LUPAHISTORIA.OID, LUPAHISTORIA.VOIMASSAOLOALKUPVM,
            LUPAHISTORIA.VOIMASSAOLOLOPPUPVM, LUPAHISTORIA.DIAARINUMERO, LUPAHISTORIA.MAAKUNTA,
            LUPAHISTORIA.FILENAME, LUPAHISTORIA.PAATOSPVM, LUPAHISTORIA.UUID, LUPAHISTORIA.KUMOTTUPVM).from(LUPAHISTORIA)
            .where(LUPAHISTORIA.YTUNNUS.eq(ytunnus))
            .orderBy(LUPAHISTORIA.PAATOSPVM).fetchInto(Lupahistoria.class);
    }

    public Optional<Lupahistoria> getByUuid(String uuid) {
        return dsl.select(LUPAHISTORIA.fields())
                .from(LUPAHISTORIA)
                .where(LUPAHISTORIA.UUID.eq(UUID.fromString(uuid)))
                .fetchOptionalInto(Lupahistoria.class);
    }
}
