package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Maarays;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static fi.minedu.oiva.backend.jooq.Tables.MAARAYS;

@Service
public class MaaraysService {

    @Autowired
    private DSLContext dsl;

    public Collection<Maarays> getAll() {
        return dsl.select(MAARAYS.fields()).from(MAARAYS).fetchInto(Maarays.class);
    }

    public Collection<Maarays> getByLupa(final Long lupaId) {
        return dsl.select(MAARAYS.fields()).from(MAARAYS).where(MAARAYS.LUPA_ID.eq(lupaId)).fetchInto(Maarays.class);
    }
}