/*
 * This file is generated by jOOQ.
 */
package fi.minedu.oiva.backend.model.jooq.tables.pojos;


import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;
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
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Maarays implements Serializable {

    private static final long serialVersionUID = -1528897378;

    private Long      id;
    private Long      parentId;
    private Long      lupaId;
    private Long      kohdeId;
    private String    koodisto;
    private String    koodiarvo;
    private String    arvo;
    private Long      maaraystyyppiId;
    private JsonNode  meta;
    private String    luoja;
    private Timestamp luontipvm;
    private String    paivittaja;
    private Timestamp paivityspvm;
    private Integer   koodistoversio;
    private UUID      uuid;
    private String    orgOid;

    public Maarays() {}

    public Maarays(Maarays value) {
        this.id = value.id;
        this.parentId = value.parentId;
        this.lupaId = value.lupaId;
        this.kohdeId = value.kohdeId;
        this.koodisto = value.koodisto;
        this.koodiarvo = value.koodiarvo;
        this.arvo = value.arvo;
        this.maaraystyyppiId = value.maaraystyyppiId;
        this.meta = value.meta;
        this.luoja = value.luoja;
        this.luontipvm = value.luontipvm;
        this.paivittaja = value.paivittaja;
        this.paivityspvm = value.paivityspvm;
        this.koodistoversio = value.koodistoversio;
        this.uuid = value.uuid;
        this.orgOid = value.orgOid;
    }

    public Maarays(
        Long      id,
        Long      parentId,
        Long      lupaId,
        Long      kohdeId,
        String    koodisto,
        String    koodiarvo,
        String    arvo,
        Long      maaraystyyppiId,
        JsonNode  meta,
        String    luoja,
        Timestamp luontipvm,
        String    paivittaja,
        Timestamp paivityspvm,
        Integer   koodistoversio,
        UUID      uuid,
        String    orgOid
    ) {
        this.id = id;
        this.parentId = parentId;
        this.lupaId = lupaId;
        this.kohdeId = kohdeId;
        this.koodisto = koodisto;
        this.koodiarvo = koodiarvo;
        this.arvo = arvo;
        this.maaraystyyppiId = maaraystyyppiId;
        this.meta = meta;
        this.luoja = luoja;
        this.luontipvm = luontipvm;
        this.paivittaja = paivittaja;
        this.paivityspvm = paivityspvm;
        this.koodistoversio = koodistoversio;
        this.uuid = uuid;
        this.orgOid = orgOid;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @NotNull
    public Long getLupaId() {
        return this.lupaId;
    }

    public void setLupaId(Long lupaId) {
        this.lupaId = lupaId;
    }

    @NotNull
    public Long getKohdeId() {
        return this.kohdeId;
    }

    public void setKohdeId(Long kohdeId) {
        this.kohdeId = kohdeId;
    }

    @Size(max = 255)
    public String getKoodisto() {
        return this.koodisto;
    }

    public void setKoodisto(String koodisto) {
        this.koodisto = koodisto;
    }

    @NotNull
    public String getKoodiarvo() {
        return this.koodiarvo;
    }

    public void setKoodiarvo(String koodiarvo) {
        this.koodiarvo = koodiarvo;
    }

    @Size(max = 255)
    public String getArvo() {
        return this.arvo;
    }

    public void setArvo(String arvo) {
        this.arvo = arvo;
    }

    @NotNull
    public Long getMaaraystyyppiId() {
        return this.maaraystyyppiId;
    }

    public void setMaaraystyyppiId(Long maaraystyyppiId) {
        this.maaraystyyppiId = maaraystyyppiId;
    }

    public JsonNode getMeta() {
        return this.meta;
    }

    public void setMeta(JsonNode meta) {
        this.meta = meta;
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

    public Integer getKoodistoversio() {
        return this.koodistoversio;
    }

    public void setKoodistoversio(Integer koodistoversio) {
        this.koodistoversio = koodistoversio;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Size(max = 255)
    public String getOrgOid() {
        return this.orgOid;
    }

    public void setOrgOid(String orgOid) {
        this.orgOid = orgOid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Maarays (");

        sb.append(id);
        sb.append(", ").append(parentId);
        sb.append(", ").append(lupaId);
        sb.append(", ").append(kohdeId);
        sb.append(", ").append(koodisto);
        sb.append(", ").append(koodiarvo);
        sb.append(", ").append(arvo);
        sb.append(", ").append(maaraystyyppiId);
        sb.append(", ").append(meta);
        sb.append(", ").append(luoja);
        sb.append(", ").append(luontipvm);
        sb.append(", ").append(paivittaja);
        sb.append(", ").append(paivityspvm);
        sb.append(", ").append(koodistoversio);
        sb.append(", ").append(uuid);
        sb.append(", ").append(orgOid);

        sb.append(")");
        return sb.toString();
    }
}
