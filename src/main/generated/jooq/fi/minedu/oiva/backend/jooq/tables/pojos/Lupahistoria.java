/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.jooq.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


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
public class Lupahistoria implements Serializable {

    private static final long serialVersionUID = -854682780;

    private Long   id;
    private String diaarinumero;
    private String ytunnus;
    private String oid;
    private String maakunta;
    private String tila;
    private String voimassaolo;
    private String paatospvm;
    private String filename;

    public Lupahistoria() {}

    public Lupahistoria(Lupahistoria value) {
        this.id = value.id;
        this.diaarinumero = value.diaarinumero;
        this.ytunnus = value.ytunnus;
        this.oid = value.oid;
        this.maakunta = value.maakunta;
        this.tila = value.tila;
        this.voimassaolo = value.voimassaolo;
        this.paatospvm = value.paatospvm;
        this.filename = value.filename;
    }

    public Lupahistoria(
        Long   id,
        String diaarinumero,
        String ytunnus,
        String oid,
        String maakunta,
        String tila,
        String voimassaolo,
        String paatospvm,
        String filename
    ) {
        this.id = id;
        this.diaarinumero = diaarinumero;
        this.ytunnus = ytunnus;
        this.oid = oid;
        this.maakunta = maakunta;
        this.tila = tila;
        this.voimassaolo = voimassaolo;
        this.paatospvm = paatospvm;
        this.filename = filename;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    @Size(max = 20)
    public String getDiaarinumero() {
        return this.diaarinumero;
    }

    public void setDiaarinumero(String diaarinumero) {
        this.diaarinumero = diaarinumero;
    }

    @NotNull
    @Size(max = 10)
    public String getYtunnus() {
        return this.ytunnus;
    }

    public void setYtunnus(String ytunnus) {
        this.ytunnus = ytunnus;
    }

    @NotNull
    @Size(max = 255)
    public String getOid() {
        return this.oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    @NotNull
    @Size(max = 100)
    public String getMaakunta() {
        return this.maakunta;
    }

    public void setMaakunta(String maakunta) {
        this.maakunta = maakunta;
    }

    @NotNull
    @Size(max = 100)
    public String getTila() {
        return this.tila;
    }

    public void setTila(String tila) {
        this.tila = tila;
    }

    @NotNull
    @Size(max = 100)
    public String getVoimassaolo() {
        return this.voimassaolo;
    }

    public void setVoimassaolo(String voimassaolo) {
        this.voimassaolo = voimassaolo;
    }

    @NotNull
    @Size(max = 20)
    public String getPaatospvm() {
        return this.paatospvm;
    }

    public void setPaatospvm(String paatospvm) {
        this.paatospvm = paatospvm;
    }

    @NotNull
    @Size(max = 255)
    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Lupahistoria (");

        sb.append(id);
        sb.append(", ").append(diaarinumero);
        sb.append(", ").append(ytunnus);
        sb.append(", ").append(oid);
        sb.append(", ").append(maakunta);
        sb.append(", ").append(tila);
        sb.append(", ").append(voimassaolo);
        sb.append(", ").append(paatospvm);
        sb.append(", ").append(filename);

        sb.append(")");
        return sb.toString();
    }
}