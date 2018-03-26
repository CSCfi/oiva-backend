/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.jooq.tables;


import com.fasterxml.jackson.databind.JsonNode;

import fi.minedu.oiva.backend.jooq.Keys;
import fi.minedu.oiva.backend.jooq.Oiva;
import fi.minedu.oiva.backend.jooq.PostgresJSONJacksonBinding;
import fi.minedu.oiva.backend.jooq.tables.records.PaatoskierrosRecord;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


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
public class Paatoskierros extends TableImpl<PaatoskierrosRecord> {

    private static final long serialVersionUID = 1860787877;

    /**
     * The reference instance of <code>oiva.paatoskierros</code>
     */
    public static final Paatoskierros PAATOSKIERROS = new Paatoskierros();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PaatoskierrosRecord> getRecordType() {
        return PaatoskierrosRecord.class;
    }

    /**
     * The column <code>oiva.paatoskierros.id</code>.
     */
    public final TableField<PaatoskierrosRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('paatoskierros_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>oiva.paatoskierros.alkupvm</code>.
     */
    public final TableField<PaatoskierrosRecord, Date> ALKUPVM = createField("alkupvm", org.jooq.impl.SQLDataType.DATE.nullable(false), this, "");

    /**
     * The column <code>oiva.paatoskierros.loppupvm</code>.
     */
    public final TableField<PaatoskierrosRecord, Date> LOPPUPVM = createField("loppupvm", org.jooq.impl.SQLDataType.DATE, this, "");

    /**
     * The column <code>oiva.paatoskierros.oletus_paatospvm</code>.
     */
    public final TableField<PaatoskierrosRecord, Date> OLETUS_PAATOSPVM = createField("oletus_paatospvm", org.jooq.impl.SQLDataType.DATE, this, "");

    /**
     * The column <code>oiva.paatoskierros.esitysmalli_id</code>.
     */
    public final TableField<PaatoskierrosRecord, Long> ESITYSMALLI_ID = createField("esitysmalli_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>oiva.paatoskierros.luoja</code>.
     */
    public final TableField<PaatoskierrosRecord, String> LUOJA = createField("luoja", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>oiva.paatoskierros.luontipvm</code>.
     */
    public final TableField<PaatoskierrosRecord, Timestamp> LUONTIPVM = createField("luontipvm", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("now()", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>oiva.paatoskierros.paivittaja</code>.
     */
    public final TableField<PaatoskierrosRecord, String> PAIVITTAJA = createField("paivittaja", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>oiva.paatoskierros.paivityspvm</code>.
     */
    public final TableField<PaatoskierrosRecord, Timestamp> PAIVITYSPVM = createField("paivityspvm", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>oiva.paatoskierros.meta</code>.
     */
    public final TableField<PaatoskierrosRecord, JsonNode> META = createField("meta", org.jooq.impl.DefaultDataType.getDefaultDataType("jsonb"), this, "", new PostgresJSONJacksonBinding());

    /**
     * The column <code>oiva.paatoskierros.uuid</code>.
     */
    public final TableField<PaatoskierrosRecord, UUID> UUID = createField("uuid", org.jooq.impl.SQLDataType.UUID.nullable(false).defaultValue(org.jooq.impl.DSL.field("uuid_generate_v1()", org.jooq.impl.SQLDataType.UUID)), this, "");

    /**
     * Create a <code>oiva.paatoskierros</code> table reference
     */
    public Paatoskierros() {
        this("paatoskierros", null);
    }

    /**
     * Create an aliased <code>oiva.paatoskierros</code> table reference
     */
    public Paatoskierros(String alias) {
        this(alias, PAATOSKIERROS);
    }

    private Paatoskierros(String alias, Table<PaatoskierrosRecord> aliased) {
        this(alias, aliased, null);
    }

    private Paatoskierros(String alias, Table<PaatoskierrosRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Oiva.OIVA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<PaatoskierrosRecord, Long> getIdentity() {
        return Keys.IDENTITY_PAATOSKIERROS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<PaatoskierrosRecord> getPrimaryKey() {
        return Keys.PAATOSKIERROS_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<PaatoskierrosRecord>> getKeys() {
        return Arrays.<UniqueKey<PaatoskierrosRecord>>asList(Keys.PAATOSKIERROS_PKEY, Keys.PAATOSKIERROS_UUID_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<PaatoskierrosRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<PaatoskierrosRecord, ?>>asList(Keys.PAATOSKIERROS__FK_ESITYSMALLI);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Paatoskierros as(String alias) {
        return new Paatoskierros(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Paatoskierros rename(String name) {
        return new Paatoskierros(name, null);
    }
}
