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
import java.util.UUID;

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

    public enum Muutospyyntotila {
        LUONNOS,            // KJ:n tekemä hakemus
        AVOIN,              // KJ lähettänyt hakemuksen eteenpäin
        VALMISTELUSSA,      // Esittelijä ottanut valmisteluun
        TAYDENNETTAVA,      // Esittelijä palauttanut täydennettäväksi
        PAATETTY,           // Valmis allekirjoitettu lupa
        PASSIVOITU;         // Lupa poistettu

        public static Muutospyyntotila convert(String str) {
            for (Muutospyyntotila muutospyyntotila : Muutospyyntotila.values()) {
                if (muutospyyntotila.toString().equals(str)) {
                    return muutospyyntotila;
                }
            }
            return null;
        }
    }

    public enum MuutosPaatostila {
        AVOIN,              // Esittelijä ei ole tehnyt vielä päätöstä
        HYVAKSYTTY,         // Esittelijä on hyväksynyt muutoksen
        HYLATTY,            // Esittelijä on hylännyt muutoksen
        TAYDENNETTAVA,      // Esittelijä palauttaa muutoksen täydennettäväksi
        HYV_MUUTETTUNA,     // Esittelijä on hyväksynyt muutoksen muutettuna
        PASSIVOITU;         // Muutos on muusta syystä poistettu

        public static MuutosPaatostila convert(String str) {
            for (MuutosPaatostila muutospaatostila : MuutosPaatostila.values()) {
                if (muutospaatostila.toString().equals(str)) {
                    return muutospaatostila;
                }
            }
            return null;
        }
    }

    // Muutospyyntölistaus (hakemukset) esittelijälle
    public Collection<Muutospyynto> getMuutospyynnot(Muutospyyntotila tila) {

        return dsl.select(MUUTOSPYYNTO.HAKUPVM, MUUTOSPYYNTO.VOIMASSALOPPUPVM, MUUTOSPYYNTO.VOIMASSAALKUPVM,
                MUUTOSPYYNTO.PAATOSKIERROS_ID, MUUTOSPYYNTO.TILA, MUUTOSPYYNTO.UUID,
                MUUTOSPYYNTO.JARJESTAJA_YTUNNUS, MUUTOSPYYNTO.LUOJA, MUUTOSPYYNTO.LUONTIPVM,
                MUUTOSPYYNTO.PAIVITTAJA, MUUTOSPYYNTO.PAIVITYSPVM, LUPA.DIAARINUMERO, MUUTOSPYYNTO.ID,
                LUPA.JARJESTAJA_OID, LUPA.UUID.as("lupa_uuid"))
                .from(MUUTOSPYYNTO, LUPA)
                .where( (MUUTOSPYYNTO.TILA.eq(tila.toString())) )
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
                MUUTOSPYYNTO.PAATOSKIERROS_ID, MUUTOSPYYNTO.TILA, MUUTOSPYYNTO.UUID,
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
            withLupaUuid(muutospyyntoOpt);
        }
        if(reqs.equals("esittelija")) {
            withMuutokset(muutospyyntoOpt);
            withPerustelu(muutospyyntoOpt);
            withPaatoskierros(muutospyyntoOpt);
            withOrganization(muutospyyntoOpt);
        }
        return muutospyyntoOpt;
    }

    protected Optional<Muutospyynto> withLupaUuid(final Optional<Muutospyynto> muutospyyntoOpt) {
        muutospyyntoOpt.ifPresent(muutospyynto -> getLupaUuid(muutospyynto.getLupaId()).ifPresent(muutospyynto::setLupaUuid));
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

    public Optional<String> getLupaUuid(long id) {
        return dsl.select(LUPA.UUID).from(LUPA)
                .where(LUPA.ID.eq(id)).fetchOptionalInto(String.class);
    }

    public Optional<Muutosperustelu> getPerusteluByMuutospyynto(long id) {
        return dsl.select(MUUTOSPERUSTELU.fields()).from(MUUTOSPERUSTELU)
                .where(MUUTOSPERUSTELU.MUUTOSPYYNTO_ID.eq(id)).fetchOptionalInto(Muutosperustelu.class);
    }

    public Optional<Long> getKohdeId(UUID uuid) {
        return dsl.select(KOHDE.ID).from(KOHDE)
                .where(KOHDE.UUID.equal(uuid)).fetchOptionalInto(Long.class);
    }

    public Optional<Long> getPaatoskierrosId(UUID uuid) {
        return dsl.select(PAATOSKIERROS.ID).from(PAATOSKIERROS)
                .where(PAATOSKIERROS.UUID.equal(uuid)).fetchOptionalInto(Long.class);
    }

    public Optional<Long> getLupaId(String uuid) {
        return dsl.select(LUPA.ID).from(LUPA)
                .where(LUPA.UUID.equal(UUID.fromString(uuid))).fetchOptionalInto(Long.class);
    }

    public Optional<Long> getMaaraystyyppiId(UUID uuid) {
        return dsl.select(MAARAYSTYYPPI.ID).from(MAARAYSTYYPPI)
                .where(MAARAYSTYYPPI.UUID.equal(uuid)).fetchOptionalInto(Long.class);
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

    // hakee yksittäinen muutospyynnön perusteluineen uuid:llä
    public Optional<Muutospyynto> getByUuid(String uuid) {
        return dsl.select(MUUTOSPYYNTO.fields()).from(MUUTOSPYYNTO)
                .where(MUUTOSPYYNTO.UUID.equal(UUID.fromString(uuid))).fetchOptionalInto(Muutospyynto.class)
                .map(muutospyynto -> with(Optional.ofNullable(muutospyynto),"yksi"))
                .filter(Optional::isPresent).map(Optional::get);
    }


    protected Optional<Muutospyynto> withMuutokset(final Optional<Muutospyynto> muutospyyntoOpt) {
        muutospyyntoOpt.ifPresent(muutospyynto -> muutospyynto.setMuutokset(getByMuutospyyntoId(muutospyynto.getId())));
        return muutospyyntoOpt;
    }

    protected void withOrganization(final Optional<Muutospyynto> muutospyyntoOpt) {
        muutospyyntoOpt.ifPresent(muutospyynto -> organisaatioService.getWithLocation(muutospyynto.getJarjestajaYtunnus()).ifPresent(muutospyynto::setJarjestaja));
    }

    public Optional<Long> create(final Muutospyynto muutospyynto) {

        try {
            muutospyynto.setId(null);
            final MuutospyyntoRecord muutospyyntoRecord = dsl.newRecord(MUUTOSPYYNTO, muutospyynto);
            //muutospyyntoRecord.setLuoja(SecurityUtil.userName().get());
            muutospyyntoRecord.setLuontipvm(Timestamp.from(Instant.now()));
            muutospyyntoRecord.setLupaId(getLupaId(muutospyynto.getLupaUuid()).get());
            muutospyyntoRecord.setPaatoskierrosId(getPaatoskierrosId(muutospyynto.getPaatoskierros().getUuid()).get());


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
                muutosRecord.setKohdeId(getKohdeId(muutos.getKohde().getUuid()).get());
                muutosRecord.setMaaraystyyppiId(getMaaraystyyppiId(muutos.getMaaraystyyppi().getUuid()).get());
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

    // Vaihtaa muutospyynnön tilan
    public Optional<Long> changeTila(final long id, Muutospyyntotila tila) {

        try {

            final Optional<MuutospyyntoRecord> muutospyyntoOpt = Optional.ofNullable(dsl.fetchOne(MUUTOSPYYNTO, MUUTOSPYYNTO.ID.eq(id)));

            if(muutospyyntoOpt.isPresent()) {

                MuutospyyntoRecord mp = muutospyyntoOpt.get();
                mp.setTila(tila.name());
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
                validation(muutospyynto.getTila(), "Muutospyynto tila is missing")
        );
    }

    public boolean validate(Muutos muutos) {
        return ValidationUtils.validate(
                validation(muutos.getUuid(), "Muutos: muutospyyntoUuid is missing"),
                validation(muutos.getKohdeId(), "Muutos: kohdeId is missing"),
                validation(muutos.getKoodiarvo(), "Muutos: koodiarvo is missing"),
                validation(muutos.getKoodisto(), "Muutos: koodisto is missing"),
                validation(muutos.getMaaraystyyppiId(), "Muutos: maaraystyyppiId is missing")
        );
    }

}
