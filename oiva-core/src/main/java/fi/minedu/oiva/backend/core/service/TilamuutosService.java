package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.model.entity.oiva.Muutospyynto;
import fi.minedu.oiva.backend.model.entity.oiva.Tilamuutos;
import fi.minedu.oiva.backend.model.jooq.tables.pojos.MuutospyyntoTilamuutos;
import fi.minedu.oiva.backend.model.jooq.tables.records.TilamuutosRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static fi.minedu.oiva.backend.model.jooq.Tables.MUUTOSPYYNTO_TILAMUUTOS;
import static fi.minedu.oiva.backend.model.jooq.Tables.TILAMUUTOS;

@Service
public class TilamuutosService {

    @Autowired
    private DSLContext dsl;

    private SelectJoinStep<Record> baseQuery() {
        return dsl.select(TILAMUUTOS.fields()).from(TILAMUUTOS);
    }

    Optional<List<Tilamuutos>> forMuutospyynto(final Muutospyynto muutospyynto) {
        return Optional.ofNullable(null != muutospyynto ?
            baseQuery()
                    .join(MUUTOSPYYNTO_TILAMUUTOS).on(MUUTOSPYYNTO_TILAMUUTOS.TILAMUUTOS_ID.eq(TILAMUUTOS.ID))
                    .where(MUUTOSPYYNTO_TILAMUUTOS.MUUTOSPYYNTO_ID.eq(muutospyynto.getId()))
                    .fetchInto(Tilamuutos.class) : null);
    }

    Tilamuutos insertForMuutospyynto(final Long muutospyyntoId, final String vanhaTila, final String uusiTila, final String username) {
        Tilamuutos tilamuutos = new Tilamuutos(vanhaTila, uusiTila, username);
        TilamuutosRecord tilamuutosRecord = dsl.newRecord(TILAMUUTOS, tilamuutos);
        tilamuutosRecord.store();
        tilamuutos.setId(tilamuutosRecord.getId());

        dsl.newRecord(MUUTOSPYYNTO_TILAMUUTOS, new MuutospyyntoTilamuutos(muutospyyntoId, tilamuutos.getId())).store();

        return tilamuutos;
    }
}