package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.entity.*;
import fi.minedu.oiva.backend.jooq.tables.records.MuutosperusteluRecord;
import fi.minedu.oiva.backend.jooq.tables.records.MuutospyyntoRecord;
import fi.minedu.oiva.backend.jooq.tables.records.MuutosRecord;
import fi.minedu.oiva.backend.security.SecurityUtil;
import fi.minedu.oiva.backend.util.ValidationUtils;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static fi.minedu.oiva.backend.jooq.Tables.*;
import static fi.minedu.oiva.backend.util.ValidationUtils.validation;

@Service
public class MuutospyyntoService {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private AuthService authService;
    // TODO: lisää autentikointitarkistukset

    @Autowired
    private OrganisaatioService organisaatioService;

    private enum Muutospyyntotila {
        LUONNOS,
        VALMIINA_KASITTELYYN,
        KASITTELYSSA,
        TAYDENNETTAVA,
        VALMIS,
        PASSIVOITU;
    }

    // Muutospyyntölistaus (hakemukset) esittelijälle
    public Collection<Muutospyynto> getByEsittelija(String nimi) {

        return dsl.select(MUUTOSPYYNTO.HAKUPVM, MUUTOSPYYNTO.VOIMASSALOPPUPVM, MUUTOSPYYNTO.VOIMASSAALKUPVM,
                MUUTOSPYYNTO.PAATOSKIERROS_ID, MUUTOSPYYNTO.TILA, MUUTOSPYYNTO.UUID,
                MUUTOSPYYNTO.JARJESTAJA_YTUNNUS, MUUTOSPYYNTO.LUOJA, MUUTOSPYYNTO.LUONTIPVM,
                MUUTOSPYYNTO.PAIVITTAJA, MUUTOSPYYNTO.PAIVITYSPVM, LUPA.DIAARINUMERO, MUUTOSPYYNTO.ID,
                LUPA.JARJESTAJA_OID, LUPA.ID.as("lupa_id"))
                .from(MUUTOSPYYNTO, LUPA)
                .where( (MUUTOSPYYNTO.LUOJA.eq(nimi)).or(MUUTOSPYYNTO.PAIVITTAJA.eq(nimi)) )
                .and(MUUTOSPYYNTO.LUPA_ID.eq(LUPA.ID))
                .orderBy(MUUTOSPYYNTO.HAKUPVM).fetchInto(Muutospyynto.class)
                .stream()
                .map(muutospyynto -> with(Optional.ofNullable(muutospyynto),"esittelija"))
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
    }

    // Muutospyyntölistaus (hakemukset) koulutuksen järjestäjälle
    public Collection<Muutospyynto> getByYtunnus(String ytunnus) {

        return dsl.select(MUUTOSPYYNTO.HAKUPVM, MUUTOSPYYNTO.VOIMASSALOPPUPVM, MUUTOSPYYNTO.VOIMASSAALKUPVM,
                MUUTOSPYYNTO.PAATOSKIERROS_ID, MUUTOSPYYNTO.TILA,
                MUUTOSPYYNTO.JARJESTAJA_YTUNNUS, MUUTOSPYYNTO.LUOJA, MUUTOSPYYNTO.LUONTIPVM,
                MUUTOSPYYNTO.PAIVITTAJA, MUUTOSPYYNTO.PAIVITYSPVM, LUPA.DIAARINUMERO, MUUTOSPYYNTO.ID, LUPA.ID.as("lupa_id"))
                .from(MUUTOSPYYNTO, LUPA)
                .where(MUUTOSPYYNTO.JARJESTAJA_YTUNNUS.eq(ytunnus))
                .and(MUUTOSPYYNTO.LUPA_ID.eq(LUPA.ID))
                .orderBy(MUUTOSPYYNTO.HAKUPVM).fetchInto(Muutospyynto.class)
                .stream()
                .map(muutospyynto -> with(Optional.ofNullable(muutospyynto),"listaus"))
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
    }

    protected Optional<Muutospyynto> with(final Optional<Muutospyynto> muutospyyntoOpt, String reqs) {
        if(reqs.equals("listaus")) { withPerustelu(muutospyyntoOpt); withPaatoskierros(muutospyyntoOpt); }
        if(reqs.equals("yksi")) {
            withMuutokset(muutospyyntoOpt);
            withPerustelu(muutospyyntoOpt);
            withPaatoskierros(muutospyyntoOpt);
        }
        if(reqs.equals("esittelija")) {
            withMuutokset(muutospyyntoOpt);
            withPerustelu(muutospyyntoOpt);
            withPaatoskierros(muutospyyntoOpt);
            withOrganization(muutospyyntoOpt);
        }
        return muutospyyntoOpt;
    }

    protected Optional<Muutospyynto> withPerustelu(final Optional<Muutospyynto> muutospyyntoOpt) {
        muutospyyntoOpt.ifPresent(muutospyynto -> getPerusteluByMuutospyynto(muutospyynto.getId()).ifPresent(muutospyynto::setMuutosperustelu));
        return muutospyyntoOpt;
    }

    protected Optional<Muutospyynto> withPaatoskierros(final Optional<Muutospyynto> muutospyyntoOpt) {
        muutospyyntoOpt.ifPresent(muutospyynto -> getPaatoskierrosByMuutospyynto(muutospyynto.getPaatoskierrosId()).ifPresent(muutospyynto::setPaatoskierros));
        return muutospyyntoOpt;
    }

    public Optional<Muutosperustelu> getPerusteluByMuutospyynto(long id) {
        return dsl.select(MUUTOSPERUSTELU.fields()).from(MUUTOSPERUSTELU)
                .where(MUUTOSPERUSTELU.MUUTOSPYYNTO_ID.eq(id)).fetchOptionalInto(Muutosperustelu.class);
    }

    public Optional<Paatoskierros> getPaatoskierrosByMuutospyynto(long id) {
        return dsl.select(PAATOSKIERROS.fields()).from(PAATOSKIERROS)
                .where(PAATOSKIERROS.ID.eq(id)).fetchOptionalInto(Paatoskierros.class);
    }

    // hakee yksittäinen muutospyynnön perusteluineen
    public Optional<Muutospyynto> getById(long id) {
        return dsl.select(MUUTOSPYYNTO.fields()).from(MUUTOSPYYNTO)
                .where(MUUTOSPYYNTO.ID.eq(id)).fetchOptionalInto(Muutospyynto.class)
                .map(muutospyynto -> with(Optional.ofNullable(muutospyynto),"yksi"))
                .filter(Optional::isPresent).map(Optional::get);
    }

    protected Optional<Muutospyynto> withMuutokset(final Optional<Muutospyynto> muutospyyntoOpt) {
        muutospyyntoOpt.ifPresent(muutospyynto -> muutospyynto.setMuutokset(getByMuutospyyntoId(muutospyynto.getId())));
        return muutospyyntoOpt;
    }

    protected void withOrganization(final Optional<Muutospyynto> muutospyyntoOpt) {
        muutospyyntoOpt.ifPresent(muutospyynto -> organisaatioService.getWithLocation(muutospyynto.getJarjestajaOid()).ifPresent(muutospyynto::setJarjestaja));
    }


    public Optional<Long> create(final Muutospyynto muutospyynto) {

        try {
            muutospyynto.setId(null);
            final MuutospyyntoRecord muutospyyntoRecord = dsl.newRecord(MUUTOSPYYNTO, muutospyynto);
            //muutospyyntoRecord.setLuoja(SecurityUtil.userName().get());
            muutospyyntoRecord.setLuontipvm(Timestamp.from(Instant.now()));
            muutospyyntoRecord.store();

            final MuutosperusteluRecord muutosperusteluRecord = dsl.newRecord(MUUTOSPERUSTELU, muutospyynto.getMuutosperustelu());
            muutosperusteluRecord.setMuutospyyntoId(muutospyyntoRecord.getId());
            //muutosperusteluRecord.setLuoja(SecurityUtil.userName().get());
            muutosperusteluRecord.setLuontipvm(Timestamp.from(Instant.now()));
            muutosperusteluRecord.store();

            muutospyynto.getMuutokset().stream().forEach(muutos -> {
                final MuutosRecord muutosRecord = dsl.newRecord(MUUTOS, muutos);
                //muutosRecord.setLuoja(SecurityUtil.userName().get());
                muutosRecord.setLuontipvm(Timestamp.from(Instant.now()));
                muutosRecord.setMuutospyyntoId(muutospyyntoRecord.getId());
                muutosRecord.store();
            });

            return Optional.of(muutospyyntoRecord.getId());

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


    // Hakee muutospyyntöön liittyvät muutokset
    public Collection<Muutos> getByMuutospyyntoId(long muutospyynto_id) {
        return dsl.select(MUUTOS.fields()).from(MUUTOS)
                .where(MUUTOS.MUUTOSPYYNTO_ID.eq(muutospyynto_id))
                .fetchInto(Muutos.class);
    }

    // Hakee yksittäisen muutoksen
    public Optional<Muutos> getMuutosById(long id) {

        return dsl.select(MUUTOS.fields()).from(MUUTOS)
                .where(MUUTOS.ID.eq(id)).fetchOptionalInto(Muutos.class);
    }

    // Luo muutoksen
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

    // Päivittää muutosta
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

    // Passivoi muutospyynnön
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

    // Passivoi muutospyynnön
    public Optional<Long> changeTila(final long id, String tila) {

        try {

            final Optional<MuutospyyntoRecord> muutospyyntoOpt = Optional.ofNullable(dsl.fetchOne(MUUTOSPYYNTO, MUUTOSPYYNTO.ID.eq(id)));

            if(muutospyyntoOpt.isPresent()) {

                MuutospyyntoRecord mp = muutospyyntoOpt.get();
                if(tila.equals("valmiina_kasittelyyn")) { mp.setTila(Muutospyyntotila.VALMIINA_KASITTELYYN.name()); }
                if(tila.equals("kasittelyssa")) { mp.setTila(Muutospyyntotila.KASITTELYSSA.name()); }
                if(tila.equals("palauta_taydennettavaksi")) { mp.setTila(Muutospyyntotila.TAYDENNETTAVA.name()); }
                if(tila.equals("valmis")) { mp.setTila(Muutospyyntotila.VALMIS.name()); }
                dsl.executeUpdate(mp);

            }

            return Optional.ofNullable(muutospyyntoOpt.get().getId());

        } catch(Exception e) {
            return Optional.empty();
        }

    }

    // VALIDOINNIT

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
