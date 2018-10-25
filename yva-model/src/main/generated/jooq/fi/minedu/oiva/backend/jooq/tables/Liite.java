/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.jooq.tables;


import com.fasterxml.jackson.databind.JsonNode;

import fi.minedu.oiva.backend.jooq.Keys;
import fi.minedu.oiva.backend.jooq.Kuja;
import fi.minedu.oiva.backend.jooq.PostgresJSONJacksonBinding;
import fi.minedu.oiva.backend.jooq.tables.records.LiiteRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Field;
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
public class Liite extends TableImpl<LiiteRecord> {

    private static final long serialVersionUID = -1232721327;

    /**
     * The reference instance of <code>kuja.liite</code>
     */
    public static final Liite LIITE = new Liite();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LiiteRecord> getRecordType() {
        return LiiteRecord.class;
    }

    /**
     * The column <code>kuja.liite.id</code>.
     */
    public final TableField<LiiteRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('liite_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>kuja.liite.nimi</code>.
     */
    public final TableField<LiiteRecord, String> NIMI = createField("nimi", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

    /**
     * The column <code>kuja.liite.polku</code>.
     */
    public final TableField<LiiteRecord, String> POLKU = createField("polku", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

    /**
     * The column <code>kuja.liite.tila</code>.
     */
    public final TableField<LiiteRecord, Boolean> TILA = createField("tila", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("false", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>kuja.liite.luoja</code>.
     */
    public final TableField<LiiteRecord, String> LUOJA = createField("luoja", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>kuja.liite.luontipvm</code>.
     */
    public final TableField<LiiteRecord, Timestamp> LUONTIPVM = createField("luontipvm", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("now()", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>kuja.liite.paivittaja</code>.
     */
    public final TableField<LiiteRecord, String> PAIVITTAJA = createField("paivittaja", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>kuja.liite.paivityspvm</code>.
     */
    public final TableField<LiiteRecord, Timestamp> PAIVITYSPVM = createField("paivityspvm", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>kuja.liite.koko</code>.
     */
    public final TableField<LiiteRecord, Long> KOKO = createField("koko", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>kuja.liite.meta</code>.
     */
    public final TableField<LiiteRecord, JsonNode> META = createField("meta", org.jooq.impl.DefaultDataType.getDefaultDataType("jsonb"), this, "", new PostgresJSONJacksonBinding());

    /**
     * The column <code>kuja.liite.tyyppi</code>.
     */
    public final TableField<LiiteRecord, String> TYYPPI = createField("tyyppi", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

    /**
     * The column <code>kuja.liite.kieli</code>.
     */
    public final TableField<LiiteRecord, String> KIELI = createField("kieli", org.jooq.impl.SQLDataType.VARCHAR.length(2).nullable(false), this, "");

    /**
     * The column <code>kuja.liite.uuid</code>.
     */
    public final TableField<LiiteRecord, UUID> UUID = createField("uuid", org.jooq.impl.SQLDataType.UUID.nullable(false).defaultValue(org.jooq.impl.DSL.field("uuid_generate_v1()", org.jooq.impl.SQLDataType.UUID)), this, "");

    /**
     * Create a <code>kuja.liite</code> table reference
     */
    public Liite() {
        this("liite", null);
    }

    /**
     * Create an aliased <code>kuja.liite</code> table reference
     */
    public Liite(String alias) {
        this(alias, LIITE);
    }

    private Liite(String alias, Table<LiiteRecord> aliased) {
        this(alias, aliased, null);
    }

    private Liite(String alias, Table<LiiteRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Kuja.KUJA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<LiiteRecord, Long> getIdentity() {
        return Keys.IDENTITY_LIITE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<LiiteRecord> getPrimaryKey() {
        return Keys.LIITE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<LiiteRecord>> getKeys() {
        return Arrays.<UniqueKey<LiiteRecord>>asList(Keys.LIITE_PKEY, Keys.LIITE_UUID_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Liite as(String alias) {
        return new Liite(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Liite rename(String name) {
        return new Liite(name, null);
    }
}
