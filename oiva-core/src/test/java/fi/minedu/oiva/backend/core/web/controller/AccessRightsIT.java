package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.it.BaseIT;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertTrue;

public class AccessRightsIT extends BaseIT {

    private static final String org_id = "org1";

    @Override
    public void beforeTest() {

    }

    @Test
    public void loginUserTwice() {
        final String username = "user1";
        loginAs(username, org_id, OivaAccess.Context_Kayttaja);
        ResponseEntity<String> response = makeRequest("/api/auth/me", HttpStatus.OK);
        assertTrue(response.getBody().contains("\"permissionDecreased\":false"));

        resetRestTemplate();
        loginAs(username, org_id, OivaAccess.Context_Kayttaja);
        response = makeRequest("/api/auth/me", HttpStatus.OK);
        assertTrue("Response was: " + response.getBody(), response.getBody().contains("\"permissionDecreased\":false"));
    }

    @Test
    public void loginAnotherSameOrganizationUser() {
        loginAs("user1", org_id, OivaAccess.Context_Kayttaja);
        ResponseEntity<String> response = super.makeRequest("/api/auth/me", HttpStatus.OK);
        assertTrue(response.getBody().contains("\"permissionDecreased\":false"));

        resetRestTemplate();
        System.out.println("Logging in");
        loginAs("user2", org_id, OivaAccess.Context_Kayttaja);
        System.out.println("Logged");
        response = makeRequest("/api/auth/me", HttpStatus.OK);
        assertTrue("Response was: " + response.getBody(), response.getBody().contains("\"permissionDecreased\":true"));
    }
}
