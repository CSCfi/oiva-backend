/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.jooq.tables.pojos;


import fi.minedu.oiva.backend.entity.AsiatyyppiValue;
import fi.minedu.oiva.backend.entity.TranslatedString;

import java.io.Serializable;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Asiatyyppi implements Serializable {

    private static final long serialVersionUID = 1611333062;

    private Long             id;
    private AsiatyyppiValue  tunniste;
    private TranslatedString selite;

    public Asiatyyppi() {}

    public Asiatyyppi(Asiatyyppi value) {
        this.id = value.id;
        this.tunniste = value.tunniste;
        this.selite = value.selite;
    }

    public Asiatyyppi(
        Long             id,
        AsiatyyppiValue  tunniste,
        TranslatedString selite
    ) {
        this.id = id;
        this.tunniste = tunniste;
        this.selite = selite;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public AsiatyyppiValue getTunniste() {
        return this.tunniste;
    }

    public void setTunniste(AsiatyyppiValue tunniste) {
        this.tunniste = tunniste;
    }

    public TranslatedString getSelite() {
        return this.selite;
    }

    public void setSelite(TranslatedString selite) {
        this.selite = selite;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Asiatyyppi (");

        sb.append(id);
        sb.append(", ").append(tunniste);
        sb.append(", ").append(selite);

        sb.append(")");
        return sb.toString();
    }
}
