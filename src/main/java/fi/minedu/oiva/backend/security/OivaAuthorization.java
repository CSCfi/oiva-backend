package fi.minedu.oiva.backend.security;

public interface OivaAuthorization {
    String ACCESS_ADMIN = "hasAuthority('APP_KOUTE_YLLAPITAJA')";
    String ACCESS_AUTHENTICATED = "isSignedIn()";
}
