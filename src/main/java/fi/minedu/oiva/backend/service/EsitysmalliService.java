package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Esitysmalli;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static fi.minedu.oiva.backend.jooq.Tables.ESITYSMALLI;

@Service
public class EsitysmalliService {

    @Autowired
    private DSLContext dsl;

    public Collection<Esitysmalli> getAll() { // TODO: IS THIS NEEDED?
        return dsl.select(ESITYSMALLI.fields()).from(ESITYSMALLI).fetchInto(Esitysmalli.class);
    }
}