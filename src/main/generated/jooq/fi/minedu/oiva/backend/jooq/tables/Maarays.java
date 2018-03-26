/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.jooq.tables;


import com.fasterxml.jackson.databind.JsonNode;

import fi.minedu.oiva.backend.jooq.Keys;
import fi.minedu.oiva.backend.jooq.Oiva;
import fi.minedu.oiva.backend.jooq.PostgresJSONJacksonBinding;
import fi.minedu.oiva.backend.jooq.tables.records.MaaraysRecord;

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
public class Maarays extends TableImpl<MaaraysRecord> {

    private static final long serialVersionUID = -1620325955;

    /**
     * The reference instance of <code>oiva.maarays</code>
     */
    public static final Maarays MAARAYS = new Maarays();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MaaraysRecord> getRecordType() {
        return MaaraysRecord.class;
    }

    /**
     * The column <code>oiva.maarays.id</code>.
     */
    public final TableField<MaaraysRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('maarays_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>oiva.maarays.parent_id</code>.
     */
    public final TableField<MaaraysRecord, Long> PARENT_ID = createField("parent_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>oiva.maarays.lupa_id</code>.
     */
    public final TableField<MaaraysRecord, Long> LUPA_ID = createField("lupa_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>oiva.maarays.kohde_id</code>.
     */
    public final TableField<MaaraysRecord, Long> KOHDE_ID = createField("kohde_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>oiva.maarays.koodisto</code>.
     */
    public final TableField<MaaraysRecord, String> KOODISTO = createField("koodisto", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

    /**
     * The column <code>oiva.maarays.koodiarvo</code>.
     */
    public final TableField<MaaraysRecord, String> KOODIARVO = createField("koodiarvo", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>oiva.maarays.arvo</code>.
     */
    public final TableField<MaaraysRecord, String> ARVO = createField("arvo", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

    /**
     * The column <code>oiva.maarays.maaraystyyppi_id</code>.
     */
    public final TableField<MaaraysRecord, Long> MAARAYSTYYPPI_ID = createField("maaraystyyppi_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>oiva.maarays.meta</code>.
     */
    public final TableField<MaaraysRecord, JsonNode> META = createField("meta", org.jooq.impl.DefaultDataType.getDefaultDataType("jsonb"), this, "", new PostgresJSONJacksonBinding());

    /**
     * The column <code>oiva.maarays.luoja</code>.
     */
    public final TableField<MaaraysRecord, String> LUOJA = createField("luoja", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>oiva.maarays.luontipvm</code>.
     */
    public final TableField<MaaraysRecord, Timestamp> LUONTIPVM = createField("luontipvm", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("now()", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>oiva.maarays.paivittaja</code>.
     */
    public final TableField<MaaraysRecord, String> PAIVITTAJA = createField("paivittaja", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>oiva.maarays.paivityspvm</code>.
     */
    public final TableField<MaaraysRecord, Timestamp> PAIVITYSPVM = createField("paivityspvm", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>oiva.maarays.koodistoversio</code>.
     */
    public final TableField<MaaraysRecord, Integer> KOODISTOVERSIO = createField("koodistoversio", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>oiva.maarays.uuid</code>.
     */
    public final TableField<MaaraysRecord, UUID> UUID = createField("uuid", org.jooq.impl.SQLDataType.UUID.nullable(false).defaultValue(org.jooq.impl.DSL.field("uuid_generate_v1()", org.jooq.impl.SQLDataType.UUID)), this, "");

    /**
     * Create a <code>oiva.maarays</code> table reference
     */
    public Maarays() {
        this("maarays", null);
    }

    /**
     * Create an aliased <code>oiva.maarays</code> table reference
     */
    public Maarays(String alias) {
        this(alias, MAARAYS);
    }

    private Maarays(String alias, Table<MaaraysRecord> aliased) {
        this(alias, aliased, null);
    }

    private Maarays(String alias, Table<MaaraysRecord> aliased, Field<?>[] parameters) {
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
    public Identity<MaaraysRecord, Long> getIdentity() {
        return Keys.IDENTITY_MAARAYS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<MaaraysRecord> getPrimaryKey() {
        return Keys.MAARAYS_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<MaaraysRecord>> getKeys() {
        return Arrays.<UniqueKey<MaaraysRecord>>asList(Keys.MAARAYS_PKEY, Keys.MAARAYS_UUID_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<MaaraysRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MaaraysRecord, ?>>asList(Keys.MAARAYS__FK_LUPA, Keys.MAARAYS__FK_KOHDE, Keys.MAARAYS__FK_MAARAYSTYYPPI);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Maarays as(String alias) {
        return new Maarays(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Maarays rename(String name) {
        return new Maarays(name, null);
    }
}
