package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Kohde;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static fi.minedu.oiva.backend.jooq.Tables.KOHDE;

@Service
public class KohdeService {

    @Autowired
    private DSLContext dsl;

    public Collection<Kohde> getAll() {
        return dsl.select(KOHDE.fields()).from(KOHDE).fetchInto(Kohde.class);
    }

    public Map<Long, Kohde> mapAll() {
        return getAll().stream().collect(Collectors.toMap(Kohde::getId, kohde -> kohde));
    }
}