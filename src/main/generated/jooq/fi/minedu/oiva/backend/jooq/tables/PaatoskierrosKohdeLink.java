/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.jooq.tables;


import fi.minedu.oiva.backend.jooq.Keys;
import fi.minedu.oiva.backend.jooq.Oiva;
import fi.minedu.oiva.backend.jooq.tables.records.PaatoskierrosKohdeLinkRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
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
public class PaatoskierrosKohdeLink extends TableImpl<PaatoskierrosKohdeLinkRecord> {

    private static final long serialVersionUID = 1754332640;

    /**
     * The reference instance of <code>oiva.paatoskierros_kohde_link</code>
     */
    public static final PaatoskierrosKohdeLink PAATOSKIERROS_KOHDE_LINK = new PaatoskierrosKohdeLink();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PaatoskierrosKohdeLinkRecord> getRecordType() {
        return PaatoskierrosKohdeLinkRecord.class;
    }

    /**
     * The column <code>oiva.paatoskierros_kohde_link.kohde_id</code>.
     */
    public final TableField<PaatoskierrosKohdeLinkRecord, Long> KOHDE_ID = createField("kohde_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>oiva.paatoskierros_kohde_link.paatoskierros_id</code>.
     */
    public final TableField<PaatoskierrosKohdeLinkRecord, Long> PAATOSKIERROS_ID = createField("paatoskierros_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>oiva.paatoskierros_kohde_link.pakollisuus</code>.
     */
    public final TableField<PaatoskierrosKohdeLinkRecord, Boolean> PAKOLLISUUS = createField("pakollisuus", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * Create a <code>oiva.paatoskierros_kohde_link</code> table reference
     */
    public PaatoskierrosKohdeLink() {
        this("paatoskierros_kohde_link", null);
    }

    /**
     * Create an aliased <code>oiva.paatoskierros_kohde_link</code> table reference
     */
    public PaatoskierrosKohdeLink(String alias) {
        this(alias, PAATOSKIERROS_KOHDE_LINK);
    }

    private PaatoskierrosKohdeLink(String alias, Table<PaatoskierrosKohdeLinkRecord> aliased) {
        this(alias, aliased, null);
    }

    private PaatoskierrosKohdeLink(String alias, Table<PaatoskierrosKohdeLinkRecord> aliased, Field<?>[] parameters) {
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
    public List<ForeignKey<PaatoskierrosKohdeLinkRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<PaatoskierrosKohdeLinkRecord, ?>>asList(Keys.PAATOSKIERROS_KOHDE_LINK__FK_KOHDE, Keys.PAATOSKIERROS_KOHDE_LINK__FK_PAATOSKIERROS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaatoskierrosKohdeLink as(String alias) {
        return new PaatoskierrosKohdeLink(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PaatoskierrosKohdeLink rename(String name) {
        return new PaatoskierrosKohdeLink(name, null);
    }
}
