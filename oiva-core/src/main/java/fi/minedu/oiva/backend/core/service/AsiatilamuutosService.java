package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.model.entity.oiva.Asiatilamuutos;
import fi.minedu.oiva.backend.model.entity.oiva.Muutospyynto;
import fi.minedu.oiva.backend.model.jooq.tables.pojos.MuutospyyntoAsiatilamuutos;
import fi.minedu.oiva.backend.model.jooq.tables.records.AsiatilamuutosRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static fi.minedu.oiva.backend.model.jooq.Tables.ASIATILAMUUTOS;
import static fi.minedu.oiva.backend.model.jooq.Tables.MUUTOSPYYNTO_ASIATILAMUUTOS;

@Service
public class AsiatilamuutosService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private final DSLContext dsl;

    @Autowired
    public AsiatilamuutosService(DSLContext dsl) {
        this.dsl = dsl;
    }

    private SelectJoinStep<Record> baseQuery() {
        return dsl.select(ASIATILAMUUTOS.fields()).from(ASIATILAMUUTOS);
    }

    Optional<List<Asiatilamuutos>> forMuutospyynto(final Muutospyynto muutospyynto) {
        return Optional.ofNullable(null != muutospyynto ?
                baseQuery()
                        .join(MUUTOSPYYNTO_ASIATILAMUUTOS).on(MUUTOSPYYNTO_ASIATILAMUUTOS.ASIATILAMUUTOS_ID.eq(ASIATILAMUUTOS.ID))
                        .where(MUUTOSPYYNTO_ASIATILAMUUTOS.MUUTOSPYYNTO_ID.eq(muutospyynto.getId()))
                        .fetchInto(Asiatilamuutos.class) : null);
    }

    Asiatilamuutos insertForMuutospyynto(final Long muutospyyntoId, final String vanhaTila, final String uusiTila, final String username) {
        Asiatilamuutos tilamuutos = new Asiatilamuutos(vanhaTila, uusiTila, username);
        AsiatilamuutosRecord tilamuutosRecord = dsl.newRecord(ASIATILAMUUTOS, tilamuutos);
        tilamuutosRecord.store();
        tilamuutos.setId(tilamuutosRecord.getId());

        dsl.newRecord(MUUTOSPYYNTO_ASIATILAMUUTOS, new MuutospyyntoAsiatilamuutos(muutospyyntoId, tilamuutos.getId())).store();

        return tilamuutos;
    }

    void deleteAll(final Long muutospyyntoId) {

        List<Long> ids = dsl.select(MUUTOSPYYNTO_ASIATILAMUUTOS.ASIATILAMUUTOS_ID)
                .from(MUUTOSPYYNTO_ASIATILAMUUTOS)
                .where(MUUTOSPYYNTO_ASIATILAMUUTOS.MUUTOSPYYNTO_ID.eq(muutospyyntoId))
                .fetchInto(Long.class);

        int joinTableDeletes = dsl.delete(MUUTOSPYYNTO_ASIATILAMUUTOS)
                .where(MUUTOSPYYNTO_ASIATILAMUUTOS.MUUTOSPYYNTO_ID.eq(muutospyyntoId))
                .execute();

        int asiamuutosDeletes = dsl.delete(ASIATILAMUUTOS)
                .where(ASIATILAMUUTOS.ID.in(ids))
                .execute();

        logger.debug("Deleted {}, {} rows", asiamuutosDeletes, joinTableDeletes);
    }
}