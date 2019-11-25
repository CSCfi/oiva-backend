/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.model.jooq.tables;


import fi.minedu.oiva.backend.model.jooq.DefaultSchema;
import fi.minedu.oiva.backend.model.jooq.Keys;
import fi.minedu.oiva.backend.model.jooq.tables.records.TilamuutosRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

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
public class Tilamuutos extends TableImpl<TilamuutosRecord> {

    private static final long serialVersionUID = 691103366;

    /**
     * The reference instance of <code>tilamuutos</code>
     */
    public static final Tilamuutos TILAMUUTOS = new Tilamuutos();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TilamuutosRecord> getRecordType() {
        return TilamuutosRecord.class;
    }

    /**
     * The column <code>tilamuutos.id</code>.
     */
    public final TableField<TilamuutosRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('tilamuutos_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>tilamuutos.alkutila</code>.
     */
    public final TableField<TilamuutosRecord, String> ALKUTILA = createField("alkutila", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false), this, "");

    /**
     * The column <code>tilamuutos.lopputila</code>.
     */
    public final TableField<TilamuutosRecord, String> LOPPUTILA = createField("lopputila", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false), this, "");

    /**
     * The column <code>tilamuutos.muutosaika</code>.
     */
    public final TableField<TilamuutosRecord, Timestamp> MUUTOSAIKA = createField("muutosaika", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>tilamuutos.kayttajatunnus</code>.
     */
    public final TableField<TilamuutosRecord, String> KAYTTAJATUNNUS = createField("kayttajatunnus", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>tilamuutos</code> table reference
     */
    public Tilamuutos() {
        this("tilamuutos", null);
    }

    /**
     * Create an aliased <code>tilamuutos</code> table reference
     */
    public Tilamuutos(String alias) {
        this(alias, TILAMUUTOS);
    }

    private Tilamuutos(String alias, Table<TilamuutosRecord> aliased) {
        this(alias, aliased, null);
    }

    private Tilamuutos(String alias, Table<TilamuutosRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<TilamuutosRecord, Long> getIdentity() {
        return Keys.IDENTITY_TILAMUUTOS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<TilamuutosRecord> getPrimaryKey() {
        return Keys.TILAMUUTOS_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<TilamuutosRecord>> getKeys() {
        return Arrays.<UniqueKey<TilamuutosRecord>>asList(Keys.TILAMUUTOS_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tilamuutos as(String alias) {
        return new Tilamuutos(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Tilamuutos rename(String name) {
        return new Tilamuutos(name, null);
    }
}
