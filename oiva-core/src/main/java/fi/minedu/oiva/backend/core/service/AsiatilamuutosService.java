package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.model.entity.oiva.Muutospyynto;
import fi.minedu.oiva.backend.model.entity.oiva.Asiatilamuutos;
import fi.minedu.oiva.backend.model.jooq.tables.pojos.MuutospyyntoAsiatilamuutos;
import fi.minedu.oiva.backend.model.jooq.tables.records.AsiatilamuutosRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static fi.minedu.oiva.backend.model.jooq.Tables.MUUTOSPYYNTO_ASIATILAMUUTOS;
import static fi.minedu.oiva.backend.model.jooq.Tables.ASIATILAMUUTOS;

@Service
public class AsiatilamuutosService {

    @Autowired
    private DSLContext dsl;

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
}