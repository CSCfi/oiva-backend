package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.it.BaseIT;
import fi.minedu.oiva.backend.model.entity.OivaTemplates;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.oiva.Maarays;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.OutputStream;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BasePrinceXMLControllerIT extends BaseIT {

    private final String muutospyyntoUuid = "2b02c730-ebef-11e9-8d25-0242ac110023";

    @Override
    public void beforeTest() {
        setUpDb("sql/lupa_data.sql",
                "sql/maarays_data.sql",
                "sql/muutospyynto_data.sql",
                "sql/muutos_data_lisays_poisto.sql");

        when(pebbleService.toHTML(any(Lupa.class), any(OivaTemplates.RenderOptions.class))).thenReturn(Optional.of(""));
        when(princeXMLService.toPDF(anyString(), any(OutputStream.class), any(OivaTemplates.RenderOptions.class)))
                .thenReturn(true);
    }

    /**
     * Test generated lupa object based on existing lupa and muutospyynto.
     * Templates used to generate html files are in another repository so html and pdf generation are mocked
     */
    @Test
    public void getLupaFromMuutospyynto() {
        loginAs("testuser", "esittelija_org", OivaAccess.Context_Esittelija);
        ResponseEntity<Object> responseEntity = restTemplate.getForEntity(createURLWithPort("/api/pdf/esikatsele/" + muutospyyntoUuid), Object.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ArgumentCaptor<Lupa> lupaCaptor = ArgumentCaptor.forClass(Lupa.class);
        verify(pebbleService).toHTML(lupaCaptor.capture(), any(OivaTemplates.RenderOptions.class));

        // Verify correct lupa is generated from muutospyynto
        Lupa lupa = lupaCaptor.getValue();
        assertNotNull(lupa);

        // Case: new muutos (kieli: sv)
        Maarays kieli = lupa.getMaaraykset().stream()
                .filter(m -> "kieli".equals(m.getKoodisto()) && "sv".equals(m.getKoodiarvo()))
                .findFirst().orElse(null);
        assertNotNull(kieli);

        // Case: new muutos, alimaarays and alialimaarays are added to kielikoodisto (fi__ -> fi_lisatty -> fi_alimaarays)
        kieli = lupa.getMaaraykset().stream()
                .filter(m -> "kieli".equals(m.getKoodisto()) && "fi__".equals(m.getKoodiarvo()))
                .findFirst().orElse(null);
        assertNotNull(kieli);
        assertEquals(1, kieli.getAliMaaraykset().size());
        Maarays alimaarays = kieli.getAliMaaraykset().iterator().next();
        assertEquals("kieli", alimaarays.getKoodisto());
        assertEquals("fi_lisatty", alimaarays.getKoodiarvo());
        assertEquals(1, alimaarays.getAliMaaraykset().size());
        alimaarays = alimaarays.getAliMaaraykset().iterator().next();
        assertEquals("kieli", alimaarays.getKoodisto());
        assertEquals("fi_alimaarays", alimaarays.getKoodiarvo());

        // Case: maarays and alimaarays are removed (koulutus 123 -> koulutus 1231)
        assertEquals(0, lupa.getMaaraykset().stream().filter(m -> "koodisto".equals(m.getKoodisto()) && "123".equals(m.getKoodiarvo())).count());
        assertEquals(0, lupa.getMaaraykset().stream().filter(m -> "koodisto".equals(m.getKoodisto()) && "1231".equals(m.getKoodiarvo())).count());
    }
}
