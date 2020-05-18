/*
 * This file is generated by jOOQ.
 */
package fi.minedu.oiva.backend.model.jooq.tables.records;


import com.fasterxml.jackson.databind.JsonNode;

import fi.minedu.oiva.backend.model.jooq.tables.Muutospyynto;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record20;
import org.jooq.Row20;
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
public class MuutospyyntoRecord extends UpdatableRecordImpl<MuutospyyntoRecord> implements Record20<Long, Long, Date, Date, Date, Long, String, String, String, Timestamp, String, Timestamp, UUID, JsonNode, String, String, Date, String, String, String> {

    private static final long serialVersionUID = 291270401;

    /**
     * Setter for <code>muutospyynto.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>muutospyynto.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>muutospyynto.lupa_id</code>.
     */
    public void setLupaId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>muutospyynto.lupa_id</code>.
     */
    public Long getLupaId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>muutospyynto.hakupvm</code>.
     */
    public void setHakupvm(Date value) {
        set(2, value);
    }

    /**
     * Getter for <code>muutospyynto.hakupvm</code>.
     */
    public Date getHakupvm() {
        return (Date) get(2);
    }

    /**
     * Setter for <code>muutospyynto.voimassaalkupvm</code>.
     */
    public void setVoimassaalkupvm(Date value) {
        set(3, value);
    }

    /**
     * Getter for <code>muutospyynto.voimassaalkupvm</code>.
     */
    public Date getVoimassaalkupvm() {
        return (Date) get(3);
    }

    /**
     * Setter for <code>muutospyynto.voimassaloppupvm</code>.
     */
    public void setVoimassaloppupvm(Date value) {
        set(4, value);
    }

    /**
     * Getter for <code>muutospyynto.voimassaloppupvm</code>.
     */
    public Date getVoimassaloppupvm() {
        return (Date) get(4);
    }

    /**
     * Setter for <code>muutospyynto.paatoskierros_id</code>.
     */
    public void setPaatoskierrosId(Long value) {
        set(5, value);
    }

    /**
     * Getter for <code>muutospyynto.paatoskierros_id</code>.
     */
    @NotNull
    public Long getPaatoskierrosId() {
        return (Long) get(5);
    }

    /**
     * Setter for <code>muutospyynto.tila</code>.
     */
    public void setTila(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>muutospyynto.tila</code>.
     */
    @NotNull
    @Size(max = 20)
    public String getTila() {
        return (String) get(6);
    }

    /**
     * Setter for <code>muutospyynto.jarjestaja_ytunnus</code>.
     */
    public void setJarjestajaYtunnus(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>muutospyynto.jarjestaja_ytunnus</code>.
     */
    @Size(max = 10)
    public String getJarjestajaYtunnus() {
        return (String) get(7);
    }

    /**
     * Setter for <code>muutospyynto.luoja</code>.
     */
    public void setLuoja(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>muutospyynto.luoja</code>.
     */
    public String getLuoja() {
        return (String) get(8);
    }

    /**
     * Setter for <code>muutospyynto.luontipvm</code>.
     */
    public void setLuontipvm(Timestamp value) {
        set(9, value);
    }

    /**
     * Getter for <code>muutospyynto.luontipvm</code>.
     */
    public Timestamp getLuontipvm() {
        return (Timestamp) get(9);
    }

    /**
     * Setter for <code>muutospyynto.paivittaja</code>.
     */
    public void setPaivittaja(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>muutospyynto.paivittaja</code>.
     */
    public String getPaivittaja() {
        return (String) get(10);
    }

    /**
     * Setter for <code>muutospyynto.paivityspvm</code>.
     */
    public void setPaivityspvm(Timestamp value) {
        set(11, value);
    }

    /**
     * Getter for <code>muutospyynto.paivityspvm</code>.
     */
    public Timestamp getPaivityspvm() {
        return (Timestamp) get(11);
    }

    /**
     * Setter for <code>muutospyynto.uuid</code>.
     */
    public void setUuid(UUID value) {
        set(12, value);
    }

    /**
     * Getter for <code>muutospyynto.uuid</code>.
     */
    public UUID getUuid() {
        return (UUID) get(12);
    }

    /**
     * Setter for <code>muutospyynto.meta</code>.
     */
    public void setMeta(JsonNode value) {
        set(13, value);
    }

    /**
     * Getter for <code>muutospyynto.meta</code>.
     */
    public JsonNode getMeta() {
        return (JsonNode) get(13);
    }

    /**
     * Setter for <code>muutospyynto.alkupera</code>.
     */
    public void setAlkupera(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>muutospyynto.alkupera</code>.
     */
    @NotNull
    @Size(max = 10)
    public String getAlkupera() {
        return (String) get(14);
    }

    /**
     * Setter for <code>muutospyynto.asianumero</code>.
     */
    public void setAsianumero(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>muutospyynto.asianumero</code>.
     */
    @Size(max = 17)
    public String getAsianumero() {
        return (String) get(15);
    }

    /**
     * Setter for <code>muutospyynto.paatospvm</code>.
     */
    public void setPaatospvm(Date value) {
        set(16, value);
    }

    /**
     * Getter for <code>muutospyynto.paatospvm</code>.
     */
    public Date getPaatospvm() {
        return (Date) get(16);
    }

    /**
     * Setter for <code>muutospyynto.diaarinumero</code>.
     */
    public void setDiaarinumero(String value) {
        set(17, value);
    }

    /**
     * Getter for <code>muutospyynto.diaarinumero</code>.
     */
    @Size(max = 255)
    public String getDiaarinumero() {
        return (String) get(17);
    }

    /**
     * Setter for <code>muutospyynto.jarjestaja_oid</code>.
     */
    public void setJarjestajaOid(String value) {
        set(18, value);
    }

    /**
     * Getter for <code>muutospyynto.jarjestaja_oid</code>.
     */
    public String getJarjestajaOid() {
        return (String) get(18);
    }

    /**
     * Setter for <code>muutospyynto.koulutustyyppi</code>.
     */
    public void setKoulutustyyppi(String value) {
        set(19, value);
    }

    /**
     * Getter for <code>muutospyynto.koulutustyyppi</code>.
     */
    public String getKoulutustyyppi() {
        return (String) get(19);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record20 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row20<Long, Long, Date, Date, Date, Long, String, String, String, Timestamp, String, Timestamp, UUID, JsonNode, String, String, Date, String, String, String> fieldsRow() {
        return (Row20) super.fieldsRow();
    }

    @Override
    public Row20<Long, Long, Date, Date, Date, Long, String, String, String, Timestamp, String, Timestamp, UUID, JsonNode, String, String, Date, String, String, String> valuesRow() {
        return (Row20) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Muutospyynto.MUUTOSPYYNTO.ID;
    }

    @Override
    public Field<Long> field2() {
        return Muutospyynto.MUUTOSPYYNTO.LUPA_ID;
    }

    @Override
    public Field<Date> field3() {
        return Muutospyynto.MUUTOSPYYNTO.HAKUPVM;
    }

    @Override
    public Field<Date> field4() {
        return Muutospyynto.MUUTOSPYYNTO.VOIMASSAALKUPVM;
    }

    @Override
    public Field<Date> field5() {
        return Muutospyynto.MUUTOSPYYNTO.VOIMASSALOPPUPVM;
    }

    @Override
    public Field<Long> field6() {
        return Muutospyynto.MUUTOSPYYNTO.PAATOSKIERROS_ID;
    }

    @Override
    public Field<String> field7() {
        return Muutospyynto.MUUTOSPYYNTO.TILA;
    }

    @Override
    public Field<String> field8() {
        return Muutospyynto.MUUTOSPYYNTO.JARJESTAJA_YTUNNUS;
    }

    @Override
    public Field<String> field9() {
        return Muutospyynto.MUUTOSPYYNTO.LUOJA;
    }

    @Override
    public Field<Timestamp> field10() {
        return Muutospyynto.MUUTOSPYYNTO.LUONTIPVM;
    }

    @Override
    public Field<String> field11() {
        return Muutospyynto.MUUTOSPYYNTO.PAIVITTAJA;
    }

    @Override
    public Field<Timestamp> field12() {
        return Muutospyynto.MUUTOSPYYNTO.PAIVITYSPVM;
    }

    @Override
    public Field<UUID> field13() {
        return Muutospyynto.MUUTOSPYYNTO.UUID;
    }

    @Override
    public Field<JsonNode> field14() {
        return Muutospyynto.MUUTOSPYYNTO.META;
    }

    @Override
    public Field<String> field15() {
        return Muutospyynto.MUUTOSPYYNTO.ALKUPERA;
    }

    @Override
    public Field<String> field16() {
        return Muutospyynto.MUUTOSPYYNTO.ASIANUMERO;
    }

    @Override
    public Field<Date> field17() {
        return Muutospyynto.MUUTOSPYYNTO.PAATOSPVM;
    }

    @Override
    public Field<String> field18() {
        return Muutospyynto.MUUTOSPYYNTO.DIAARINUMERO;
    }

    @Override
    public Field<String> field19() {
        return Muutospyynto.MUUTOSPYYNTO.JARJESTAJA_OID;
    }

    @Override
    public Field<String> field20() {
        return Muutospyynto.MUUTOSPYYNTO.KOULUTUSTYYPPI;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getLupaId();
    }

    @Override
    public Date component3() {
        return getHakupvm();
    }

    @Override
    public Date component4() {
        return getVoimassaalkupvm();
    }

    @Override
    public Date component5() {
        return getVoimassaloppupvm();
    }

    @Override
    public Long component6() {
        return getPaatoskierrosId();
    }

    @Override
    public String component7() {
        return getTila();
    }

    @Override
    public String component8() {
        return getJarjestajaYtunnus();
    }

    @Override
    public String component9() {
        return getLuoja();
    }

    @Override
    public Timestamp component10() {
        return getLuontipvm();
    }

    @Override
    public String component11() {
        return getPaivittaja();
    }

    @Override
    public Timestamp component12() {
        return getPaivityspvm();
    }

    @Override
    public UUID component13() {
        return getUuid();
    }

    @Override
    public JsonNode component14() {
        return getMeta();
    }

    @Override
    public String component15() {
        return getAlkupera();
    }

    @Override
    public String component16() {
        return getAsianumero();
    }

    @Override
    public Date component17() {
        return getPaatospvm();
    }

    @Override
    public String component18() {
        return getDiaarinumero();
    }

    @Override
    public String component19() {
        return getJarjestajaOid();
    }

    @Override
    public String component20() {
        return getKoulutustyyppi();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getLupaId();
    }

    @Override
    public Date value3() {
        return getHakupvm();
    }

    @Override
    public Date value4() {
        return getVoimassaalkupvm();
    }

    @Override
    public Date value5() {
        return getVoimassaloppupvm();
    }

    @Override
    public Long value6() {
        return getPaatoskierrosId();
    }

    @Override
    public String value7() {
        return getTila();
    }

    @Override
    public String value8() {
        return getJarjestajaYtunnus();
    }

    @Override
    public String value9() {
        return getLuoja();
    }

    @Override
    public Timestamp value10() {
        return getLuontipvm();
    }

    @Override
    public String value11() {
        return getPaivittaja();
    }

    @Override
    public Timestamp value12() {
        return getPaivityspvm();
    }

    @Override
    public UUID value13() {
        return getUuid();
    }

    @Override
    public JsonNode value14() {
        return getMeta();
    }

    @Override
    public String value15() {
        return getAlkupera();
    }

    @Override
    public String value16() {
        return getAsianumero();
    }

    @Override
    public Date value17() {
        return getPaatospvm();
    }

    @Override
    public String value18() {
        return getDiaarinumero();
    }

    @Override
    public String value19() {
        return getJarjestajaOid();
    }

    @Override
    public String value20() {
        return getKoulutustyyppi();
    }

    @Override
    public MuutospyyntoRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value2(Long value) {
        setLupaId(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value3(Date value) {
        setHakupvm(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value4(Date value) {
        setVoimassaalkupvm(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value5(Date value) {
        setVoimassaloppupvm(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value6(Long value) {
        setPaatoskierrosId(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value7(String value) {
        setTila(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value8(String value) {
        setJarjestajaYtunnus(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value9(String value) {
        setLuoja(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value10(Timestamp value) {
        setLuontipvm(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value11(String value) {
        setPaivittaja(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value12(Timestamp value) {
        setPaivityspvm(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value13(UUID value) {
        setUuid(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value14(JsonNode value) {
        setMeta(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value15(String value) {
        setAlkupera(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value16(String value) {
        setAsianumero(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value17(Date value) {
        setPaatospvm(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value18(String value) {
        setDiaarinumero(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value19(String value) {
        setJarjestajaOid(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord value20(String value) {
        setKoulutustyyppi(value);
        return this;
    }

    @Override
    public MuutospyyntoRecord values(Long value1, Long value2, Date value3, Date value4, Date value5, Long value6, String value7, String value8, String value9, Timestamp value10, String value11, Timestamp value12, UUID value13, JsonNode value14, String value15, String value16, Date value17, String value18, String value19, String value20) {
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
        value17(value17);
        value18(value18);
        value19(value19);
        value20(value20);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MuutospyyntoRecord
     */
    public MuutospyyntoRecord() {
        super(Muutospyynto.MUUTOSPYYNTO);
    }

    /**
     * Create a detached, initialised MuutospyyntoRecord
     */
    public MuutospyyntoRecord(Long id, Long lupaId, Date hakupvm, Date voimassaalkupvm, Date voimassaloppupvm, Long paatoskierrosId, String tila, String jarjestajaYtunnus, String luoja, Timestamp luontipvm, String paivittaja, Timestamp paivityspvm, UUID uuid, JsonNode meta, String alkupera, String asianumero, Date paatospvm, String diaarinumero, String jarjestajaOid, String koulutustyyppi) {
        super(Muutospyynto.MUUTOSPYYNTO);

        set(0, id);
        set(1, lupaId);
        set(2, hakupvm);
        set(3, voimassaalkupvm);
        set(4, voimassaloppupvm);
        set(5, paatoskierrosId);
        set(6, tila);
        set(7, jarjestajaYtunnus);
        set(8, luoja);
        set(9, luontipvm);
        set(10, paivittaja);
        set(11, paivityspvm);
        set(12, uuid);
        set(13, meta);
        set(14, alkupera);
        set(15, asianumero);
        set(16, paatospvm);
        set(17, diaarinumero);
        set(18, jarjestajaOid);
        set(19, koulutustyyppi);
    }
}
