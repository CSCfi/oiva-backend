/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.jooq.tables;


import fi.minedu.oiva.backend.entity.TekstityyppiValue;
import fi.minedu.oiva.backend.entity.TekstityyppiValue.Converter;
import fi.minedu.oiva.backend.entity.TranslatedString;
import fi.minedu.oiva.backend.jooq.Keys;
import fi.minedu.oiva.backend.jooq.Oiva;
import fi.minedu.oiva.backend.jooq.TranslatedStringBinding;
import fi.minedu.oiva.backend.jooq.tables.records.TekstityyppiRecord;

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
public class Tekstityyppi extends TableImpl<TekstityyppiRecord> {

    private static final long serialVersionUID = -838975225;

    /**
     * The reference instance of <code>oiva.tekstityyppi</code>
     */
    public static final Tekstityyppi TEKSTITYYPPI = new Tekstityyppi();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TekstityyppiRecord> getRecordType() {
        return TekstityyppiRecord.class;
    }

    /**
     * The column <code>oiva.tekstityyppi.id</code>.
     */
    public final TableField<TekstityyppiRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('tekstityyppi_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>oiva.tekstityyppi.tunniste</code>.
     */
    public final TableField<TekstityyppiRecord, TekstityyppiValue> TUNNISTE = createField("tunniste", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "", new Converter());

    /**
     * The column <code>oiva.tekstityyppi.selite</code>.
     */
    public final TableField<TekstityyppiRecord, TranslatedString> SELITE = createField("selite", org.jooq.impl.DefaultDataType.getDefaultDataType("jsonb"), this, "", new TranslatedStringBinding());

    /**
     * Create a <code>oiva.tekstityyppi</code> table reference
     */
    public Tekstityyppi() {
        this("tekstityyppi", null);
    }

    /**
     * Create an aliased <code>oiva.tekstityyppi</code> table reference
     */
    public Tekstityyppi(String alias) {
        this(alias, TEKSTITYYPPI);
    }

    private Tekstityyppi(String alias, Table<TekstityyppiRecord> aliased) {
        this(alias, aliased, null);
    }

    private Tekstityyppi(String alias, Table<TekstityyppiRecord> aliased, Field<?>[] parameters) {
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
    public Identity<TekstityyppiRecord, Long> getIdentity() {
        return Keys.IDENTITY_TEKSTITYYPPI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<TekstityyppiRecord> getPrimaryKey() {
        return Keys.TEKSTITYYPPI_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<TekstityyppiRecord>> getKeys() {
        return Arrays.<UniqueKey<TekstityyppiRecord>>asList(Keys.TEKSTITYYPPI_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tekstityyppi as(String alias) {
        return new Tekstityyppi(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Tekstityyppi rename(String name) {
        return new Tekstityyppi(name, null);
    }
}
