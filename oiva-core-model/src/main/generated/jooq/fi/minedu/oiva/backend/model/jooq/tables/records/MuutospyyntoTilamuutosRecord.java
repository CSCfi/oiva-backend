/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.model.jooq.tables.records;


import fi.minedu.oiva.backend.model.jooq.tables.MuutospyyntoTilamuutos;

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
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MuutospyyntoTilamuutosRecord extends UpdatableRecordImpl<MuutospyyntoTilamuutosRecord> implements Record2<Long, Long> {

    private static final long serialVersionUID = -784420660;

    /**
     * Setter for <code>muutospyynto_tilamuutos.muutospyynto_id</code>.
     */
    public void setMuutospyyntoId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>muutospyynto_tilamuutos.muutospyynto_id</code>.
     */
    @NotNull
    public Long getMuutospyyntoId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>muutospyynto_tilamuutos.tilamuutos_id</code>.
     */
    public void setTilamuutosId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>muutospyynto_tilamuutos.tilamuutos_id</code>.
     */
    @NotNull
    public Long getTilamuutosId() {
        return (Long) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record2<Long, Long> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<Long, Long> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<Long, Long> valuesRow() {
        return (Row2) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return MuutospyyntoTilamuutos.MUUTOSPYYNTO_TILAMUUTOS.MUUTOSPYYNTO_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field2() {
        return MuutospyyntoTilamuutos.MUUTOSPYYNTO_TILAMUUTOS.TILAMUUTOS_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getMuutospyyntoId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value2() {
        return getTilamuutosId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MuutospyyntoTilamuutosRecord value1(Long value) {
        setMuutospyyntoId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MuutospyyntoTilamuutosRecord value2(Long value) {
        setTilamuutosId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MuutospyyntoTilamuutosRecord values(Long value1, Long value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MuutospyyntoTilamuutosRecord
     */
    public MuutospyyntoTilamuutosRecord() {
        super(MuutospyyntoTilamuutos.MUUTOSPYYNTO_TILAMUUTOS);
    }

    /**
     * Create a detached, initialised MuutospyyntoTilamuutosRecord
     */
    public MuutospyyntoTilamuutosRecord(Long muutospyyntoId, Long tilamuutosId) {
        super(MuutospyyntoTilamuutos.MUUTOSPYYNTO_TILAMUUTOS);

        set(0, muutospyyntoId);
        set(1, tilamuutosId);
    }
}
