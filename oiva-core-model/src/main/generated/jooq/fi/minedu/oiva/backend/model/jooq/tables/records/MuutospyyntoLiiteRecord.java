/*
 * This file is generated by jOOQ.
 */
package fi.minedu.oiva.backend.model.jooq.tables.records;


import fi.minedu.oiva.backend.model.jooq.tables.MuutospyyntoLiite;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
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
public class MuutospyyntoLiiteRecord extends UpdatableRecordImpl<MuutospyyntoLiiteRecord> implements Record2<Long, Long> {

    private static final long serialVersionUID = -652105671;

    /**
     * Setter for <code>muutospyynto_liite.muutospyynto_id</code>.
     */
    public void setMuutospyyntoId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>muutospyynto_liite.muutospyynto_id</code>.
     */
    @NotNull
    public Long getMuutospyyntoId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>muutospyynto_liite.liite_id</code>.
     */
    public void setLiiteId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>muutospyynto_liite.liite_id</code>.
     */
    @NotNull
    public Long getLiiteId() {
        return (Long) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<Long, Long> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<Long, Long> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<Long, Long> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return MuutospyyntoLiite.MUUTOSPYYNTO_LIITE.MUUTOSPYYNTO_ID;
    }

    @Override
    public Field<Long> field2() {
        return MuutospyyntoLiite.MUUTOSPYYNTO_LIITE.LIITE_ID;
    }

    @Override
    public Long component1() {
        return getMuutospyyntoId();
    }

    @Override
    public Long component2() {
        return getLiiteId();
    }

    @Override
    public Long value1() {
        return getMuutospyyntoId();
    }

    @Override
    public Long value2() {
        return getLiiteId();
    }

    @Override
    public MuutospyyntoLiiteRecord value1(Long value) {
        setMuutospyyntoId(value);
        return this;
    }

    @Override
    public MuutospyyntoLiiteRecord value2(Long value) {
        setLiiteId(value);
        return this;
    }

    @Override
    public MuutospyyntoLiiteRecord values(Long value1, Long value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MuutospyyntoLiiteRecord
     */
    public MuutospyyntoLiiteRecord() {
        super(MuutospyyntoLiite.MUUTOSPYYNTO_LIITE);
    }

    /**
     * Create a detached, initialised MuutospyyntoLiiteRecord
     */
    public MuutospyyntoLiiteRecord(Long muutospyyntoId, Long liiteId) {
        super(MuutospyyntoLiite.MUUTOSPYYNTO_LIITE);

        set(0, muutospyyntoId);
        set(1, liiteId);
    }
}
