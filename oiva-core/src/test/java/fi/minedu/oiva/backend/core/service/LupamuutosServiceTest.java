package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.core.exception.ForbiddenException;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.oiva.Muutospyynto;
import fi.minedu.oiva.backend.model.entity.opintopolku.Organisaatio;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import org.apache.commons.lang3.Functions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;

import static fi.minedu.oiva.backend.core.service.MuutospyyntoService.Muutospyyntotila;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class LupamuutosServiceTest {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private LupamuutosService lupamuutosService;
    private AuthService authService = mock(AuthService.class);
    private LupaService lupaService = mock(LupaService.class);

    private MuutospyyntoService service = mock(MuutospyyntoService.class);
    private Lupa lupa;

    @Before
    public void setUp() {
        this.lupamuutosService = new LupamuutosService(service, authService);
        this.lupa = new Lupa();
        this.lupa.setJarjestajaYtunnus("123");
        when(lupaService.getByUuid(anyString())).thenReturn(Optional.of(lupa));
        Answer<Optional<Muutospyynto>> firstArgument = invocationOnMock -> {
            Muutospyynto mp = (Muutospyynto) invocationOnMock.getArguments()[0];
            if (mp.getId() == null) {
                mp.setId(1L);
            }
            return Optional.of(mp);
        };
        when(service.save(any(Muutospyynto.class), anyMap())).thenAnswer(firstArgument);
        when(service.update(any(Muutospyynto.class), anyMap())).thenAnswer(firstArgument);
    }

    @Test
    public void onlyEsittelijaCanCreateMuutospyynto() throws Throwable {
        Muutospyynto muutospyynto = generateMuutospyynto();
        doReturn(Optional.of(muutospyynto)).when(service).getById(anyLong());

        final String allowedRole = OivaAccess.Role_Esittelija;
        Functions.FailableCallable<Optional<Muutospyynto>, Throwable> create = () -> lupamuutosService.executeAction(null, MuutospyyntoService.Action.LUO, muutospyynto, new HashMap<>());

        for (final String role : OivaAccess.roles) {
            reset(authService);
            setUserOrgToMuutospyyntoOrg("oid", muutospyynto);
            when(authService.hasAnyRole(eq(role))).thenReturn(true);

            logger.debug("Testing role " + role);

            if (allowedRole.equals(role)) {
                try {
                    create.call();
                    ArgumentCaptor<Muutospyynto> mp = ArgumentCaptor.forClass(Muutospyynto.class);
                    // Verify save is called once
                    // NB! Resettinginvocation counter is not needed because only one role can call save
                    verify(service).save(mp.capture(), anyMap());
                    assertEquals(Muutospyyntotila.VALMISTELUSSA.toString(), mp.getValue().getTila());
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
    }

    @Test
    public void esittelijaCanSaveMuutospyynto() throws Throwable {
        Muutospyynto muutospyynto = generateMuutospyynto();
        muutospyynto.setUuid(new UUID(1, 2));
        muutospyynto.setTila(Muutospyyntotila.VALMISTELUSSA.toString());
        doReturn(Optional.of(muutospyynto)).when(service).getByUuid(anyString());
        doReturn(Optional.of(muutospyynto)).when(service).getById(anyLong());

        final String allowedRole = OivaAccess.Role_Esittelija;
        Functions.FailableCallable<Optional<Muutospyynto>, Throwable> update = () -> lupamuutosService.executeAction(muutospyynto.getUuid().toString(), MuutospyyntoService.Action.TALLENNA, muutospyynto, new HashMap<>());

        // Test user in same organization
        for (final String role : OivaAccess.roles) {
            reset(authService);
            setUserOrgToMuutospyyntoOrg("oid", muutospyynto);
            when(authService.hasAnyRole(eq(role))).thenReturn(true);

            logger.debug("Testing role " + role);

            if (allowedRole.equals(role)) {
                try {
                    update.call();
                    // Verify save is called once
                    // NB! Resettinginvocation counter is not needed because only one role can call save
                    verify(service).update(any(Muutospyynto.class), anyMap());
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
            when(authService.hasAnyRole(eq(role))).thenReturn(true);
            logger.debug("Testing role " + role);
            try {
                update.call();
                if (!allowedRole.equals(role)) {
                    fail("Role " + role + " is in different organization and should not have access to update new muutospyynto");
                }
            } catch (ForbiddenException e) {
                // NOP
            }
        }
    }

    private Muutospyynto generateMuutospyynto() {
        Muutospyynto muutospyynto = new Muutospyynto();
        muutospyynto.setLiitteet(new LinkedList<>());
        muutospyynto.setMuutokset(new LinkedList<>());
        muutospyynto.setJarjestajaYtunnus(lupa.getJarjestajaYtunnus());
        muutospyynto.setLupaUuid(UUID.randomUUID().toString());
        return muutospyynto;
    }

    private void setUserOrgToMuutospyyntoOrg(String oid, Muutospyynto muutospyynto) {
        Organisaatio organisaatio = new Organisaatio();
        organisaatio.setOid(oid);
        muutospyynto.setJarjestajaYtunnus(this.lupa.getJarjestajaYtunnus());
        this.lupa.setJarjestajaOid(oid);
        when(authService.getUserOrganisationOid()).thenReturn(oid);
    }

}