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

    //@Cacheable(value = {"EsitysmalliService:getAll"}, key = "''")
    public Collection<Esitysmalli> getAll() {
        return dsl.select(ESITYSMALLI.fields()).from(ESITYSMALLI).fetchInto(Esitysmalli.class);
    }
}