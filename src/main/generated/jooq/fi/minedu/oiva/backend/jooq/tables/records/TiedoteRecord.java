/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.jooq.tables.records;


import com.fasterxml.jackson.databind.JsonNode;

import fi.minedu.oiva.backend.jooq.tables.Tiedote;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;


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
public class TiedoteRecord extends UpdatableRecordImpl<TiedoteRecord> implements Record7<Long, JsonNode, JsonNode, String, Timestamp, String, Timestamp> {

    private static final long serialVersionUID = -2096437121;

    /**
     * Setter for <code>oiva.tiedote.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>oiva.tiedote.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>oiva.tiedote.otsikko</code>.
     */
    public void setOtsikko(JsonNode value) {
        set(1, value);
    }

    /**
     * Getter for <code>oiva.tiedote.otsikko</code>.
     */
    @NotNull
    public JsonNode getOtsikko() {
        return (JsonNode) get(1);
    }

    /**
     * Setter for <code>oiva.tiedote.sisalto</code>.
     */
    public void setSisalto(JsonNode value) {
        set(2, value);
    }

    /**
     * Getter for <code>oiva.tiedote.sisalto</code>.
     */
    @NotNull
    public JsonNode getSisalto() {
        return (JsonNode) get(2);
    }

    /**
     * Setter for <code>oiva.tiedote.luoja</code>.
     */
    public void setLuoja(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oiva.tiedote.luoja</code>.
     */
    @NotNull
    @Size(max = 255)
    public String getLuoja() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oiva.tiedote.luontipvm</code>.
     */
    public void setLuontipvm(Timestamp value) {
        set(4, value);
    }

    /**
     * Getter for <code>oiva.tiedote.luontipvm</code>.
     */
    public Timestamp getLuontipvm() {
        return (Timestamp) get(4);
    }

    /**
     * Setter for <code>oiva.tiedote.paivittaja</code>.
     */
    public void setPaivittaja(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oiva.tiedote.paivittaja</code>.
     */
    @NotNull
    @Size(max = 255)
    public String getPaivittaja() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oiva.tiedote.paivityspvm</code>.
     */
    public void setPaivityspvm(Timestamp value) {
        set(6, value);
    }

    /**
     * Getter for <code>oiva.tiedote.paivityspvm</code>.
     */
    public Timestamp getPaivityspvm() {
        return (Timestamp) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<Long, JsonNode, JsonNode, String, Timestamp, String, Timestamp> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<Long, JsonNode, JsonNode, String, Timestamp, String, Timestamp> valuesRow() {
        return (Row7) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Tiedote.TIEDOTE.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<JsonNode> field2() {
        return Tiedote.TIEDOTE.OTSIKKO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<JsonNode> field3() {
        return Tiedote.TIEDOTE.SISALTO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Tiedote.TIEDOTE.LUOJA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field5() {
        return Tiedote.TIEDOTE.LUONTIPVM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Tiedote.TIEDOTE.PAIVITTAJA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field7() {
        return Tiedote.TIEDOTE.PAIVITYSPVM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonNode value2() {
        return getOtsikko();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonNode value3() {
        return getSisalto();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getLuoja();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value5() {
        return getLuontipvm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getPaivittaja();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value7() {
        return getPaivityspvm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TiedoteRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TiedoteRecord value2(JsonNode value) {
        setOtsikko(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TiedoteRecord value3(JsonNode value) {
        setSisalto(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TiedoteRecord value4(String value) {
        setLuoja(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TiedoteRecord value5(Timestamp value) {
        setLuontipvm(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TiedoteRecord value6(String value) {
        setPaivittaja(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TiedoteRecord value7(Timestamp value) {
        setPaivityspvm(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TiedoteRecord values(Long value1, JsonNode value2, JsonNode value3, String value4, Timestamp value5, String value6, Timestamp value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TiedoteRecord
     */
    public TiedoteRecord() {
        super(Tiedote.TIEDOTE);
    }

    /**
     * Create a detached, initialised TiedoteRecord
     */
    public TiedoteRecord(Long id, JsonNode otsikko, JsonNode sisalto, String luoja, Timestamp luontipvm, String paivittaja, Timestamp paivityspvm) {
        super(Tiedote.TIEDOTE);

        set(0, id);
        set(1, otsikko);
        set(2, sisalto);
        set(3, luoja);
        set(4, luontipvm);
        set(5, paivittaja);
        set(6, paivityspvm);
    }
}
