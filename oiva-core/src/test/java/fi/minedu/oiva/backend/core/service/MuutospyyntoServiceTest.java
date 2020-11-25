package fi.minedu.oiva.backend.core.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.minedu.oiva.backend.core.exception.ForbiddenException;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.oiva.Maarays;
import fi.minedu.oiva.backend.model.entity.oiva.Muutos;
import fi.minedu.oiva.backend.model.entity.oiva.Muutospyynto;
import fi.minedu.oiva.backend.model.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.model.entity.opintopolku.Organisaatio;
import fi.minedu.oiva.backend.model.jooq.Tables;
import fi.minedu.oiva.backend.model.jooq.tables.records.MuutospyyntoRecord;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import org.apache.commons.lang3.Functions;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.matchers.VarargMatcher;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fi.minedu.oiva.backend.core.service.MuutospyyntoService.Muutospyyntotila;
import static fi.minedu.oiva.backend.model.jooq.Tables.MUUTOSPYYNTO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class MuutospyyntoServiceTest {

    private Logger logger = LoggerFactory.getLogger(getClass());


    private DSLContext dsl = mock(DSLContext.class);
    private AuthService authService = mock(AuthService.class);
    private OrganisaatioService organisaatioService = mock(OrganisaatioService.class);
    private LiiteService liiteService = mock(LiiteService.class);
    private OpintopolkuService opintopolkuService = mock(OpintopolkuService.class);
    private MaaraysService maaraysService = mock(MaaraysService.class);
    private FileStorageService fileStorageService = mock(FileStorageService.class);
    private AsiatilamuutosService asiatilamuutosService = mock(AsiatilamuutosService.class);
    private LupaService lupaService = mock(LupaService.class);
    private KoodistoService koodistoService = mock(KoodistoService.class);
    private EsitysmalliService esitysmalliService = mock(EsitysmalliService.class);

    private MuutospyyntoService service;
    private Lupa lupa;

    @Before
    public void setUp() {
        this.service = spy(new MuutospyyntoService(dsl, authService, organisaatioService, liiteService,
                opintopolkuService, maaraysService, fileStorageService, asiatilamuutosService, lupaService, koodistoService, esitysmalliService));

        this.lupa = new Lupa();
        this.lupa.setJarjestajaYtunnus("123");
        when(lupaService.getByUuid(anyString())).thenReturn(Optional.of(lupa));
    }

    @Test
    public void kayttajaAndNimenkirjoittajaCanCreateMuutospyynto() throws Throwable {
        Muutospyynto muutospyynto = generateMuutospyynto();
        doReturn(Optional.of(muutospyynto)).when(service).getByUuid(anyString());

        final HashSet<String> allowedRoles = Sets.newHashSet(OivaAccess.Role_Nimenkirjoittaja, OivaAccess.Role_Kayttaja);
        Functions.FailableCallable<Optional<Muutospyynto>, Throwable> create = () -> service.executeAction(null, MuutospyyntoService.Action.LUO, muutospyynto, new HashMap<>());

        // Test user in same organization
        for (final String role : OivaAccess.roles) {
            reset(authService);
            setUserOrgToMuutospyyntoOrg("oid", muutospyynto);
            when(authService.hasAnyRole(argThat(new StringVarargMatcher(role)))).thenReturn(true);

            logger.debug("Testing role " + role);

            if (allowedRoles.contains(role)) {
                try {
                    create.call();
                    // Verify new record is called once
                    verify(dsl).newRecord(eq(MUUTOSPYYNTO), any(Muutospyynto.class));
                    // Reset .newRecord counter
                    reset(dsl);
                } catch (ForbiddenException e) {
                    e.printStackTrace();
                    fail("User with role " + role + " should be able to create muutospyynto");
                }
            }
            else {
                try {
                    create.call();
                    fail("Role " + role + " should not have access to create new muutospyynto");
                } catch (ForbiddenException e) {
                    // NOP
                }
            }

            when(authService.hasAnyRole(eq(role))).thenReturn(false);
        }


        // Tests users in different organization
        for (String role : OivaAccess.roles) {
            reset(authService);
            when(authService.hasAnyRole(argThat(new StringVarargMatcher(role)))).thenReturn(true);
            logger.debug("Testing role " + role);
            try {
                create.call();
                fail("Role " + role + " is in different organization and should not have access to create new muutospyynto");
            } catch (ForbiddenException e) {
                // NOP
            }
        }
    }

    @Test
    public void kayttajaAndNimenkirjoittajaCanSaveMuutospyynto() throws Throwable {
        Muutospyynto muutospyynto = generateMuutospyynto();
        muutospyynto.setUuid(new UUID(1, 2));
        muutospyynto.setTila(Muutospyyntotila.LUONNOS.toString());
        doReturn(Optional.of(muutospyynto)).when(service).getByUuid(anyString());
        doReturn(Optional.of(muutospyynto)).when(service).update(any(Muutospyynto.class), anyMap());
        doReturn(Optional.of(muutospyynto)).when(service).getById(anyLong());

        final HashSet<String> allowedRoles = Sets.newHashSet(OivaAccess.Role_Nimenkirjoittaja, OivaAccess.Role_Kayttaja);
        Functions.FailableCallable<Optional<Muutospyynto>, Throwable> update = () -> service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.TALLENNA, muutospyynto, new HashMap<>());

        // Test user in same organization
        for (final String role : OivaAccess.roles) {
            reset(authService);
            setUserOrgToMuutospyyntoOrg("oid", muutospyynto);
            when(authService.hasAnyRole(argThat(new StringVarargMatcher(role)))).thenReturn(true);

            logger.debug("Testing role " + role);

            if (allowedRoles.contains(role)) {
                try {
                    update.call();
                    // Verify new record is called at least once
                    verify(service, atLeastOnce()).update(any(Muutospyynto.class), anyMap());
                } catch (ForbiddenException e) {
                    e.printStackTrace();
                    fail("User with role " + role + " should be able to update muutospyynto");
                }
            }
            else {
                try {
                    update.call();
                    fail("Role " + role + " should not have access to update new muutospyynto");
                } catch (ForbiddenException e) {
                    // NOP
                }
            }

            when(authService.hasAnyRole(eq(role))).thenReturn(false);
        }


        // Tests users in different organization
        for (String role : OivaAccess.roles) {
            reset(authService);
            when(authService.getUserOrganisationOid()).thenReturn("1");
            when(authService.hasAnyRole(argThat(new StringVarargMatcher(role)))).thenReturn(true);
            logger.debug("Testing role " + role);
            try {
                update.call();
                fail("Role " + role + " is in different organization and should not have access to update new muutospyynto");
            } catch (ForbiddenException e) {
                // NOP
            }
        }
    }

    @Test
    public void nimenkirjoittajaCanSendMuutospyynto() throws Throwable {
        Muutospyynto muutospyynto = generateMuutospyynto();
        muutospyynto.setUuid(new UUID(4, 4));
        muutospyynto.setTila(Muutospyyntotila.LUONNOS.toString());

        doReturn(Optional.of(muutospyynto)).when(service).getByUuid(anyString());
        doReturn(Optional.of(muutospyynto)).when(service).update(any(Muutospyynto.class), anyMap());
        when(dsl.fetchOne(any(Table.class), any(Condition.class))).thenReturn(mock(MuutospyyntoRecord.class));

        final HashSet<String> allowedRoles = Sets.newHashSet(OivaAccess.Role_Nimenkirjoittaja);

        Functions.FailableCallable<Optional<Muutospyynto>, Throwable> send = () -> service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.LAHETA, muutospyynto, new HashMap<>());

        // Test user in same organization
        for (final String role : OivaAccess.roles) {
            reset(authService);
            setUserOrgToMuutospyyntoOrg("oid", muutospyynto);
            when(authService.hasAnyRole(eq(role))).thenReturn(true);

            logger.debug("Testing role " + role);

            if (allowedRoles.contains(role)) {
                try {
                    send.call();
                    // Verify new record is called at least once
                    System.out.println("ASDF");
                    verify(dsl).executeUpdate(any());
                    reset(dsl);
                } catch (ForbiddenException e) {
                    e.printStackTrace();
                    fail("User with role " + role + " should be able to send muutospyynto");
                }
            }
            else {
                try {
                    send.call();
                    fail("Role " + role + " should not have access to send new muutospyynto");
                } catch (ForbiddenException e) {
                    // NOP
                }
            }

            when(authService.hasAnyRole(eq(role))).thenReturn(false);
        }


        // Tests users in different organization
        for (String role : OivaAccess.roles) {
            reset(authService);
            when(authService.getUserOrganisationOid()).thenReturn("1");
            when(authService.hasAnyRole(argThat(new StringVarargMatcher(role)))).thenReturn(true);
            logger.debug("Testing role " + role);
            try {
                send.call();
                fail("Role " + role + " is in different organization and should not have access to send new muutospyynto");
            } catch (ForbiddenException e) {
                // NOP
            }
        }
    }

    @Test
    public void esitettelijaCanReverseEsittelyssa() throws Exception {
        Muutospyynto muutospyynto = generateMuutospyynto();
        muutospyynto.setUuid(new UUID(4, 4));
        muutospyynto.setTila(Muutospyyntotila.ESITTELYSSA.toString());

        doReturn(Optional.of(muutospyynto)).when(service).getByUuid(anyString());
        doReturn(Optional.of(muutospyynto)).when(service).update(any(Muutospyynto.class), anyMap());
        when(dsl.fetchOne(any(Table.class), any(Condition.class))).thenReturn(mock(MuutospyyntoRecord.class));

        when(authService.hasAnyRole(eq(OivaAccess.Role_Esittelija))).thenReturn(true);

        // Happy case
        muutospyynto.setAlkupera(MuutospyyntoService.Tyyppi.ESITTELIJA.toString());
        service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.OTA_KASITTELYYN);

        // Only esittelija self created muutospyynnot can be reversed
        muutospyynto.setAlkupera(MuutospyyntoService.Tyyppi.KJ.toString());
        catchExpectedException(
                ForbiddenException.class,
                "Action is not allowed",
                () -> service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.OTA_KASITTELYYN));

        // Nimenkirjoittaja has no right
        muutospyynto.setAlkupera(MuutospyyntoService.Tyyppi.KJ.toString());
        when(authService.hasAnyRole(eq(OivaAccess.Role_Esittelija))).thenReturn(false);
        when(authService.hasAnyRole(eq(OivaAccess.Role_Nimenkirjoittaja))).thenReturn(true);
        catchExpectedException(
                ForbiddenException.class,
                "Action is not allowed",
                () -> service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.OTA_KASITTELYYN));
    }

    @Test
    public void esittelijaCanEsitteleMuutospyynto() throws Exception {
        Muutospyynto muutospyynto = generateMuutospyynto();
        muutospyynto.setUuid(new UUID(4, 4));
        muutospyynto.setTila(Muutospyyntotila.VALMISTELUSSA.toString());

        doReturn(Optional.of(muutospyynto)).when(service).getByUuid(anyString());
        doReturn(Optional.of(muutospyynto)).when(service).update(any(Muutospyynto.class), anyMap());
        when(dsl.fetchOne(any(Table.class), any(Condition.class))).thenReturn(mock(MuutospyyntoRecord.class));

        when(authService.hasAnyRole(eq(OivaAccess.Role_Esittelija))).thenReturn(true);

        // Happy case
        service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.ESITTELE);

        // Wrong tila
        muutospyynto.setTila(Muutospyyntotila.LUONNOS.toString());
        catchExpectedException(
                ForbiddenException.class,
                "Action is not allowed",
                () -> service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.ESITTELE));
        muutospyynto.setTila(Muutospyyntotila.VALMISTELUSSA.toString());

        // Wrong user
        when(authService.hasAnyRole(eq(OivaAccess.Role_Esittelija))).thenReturn(false);
        catchExpectedException(
                ForbiddenException.class,
                "User has no right",
                () -> service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.ESITTELE));
    }

    @Test
    public void testKJActions() throws Exception {
        Muutospyynto muutospyynto = generateMuutospyynto();
        muutospyynto.setTila(Muutospyyntotila.LUONNOS.toString());
        setUserOrgToMuutospyyntoOrg("123", muutospyynto);
        muutospyynto.setUuid(new UUID(2, 3));

        doReturn(Optional.of(muutospyynto)).when(service).getByUuid(anyString());
        doReturn(Optional.of(muutospyynto)).when(service).update(any(Muutospyynto.class), anyMap());
        doReturn(Optional.of(muutospyynto)).when(service).getById(anyLong());

        // Save state changes to local muutospyynto object
        when(dsl.fetchOne(any(Tables.MUUTOSPYYNTO.getClass()), any(Condition.class))).thenReturn(new MuutospyyntoRecord());
        when(dsl.executeUpdate(any(MuutospyyntoRecord.class))).then(invocation -> {
            muutospyynto.setTila(invocation.getArgumentAt(0, MuutospyyntoRecord.class).getTila());
            return 1;
        });

        // Happy path
        when(authService.hasAnyRole(argThat(new StringVarargMatcher(OivaAccess.Role_Nimenkirjoittaja)))).thenReturn(true);
        service.executeAction(null, MuutospyyntoService.Action.LUO, muutospyynto, new HashMap<>());
        service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.TALLENNA, muutospyynto, new HashMap<>());
        when(authService.hasAnyRole(eq(OivaAccess.Role_Nimenkirjoittaja))).thenReturn(true);
        service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.LAHETA);

        assertEquals(Muutospyyntotila.AVOIN.toString(), muutospyynto.getTila());
        catchExpectedException(
                ForbiddenException.class,
                "Action is not allowed",
                () -> service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.TALLENNA, muutospyynto, new HashMap<>()));

        catchExpectedException(
                ForbiddenException.class,
                "Action is not allowed",
                () -> service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.LAHETA));

    }

    @Test
    public void testEsittelijaCanDeleteMp() throws Exception {
        Muutospyynto muutospyynto = generateMuutospyynto();
        muutospyynto.setUuid(UUID.randomUUID());
        doReturn(Optional.of(muutospyynto)).when(service).getByUuid(anyString());
        doNothing().when(service).delete(any(Muutospyynto.class));

        when(authService.hasAnyRole(eq(OivaAccess.Role_Esittelija))).thenReturn(true);

        // Muutospyynto is created by KJ
        muutospyynto.setTila(Muutospyyntotila.VALMISTELUSSA.toString());
        catchExpectedException(
                ForbiddenException.class,
                "Action is not allowed",
                () -> service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.POISTA));
        muutospyynto.setAlkupera(MuutospyyntoService.Tyyppi.ESITTELIJA.toString());

        // Wrong status
        for (Muutospyyntotila tila : EnumSet.complementOf(EnumSet.of(Muutospyyntotila.VALMISTELUSSA))) {
            muutospyynto.setTila(tila.toString());

            catchExpectedException(
                    ForbiddenException.class,
                    "Action is not allowed",
                    () -> service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.POISTA));
        }
        muutospyynto.setTila(Muutospyyntotila.VALMISTELUSSA.toString());

        // Wrong role
        when(authService.hasAnyRole(eq(OivaAccess.Role_Esittelija))).thenReturn(false);
        catchExpectedException(
                ForbiddenException.class,
                "User has no right",
                () -> service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.POISTA));
        when(authService.hasAnyRole(eq(OivaAccess.Role_Esittelija))).thenReturn(true);

        // Correct rights
        service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.POISTA);
        verify(service).delete(any(Muutospyynto.class));
    }

    @Test
    public void testKJCanDeleteMp() throws Exception {
        Muutospyynto muutospyynto = generateMuutospyynto();
        setUserOrgToMuutospyyntoOrg("123", muutospyynto);
        muutospyynto.setUuid(new UUID(2, 3));
        doReturn(Optional.of(muutospyynto)).when(service).getByUuid(anyString());
        doNothing().when(service).delete(any(Muutospyynto.class));

        // Muutospyynto in wrong status
        when(authService.hasAnyRole(eq(OivaAccess.Role_Nimenkirjoittaja))).thenReturn(true);
        for (Muutospyyntotila tila : EnumSet.complementOf(EnumSet.of(Muutospyyntotila.LUONNOS))) {
            muutospyynto.setTila(tila.toString());

            catchExpectedException(
                    ForbiddenException.class,
                    "Action is not allowed",
                    () -> service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.POISTA));
        }

        muutospyynto.setTila(Muutospyyntotila.LUONNOS.toString());

        // Wrong organization
        when(authService.getUserOrganisationOid()).thenReturn(lupa.getJarjestajaOid() + "123");
        catchExpectedException(
                ForbiddenException.class,
                "User has no right",
                () -> service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.POISTA));

        setUserOrgToMuutospyyntoOrg("123", muutospyynto);

        // Wrong role
        when(authService.hasAnyRole(eq(OivaAccess.Role_Nimenkirjoittaja))).thenReturn(false);
        catchExpectedException(
                ForbiddenException.class,
                "User has no right",
                () -> service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.POISTA));

        when(authService.hasAnyRole(eq(OivaAccess.Role_Nimenkirjoittaja))).thenReturn(true);
        // Happy case
        service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.POISTA);
        verify(service).delete(any(Muutospyynto.class));
    }

    @Test
    public void testConvertToMaaraykset() {
        final Long id = 1L;
        List<Muutos> muutosList = new ArrayList<>();
        final Muutos muutos = new Muutos();
        muutos.setParentMaaraysId(id);
        muutos.setKoodisto("kieli");
        muutos.setKoodiarvo("en");
        muutosList.add(muutos);

        List<Maarays> maaraysList = new ArrayList<>();
        final Maarays maarays = new Maarays();
        maarays.setId(id);
        maaraysList.add(maarays);

        final Collection<Maarays> result = service.convertToMaaraykset(muutosList, maaraysList);
        assertEquals("Count should be 0 because muutos is alimaarays to existing maarays", 0, result.size());
        assertEquals("Existing maarays should have 1 alimaarays created from muutos", 1, maarays.getAliMaaraykset().size());
        final Optional<Maarays> first = maaraysList.stream().findFirst();
        assertEquals("Alimaarays count should match", Integer.valueOf(1), first.map(m -> m.getAliMaaraykset().size()).orElse(0));
        final Optional<Maarays> firstAli = first.flatMap(m -> m.getAliMaaraykset().stream().findFirst());
        assertTrue("There should be alimaarays", firstAli.isPresent());
        assertEquals("Alimaarays should have right koodisto", "kieli", firstAli.get().getKoodisto());
        assertEquals("Alimaarays should have right koodiarvo", "en", firstAli.get().getKoodiarvo());
    }

    @Test
    public void testFilterOldMaaraykset() {
        Maarays maarays = new Maarays();
        Collection<Maarays> maaraykset = service.filterOutRemoved(Lists.newArrayList(maarays), Lists.newLinkedList());

        // ### Case no koodisto nor koodiarvo => no filtering ###
        assertEquals(1, maaraykset.size());

        // Set loppupvm to yesterday
        final KoodistoKoodi koodistoKoodi = new KoodistoKoodi();
        koodistoKoodi.setVoimassaLoppuPvm(LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE));

        doAnswer((Answer<Void>) invocation -> {
            Maarays m = (Maarays) invocation.getArguments()[0];
            m.setKoodi(koodistoKoodi);
            return null;
        }).when(maaraysService).withKoodisto(any(Maarays.class));
        maarays.setKoodisto("old");
        maarays.setKoodiarvo("1");

        maaraykset = service.filterOutRemoved(Lists.newArrayList(maarays), Lists.newLinkedList());

        // ### Case old koodisto => filtered ###
        assertEquals("Old koodi should be filtered out", 0, maaraykset.size());

        // ### Case today valid koodisto => no filtering ###
        koodistoKoodi.setVoimassaLoppuPvm(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        maaraykset = service.filterOutRemoved(Lists.newArrayList(maarays), Lists.newLinkedList());
        assertEquals(1, maaraykset.size());
    }

    @Test
    public void testFilterOldMuutokset() {
        Muutos muutos = new Muutos();
        Collection<Maarays> maaraykset = service.convertToMaaraykset(Lists.newArrayList(muutos), Lists.newLinkedList());

        // ### Case no koodisto nor koodiarvo => no filtering ###
        assertEquals(1, maaraykset.size());

        // Set loppupvm to yesterday
        final KoodistoKoodi koodistoKoodi = new KoodistoKoodi();
        koodistoKoodi.setVoimassaLoppuPvm(LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE));

        doAnswer((Answer<Void>) invocation -> {
            Maarays m = (Maarays) invocation.getArguments()[0];
            m.setKoodi(koodistoKoodi);
            return null;
        }).when(maaraysService).withKoodisto(any(Maarays.class));
        muutos.setKoodisto("old");
        muutos.setKoodiarvo("1");

        maaraykset = service.convertToMaaraykset(Lists.newArrayList(muutos), Lists.newLinkedList());

        // ### Case old koodisto => filtered ###
        assertEquals("Old koodi should be filtered out", 0, maaraykset.size());

        // ### Case today valid koodisto => no filtering ###
        koodistoKoodi.setVoimassaLoppuPvm(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        maaraykset = service.convertToMaaraykset(Lists.newArrayList(muutos), Lists.newLinkedList());
        assertEquals(1, maaraykset.size());
    }

    @Test
    public void testValidAsianumero() {
        assertFalse(service.validAsianumero("VN/1234/123"));
        assertFalse(service.validAsianumero("VN/1456/1234T"));
        assertFalse(service.validAsianumero("VN//1234"));
        assertFalse(service.validAsianumero("VN/1234567890/1234"));
        assertFalse(service.validAsianumero("VL/1234/1234"));
        assertTrue(service.validAsianumero("VN/1/1234"));
        assertTrue(service.validAsianumero("VN/123456789/1234"));
        assertTrue(service.validAsianumero("VN/1234/1234"));
    }

    @Test
    public void testDuplicateAsianumeroExists() {
        Muutospyynto muutospyynto = new Muutospyynto();
        muutospyynto.setUuid(UUID.fromString("163d370c-cc1a-11ea-87d0-0242ac130003"));

        doReturn(Optional.of(muutospyynto)).when(service).getByAsianumero("1234");
        doReturn(Optional.empty()).when(service).getByAsianumero("DoesNotExist");

        // Muutospyynto found UUIDs match
        assertFalse(service.duplicateAsianumeroExists("163d370c-cc1a-11ea-87d0-0242ac130003", "1234"));
        // Muutospyynto found UUIDs do not match
        assertTrue(service.duplicateAsianumeroExists("d50b1a45-96ab-44b5-bba4-7eadcde0c0da", "1234"));
        // Muutospyynto found while creating new Muutospyynto (UUID null)
        assertTrue(service.duplicateAsianumeroExists(null, "1234"));
        // Muutospyynto not found by asianumero
        assertFalse(service.duplicateAsianumeroExists(null, "DoesNotExist"));
    }

    @Test(expected = ValidationException.class)
    public void testAssertValidMuutospyyntoWithDiaariAndInvalidAsianumero() {
        final Muutospyynto muutospyynto = new Muutospyynto();
        muutospyynto.setDiaarinumero("30/40/2020");
        muutospyynto.setAsianumero("invalid");
        // Should validate Asianumero and fail
        service.assertValidMuutospyynto(muutospyynto, false);
    }

    @Test()
    public void testAssertValidMuutospyyntoWithDiaariAndAsianumero() {
        final Muutospyynto muutospyynto = new Muutospyynto();
        muutospyynto.setDiaarinumero("30/40/2020");
        muutospyynto.setAsianumero("VN/1/2020");
        // Should validate Asianumero and pass
        service.assertValidMuutospyynto(muutospyynto, false);
    }

    @Test()
    public void testAssertValidMuutospyyntoWithDiaariAndKoulutustyyppi() {
        final Muutospyynto muutospyynto = new Muutospyynto();
        muutospyynto.setDiaarinumero("30/40/2020");
        muutospyynto.setAsianumero("invalid");
        muutospyynto.setKoulutustyyppi("3");
        // Should not validate Asianumero and pass
        service.assertValidMuutospyynto(muutospyynto, false);
    }

    private Muutospyynto generateMuutospyynto() {
        Muutospyynto muutospyynto = new Muutospyynto();
        muutospyynto.setLiitteet(new LinkedList<>());
        muutospyynto.setMuutokset(new LinkedList<>());
        muutospyynto.setJarjestajaYtunnus(lupa.getJarjestajaYtunnus());
        muutospyynto.setLupaUuid(UUID.randomUUID().toString());
        muutospyynto.setAlkupera(MuutospyyntoService.Tyyppi.KJ.toString());
        muutospyynto.setAsianumero("VN/1234/1234");
        return muutospyynto;
    }

    private void catchExpectedException(Class<?> expected, String msg, ThrowableFunction fn) throws Exception {
        try {
            fn.apply();
            fail("Exception is expected but not thrown");
        } catch (Exception e) {
            if (!expected.isInstance(e)) {
                throw new RuntimeException("Exception is wrong type: " + e.getClass().getSimpleName(), e);
            }
            else if (msg != null && !msg.equals(e.getMessage())) {
                throw new RuntimeException("Exception message does not match " + msg + " != " + e.getMessage() , e);
            }
        }
    }

    private interface ThrowableFunction {
        void apply() throws Exception;
    }

    private void setUserOrgToMuutospyyntoOrg(String oid, Muutospyynto muutospyynto) {
        Organisaatio organisaatio = new Organisaatio();
        organisaatio.setOid(oid);
        muutospyynto.setJarjestajaYtunnus(this.lupa.getJarjestajaYtunnus());
        this.lupa.setJarjestajaOid(oid);
        when(authService.getUserOrganisationOid()).thenReturn(oid);
    }

    private static class StringVarargMatcher extends ArgumentMatcher<String[]> implements VarargMatcher {

        private final String matching;

        private StringVarargMatcher(String matching) {
            this.matching = matching;
        }

        @Override
        public boolean matches(Object o) {
            return o != null && Arrays.asList((String[]) o).contains(matching);
        }
    }
}