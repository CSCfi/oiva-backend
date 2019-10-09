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
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Liite implements Serializable {

    private static final long serialVersionUID = -67664477;

    private Long      id;
    private String    nimi;
    private String    polku;
    private Boolean   tila;
    private String    luoja;
    private Timestamp luontipvm;
    private String    paivittaja;
    private Timestamp paivityspvm;
    private Long      koko;
    private JsonNode  meta;
    private String    tyyppi;
    private String    kieli;
    private UUID      uuid;
    private Boolean   salainen;
    private String    paikka;
    private String    tiedostoId;

    public Liite() {}

    public Liite(Liite value) {
        this.id = value.id;
        this.nimi = value.nimi;
        this.polku = value.polku;
        this.tila = value.tila;
        this.luoja = value.luoja;
        this.luontipvm = value.luontipvm;
        this.paivittaja = value.paivittaja;
        this.paivityspvm = value.paivityspvm;
        this.koko = value.koko;
        this.meta = value.meta;
        this.tyyppi = value.tyyppi;
        this.kieli = value.kieli;
        this.uuid = value.uuid;
        this.salainen = value.salainen;
        this.paikka = value.paikka;
        this.tiedostoId = value.tiedostoId;
    }

    public Liite(
        Long      id,
        String    nimi,
        String    polku,
        Boolean   tila,
        String    luoja,
        Timestamp luontipvm,
        String    paivittaja,
        Timestamp paivityspvm,
        Long      koko,
        JsonNode  meta,
        String    tyyppi,
        String    kieli,
        UUID      uuid,
        Boolean   salainen,
        String    paikka,
        String    tiedostoId
    ) {
        this.id = id;
        this.nimi = nimi;
        this.polku = polku;
        this.tila = tila;
        this.luoja = luoja;
        this.luontipvm = luontipvm;
        this.paivittaja = paivittaja;
        this.paivityspvm = paivityspvm;
        this.koko = koko;
        this.meta = meta;
        this.tyyppi = tyyppi;
        this.kieli = kieli;
        this.uuid = uuid;
        this.salainen = salainen;
        this.paikka = paikka;
        this.tiedostoId = tiedostoId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    @Size(max = 255)
    public String getNimi() {
        return this.nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    @NotNull
    @Size(max = 255)
    public String getPolku() {
        return this.polku;
    }

    public void setPolku(String polku) {
        this.polku = polku;
    }

    public Boolean getTila() {
        return this.tila;
    }

    public void setTila(Boolean tila) {
        this.tila = tila;
    }

    @NotNull
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

    public Long getKoko() {
        return this.koko;
    }

    public void setKoko(Long koko) {
        this.koko = koko;
    }

    public JsonNode getMeta() {
        return this.meta;
    }

    public void setMeta(JsonNode meta) {
        this.meta = meta;
    }

    @NotNull
    @Size(max = 255)
    public String getTyyppi() {
        return this.tyyppi;
    }

    public void setTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    @NotNull
    @Size(max = 2)
    public String getKieli() {
        return this.kieli;
    }

    public void setKieli(String kieli) {
        this.kieli = kieli;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Boolean getSalainen() {
        return this.salainen;
    }

    public void setSalainen(Boolean salainen) {
        this.salainen = salainen;
    }

    @Size(max = 255)
    public String getPaikka() {
        return this.paikka;
    }

    public void setPaikka(String paikka) {
        this.paikka = paikka;
    }

    @Size(max = 255)
    public String getTiedostoId() {
        return this.tiedostoId;
    }

    public void setTiedostoId(String tiedostoId) {
        this.tiedostoId = tiedostoId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Liite (");

        sb.append(id);
        sb.append(", ").append(nimi);
        sb.append(", ").append(polku);
        sb.append(", ").append(tila);
        sb.append(", ").append(luoja);
        sb.append(", ").append(luontipvm);
        sb.append(", ").append(paivittaja);
        sb.append(", ").append(paivityspvm);
        sb.append(", ").append(koko);
        sb.append(", ").append(meta);
        sb.append(", ").append(tyyppi);
        sb.append(", ").append(kieli);
        sb.append(", ").append(uuid);
        sb.append(", ").append(salainen);
        sb.append(", ").append(paikka);
        sb.append(", ").append(tiedostoId);

        sb.append(")");
        return sb.toString();
    }
}
