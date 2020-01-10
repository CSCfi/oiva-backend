package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.model.entity.oiva.Lupahistoria;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collection;

import static fi.minedu.oiva.backend.model.jooq.Tables.LUPAHISTORIA;

@Service
public class LupahistoriaService {

    @Autowired
    private DSLContext dsl;

    public Collection<Lupahistoria> getHistoriaByOid(String oid) {
        return dsl.select(LUPAHISTORIA.ID, LUPAHISTORIA.YTUNNUS, LUPAHISTORIA.OID, LUPAHISTORIA.VOIMASSAOLOALKUPVM,
            LUPAHISTORIA.VOIMASSAOLOLOPPUPVM, LUPAHISTORIA.DIAARINUMERO, LUPAHISTORIA.MAAKUNTA,
            LUPAHISTORIA.FILENAME, LUPAHISTORIA.PAATOSPVM).from(LUPAHISTORIA)
            .where(LUPAHISTORIA.OID.eq(oid))
            .orderBy(LUPAHISTORIA.PAATOSPVM).fetchInto(Lupahistoria.class);
    }

    public Collection<Lupahistoria> getHistoriaByYtunnus(String ytunnus) {
        return dsl.select(LUPAHISTORIA.ID, LUPAHISTORIA.YTUNNUS, LUPAHISTORIA.OID, LUPAHISTORIA.VOIMASSAOLOALKUPVM,
            LUPAHISTORIA.VOIMASSAOLOLOPPUPVM, LUPAHISTORIA.DIAARINUMERO, LUPAHISTORIA.MAAKUNTA,
            LUPAHISTORIA.FILENAME, LUPAHISTORIA.PAATOSPVM).from(LUPAHISTORIA)
            .where(LUPAHISTORIA.YTUNNUS.eq(ytunnus))
            .orderBy(LUPAHISTORIA.PAATOSPVM).fetchInto(Lupahistoria.class);
    }
}
