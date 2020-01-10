package fi.minedu.oiva.backend.model.security.annotations;

/*
 * OIVA_APP
 * OIVA_APP_ADMIN
 * OIVA_APP_ESITTELIJA
 * OIVA_APP_NIMENKIRJOITTAJA
 * OIVA_APP_KAYTTAJA
 * OIVA_APP_KATSELIJA
*/

import java.util.Arrays;
import java.util.List;

public interface OivaAccess {

    enum Type {
        All,
        OrganizationAndPublic,
        OnlyPublic
    }

    String Context_Oiva = "OIVA_APP";
    String Context_Yllapitaja = "OIVA_APP_ADMIN";
    String Context_Esittelija = "ESITTELIJA";
    String Context_Kayttaja = "MUOKKAAJA";
    String Context_Katselija = "KATSELIJA";
    String Context_Nimenkirjoittaja = "NIMENKIRJOITTAJA";

    String Role_Application = Context_Oiva;
    String Role_Yllapitaja = Context_Yllapitaja;
    String Role_Esittelija = Context_Oiva + "_" + Context_Esittelija;
    String Role_Kayttaja = Context_Oiva + "_" + Context_Kayttaja;
    String Role_Katselija = Context_Oiva + "_" + Context_Katselija;
    String Role_Nimenkirjoittaja = Context_Oiva + "_" + Context_Nimenkirjoittaja;

    String Application = "hasAuthority('" + Role_Application + "')";
    String Yllapitaja = Application + " and hasAuthority('" + Role_Yllapitaja + "')";
    String Esittelija = Application + " and hasAuthority('" + Role_Esittelija + "')";
    String Nimenkirjoittaja = Application + " and hasAuthority('" + Role_Nimenkirjoittaja + "')";
    String Kayttaja = Application + " and hasAnyAuthority('" + Role_Nimenkirjoittaja + "," + Role_Kayttaja + "')";
    String Katselija = Application + " and hasAnyAuthority('" + Role_Nimenkirjoittaja + "," + Role_Kayttaja + "," +
            Role_Katselija + "')";

    List<String> roles = Arrays.asList(
            Role_Application,
            Role_Yllapitaja,
            Role_Esittelija,
            Role_Kayttaja,
            Role_Katselija,
            Role_Nimenkirjoittaja
    );
}
