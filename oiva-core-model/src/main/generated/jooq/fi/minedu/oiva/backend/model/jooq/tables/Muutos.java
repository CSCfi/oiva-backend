/*
 * This file is generated by jOOQ.
 */
package fi.minedu.oiva.backend.model.jooq.tables;


import com.fasterxml.jackson.databind.JsonNode;

import fi.minedu.oiva.backend.model.jooq.DefaultSchema;
import fi.minedu.oiva.backend.model.jooq.Indexes;
import fi.minedu.oiva.backend.model.jooq.Keys;
import fi.minedu.oiva.backend.model.jooq.PostgresJSONJacksonBinding;
import fi.minedu.oiva.backend.model.jooq.tables.records.MuutosRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row20;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


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
public class Muutos extends TableImpl<MuutosRecord> {

    private static final long serialVersionUID = 40798982;

    /**
     * The reference instance of <code>muutos</code>
     */
    public static final Muutos MUUTOS = new Muutos();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MuutosRecord> getRecordType() {
        return MuutosRecord.class;
    }

    /**
     * The column <code>muutos.id</code>.
     */
    public final TableField<MuutosRecord, Long> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('muutos_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>muutos.muutospyynto_id</code>.
     */
    public final TableField<MuutosRecord, Long> MUUTOSPYYNTO_ID = createField(DSL.name("muutospyynto_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>muutos.kohde_id</code>.
     */
    public final TableField<MuutosRecord, Long> KOHDE_ID = createField(DSL.name("kohde_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>muutos.parent_id</code>.
     */
    public final TableField<MuutosRecord, Long> PARENT_ID = createField(DSL.name("parent_id"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>muutos.koodisto</code>.
     */
    public final TableField<MuutosRecord, String> KOODISTO = createField(DSL.name("koodisto"), org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>muutos.koodiarvo</code>.
     */
    public final TableField<MuutosRecord, String> KOODIARVO = createField(DSL.name("koodiarvo"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>muutos.arvo</code>.
     */
    public final TableField<MuutosRecord, String> ARVO = createField(DSL.name("arvo"), org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>muutos.maaraystyyppi_id</code>.
     */
    public final TableField<MuutosRecord, Long> MAARAYSTYYPPI_ID = createField(DSL.name("maaraystyyppi_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>muutos.meta</code>.
     */
    public final TableField<MuutosRecord, JsonNode> META = createField(DSL.name("meta"), org.jooq.impl.SQLDataType.JSONB, this, "", new PostgresJSONJacksonBinding());

    /**
     * The column <code>muutos.luoja</code>.
     */
    public final TableField<MuutosRecord, String> LUOJA = createField(DSL.name("luoja"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>muutos.luontipvm</code>.
     */
    public final TableField<MuutosRecord, Timestamp> LUONTIPVM = createField(DSL.name("luontipvm"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("now()", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>muutos.paivittaja</code>.
     */
    public final TableField<MuutosRecord, String> PAIVITTAJA = createField(DSL.name("paivittaja"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>muutos.paivityspvm</code>.
     */
    public final TableField<MuutosRecord, Timestamp> PAIVITYSPVM = createField(DSL.name("paivityspvm"), org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>muutos.maarays_id</code>.
     */
    public final TableField<MuutosRecord, Long> MAARAYS_ID = createField(DSL.name("maarays_id"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>muutos.tila</code>.
     */
    public final TableField<MuutosRecord, String> TILA = createField(DSL.name("tila"), org.jooq.impl.SQLDataType.VARCHAR(10), this, "");

    /**
     * The column <code>muutos.uuid</code>.
     */
    public final TableField<MuutosRecord, UUID> UUID = createField(DSL.name("uuid"), org.jooq.impl.SQLDataType.UUID.nullable(false).defaultValue(org.jooq.impl.DSL.field("uuid_generate_v1()", org.jooq.impl.SQLDataType.UUID)), this, "");

    /**
     * The column <code>muutos.paatos_tila</code>.
     */
    public final TableField<MuutosRecord, String> PAATOS_TILA = createField(DSL.name("paatos_tila"), org.jooq.impl.SQLDataType.VARCHAR(20), this, "");

    /**
     * The column <code>muutos.muutosperustelukoodiarvo</code>.
     */
    public final TableField<MuutosRecord, String> MUUTOSPERUSTELUKOODIARVO = createField(DSL.name("muutosperustelukoodiarvo"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>muutos.org_oid</code>.
     */
    public final TableField<MuutosRecord, String> ORG_OID = createField(DSL.name("org_oid"), org.jooq.impl.SQLDataType.VARCHAR(255).defaultValue(org.jooq.impl.DSL.field("NULL::character varying", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>muutos.parent_maarays_id</code>.
     */
    public final TableField<MuutosRecord, Long> PARENT_MAARAYS_ID = createField(DSL.name("parent_maarays_id"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * Create a <code>muutos</code> table reference
     */
    public Muutos() {
        this(DSL.name("muutos"), null);
    }

    /**
     * Create an aliased <code>muutos</code> table reference
     */
    public Muutos(String alias) {
        this(DSL.name(alias), MUUTOS);
    }

    /**
     * Create an aliased <code>muutos</code> table reference
     */
    public Muutos(Name alias) {
        this(alias, MUUTOS);
    }

    private Muutos(Name alias, Table<MuutosRecord> aliased) {
        this(alias, aliased, null);
    }

    private Muutos(Name alias, Table<MuutosRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Muutos(Table<O> child, ForeignKey<O, MuutosRecord> key) {
        super(child, key, MUUTOS);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.MUUTOS_PKEY, Indexes.MUUTOS_UUID_KEY);
    }

    @Override
    public Identity<MuutosRecord, Long> getIdentity() {
        return Keys.IDENTITY_MUUTOS;
    }

    @Override
    public UniqueKey<MuutosRecord> getPrimaryKey() {
        return Keys.MUUTOS_PKEY;
    }

    @Override
    public List<UniqueKey<MuutosRecord>> getKeys() {
        return Arrays.<UniqueKey<MuutosRecord>>asList(Keys.MUUTOS_PKEY, Keys.MUUTOS_UUID_KEY);
    }

    @Override
    public List<ForeignKey<MuutosRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MuutosRecord, ?>>asList(Keys.MUUTOS__FK_MUUTOSPYYNTO, Keys.MUUTOS__FK_KOHDE);
    }

    public Muutospyynto muutospyynto() {
        return new Muutospyynto(this, Keys.MUUTOS__FK_MUUTOSPYYNTO);
    }

    public Kohde kohde() {
        return new Kohde(this, Keys.MUUTOS__FK_KOHDE);
    }

    @Override
    public Muutos as(String alias) {
        return new Muutos(DSL.name(alias), this);
    }

    @Override
    public Muutos as(Name alias) {
        return new Muutos(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Muutos rename(String name) {
        return new Muutos(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Muutos rename(Name name) {
        return new Muutos(name, null);
    }

    // -------------------------------------------------------------------------
    // Row20 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row20<Long, Long, Long, Long, String, String, String, Long, JsonNode, String, Timestamp, String, Timestamp, Long, String, UUID, String, String, String, Long> fieldsRow() {
        return (Row20) super.fieldsRow();
    }
}
