/*
 * This file is generated by jOOQ.
 */
package fi.minedu.oiva.backend.model.jooq.tables.records;


import com.fasterxml.jackson.databind.JsonNode;

import fi.minedu.oiva.backend.model.jooq.tables.Maarays;

import java.sql.Timestamp;
import java.util.UUID;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record16;
import org.jooq.Row16;
import org.jooq.impl.UpdatableRecordImpl;


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
public class MaaraysRecord extends UpdatableRecordImpl<MaaraysRecord> implements Record16<Long, Long, Long, Long, String, String, String, Long, JsonNode, String, Timestamp, String, Timestamp, Integer, UUID, String> {

    private static final long serialVersionUID = -957762399;

    /**
     * Setter for <code>maarays.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>maarays.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>maarays.parent_id</code>.
     */
    public void setParentId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>maarays.parent_id</code>.
     */
    public Long getParentId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>maarays.lupa_id</code>.
     */
    public void setLupaId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>maarays.lupa_id</code>.
     */
    @NotNull
    public Long getLupaId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>maarays.kohde_id</code>.
     */
    public void setKohdeId(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>maarays.kohde_id</code>.
     */
    @NotNull
    public Long getKohdeId() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>maarays.koodisto</code>.
     */
    public void setKoodisto(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>maarays.koodisto</code>.
     */
    @Size(max = 255)
    public String getKoodisto() {
        return (String) get(4);
    }

    /**
     * Setter for <code>maarays.koodiarvo</code>.
     */
    public void setKoodiarvo(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>maarays.koodiarvo</code>.
     */
    @NotNull
    public String getKoodiarvo() {
        return (String) get(5);
    }

    /**
     * Setter for <code>maarays.arvo</code>.
     */
    public void setArvo(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>maarays.arvo</code>.
     */
    @Size(max = 255)
    public String getArvo() {
        return (String) get(6);
    }

    /**
     * Setter for <code>maarays.maaraystyyppi_id</code>.
     */
    public void setMaaraystyyppiId(Long value) {
        set(7, value);
    }

    /**
     * Getter for <code>maarays.maaraystyyppi_id</code>.
     */
    @NotNull
    public Long getMaaraystyyppiId() {
        return (Long) get(7);
    }

    /**
     * Setter for <code>maarays.meta</code>.
     */
    public void setMeta(JsonNode value) {
        set(8, value);
    }

    /**
     * Getter for <code>maarays.meta</code>.
     */
    public JsonNode getMeta() {
        return (JsonNode) get(8);
    }

    /**
     * Setter for <code>maarays.luoja</code>.
     */
    public void setLuoja(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>maarays.luoja</code>.
     */
    public String getLuoja() {
        return (String) get(9);
    }

    /**
     * Setter for <code>maarays.luontipvm</code>.
     */
    public void setLuontipvm(Timestamp value) {
        set(10, value);
    }

    /**
     * Getter for <code>maarays.luontipvm</code>.
     */
    public Timestamp getLuontipvm() {
        return (Timestamp) get(10);
    }

    /**
     * Setter for <code>maarays.paivittaja</code>.
     */
    public void setPaivittaja(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>maarays.paivittaja</code>.
     */
    public String getPaivittaja() {
        return (String) get(11);
    }

    /**
     * Setter for <code>maarays.paivityspvm</code>.
     */
    public void setPaivityspvm(Timestamp value) {
        set(12, value);
    }

    /**
     * Getter for <code>maarays.paivityspvm</code>.
     */
    public Timestamp getPaivityspvm() {
        return (Timestamp) get(12);
    }

    /**
     * Setter for <code>maarays.koodistoversio</code>.
     */
    public void setKoodistoversio(Integer value) {
        set(13, value);
    }

    /**
     * Getter for <code>maarays.koodistoversio</code>.
     */
    public Integer getKoodistoversio() {
        return (Integer) get(13);
    }

    /**
     * Setter for <code>maarays.uuid</code>.
     */
    public void setUuid(UUID value) {
        set(14, value);
    }

    /**
     * Getter for <code>maarays.uuid</code>.
     */
    public UUID getUuid() {
        return (UUID) get(14);
    }

    /**
     * Setter for <code>maarays.org_oid</code>.
     */
    public void setOrgOid(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>maarays.org_oid</code>.
     */
    @Size(max = 255)
    public String getOrgOid() {
        return (String) get(15);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record16 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row16<Long, Long, Long, Long, String, String, String, Long, JsonNode, String, Timestamp, String, Timestamp, Integer, UUID, String> fieldsRow() {
        return (Row16) super.fieldsRow();
    }

    @Override
    public Row16<Long, Long, Long, Long, String, String, String, Long, JsonNode, String, Timestamp, String, Timestamp, Integer, UUID, String> valuesRow() {
        return (Row16) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Maarays.MAARAYS.ID;
    }

    @Override
    public Field<Long> field2() {
        return Maarays.MAARAYS.PARENT_ID;
    }

    @Override
    public Field<Long> field3() {
        return Maarays.MAARAYS.LUPA_ID;
    }

    @Override
    public Field<Long> field4() {
        return Maarays.MAARAYS.KOHDE_ID;
    }

    @Override
    public Field<String> field5() {
        return Maarays.MAARAYS.KOODISTO;
    }

    @Override
    public Field<String> field6() {
        return Maarays.MAARAYS.KOODIARVO;
    }

    @Override
    public Field<String> field7() {
        return Maarays.MAARAYS.ARVO;
    }

    @Override
    public Field<Long> field8() {
        return Maarays.MAARAYS.MAARAYSTYYPPI_ID;
    }

    @Override
    public Field<JsonNode> field9() {
        return Maarays.MAARAYS.META;
    }

    @Override
    public Field<String> field10() {
        return Maarays.MAARAYS.LUOJA;
    }

    @Override
    public Field<Timestamp> field11() {
        return Maarays.MAARAYS.LUONTIPVM;
    }

    @Override
    public Field<String> field12() {
        return Maarays.MAARAYS.PAIVITTAJA;
    }

    @Override
    public Field<Timestamp> field13() {
        return Maarays.MAARAYS.PAIVITYSPVM;
    }

    @Override
    public Field<Integer> field14() {
        return Maarays.MAARAYS.KOODISTOVERSIO;
    }

    @Override
    public Field<UUID> field15() {
        return Maarays.MAARAYS.UUID;
    }

    @Override
    public Field<String> field16() {
        return Maarays.MAARAYS.ORG_OID;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getParentId();
    }

    @Override
    public Long component3() {
        return getLupaId();
    }

    @Override
    public Long component4() {
        return getKohdeId();
    }

    @Override
    public String component5() {
        return getKoodisto();
    }

    @Override
    public String component6() {
        return getKoodiarvo();
    }

    @Override
    public String component7() {
        return getArvo();
    }

    @Override
    public Long component8() {
        return getMaaraystyyppiId();
    }

    @Override
    public JsonNode component9() {
        return getMeta();
    }

    @Override
    public String component10() {
        return getLuoja();
    }

    @Override
    public Timestamp component11() {
        return getLuontipvm();
    }

    @Override
    public String component12() {
        return getPaivittaja();
    }

    @Override
    public Timestamp component13() {
        return getPaivityspvm();
    }

    @Override
    public Integer component14() {
        return getKoodistoversio();
    }

    @Override
    public UUID component15() {
        return getUuid();
    }

    @Override
    public String component16() {
        return getOrgOid();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getParentId();
    }

    @Override
    public Long value3() {
        return getLupaId();
    }

    @Override
    public Long value4() {
        return getKohdeId();
    }

    @Override
    public String value5() {
        return getKoodisto();
    }

    @Override
    public String value6() {
        return getKoodiarvo();
    }

    @Override
    public String value7() {
        return getArvo();
    }

    @Override
    public Long value8() {
        return getMaaraystyyppiId();
    }

    @Override
    public JsonNode value9() {
        return getMeta();
    }

    @Override
    public String value10() {
        return getLuoja();
    }

    @Override
    public Timestamp value11() {
        return getLuontipvm();
    }

    @Override
    public String value12() {
        return getPaivittaja();
    }

    @Override
    public Timestamp value13() {
        return getPaivityspvm();
    }

    @Override
    public Integer value14() {
        return getKoodistoversio();
    }

    @Override
    public UUID value15() {
        return getUuid();
    }

    @Override
    public String value16() {
        return getOrgOid();
    }

    @Override
    public MaaraysRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public MaaraysRecord value2(Long value) {
        setParentId(value);
        return this;
    }

    @Override
    public MaaraysRecord value3(Long value) {
        setLupaId(value);
        return this;
    }

    @Override
    public MaaraysRecord value4(Long value) {
        setKohdeId(value);
        return this;
    }

    @Override
    public MaaraysRecord value5(String value) {
        setKoodisto(value);
        return this;
    }

    @Override
    public MaaraysRecord value6(String value) {
        setKoodiarvo(value);
        return this;
    }

    @Override
    public MaaraysRecord value7(String value) {
        setArvo(value);
        return this;
    }

    @Override
    public MaaraysRecord value8(Long value) {
        setMaaraystyyppiId(value);
        return this;
    }

    @Override
    public MaaraysRecord value9(JsonNode value) {
        setMeta(value);
        return this;
    }

    @Override
    public MaaraysRecord value10(String value) {
        setLuoja(value);
        return this;
    }

    @Override
    public MaaraysRecord value11(Timestamp value) {
        setLuontipvm(value);
        return this;
    }

    @Override
    public MaaraysRecord value12(String value) {
        setPaivittaja(value);
        return this;
    }

    @Override
    public MaaraysRecord value13(Timestamp value) {
        setPaivityspvm(value);
        return this;
    }

    @Override
    public MaaraysRecord value14(Integer value) {
        setKoodistoversio(value);
        return this;
    }

    @Override
    public MaaraysRecord value15(UUID value) {
        setUuid(value);
        return this;
    }

    @Override
    public MaaraysRecord value16(String value) {
        setOrgOid(value);
        return this;
    }

    @Override
    public MaaraysRecord values(Long value1, Long value2, Long value3, Long value4, String value5, String value6, String value7, Long value8, JsonNode value9, String value10, Timestamp value11, String value12, Timestamp value13, Integer value14, UUID value15, String value16) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MaaraysRecord
     */
    public MaaraysRecord() {
        super(Maarays.MAARAYS);
    }

    /**
     * Create a detached, initialised MaaraysRecord
     */
    public MaaraysRecord(Long id, Long parentId, Long lupaId, Long kohdeId, String koodisto, String koodiarvo, String arvo, Long maaraystyyppiId, JsonNode meta, String luoja, Timestamp luontipvm, String paivittaja, Timestamp paivityspvm, Integer koodistoversio, UUID uuid, String orgOid) {
        super(Maarays.MAARAYS);

        set(0, id);
        set(1, parentId);
        set(2, lupaId);
        set(3, kohdeId);
        set(4, koodisto);
        set(5, koodiarvo);
        set(6, arvo);
        set(7, maaraystyyppiId);
        set(8, meta);
        set(9, luoja);
        set(10, luontipvm);
        set(11, paivittaja);
        set(12, paivityspvm);
        set(13, koodistoversio);
        set(14, uuid);
        set(15, orgOid);
    }
}
