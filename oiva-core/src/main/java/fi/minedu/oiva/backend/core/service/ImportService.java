package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.oiva.Maarays;
import fi.minedu.oiva.backend.model.jooq.tables.records.LupaRecord;
import fi.minedu.oiva.backend.model.jooq.tables.records.MaaraysRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static fi.minedu.oiva.backend.model.jooq.Tables.MAARAYS;
import static fi.minedu.oiva.backend.model.jooq.tables.Lupa.LUPA;
import static java.util.Optional.ofNullable;
import static java.util.Optional.empty;

@Service
public class ImportService {

    private final static String importerName = "Importer";

    @Autowired
    private DSLContext dsl;

    @Autowired
    private LupaService lupaService;

    @Autowired
    private MaaraysService maaraysService;

    @Autowired
    private PaatoskierrosService paatoskierrosService;

    @Autowired
    private AsiatyyppiService asiatyyppiService;

    @Autowired
    private LupatilaService lupatilaService;

    @Autowired
    private KohdeService kohdeService;

    @Autowired
    private MaaraystyyppiService maaraystyyppiService;

    public List<String> importEntity(final Lupa entity) {
        final List<String> results = new ArrayList<>();
        final BiConsumer<Class, Long> includeResult = (clazz, id) -> results.add(clazz.getSimpleName() + ":" + id);

        importLupa(entity).ifPresent(lupa -> {
            includeResult.accept(Lupa.class, lupa.getId());
            entity.getMaaraykset().stream().forEach(maaraysEntity -> {
                maaraysEntity.setLupaId(lupa.getId());
                importMaarays(maaraysEntity).ifPresent(maarays -> {
                    includeResult.accept(Maarays.class, maarays.getId());
                    ofNullable(maaraysEntity.getAliMaaraykset()).ifPresent(alimaaraykset ->
                        alimaaraykset.stream().forEach(alimaaraysEntity -> {
                            alimaaraysEntity.setParentId(maarays.getId());
                            importMaarays(alimaaraysEntity).ifPresent(alimaarays -> includeResult.accept(Maarays.class, alimaarays.getId()));
                        })
                    );
                });
            });
        });
        return results;
    }

    protected Optional<Lupa> importLupa(final Lupa entity) {
        if(null != entity) {
            entity.setId(null);
            entity.setUuid(null);
            entity.setLuoja(importerName);
            entity.setLuontipvm(Timestamp.from(Instant.now()));
            ofNullable(entity.getPaatoskierros()).ifPresent(paatoskierros -> paatoskierrosService.idForUuid(paatoskierros.getUuid()).ifPresent(entity::setPaatoskierrosId));
            ofNullable(entity.getAsiatyyppi()).ifPresent(asiatyyppi -> asiatyyppiService.idForUuid(asiatyyppi.getUuid()).ifPresent(entity::setAsiatyyppiId));
            ofNullable(entity.getLupatila()).ifPresent(lupatila -> lupatilaService.idForUuid(lupatila.getUuid()).ifPresent(entity::setLupatilaId));

            final LupaRecord record = dsl.newRecord(LUPA, entity);
            record.store();

            return lupaService.getById(record.getId());
        } return empty();
    }

    protected Optional<Maarays> importMaarays(final Maarays entity) {
        if(null != entity) {
            entity.setId(null);
            entity.setUuid(null);
            entity.setLuoja(importerName);
            entity.setLuontipvm(Timestamp.from(Instant.now()));
            ofNullable(entity.getKohde()).ifPresent(kohde -> kohdeService.idForTunniste(kohde.getTunniste()).ifPresent(entity::setKohdeId));
            ofNullable(entity.getMaaraystyyppi()).ifPresent(maaraystyyppi -> maaraystyyppiService.idForUuid(maaraystyyppi.getUuid()).ifPresent(entity::setMaaraystyyppiId));

            final MaaraysRecord record = dsl.newRecord(MAARAYS, entity);
            record.store();

            return maaraysService.getById(record.getId());
        } return empty();
    }
}