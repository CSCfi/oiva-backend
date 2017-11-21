package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Kohde;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static fi.minedu.oiva.backend.jooq.Tables.KOHDE;

@Service
public class KohdeService {

    @Autowired
    private DSLContext dsl;

    public Collection<Kohde> getAll() { // TODO: IS THIS NEEDED?
        return dsl.select(KOHDE.fields()).from(KOHDE).fetchInto(Kohde.class);
    }
}