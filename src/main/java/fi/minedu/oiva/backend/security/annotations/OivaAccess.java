package fi.minedu.oiva.backend.security.annotations;

public interface OivaAccess {

    enum Type {
        All,
        OrganizationAndPublic,
        Public
    }

    String Role_Application = "APP_KOUTE";
    String Role_Yllapitaja = "APP_KOUTE_YLLAPITAJA";
    String Role_Esittelija = "APP_KOUTE_ESITTELIJA";
    String Role_Kayttaja = "APP_KOUTE_KAYTTAJA";

    String Application = "hasAuthority('" + Role_Application + "')";
    String Yllapitaja = Application + " and hasAuthority('" + Role_Yllapitaja + "')";
    String Esittelija = Application + " and hasAuthority('" + Role_Esittelija + "')";
    String Kayttaja = Application + " and hasAuthority('" + Role_Kayttaja + "')";
}
