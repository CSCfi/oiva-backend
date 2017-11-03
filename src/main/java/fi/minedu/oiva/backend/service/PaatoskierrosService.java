package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Paatoskierros;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static fi.minedu.oiva.backend.jooq.Tables.PAATOSKIERROS;

@Service
public class PaatoskierrosService {

    @Autowired
    private DSLContext dsl;

    @Cacheable(value = {"PaatoskierrosService:getAll"}, key = "''")
    public Collection<Paatoskierros> getAll() {
        return dsl.select(PAATOSKIERROS.fields()).from(PAATOSKIERROS).fetchInto(Paatoskierros.class);
    }
}