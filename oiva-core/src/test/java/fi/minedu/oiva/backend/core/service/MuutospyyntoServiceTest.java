package fi.minedu.oiva.backend.core.service;

import com.google.common.collect.Sets;
import fi.minedu.oiva.backend.core.exception.ForbiddenException;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.oiva.Muutospyynto;
import fi.minedu.oiva.backend.model.entity.opintopolku.Organisaatio;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;

import static fi.minedu.oiva.backend.core.service.MuutospyyntoService.Muutospyyntotila;
import static fi.minedu.oiva.backend.model.jooq.Tables.MUUTOSPYYNTO;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.atLeastOnce;
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

    private MuutospyyntoService service;
    private Lupa lupa;

    @Before
    public void setUp() {
        this.service = spy(new MuutospyyntoService(dsl, authService, organisaatioService, liiteService,
                opintopolkuService, maaraysService, fileStorageService, asiatilamuutosService, lupaService, koodistoService));

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

        // Happy path
        when(authService.hasAnyRole(argThat(new StringVarargMatcher(OivaAccess.Role_Nimenkirjoittaja)))).thenReturn(true);
        service.executeAction(null, MuutospyyntoService.Action.LUO, muutospyynto, new HashMap<>());
        service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.TALLENNA, muutospyynto, new HashMap<>());
        when(authService.hasAnyRole(eq(OivaAccess.Role_Nimenkirjoittaja))).thenReturn(true);
        service.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.LAHETA);


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

    private Muutospyynto generateMuutospyynto() {
        Muutospyynto muutospyynto = new Muutospyynto();
        muutospyynto.setLiitteet(new LinkedList<>());
        muutospyynto.setMuutokset(new LinkedList<>());
        muutospyynto.setJarjestajaYtunnus(lupa.getJarjestajaYtunnus());
        muutospyynto.setLupaUuid(UUID.randomUUID().toString());
        muutospyynto.setAlkupera(MuutospyyntoService.Tyyppi.KJ.toString());
        return muutospyynto;
    }

    private void catchExpectedException(Class<?> expected, String msg, ThrowableFunction fn) throws Exception {
        try {
            fn.apply();
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