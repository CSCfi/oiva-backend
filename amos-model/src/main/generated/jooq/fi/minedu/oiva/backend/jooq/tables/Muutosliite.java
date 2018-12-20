/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.jooq.tables;


import com.fasterxml.jackson.databind.JsonNode;

import fi.minedu.oiva.backend.jooq.Keys;
import fi.minedu.oiva.backend.jooq.Oiva;
import fi.minedu.oiva.backend.jooq.PostgresJSONJacksonBinding;
import fi.minedu.oiva.backend.jooq.tables.records.MuutosliiteRecord;

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
public class Muutosliite extends TableImpl<MuutosliiteRecord> {

    private static final long serialVersionUID = 1232597504;

    /**
     * The reference instance of <code>oiva.muutosliite</code>
     */
    public static final Muutosliite MUUTOSLIITE = new Muutosliite();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MuutosliiteRecord> getRecordType() {
        return MuutosliiteRecord.class;
    }

    /**
     * The column <code>oiva.muutosliite.id</code>.
     */
    public final TableField<MuutosliiteRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('muutosliite_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>oiva.muutosliite.nimi</code>.
     */
    public final TableField<MuutosliiteRecord, String> NIMI = createField("nimi", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

    /**
     * The column <code>oiva.muutosliite.polku</code>.
     */
    public final TableField<MuutosliiteRecord, String> POLKU = createField("polku", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

    /**
     * The column <code>oiva.muutosliite.tila</code>.
     */
    public final TableField<MuutosliiteRecord, Boolean> TILA = createField("tila", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("false", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>oiva.muutosliite.luoja</code>.
     */
    public final TableField<MuutosliiteRecord, String> LUOJA = createField("luoja", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>oiva.muutosliite.luontipvm</code>.
     */
    public final TableField<MuutosliiteRecord, Timestamp> LUONTIPVM = createField("luontipvm", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("now()", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>oiva.muutosliite.paivittaja</code>.
     */
    public final TableField<MuutosliiteRecord, String> PAIVITTAJA = createField("paivittaja", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>oiva.muutosliite.paivityspvm</code>.
     */
    public final TableField<MuutosliiteRecord, Timestamp> PAIVITYSPVM = createField("paivityspvm", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>oiva.muutosliite.koko</code>.
     */
    public final TableField<MuutosliiteRecord, Long> KOKO = createField("koko", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>oiva.muutosliite.meta</code>.
     */
    public final TableField<MuutosliiteRecord, JsonNode> META = createField("meta", org.jooq.impl.DefaultDataType.getDefaultDataType("jsonb"), this, "", new PostgresJSONJacksonBinding());

    /**
     * The column <code>oiva.muutosliite.muutospyynto_id</code>.
     */
    public final TableField<MuutosliiteRecord, Long> MUUTOSPYYNTO_ID = createField("muutospyynto_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>oiva.muutosliite.uuid</code>.
     */
    public final TableField<MuutosliiteRecord, UUID> UUID = createField("uuid", org.jooq.impl.SQLDataType.UUID.nullable(false).defaultValue(org.jooq.impl.DSL.field("uuid_generate_v1()", org.jooq.impl.SQLDataType.UUID)), this, "");

    /**
     * The column <code>oiva.muutosliite.muutos_id</code>.
     */
    public final TableField<MuutosliiteRecord, Long> MUUTOS_ID = createField("muutos_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * Create a <code>oiva.muutosliite</code> table reference
     */
    public Muutosliite() {
        this("muutosliite", null);
    }

    /**
     * Create an aliased <code>oiva.muutosliite</code> table reference
     */
    public Muutosliite(String alias) {
        this(alias, MUUTOSLIITE);
    }

    private Muutosliite(String alias, Table<MuutosliiteRecord> aliased) {
        this(alias, aliased, null);
    }

    private Muutosliite(String alias, Table<MuutosliiteRecord> aliased, Field<?>[] parameters) {
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
    public Identity<MuutosliiteRecord, Long> getIdentity() {
        return Keys.IDENTITY_MUUTOSLIITE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<MuutosliiteRecord> getPrimaryKey() {
        return Keys.MUUTOSLIITE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<MuutosliiteRecord>> getKeys() {
        return Arrays.<UniqueKey<MuutosliiteRecord>>asList(Keys.MUUTOSLIITE_PKEY, Keys.MUUTOSLIITE_UUID_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<MuutosliiteRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MuutosliiteRecord, ?>>asList(Keys.MUUTOSLIITE__FK_MUUTOSPYYNTO, Keys.MUUTOSLIITE__FK_MUUTOS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Muutosliite as(String alias) {
        return new Muutosliite(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Muutosliite rename(String name) {
        return new Muutosliite(name, null);
    }
}