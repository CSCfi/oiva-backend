package fi.minedu.oiva.backend.security.annotations;

import java.util.regex.Pattern;

/*
 * APP_KOUTE
 * APP_KOUTE_YLLAPITAJA
 * APP_KOUTE_ESITTELIJA
 * APP_KOUTE_KAYTTAJA
 * APP_KOUTE_KAYTTAJA_111.222.333
*/

public interface OivaAccess {

    enum Type {
        All,
        OrganizationAndPublic,
        OnlyPublic
    }

    Pattern Format_Role = Pattern.compile("^([A-Za-z_]+)(?:_((?:\\d+\\.?)+)?)?$");

    String Context_Application = "APP";
    String Context_Oiva = "KOUTE";
    String Context_Yllapitaja = "YLLAPITAJA";
    String Context_Esittelija = "ESITTELIJA";
    String Context_Kayttaja = "KAYTTAJA";

    String Role_Application = Context_Application + "_" + Context_Oiva;
    String Role_Yllapitaja = Context_Application + "_" + Context_Oiva + "_" + Context_Yllapitaja;
    String Role_Esittelija = Context_Application + "_" + Context_Oiva + "_" + Context_Esittelija;
    String Role_Kayttaja = Context_Application + "_" + Context_Oiva + "_" + Context_Kayttaja;

    String Application = "hasAuthority('" + Role_Application + "')";
    String Yllapitaja = Application + " and hasAuthority('" + Role_Yllapitaja + "')";
    String Esittelija = Application + " and hasAuthority('" + Role_Esittelija + "')";
    String Kayttaja = Application + " and hasAuthority('" + Role_Kayttaja + "')";
}
