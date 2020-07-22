/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.model.jooq.tables.pojos;


import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

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
public class Muutospyynto implements Serializable {

    private static final long serialVersionUID = 664809006;

    private Long      id;
    private Long      lupaId;
    private Date      hakupvm;
    private Date      voimassaalkupvm;
    private Date      voimassaloppupvm;
    private Long      paatoskierrosId;
    private String    tila;
    private String    jarjestajaYtunnus;
    private String    luoja;
    private Timestamp luontipvm;
    private String    paivittaja;
    private Timestamp paivityspvm;
    private UUID      uuid;
    private JsonNode  meta;
    private String    alkupera;
    private String    asianumero;
    private Date      paatospvm;
    private String    diaarinumero;
    private String    jarjestajaOid;

    public Muutospyynto() {}

    public Muutospyynto(Muutospyynto value) {
        this.id = value.id;
        this.lupaId = value.lupaId;
        this.hakupvm = value.hakupvm;
        this.voimassaalkupvm = value.voimassaalkupvm;
        this.voimassaloppupvm = value.voimassaloppupvm;
        this.paatoskierrosId = value.paatoskierrosId;
        this.tila = value.tila;
        this.jarjestajaYtunnus = value.jarjestajaYtunnus;
        this.luoja = value.luoja;
        this.luontipvm = value.luontipvm;
        this.paivittaja = value.paivittaja;
        this.paivityspvm = value.paivityspvm;
        this.uuid = value.uuid;
        this.meta = value.meta;
        this.alkupera = value.alkupera;
        this.asianumero = value.asianumero;
        this.paatospvm = value.paatospvm;
        this.diaarinumero = value.diaarinumero;
        this.jarjestajaOid = value.jarjestajaOid;
    }

    public Muutospyynto(
        Long      id,
        Long      lupaId,
        Date      hakupvm,
        Date      voimassaalkupvm,
        Date      voimassaloppupvm,
        Long      paatoskierrosId,
        String    tila,
        String    jarjestajaYtunnus,
        String    luoja,
        Timestamp luontipvm,
        String    paivittaja,
        Timestamp paivityspvm,
        UUID      uuid,
        JsonNode  meta,
        String    alkupera,
        String    asianumero,
        Date      paatospvm,
        String    diaarinumero,
        String    jarjestajaOid
    ) {
        this.id = id;
        this.lupaId = lupaId;
        this.hakupvm = hakupvm;
        this.voimassaalkupvm = voimassaalkupvm;
        this.voimassaloppupvm = voimassaloppupvm;
        this.paatoskierrosId = paatoskierrosId;
        this.tila = tila;
        this.jarjestajaYtunnus = jarjestajaYtunnus;
        this.luoja = luoja;
        this.luontipvm = luontipvm;
        this.paivittaja = paivittaja;
        this.paivityspvm = paivityspvm;
        this.uuid = uuid;
        this.meta = meta;
        this.alkupera = alkupera;
        this.asianumero = asianumero;
        this.paatospvm = paatospvm;
        this.diaarinumero = diaarinumero;
        this.jarjestajaOid = jarjestajaOid;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public Long getLupaId() {
        return this.lupaId;
    }

    public void setLupaId(Long lupaId) {
        this.lupaId = lupaId;
    }

    public Date getHakupvm() {
        return this.hakupvm;
    }

    public void setHakupvm(Date hakupvm) {
        this.hakupvm = hakupvm;
    }

    public Date getVoimassaalkupvm() {
        return this.voimassaalkupvm;
    }

    public void setVoimassaalkupvm(Date voimassaalkupvm) {
        this.voimassaalkupvm = voimassaalkupvm;
    }

    public Date getVoimassaloppupvm() {
        return this.voimassaloppupvm;
    }

    public void setVoimassaloppupvm(Date voimassaloppupvm) {
        this.voimassaloppupvm = voimassaloppupvm;
    }

    @NotNull
    public Long getPaatoskierrosId() {
        return this.paatoskierrosId;
    }

    public void setPaatoskierrosId(Long paatoskierrosId) {
        this.paatoskierrosId = paatoskierrosId;
    }

    @NotNull
    @Size(max = 20)
    public String getTila() {
        return this.tila;
    }

    public void setTila(String tila) {
        this.tila = tila;
    }

    @NotNull
    @Size(max = 10)
    public String getJarjestajaYtunnus() {
        return this.jarjestajaYtunnus;
    }

    public void setJarjestajaYtunnus(String jarjestajaYtunnus) {
        this.jarjestajaYtunnus = jarjestajaYtunnus;
    }

    public String getLuoja() {
        return this.luoja;
    }

    public void setLuoja(String luoja) {
        this.luoja = luoja;
    }

    public Timestamp getLuontipvm() {
        return this.luontipvm;
    }

    public void setLuontipvm(Timestamp luontipvm) {
        this.luontipvm = luontipvm;
    }

    public String getPaivittaja() {
        return this.paivittaja;
    }

    public void setPaivittaja(String paivittaja) {
        this.paivittaja = paivittaja;
    }

    public Timestamp getPaivityspvm() {
        return this.paivityspvm;
    }

    public void setPaivityspvm(Timestamp paivityspvm) {
        this.paivityspvm = paivityspvm;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public JsonNode getMeta() {
        return this.meta;
    }

    public void setMeta(JsonNode meta) {
        this.meta = meta;
    }

    public String getUuidString() {
        return getUuid() != null ? getUuid().toString() : null;
    }

    @NotNull
    @Size(max = 10)
    public String getAlkupera() {
        return this.alkupera;
    }

    public void setAlkupera(String alkupera) {
        this.alkupera = alkupera;
    }

    @Size(max = 16)
    public String getAsianumero() {
        return this.asianumero;
    }

    public void setAsianumero(String asianumero) {
        this.asianumero = asianumero;
    }

    public Date getPaatospvm() {
        return this.paatospvm;
    }

    public void setPaatospvm(Date paatospvm) {
        this.paatospvm = paatospvm;
    }

    @Size(max = 20)
    public String getDiaarinumero() {
        return this.diaarinumero;
    }

    public void setDiaarinumero(String diaarinumero) {
        this.diaarinumero = diaarinumero;
    }

    public String getJarjestajaOid() {
        return this.jarjestajaOid;
    }

    public void setJarjestajaOid(String jarjestajaOid) {
        this.jarjestajaOid = jarjestajaOid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Muutospyynto (");

        sb.append(id);
        sb.append(", ").append(lupaId);
        sb.append(", ").append(hakupvm);
        sb.append(", ").append(voimassaalkupvm);
        sb.append(", ").append(voimassaloppupvm);
        sb.append(", ").append(paatoskierrosId);
        sb.append(", ").append(tila);
        sb.append(", ").append(jarjestajaYtunnus);
        sb.append(", ").append(luoja);
        sb.append(", ").append(luontipvm);
        sb.append(", ").append(paivittaja);
        sb.append(", ").append(paivityspvm);
        sb.append(", ").append(uuid);
        sb.append(", ").append(meta);
        sb.append(", ").append(alkupera);
        sb.append(", ").append(asianumero);
        sb.append(", ").append(paatospvm);
        sb.append(", ").append(diaarinumero);
        sb.append(", ").append(jarjestajaOid);

        sb.append(")");
        return sb.toString();
    }
}
