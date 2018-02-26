package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.Muutospyynto;
import fi.minedu.oiva.backend.jooq.tables.Muutosperustelu;
import fi.minedu.oiva.backend.jooq.tables.pojos.Muutos;
import fi.minedu.oiva.backend.jooq.tables.records.MuutospyyntoRecord;
import fi.minedu.oiva.backend.jooq.tables.records.MuutosRecord;
import fi.minedu.oiva.backend.security.SecurityUtil;
import fi.minedu.oiva.backend.util.ValidationUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

import static fi.minedu.oiva.backend.jooq.Tables.*;
import static fi.minedu.oiva.backend.util.ValidationUtils.validation;

@Service
public class MuutospyyntoService {

    @Autowired
    private DSLContext dsl;

    private enum Muutospyyntotila {
        LUONNOS,
        KASITTELYSSA,
        VALMIS,
        PASSIVOITU;
    }

    public Collection<Muutospyynto> getByYtunnus(String ytunnus) {

        return dsl.select(MUUTOSPYYNTO.HAKUPVM, MUUTOSPYYNTO.VOIMASSALOPPUPVM, MUUTOSPYYNTO.VOIMASSAALKUPVM,
                MUUTOSPYYNTO.PAATOSKIERROS_ID, MUUTOSPYYNTO.MUUTOSPERUSTELU_ID, MUUTOSPYYNTO.TILA,
                MUUTOSPYYNTO.JARJESTAJA_YTUNNUS, MUUTOSPYYNTO.LUOJA, MUUTOSPYYNTO.LUONTIPVM,
                MUUTOSPYYNTO.PAIVITTAJA, MUUTOSPYYNTO.PAIVITYSPVM, LUPA.DIAARINUMERO).from(MUUTOSPYYNTO,LUPA)
                .where(MUUTOSPYYNTO.JARJESTAJA_YTUNNUS.eq(ytunnus))
                .and(MUUTOSPYYNTO.LUPA_ID.eq(LUPA.ID))
                .orderBy(MUUTOSPYYNTO.HAKUPVM).fetchInto(Muutospyynto.class);
    }

    public Collection<Muutos> getByMuutospyyntoId(long muutospyynto_id) {

        return dsl.select(MUUTOS.fields()).from(MUUTOS)
                .where(MUUTOS.MUUTOSPYYNTO_ID.eq(muutospyynto_id))
                .fetchInto(Muutos.class);
    }

    public Collection<Muutosperustelu> getAll() {

        return dsl.select(MUUTOSPERUSTELU.fields()).from(MUUTOSPERUSTELU)
                .fetchInto(Muutosperustelu.class);
    }


    public Optional<Muutospyynto> getById(long id) {
        return dsl.select(MUUTOSPYYNTO.fields()).from(MUUTOSPYYNTO)
                .where(MUUTOSPYYNTO.ID.eq(id)).fetchOptionalInto(Muutospyynto.class);
    }

    public Optional<Muutos> getMuutosById(long id) {

        return dsl.select(MUUTOS.fields()).from(MUUTOS)
                .where(MUUTOS.ID.eq(id)).fetchOptionalInto(Muutos.class);
    }

    public Optional<Muutosperustelu> getPerusteluById(long id) {
        return dsl.select(MUUTOSPERUSTELU.fields()).from(MUUTOSPERUSTELU)
                .where(MUUTOSPERUSTELU.ID.eq(id)).fetchOptionalInto(Muutosperustelu.class);
    }


    public Optional<Long> create(final Muutospyynto muutospyynto) {

        try {
            muutospyynto.setId(null);
            final MuutospyyntoRecord muutospyyntoRecord = dsl.newRecord(MUUTOSPYYNTO, muutospyynto);
            //muutospyyntoRecord.setLuoja(SecurityUtil.userName().get());
            muutospyyntoRecord.setLuontipvm(Timestamp.from(Instant.now()));
            muutospyyntoRecord.store();

            return Optional.of(muutospyyntoRecord.getId());

        } catch(Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Long> createMuutos(final Muutos muutos) {

        try {
            muutos.setId(null);
            final MuutosRecord muutosRecord = dsl.newRecord(MUUTOS, muutos);
            //muutosRecord.setLuoja(SecurityUtil.userName().get());
            muutosRecord.setLuontipvm(Timestamp.from(Instant.now()));
            muutosRecord.store();

            return Optional.of(muutosRecord.getId());

        } catch(Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Long> update(final Muutospyynto muutospyynto) {

        try {
            final MuutospyyntoRecord muutospyyntoRecord = dsl.newRecord(MUUTOSPYYNTO, muutospyynto);
            //muutospyyntoRecord.setPaivittaja(SecurityUtil.userName().get());
            muutospyyntoRecord.setPaivityspvm(Timestamp.from(Instant.now()));
            dsl.executeUpdate(muutospyyntoRecord);
            return Optional.of(muutospyyntoRecord.getId());

        } catch(Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Long> updateMuutos(final Muutos muutos) {

        try {
            final MuutosRecord muutosRecord = dsl.newRecord(MUUTOS, muutos);
            //muutosRecord.setPaivittaja(SecurityUtil.userName().get());
            muutosRecord.setPaivityspvm(Timestamp.from(Instant.now()));
            dsl.executeUpdate(muutosRecord);
            return Optional.of(muutosRecord.getId());

        } catch(Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Long> passivoi(final long id) {

        try {

            final Optional<MuutospyyntoRecord> muutospyyntoOpt = Optional.ofNullable(dsl.fetchOne(MUUTOSPYYNTO, MUUTOSPYYNTO.ID.eq(id)));

            if(muutospyyntoOpt.isPresent()) {

                MuutospyyntoRecord mp = muutospyyntoOpt.get();
                mp.setTila(Muutospyyntotila.PASSIVOITU.name());
                dsl.executeUpdate(mp);
            }

            return Optional.empty();

        } catch(Exception e) {
            return Optional.empty();
        }

    }

    public boolean validate(Muutospyynto muutospyynto) {
        return ValidationUtils.validate(
                validation(muutospyynto.getLupaId(), "Muutospyyntö lupaId is missing"),
                validation(muutospyynto.getPaatoskierrosId(), "PaatoskierrosId is missing"),
                validation(muutospyynto.getTila(), "Muutospyynto tila is missing")
        );
    }

    public boolean validate(Muutos muutos) {
        return ValidationUtils.validate(
                validation(muutos.getMuutospyyntoId(), "Muutos: muutospyyntoId is missing"),
                validation(muutos.getKohdeId(), "Muutos: kohdeId is missing"),
                validation(muutos.getKoodiarvo(), "Muutos: koodiarvo is missing"),
                validation(muutos.getKoodisto(), "Muutos: koodisto is missing"),
                validation(muutos.getMaaraystyyppiId(), "Muutos: maaraystyyppiId is missing")
        );
    }

}
