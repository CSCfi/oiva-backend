package fi.minedu.oiva.backend.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fi.minedu.oiva.backend.core.exception.ForbiddenException;
import fi.minedu.oiva.backend.core.exception.ResourceNotFoundException;
import fi.minedu.oiva.backend.core.security.OivaPermission;
import fi.minedu.oiva.backend.core.util.ValidationUtils;
import fi.minedu.oiva.backend.model.entity.json.ObjectMapperSingleton;
import fi.minedu.oiva.backend.model.entity.oiva.Kohde;
import fi.minedu.oiva.backend.model.entity.oiva.Liite;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.oiva.Maaraystyyppi;
import fi.minedu.oiva.backend.model.entity.oiva.Muutos;
import fi.minedu.oiva.backend.model.entity.oiva.Muutospyynto;
import fi.minedu.oiva.backend.model.entity.oiva.Paatoskierros;
import fi.minedu.oiva.backend.model.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.model.jooq.tables.pojos.MuutosLiite;
import fi.minedu.oiva.backend.model.jooq.tables.pojos.MuutospyyntoLiite;
import fi.minedu.oiva.backend.model.jooq.tables.records.MuutosLiiteRecord;
import fi.minedu.oiva.backend.model.jooq.tables.records.MuutosRecord;
import fi.minedu.oiva.backend.model.jooq.tables.records.MuutospyyntoLiiteRecord;
import fi.minedu.oiva.backend.model.jooq.tables.records.MuutospyyntoRecord;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectOnConditionStep;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ValidationException;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fi.minedu.oiva.backend.core.util.ValidationUtils.validation;
import static fi.minedu.oiva.backend.model.jooq.Tables.KOHDE;
import static fi.minedu.oiva.backend.model.jooq.Tables.LIITE;
import static fi.minedu.oiva.backend.model.jooq.Tables.LUPA;
import static fi.minedu.oiva.backend.model.jooq.Tables.MAARAYSTYYPPI;
import static fi.minedu.oiva.backend.model.jooq.Tables.MUUTOS;
import static fi.minedu.oiva.backend.model.jooq.Tables.MUUTOSPYYNTO;
import static fi.minedu.oiva.backend.model.jooq.Tables.MUUTOSPYYNTO_LIITE;
import static fi.minedu.oiva.backend.model.jooq.Tables.MUUTOS_LIITE;
import static fi.minedu.oiva.backend.model.jooq.Tables.PAATOSKIERROS;

@Service
public class MuutospyyntoService {

    // Default value for paatokierros
    private static final Long paatoskierrosDefaultId = 19L;
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final DSLContext dsl;

    private final AuthService authService;
    // TODO: lisää autentikointitarkistukset

    private final OrganisaatioService organisaatioService;
    private final LiiteService liiteService;
    private final OpintopolkuService opintopolkuService;
    private final MaaraysService maaraysService;
    private final FileStorageService fileStorageService;
    private final AsiatilamuutosService asiatilamuutosService;
    private final LupaService lupaService;

    @Autowired
    public MuutospyyntoService(DSLContext dsl, AuthService authService, OrganisaatioService organisaatioService,
                               LiiteService liiteService, OpintopolkuService opintopolkuService,
                               MaaraysService maaraysService, FileStorageService fileStorageService,
                               AsiatilamuutosService asiatilamuutosService, LupaService lupaService) {
        this.dsl = dsl;
        this.authService = authService;
        this.organisaatioService = organisaatioService;
        this.liiteService = liiteService;
        this.opintopolkuService = opintopolkuService;
        this.maaraysService = maaraysService;
        this.fileStorageService = fileStorageService;
        this.asiatilamuutosService = asiatilamuutosService;
        this.lupaService = lupaService;
    }

    public enum Action {
        LUO,
        TALLENNA,
        LAHETA,
        OTA_KASITTELYYN
    }

    public enum Muutospyyntotila {
        LUONNOS,            // KJ:n tekemä hakemus
        AVOIN,              // KJ lähettänyt hakemuksen eteenpäin
        VALMISTELUSSA,      // Esittelijä ottanut valmisteluun
        TAYDENNETTAVA,      // Esittelijä palauttanut täydennettäväksi
        PAATETTY,           // Valmis allekirjoitettu lupa
        PASSIVOITU;         // Lupa poistettu
    }

    public enum MuutosPaatostila {
        AVOIN,              // Esittelijä ei ole tehnyt vielä päätöstä
        HYVAKSYTTY,         // Esittelijä on hyväksynyt muutoksen
        HYLATTY,            // Esittelijä on hylännyt muutoksen
        TAYDENNETTAVA,      // Esittelijä palauttaa muutoksen täydennettäväksi
        HYV_MUUTETTUNA,     // Esittelijä on hyväksynyt muutoksen muutettuna
        PASSIVOITU;         // Muutos on muusta syystä poistettu
    }

    // hakee yksittäinen muutospyynnön perusteluineen
    public Optional<Muutospyynto> getById(Long id) {
        return dsl.select(MUUTOSPYYNTO.fields()).from(MUUTOSPYYNTO)
                .where(MUUTOSPYYNTO.ID.eq(id)).fetchOptionalInto(Muutospyynto.class)
                .map(muutospyynto -> with(muutospyynto, "yksi"))
                .filter(Optional::isPresent).map(Optional::get);
    }

    // Muutospyyntölistaus (hakemukset) esittelijälle
    public Collection<Muutospyynto> getMuutospyynnot(Muutospyyntotila tila, boolean vainOmat) {
        return getBaseSelect()
                .where(baseFilter())
                .and(MUUTOSPYYNTO.TILA.eq(tila.toString()))
                .orderBy(MUUTOSPYYNTO.HAKUPVM).fetchInto(Muutospyynto.class)
                .stream()
                .map(muutospyynto -> with(muutospyynto, "esittelija"))
                .filter(Optional::isPresent).map(Optional::get)
                // Filter only own if param is set. Own is defined by having any change made by user
                .filter(m -> !vainOmat || m.getAsiatilamuutokset().stream().anyMatch(t -> authService.getUsername().equals(t.getKayttajatunnus())))
                .collect(Collectors.toList());
    }

    // Muutospyyntölistaus (hakemukset) koulutuksen järjestäjälle
    public Collection<Muutospyynto> getByYtunnus(String ytunnus) {
        return getBaseSelect()
                .where(baseFilter())
                .and(MUUTOSPYYNTO.JARJESTAJA_YTUNNUS.eq(ytunnus))
                .orderBy(MUUTOSPYYNTO.HAKUPVM).fetchInto(Muutospyynto.class)
                .stream()
                .map(muutospyynto -> with(muutospyynto, "listaus"))
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Return all liite objects which are related to requested muutospyynto.
     *
     * @param muutospyyntoUuid UUID of muutospyynto
     * @return Collection of liite objects.
     */
    public Optional<Collection<Liite>> getLiitteetByUuid(String muutospyyntoUuid) {
        return getByUuid(muutospyyntoUuid).map(m -> {
            final List<Liite> liiteList = new ArrayList<>(m.getLiitteet());
            liiteList.addAll(m.getMuutokset().stream()
                    .flatMap(this::getAlimaaraykset)
                    .flatMap(muutos -> Stream.concat(muutos.getLiitteet().stream(),
                            getMetaLiitteet(muutos.getMeta()).stream()))
                    .collect(Collectors.toList()));
            return liiteList;
        });
    }

    private Stream<Muutos> getAlimaaraykset(Muutos muutos) {
        return Stream.concat(Stream.of(muutos), muutos.getAliMaaraykset().stream().flatMap(this::getAlimaaraykset));
    }

    private Optional<Muutospyynto> luo(final Muutospyynto muutospyynto, final Map<String, MultipartFile> fileMap) {
        assertValid(muutospyynto);
        if (!lupaAndMuutospyyntoOrganizationsMatch(muutospyynto) ||
                !authService.hasAnyRole(OivaAccess.Role_Kayttaja, OivaAccess.Role_Nimenkirjoittaja) ||
                !userOidMatchMuutospyynto(muutospyynto)) {
            throw new ForbiddenException("User has no right");
        }
        muutospyynto.setTila(Muutospyyntotila.LUONNOS.name());
        return save(muutospyynto, fileMap).flatMap(m -> getById(m.getId()));
    }

    private Optional<Muutospyynto> tallenna(final Muutospyynto muutospyynto, final Map<String, MultipartFile> fileMap) {
        assertValid(muutospyynto);
        Muutospyynto existing = getByUuid(muutospyynto.getUuid().toString()).orElseThrow(() -> new ResourceNotFoundException("Muutospyynto is not found with uuid " + muutospyynto.getUuid()));
        if (!Muutospyyntotila.LUONNOS.toString().equals(existing.getTila())) {
            throw new ForbiddenException("Action is not allowed");
        }

        if (!lupaAndMuutospyyntoOrganizationsMatch(existing) ||
                !authService.hasAnyRole(OivaAccess.Role_Kayttaja, OivaAccess.Role_Nimenkirjoittaja) ||
                !userOidMatchMuutospyynto(existing) ||
                !userOidMatchMuutospyynto(muutospyynto)) {
            throw new ForbiddenException("User has no right");
        }

        String uusiTila = Muutospyyntotila.LUONNOS.name();
        asiatilamuutosService.insertForMuutospyynto(existing.getId(), muutospyynto.getTila(), uusiTila, authService.getUsername());
        muutospyynto.setTila(uusiTila);
        return update(muutospyynto, fileMap).flatMap(m -> getById(m.getId()));
    }

    private Optional<Muutospyynto> laheta(String uuid) {
        Muutospyynto mp = getByUuid(uuid).orElseThrow(() -> new ResourceNotFoundException("Muutospyynto is not found with uuid " + uuid));
        if (!Muutospyyntotila.LUONNOS.toString().equals(mp.getTila())) {
            throw new ForbiddenException("Action is not allowed");
        }
        if (!userOidMatchMuutospyynto(mp) || !authService.hasAnyRole(OivaAccess.Role_Nimenkirjoittaja)) {
            throw new ForbiddenException("User has no right");
        }
        findMuutospyyntoAndSetTila(uuid, Muutospyyntotila.AVOIN);
        fileStorageService.writeHakemusPDF(mp);
        return getByUuid(uuid);
    }

    private Optional<Muutospyynto> otaKasittelyyn(String uuid) {
        Muutospyynto mp = getByUuid(uuid).orElseThrow(() -> new ResourceNotFoundException("Muutospyynto is not found with uuid " + uuid));
        if (!Muutospyyntotila.AVOIN.toString().equals(mp.getTila())) {
            throw new ForbiddenException("Action is not allowed");
        }
        if (!authService.hasAnyRole(OivaAccess.Role_Esittelija)) {
            throw new ForbiddenException("User has no right");
        }
        return findMuutospyyntoAndSetTila(uuid, Muutospyyntotila.VALMISTELUSSA)
                .flatMap(uuid_ -> getByUuid(uuid_.toString()));
    }

    // Check that muutospyynto and lupa organizations match each other
    private boolean lupaAndMuutospyyntoOrganizationsMatch(final Muutospyynto toBeSaved) {
        return lupaService.getByUuid(toBeSaved.getLupaUuid()).map(l -> l.getJarjestajaYtunnus().equals(toBeSaved.getJarjestajaYtunnus())).orElse(false);
    }

    // Check that user is in same org than muutospyynto
    private boolean userOidMatchMuutospyynto(final Muutospyynto muutospyynto) {
        return lupaService.getByUuid(muutospyynto.getLupaUuid()).map(l -> l.getJarjestajaOid().equals(authService.getUserOrganisationOid())).orElse(false);
    }

    public Optional<Muutospyynto> executeAction(String uuid, Action action) {
        return executeAction(uuid, action, null, null);
    }

    @Transactional
    public Optional<Muutospyynto> executeAction(String uuid, Action action, Muutospyynto muutospyynto, final Map<String, MultipartFile> fileMap) {
        try {
            logger.info("Executing muutospyynto action " + action);

            switch (action) {
                case LUO:
                    return luo(muutospyynto, fileMap);
                case TALLENNA:
                    return tallenna(muutospyynto, fileMap);
                case LAHETA:
                    return laheta(uuid);
                case OTA_KASITTELYYN:
                    return otaKasittelyyn(uuid);
                default:
                    throw new UnsupportedOperationException("Action " + action + " is not supported for muutospyynto");
            }
        } catch (Exception e) {
            logger.warn("Error executing muutospyynto " + action, e);
            throw e;
        }
    }

    /**
     * Find muutospyynto based on uuid and set its tila
     * @param uuid
     * @param tila
     * @return UUID of muutospyynto or empty
     */
    private Optional<UUID> findMuutospyyntoAndSetTila(final String uuid, Muutospyyntotila tila) {
        try {
            final Optional<MuutospyyntoRecord> muutospyyntoOpt =
                    Optional.ofNullable(dsl.fetchOne(MUUTOSPYYNTO, MUUTOSPYYNTO.UUID.equal(UUID.fromString(uuid))));
            if (muutospyyntoOpt.isPresent()) {
                MuutospyyntoRecord mp = muutospyyntoOpt.get();
                asiatilamuutosService.insertForMuutospyynto(mp.getId(), mp.getTila(), tila.name(), authService.getUsername());
                mp.setTila(tila.name());
                dsl.executeUpdate(mp);
                return Optional.ofNullable(muutospyyntoOpt.get().getUuid());
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to change muutospyyntotila!", e);
            return Optional.empty();
        }
    }

    // VALIDOINNIT
    private void assertValid(Muutospyynto muutospyynto) {
        boolean isValid = muutospyynto != null &&
                Optional.ofNullable(muutospyynto.getLiitteet())
                        .map(liitteet -> liitteet.stream().allMatch(this::validate)).orElse(true) &&
                Optional.ofNullable(muutospyynto.getMuutokset())
                        .map(muutokset -> muutokset.stream().allMatch(this::validate)).orElse(true);

        if (!isValid) {
            throw new ValidationException("Invalid object");
        }
    }

    // hakee yksittäinen muutospyynnön perusteluineen uuid:llä
    public Optional<Muutospyynto> getByUuid(String uuid) {
        return getBaseSelect()
                .where(baseFilter())
                .and(MUUTOSPYYNTO.UUID.equal(UUID.fromString(uuid)))
                .fetchOptionalInto(Muutospyynto.class)
                .map(muutospyynto -> with(muutospyynto, "yksi"))
                .filter(Optional::isPresent).map(Optional::get);
    }

    // Hakee muutospyyntöön liittyvät muutokset
    public Collection<Muutos> getByMuutospyyntoUuid(String muutospyynto_uuid) {
        Optional<Muutospyynto> muutospyyntoOpt = getBaseSelect()
                .where(baseFilter())
                .and(MUUTOSPYYNTO.UUID.equal(UUID.fromString(muutospyynto_uuid)))
                .fetchOptionalInto(Muutospyynto.class);
        if (!muutospyyntoOpt.isPresent()) {
            return Collections.emptyList();
        }
        return getByMuutospyyntoId(muutospyyntoOpt.get().getId());
    }

    // Hakee yksittäisen muutoksen
    public Optional<Muutos> getMuutosByUuId(String uuid) {
        return dsl.select(MUUTOS.fields()).from(MUUTOS)
                .where(MUUTOS.UUID.eq(UUID.fromString(uuid))).fetchOptionalInto(Muutos.class);
    }

    public Optional<Muutos> getMuutosById(Long id) {
        return dsl.select(MUUTOS.fields()).from(MUUTOS)
                .where(MUUTOS.ID.eq(id)).fetchOptionalInto(Muutos.class);
    }

    private Optional<Muutospyynto> save(final Muutospyynto muutospyynto, final Map<String, MultipartFile> fileMap) {
        logger.debug("Save muutospyynto: {}", muutospyynto.toString());
        try {
            Long paatoskierrosId = getPaatoskierrosId(muutospyynto);
            return createMuutospyynto(muutospyynto, paatoskierrosId, fileMap);
        } catch (Exception e) {
            throw new DataAccessException("Failed to save muutospyynto!", e);
        }
    }

    @Transactional
    public Optional<Muutospyynto> update(final Muutospyynto muutospyynto, final Map<String, MultipartFile> fileMap) {
        logger.debug("Update muutospyynto: {}", muutospyynto.toString());
        try {
            return updateMuutospyynto(muutospyynto, fileMap);
        } catch (Exception e) {
            throw new DataAccessException("Failed to update muutospyynto!", e);
        }
    }

    protected Optional<Muutospyynto> with(final Muutospyynto muutospyynto, String reqs) {
        if (reqs.equals("listaus")) {
            withPaatoskierros(muutospyynto);
        }
        if (reqs.equals("yksi")) {
            withLiitteet(muutospyynto);
            withMuutokset(muutospyynto);
            withPaatoskierros(muutospyynto);
            withLupaUuid(muutospyynto);
            withOrganization(muutospyynto);
        }
        if (reqs.equals("esittelija")) {
            withLiitteet(muutospyynto);
            withMuutokset(muutospyynto);
            withLupaUuid(muutospyynto);
            withPaatoskierros(muutospyynto);
            withOrganization(muutospyynto);
            withTilamuutokset(muutospyynto);
        }
        return Optional.ofNullable(muutospyynto);
    }

    private SelectOnConditionStep<Record> getBaseSelect() {
        return dsl.select(MUUTOSPYYNTO.fields())
                .from(MUUTOSPYYNTO)
                .leftJoin(LUPA).on(MUUTOSPYYNTO.LUPA_ID.eq(LUPA.ID));
    }

    private Condition baseFilter() {
        final OivaPermission accessPermission = authService.accessPermission();
        final Condition lupaCondition = LUPA.JARJESTAJA_OID.in(accessPermission.oids);
        if (accessPermission.is(OivaAccess.Type.All)) {
            return null;
        }
        return lupaCondition;
    }

    private boolean validate(Muutos muutos) {
        return ValidationUtils.validate(
                validation(muutos.getKoodiarvo(), "Muutos: koodiarvo is missing"),
                validation(muutos.getKoodisto(), "Muutos: koodisto is missing")
        ) && Optional.ofNullable(muutos.getLiitteet()).map(liitteet -> liitteet.stream().allMatch(this::validate))
                .orElse(true);
    }

    private boolean validate(Liite liite) {
        return ValidationUtils.validate(
                validation(liite.getTyyppi(), "Liite: tyyppi is missing"),
                validation(liite.getKieli(), "Liite: kieli is missing")
        );
    }

    private void withLiitteet(Muutospyynto muutospyynto) {
        Optional.ofNullable(muutospyynto)
                .ifPresent(m -> getMuutospyyntoLiitteet(m.getId()).ifPresent(m::setLiitteet));
    }

    private Optional<Collection<Liite>> getMuutospyyntoLiitteet(Long id) {
        return Optional.ofNullable(
                dsl.select(LIITE.fields()).from(MUUTOSPYYNTO_LIITE).leftJoin(LIITE)
                        .on(MUUTOSPYYNTO_LIITE.LIITE_ID.eq(LIITE.ID)).where(MUUTOSPYYNTO_LIITE.MUUTOSPYYNTO_ID.eq(id))
                        .fetchInto(Liite.class)

        );
    }

    private void withLupaUuid(final Muutospyynto muutospyynto) {
        Optional.ofNullable(muutospyynto)
                .ifPresent(m -> getLupaUuid(m.getLupaId()).ifPresent(m::setLupaUuid));
    }

    private void withPaatoskierros(final Muutospyynto muutospyynto) {
        Optional.ofNullable(muutospyynto)
                .ifPresent(m -> getPaatoskierrosByMuutospyynto(m.getPaatoskierrosId())
                        .ifPresent(m::setPaatoskierros));
    }

    private Optional<String> getLupaUuid(long id) {
        return dsl.select(LUPA.UUID).from(LUPA)
                .where(LUPA.ID.eq(id)).fetchOptionalInto(String.class);
    }

    private Optional<Long> getKohdeId(UUID uuid) {
        return dsl.select(KOHDE.ID).from(KOHDE)
                .where(KOHDE.UUID.equal(uuid)).fetchOptionalInto(Long.class);
    }

    private Optional<Long> getPaatoskierrosId(UUID uuid) {
        return dsl.select(PAATOSKIERROS.ID).from(PAATOSKIERROS)
                .where(PAATOSKIERROS.UUID.equal(uuid)).fetchOptionalInto(Long.class);
    }

    private Optional<Long> getMaaraystyyppiId(UUID uuid) {
        return dsl.select(MAARAYSTYYPPI.ID).from(MAARAYSTYYPPI)
                .where(MAARAYSTYYPPI.UUID.equal(uuid)).fetchOptionalInto(Long.class);
    }

    private Optional<Paatoskierros> getPaatoskierrosByMuutospyynto(long id) {
        return dsl.select(PAATOSKIERROS.fields()).from(PAATOSKIERROS)
                .where(PAATOSKIERROS.ID.eq(id)).fetchOptionalInto(Paatoskierros.class);
    }

    private Collection<Muutos> constructMuutosTree(Collection<Muutos> allMuutokset, Long parentId) {

        // Find children related to parent id
        Map<Long, Muutos> children = allMuutokset.stream()
                .filter(m -> Objects.equals(m.getParentId(), parentId))
                .collect(Collectors.toMap(Muutos::getId, Function.identity()));

        // Remove children leaving uknown descendants
        allMuutokset.removeAll(children.values());

        // Try to find children's children
        for (Muutos child : children.values()) {
            child.setAliMaaraykset(constructMuutosTree(allMuutokset, child.getId()));
        }

        return children.values();
    }

    private void withMuutokset(final Muutospyynto muutospyynto) {
        Optional.ofNullable(muutospyynto)
                .ifPresent(m -> m.setMuutokset(constructMuutosTree(getByMuutospyyntoId(m.getId()), null)));
    }

    private void withOrganization(final Muutospyynto muutospyynto) {
        Optional.ofNullable(muutospyynto)
                .ifPresent(m -> organisaatioService.getWithLocation(m.getJarjestajaYtunnus())
                        .ifPresent(m::setJarjestaja));
    }

    private void withTilamuutokset(final Muutospyynto muutospyynto) {
        Optional.ofNullable(muutospyynto)
                .ifPresent(m -> asiatilamuutosService.forMuutospyynto(m)
                        .ifPresent(m::setAsiatilamuutokset));
    }

    // Hakee muutospyyntöön liittyvät muutokset
    private Collection<Muutos> getByMuutospyyntoId(long muutospyynto_id) {
        return dsl.select(MUUTOS.fields()).from(MUUTOS)
                .where(MUUTOS.MUUTOSPYYNTO_ID.eq(muutospyynto_id))
                .fetchInto(Muutos.class)
                .stream()
                .map(this::withAll)
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<Muutos> withAll(final Muutos muutos) {
        withKohde(muutos);
        withMaaraystyyppi(muutos);
        withLiitteet(muutos);
        withKoodisto(muutos);
        return Optional.ofNullable(muutos);
    }

    private void withKoodisto(final Muutos muutos) {
        final Function<Muutos, Optional<KoodistoKoodi>> getKoodi = m ->
                Optional.ofNullable(opintopolkuService.getKoodi(m.getKoodisto(), m.getKoodiarvo(), null));
        Optional.ofNullable(muutos).ifPresent(m -> {
            if (m.hasKoodistoAndKoodiArvo()) {
                getKoodi.apply(m).ifPresent(koodi -> {
                    m.setKoodi(koodi);
                    if (koodi.isKoodisto("koulutus")) {
                        final String koodiArvo = koodi.koodiArvo();
                        opintopolkuService.getKoulutustyyppiKoodiForKoulutus(koodiArvo)
                                .ifPresent(koulutustyyppiKoodit -> koulutustyyppiKoodit.forEach(m::addYlaKoodi));
                        opintopolkuService.getKoulutusalaKoodiForKoulutus(koodiArvo).ifPresent(m::addYlaKoodi);
                    }
                });
            }
            if (m.hasAliMaarays()) m.getAliMaaraykset().forEach(this::withKoodisto);
        });
    }

    private void withLiitteet(Muutos muutos) {
        Optional.ofNullable(muutos)
                .ifPresent(m -> getMuutosLiitteet(m.getId())
                        .ifPresent(m::setLiitteet));
    }

    private Optional<Collection<Liite>> getMuutosLiitteet(Long id) {
        return Optional.ofNullable(
                dsl.select(LIITE.fields()).from(MUUTOS_LIITE).leftJoin(LIITE)
                        .on(MUUTOS_LIITE.LIITE_ID.eq(LIITE.ID)).where(MUUTOS_LIITE.MUUTOS_ID.eq(id))
                        .fetchInto(Liite.class)

        );
    }

    private void withKohde(final Muutos muutos) {
        Optional.ofNullable(muutos)
                .ifPresent(m -> getKohdeByMuutospyynto(m.getKohdeId()).ifPresent(m::setKohde));
    }

    private Optional<Kohde> getKohdeByMuutospyynto(long id) {
        return dsl.select(KOHDE.fields()).from(KOHDE)
                .where(KOHDE.ID.eq(id)).fetchOptionalInto(Kohde.class);
    }

    private void withMaaraystyyppi(final Muutos muutos) {
        Optional.ofNullable(muutos)
                .ifPresent(m -> getMaaraystyyppiByMuutospyynto(m.getMaaraystyyppiId())
                        .ifPresent(m::setMaaraystyyppi));
    }

    private Optional<Maaraystyyppi> getMaaraystyyppiByMuutospyynto(long id) {
        return dsl.select(MAARAYSTYYPPI.fields()).from(MAARAYSTYYPPI)
                .where(MAARAYSTYYPPI.ID.eq(id)).fetchOptionalInto(Maaraystyyppi.class);
    }

    private Long getPaatoskierrosId(Muutospyynto muutospyynto) {
        if (muutospyynto.getPaatoskierros() == null) {
            // use default
            return paatoskierrosDefaultId;
        }
        return getPaatoskierrosId(muutospyynto.getPaatoskierros().getUuid())
                .orElse(paatoskierrosDefaultId);
    }

    private Optional<Muutospyynto> updateMuutospyynto(Muutospyynto muutospyynto, Map<String, MultipartFile> fileMap) {
        return getByUuid(muutospyynto.getUuid().toString()).map(muutospyyntoRecord -> {
            muutospyynto.setId(muutospyyntoRecord.getId());
            muutospyynto.setPaivittaja(authService.getUsername());
            muutospyynto.setPaivityspvm(Timestamp.from(Instant.now()));
            MuutospyyntoRecord muutospyyntoRecordUp = dsl.newRecord(MUUTOSPYYNTO, muutospyynto);
            dsl.executeUpdate(muutospyyntoRecordUp);

            deleteFromExistingMetaLiitteet(muutospyynto.getMeta());
            deleteFromExistingLiitteet(muutospyynto.getLiitteet());
            createMuutospyyntoLiitteet(muutospyynto, fileMap, muutospyyntoRecordUp.getId());
            clearNonExistingMuutokset(muutospyynto);
            saveMuutokset(muutospyynto, null, muutospyynto.getMuutokset(), fileMap);

            return muutospyynto;
        });
    }

    private Optional<Muutospyynto> createMuutospyynto(Muutospyynto muutospyynto, Long paatoskierrosId,
                                                      Map<String, MultipartFile> fileMap) {
        final Optional<MuutospyyntoRecord> muutospyyntoRecordOpt =
                Optional.ofNullable(dsl.newRecord(MUUTOSPYYNTO, muutospyynto));
        return muutospyyntoRecordOpt.map(muutospyyntoRecord -> {
            createMetaLiitteet(muutospyynto.getMeta(), fileMap);

            muutospyyntoRecord.setLuoja(authService.getUsername());
            muutospyyntoRecord.setLuontipvm(Timestamp.from(Instant.now()));
            Optional<Lupa> lupa = lupaService.getByUuid(muutospyynto.getLupaUuid());
            if (lupa.map(m -> !m.getJarjestajaYtunnus().equals(muutospyynto.getJarjestajaYtunnus())).orElse(false)) {
                throw new ForbiddenException("Muutospyynto and lupa must have same organizations");
            }
            muutospyyntoRecord.setLupaId(lupa.get().getId());
            muutospyyntoRecord.setPaatoskierrosId(paatoskierrosId);
            logger.debug("Create muutospyynto: " + muutospyyntoRecord.toString());
            muutospyyntoRecord.store();
            muutospyynto.setId(muutospyyntoRecord.getId());
            asiatilamuutosService.insertForMuutospyynto(muutospyyntoRecord.getId(), "",
                    muutospyynto.getTila(), authService.getUsername());

            createMuutospyyntoLiitteet(muutospyynto, fileMap, muutospyyntoRecord.getId());
            saveMuutokset(muutospyynto, null, muutospyynto.getMuutokset(), fileMap);

            return muutospyynto;
        });
    }

    private void createMuutospyyntoLiitteet(Muutospyynto muutospyynto, Map<String, MultipartFile> fileMap, Long muutospyyntoId) {
        Optional.ofNullable(muutospyynto.getLiitteet())
                .ifPresent(liitteet -> liitteet.forEach(liite -> {
                            final Optional<MultipartFile> file = Optional.ofNullable(fileMap.get(liite.getTiedostoId()));
                            if (file.isPresent()) {
                                createMuutospyyntoLiite(muutospyyntoId, liite, file.get());
                            } else {
                                // Only update liite information to database.
                                liiteService.update(liite);
                            }
                        }
                ));
    }

    private void createMuutospyyntoLiite(Long muutospyyntoId, Liite liite, MultipartFile file) {
        // Remove old if exists and replace it with new one
        liiteService.delete(liite);
        liiteService.save(file, liite)
                .ifPresent(l -> {
                    final MuutospyyntoLiite link = new MuutospyyntoLiite();
                    link.setLiiteId(l.getId());
                    link.setMuutospyyntoId(muutospyyntoId);
                    final MuutospyyntoLiiteRecord muutospyyntoLiiteRecord =
                            dsl.newRecord(MUUTOSPYYNTO_LIITE, link);
                    muutospyyntoLiiteRecord.store();
                });
    }

    private void saveMuutokset(Muutospyynto muutospyynto, Muutos parentMuutos, Collection<Muutos> muutokset, Map<String, MultipartFile> fileMap) {

        // key TRUE => parents, FALSE => possible children
        Map<Boolean, List<Muutos>> muutosGroups = muutokset.stream().collect(Collectors.groupingBy(muutos ->
                parentMuutos == null && muutos.hasNoParents() || parentMuutos != null && muutos.isChildTo(parentMuutos)));

        List<Muutos> directChildren = muutosGroups.get(Boolean.TRUE);
        List<Muutos> descendats = muutosGroups.get(Boolean.FALSE);

        if (directChildren != null) {
            directChildren.forEach(muutos -> {
                muutos.setParentId(parentMuutos !=null ? parentMuutos.getId() : null);
                Long muutosId;
                if (muutos.getUuid() == null) {
                    muutosId = createMuutos(muutospyynto.getId(), muutos, fileMap);
                } else {
                    muutosId = updateMuutos(muutospyynto.getId(), muutos, fileMap);
                }
                muutos.setId(muutosId);
                // save children for parent muutos
                if (descendats != null) {
                    saveMuutokset(muutospyynto, muutos, descendats, fileMap);
                }
            });
        }
    }

    /**
     * Get all muutokset, alimuutokset, ali-alimuutokset etc.
     */
    private Stream<Muutos> getMuutoksetRecursively(Collection<Muutos> muutokset) {
        return Stream.concat(
                muutokset.stream(),
                muutokset.stream().flatMap(parent -> {
                    if (parent.getAliMaaraykset() != null) {
                        return getMuutoksetRecursively(parent.getAliMaaraykset());
                    }
                    return Stream.empty();
                }));
    }

    /**
     * Clear old muutokset which are in db but not in request.
     *
     * @param muutospyynto Muutospyynto from request
     */
    private void clearNonExistingMuutokset(Muutospyynto muutospyynto) {
        Set<UUID> muutosIds = getMuutoksetRecursively(muutospyynto.getMuutokset())
                .map(Muutos::getUuid)
                .collect(Collectors.toSet());

        getByMuutospyyntoId(muutospyynto.getId()).stream()
                .filter(orig -> !muutosIds.contains(orig.getUuid()))
                .forEach(poistettava -> {
                    deleteFromExistingLiitteet(poistettava.getLiitteet(), true);
                    deleteFromExistingMetaLiitteet(poistettava.getMeta(), true);
                    dsl.deleteFrom(MUUTOS).where(MUUTOS.ID.eq(poistettava.getId())).execute();
                });
    }

    private Long updateMuutos(Long muutosPyyntoId, Muutos muutos, Map<String, MultipartFile> fileMap) {
        Optional<Muutos> mu = getMuutosByUuId(muutos.getUuid().toString());
        mu.ifPresent(m -> {
            muutos.setId(m.getId());
            muutos.setLuoja(authService.getUsername());
            muutos.setPaivityspvm(Timestamp.from(Instant.now()));
            muutos.setMuutospyyntoId(muutosPyyntoId);
            muutos.setKohdeId(getKohdeId(muutos.getKohde().getUuid()).orElse(null));
            MuutosRecord muutosRecordUp = dsl.newRecord(MUUTOS, muutos);
            deleteFromExistingLiitteet(muutos.getLiitteet());
            deleteFromExistingMetaLiitteet(muutos.getMeta());
            createMuutosLiitteet(muutos, fileMap, muutosRecordUp.getId());
            dsl.executeUpdate(muutosRecordUp);
        });
        return mu.map(Muutos::getId).orElse(null);
    }

    private void deleteFromExistingMetaLiitteet(JsonNode meta) {
        deleteFromExistingMetaLiitteet(meta, false);
    }

    private void deleteFromExistingMetaLiitteet(JsonNode meta, boolean forceDelete) {
        ObjectMapper mapper = ObjectMapperSingleton.mapper;
        final ArrayList<Liite> liitteet = getMetaLiitteet(meta);
        final List<Liite> removed = liitteet.stream().filter(l -> l.isRemoved() || forceDelete)
                .peek(liiteService::delete)
                .collect(Collectors.toList());
        liitteet.removeAll(removed);
        final ArrayNode liiteArray = ((ObjectNode) meta).putArray("liitteet");
        liitteet.stream().map(l -> (JsonNode) mapper.valueToTree(l))
                .forEach(liiteArray::add);
    }

    private Long createMuutos(Long muutosPyyntoId, Muutos muutos, Map<String, MultipartFile> fileMap) {
        final Optional<MuutosRecord> muutosRecordOpt = Optional.ofNullable(dsl.newRecord(MUUTOS, muutos));
        muutosRecordOpt.ifPresent(muutosRecord -> {
            if (muutos.getMeta() != null) {
                createMetaLiitteet(muutos.getMeta(), fileMap);
            }
            muutosRecord.setLuoja(authService.getUsername());
            muutosRecord.setLuontipvm(Timestamp.from(Instant.now()));
            muutosRecord.setMuutospyyntoId(muutosPyyntoId);
            muutosRecord.setKohdeId(getKohdeId(muutos.getKohde().getUuid()).orElse(null));
            muutosRecord.setMaaraystyyppiId(getMaaraystyyppiId(muutos.getMaaraystyyppi().getUuid()).orElse(null));
            muutosRecord.setParentId(muutos.getParentId());
            if (muutos.getMaaraysUuid() != null) {
                maaraysService.getByUuid(muutos.getMaaraysUuid()).ifPresent(m -> muutosRecord.setMaaraysId(m.getId()));
            }
            muutosRecord.store();

            createMuutosLiitteet(muutos, fileMap, muutosRecord.getId());
        });
        return muutosRecordOpt.map(MuutosRecord::getId).orElse(null);
    }

    private void createMuutosLiitteet(Muutos muutos, Map<String, MultipartFile> fileMap, Long muutosId) {
        Optional.ofNullable(muutos.getLiitteet())
                .ifPresent(liitteet -> liitteet.forEach(liite -> {
                            final Optional<MultipartFile> file = Optional.ofNullable(fileMap.get(liite.getTiedostoId()));
                            if (file.isPresent()) {
                                createMuutosLiite(muutosId, liite, file.get());
                            } else {
                                // Only update liite information to database.
                                liiteService.update(liite);
                            }
                        }
                ));
    }

    private void createMuutosLiite(Long muutosId, Liite liite, MultipartFile file) {
        // Remove old if exists and replace it with new one.
        liiteService.delete(liite);
        liiteService.save(file, liite)
                .ifPresent(l -> {
                    final MuutosLiite link = new MuutosLiite();
                    link.setLiiteId(l.getId());
                    link.setMuutosId(muutosId);
                    final MuutosLiiteRecord muutosLiiteRecord = dsl.newRecord(MUUTOS_LIITE, link);
                    muutosLiiteRecord.store();
                });
    }

    private void deleteFromExistingLiitteet(Collection<Liite> liitteet) {
        deleteFromExistingLiitteet(liitteet, false);
    }

    private void deleteFromExistingLiitteet(Collection<Liite> liitteet, boolean forceDelete) {
        Optional.ofNullable(liitteet).ifPresent(list -> {
            list.stream().filter(l -> l.isRemoved() || forceDelete).forEach(liiteService::delete);
            list.removeIf(Liite::isRemoved);
        });
    }

    private void createMetaLiitteet(JsonNode meta, Map<String, MultipartFile> fileMap) {
        final ObjectMapper mapper = ObjectMapperSingleton.mapper;
        final List<JsonNode> liitteet = getMetaLiitteet(meta)
                .stream().map(l -> {
                    final Optional<MultipartFile> file = Optional.ofNullable(fileMap.get(l.getTiedostoId()));
                    if (file.isPresent()) {
                        liiteService.save(file.get(), l);
                        return mapper.valueToTree(liiteService.get(l.getId()).orElse(null));
                    } else if (l.getUuid() != null) {
                        // Only update liite information to database.
                        liiteService.update(l);
                        return (JsonNode) mapper.valueToTree(liiteService.get(l.getId()).orElse(null));
                    }
                    // This is not valid meta liite.
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());
        ((ObjectNode) meta).putArray("liitteet").addAll(liitteet);
    }

    private ArrayList<Liite> getMetaLiitteet(JsonNode meta) {
        ObjectMapper mapper = ObjectMapperSingleton.mapper;
        return Optional.ofNullable(meta.path("liitteet")).filter(JsonNode::isArray)
                .map(node -> {
                    try {
                        return mapper.readerFor(new TypeReference<List<Liite>>() {
                        }).readValue(node);
                    } catch (IOException e) {
                        logger.error("Could not read liitteet node from meta json!", e);
                    }
                    return new ArrayList<Liite>();
                }).orElse(new ArrayList<>());
    }
}
