/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.model.jooq.tables.records;


import fi.minedu.oiva.backend.model.entity.AsiatyyppiValue;
import fi.minedu.oiva.backend.model.entity.TranslatedString;
import fi.minedu.oiva.backend.model.jooq.tables.Asiatyyppi;

import java.util.UUID;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
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
public class AsiatyyppiRecord extends UpdatableRecordImpl<AsiatyyppiRecord> implements Record4<Long, AsiatyyppiValue, TranslatedString, UUID> {

    private static final long serialVersionUID = -2061881845;

    /**
     * Setter for <code>asiatyyppi.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>asiatyyppi.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>asiatyyppi.tunniste</code>.
     */
    public void setTunniste(AsiatyyppiValue value) {
        set(1, value);
    }

    /**
     * Getter for <code>asiatyyppi.tunniste</code>.
     */
    @NotNull
    public AsiatyyppiValue getTunniste() {
        return (AsiatyyppiValue) get(1);
    }

    /**
     * Setter for <code>asiatyyppi.selite</code>.
     */
    public void setSelite(TranslatedString value) {
        set(2, value);
    }

    /**
     * Getter for <code>asiatyyppi.selite</code>.
     */
    public TranslatedString getSelite() {
        return (TranslatedString) get(2);
    }

    /**
     * Setter for <code>asiatyyppi.uuid</code>.
     */
    public void setUuid(UUID value) {
        set(3, value);
    }

    /**
     * Getter for <code>asiatyyppi.uuid</code>.
     */
    public UUID getUuid() {
        return (UUID) get(3);
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
    // Record4 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<Long, AsiatyyppiValue, TranslatedString, UUID> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<Long, AsiatyyppiValue, TranslatedString, UUID> valuesRow() {
        return (Row4) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Asiatyyppi.ASIATYYPPI.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<AsiatyyppiValue> field2() {
        return Asiatyyppi.ASIATYYPPI.TUNNISTE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<TranslatedString> field3() {
        return Asiatyyppi.ASIATYYPPI.SELITE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UUID> field4() {
        return Asiatyyppi.ASIATYYPPI.UUID;
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
    public AsiatyyppiValue value2() {
        return getTunniste();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TranslatedString value3() {
        return getSelite();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID value4() {
        return getUuid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsiatyyppiRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsiatyyppiRecord value2(AsiatyyppiValue value) {
        setTunniste(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsiatyyppiRecord value3(TranslatedString value) {
        setSelite(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsiatyyppiRecord value4(UUID value) {
        setUuid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsiatyyppiRecord values(Long value1, AsiatyyppiValue value2, TranslatedString value3, UUID value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AsiatyyppiRecord
     */
    public AsiatyyppiRecord() {
        super(Asiatyyppi.ASIATYYPPI);
    }

    /**
     * Create a detached, initialised AsiatyyppiRecord
     */
    public AsiatyyppiRecord(Long id, AsiatyyppiValue tunniste, TranslatedString selite, UUID uuid) {
        super(Asiatyyppi.ASIATYYPPI);

        set(0, id);
        set(1, tunniste);
        set(2, selite);
        set(3, uuid);
    }
}