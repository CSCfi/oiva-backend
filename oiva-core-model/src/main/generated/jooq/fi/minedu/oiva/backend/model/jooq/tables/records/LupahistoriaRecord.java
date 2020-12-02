/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.model.jooq.tables.records;


import fi.minedu.oiva.backend.model.jooq.tables.Lupahistoria;

import java.sql.Date;
import java.util.UUID;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record16;
import org.jooq.Row16;
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
public class LupahistoriaRecord extends UpdatableRecordImpl<LupahistoriaRecord> implements Record16<Long, String, String, String, String, String, Date, Date, Date, String, UUID, String, Date, Long, String, String> {

    private static final long serialVersionUID = 1304964558;

    /**
     * Setter for <code>lupahistoria.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>lupahistoria.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>lupahistoria.diaarinumero</code>.
     */
    public void setDiaarinumero(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>lupahistoria.diaarinumero</code>.
     */
    @NotNull
    @Size(max = 255)
    public String getDiaarinumero() {
        return (String) get(1);
    }

    /**
     * Setter for <code>lupahistoria.ytunnus</code>.
     */
    public void setYtunnus(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>lupahistoria.ytunnus</code>.
     */
    @NotNull
    @Size(max = 10)
    public String getYtunnus() {
        return (String) get(2);
    }

    /**
     * Setter for <code>lupahistoria.oid</code>.
     */
    public void setOid(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>lupahistoria.oid</code>.
     */
    @NotNull
    @Size(max = 255)
    public String getOid() {
        return (String) get(3);
    }

    /**
     * Setter for <code>lupahistoria.maakunta</code>.
     */
    public void setMaakunta(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>lupahistoria.maakunta</code>.
     */
    @NotNull
    @Size(max = 100)
    public String getMaakunta() {
        return (String) get(4);
    }

    /**
     * Setter for <code>lupahistoria.tila</code>.
     */
    public void setTila(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>lupahistoria.tila</code>.
     */
    @NotNull
    @Size(max = 100)
    public String getTila() {
        return (String) get(5);
    }

    /**
     * Setter for <code>lupahistoria.voimassaoloalkupvm</code>.
     */
    public void setVoimassaoloalkupvm(Date value) {
        set(6, value);
    }

    /**
     * Getter for <code>lupahistoria.voimassaoloalkupvm</code>.
     */
    @NotNull
    public Date getVoimassaoloalkupvm() {
        return (Date) get(6);
    }

    /**
     * Setter for <code>lupahistoria.voimassaololoppupvm</code>.
     */
    public void setVoimassaololoppupvm(Date value) {
        set(7, value);
    }

    /**
     * Getter for <code>lupahistoria.voimassaololoppupvm</code>.
     */
    @NotNull
    public Date getVoimassaololoppupvm() {
        return (Date) get(7);
    }

    /**
     * Setter for <code>lupahistoria.paatospvm</code>.
     */
    public void setPaatospvm(Date value) {
        set(8, value);
    }

    /**
     * Getter for <code>lupahistoria.paatospvm</code>.
     */
    @NotNull
    public Date getPaatospvm() {
        return (Date) get(8);
    }

    /**
     * Setter for <code>lupahistoria.filename</code>.
     */
    public void setFilename(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>lupahistoria.filename</code>.
     */
    @NotNull
    @Size(max = 255)
    public String getFilename() {
        return (String) get(9);
    }

    /**
     * Setter for <code>lupahistoria.uuid</code>.
     */
    public void setUuid(UUID value) {
        set(10, value);
    }

    /**
     * Getter for <code>lupahistoria.uuid</code>.
     */
    public UUID getUuid() {
        return (UUID) get(10);
    }

    /**
     * Setter for <code>lupahistoria.asianumero</code>.
     */
    public void setAsianumero(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>lupahistoria.asianumero</code>.
     */
    @Size(max = 16)
    public String getAsianumero() {
        return (String) get(11);
    }

    /**
     * Setter for <code>lupahistoria.kumottupvm</code>.
     */
    public void setKumottupvm(Date value) {
        set(12, value);
    }

    /**
     * Getter for <code>lupahistoria.kumottupvm</code>.
     */
    public Date getKumottupvm() {
        return (Date) get(12);
    }

    /**
     * Setter for <code>lupahistoria.lupa_id</code>.
     */
    public void setLupaId(Long value) {
        set(13, value);
    }

    /**
     * Getter for <code>lupahistoria.lupa_id</code>.
     */
    public Long getLupaId() {
        return (Long) get(13);
    }

    /**
     * Setter for <code>lupahistoria.koulutustyyppi</code>.
     */
    public void setKoulutustyyppi(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>lupahistoria.koulutustyyppi</code>.
     */
    public String getKoulutustyyppi() {
        return (String) get(14);
    }

    /**
     * Setter for <code>lupahistoria.oppilaitostyyppi</code>.
     */
    public void setOppilaitostyyppi(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>lupahistoria.oppilaitostyyppi</code>.
     */
    public String getOppilaitostyyppi() {
        return (String) get(15);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record16 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row16<Long, String, String, String, String, String, Date, Date, Date, String, UUID, String, Date, Long, String, String> fieldsRow() {
        return (Row16) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row16<Long, String, String, String, String, String, Date, Date, Date, String, UUID, String, Date, Long, String, String> valuesRow() {
        return (Row16) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Lupahistoria.LUPAHISTORIA.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Lupahistoria.LUPAHISTORIA.DIAARINUMERO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Lupahistoria.LUPAHISTORIA.YTUNNUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Lupahistoria.LUPAHISTORIA.OID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Lupahistoria.LUPAHISTORIA.MAAKUNTA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Lupahistoria.LUPAHISTORIA.TILA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field7() {
        return Lupahistoria.LUPAHISTORIA.VOIMASSAOLOALKUPVM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field8() {
        return Lupahistoria.LUPAHISTORIA.VOIMASSAOLOLOPPUPVM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field9() {
        return Lupahistoria.LUPAHISTORIA.PAATOSPVM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return Lupahistoria.LUPAHISTORIA.FILENAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UUID> field11() {
        return Lupahistoria.LUPAHISTORIA.UUID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return Lupahistoria.LUPAHISTORIA.ASIANUMERO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field13() {
        return Lupahistoria.LUPAHISTORIA.KUMOTTUPVM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field14() {
        return Lupahistoria.LUPAHISTORIA.LUPA_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field15() {
        return Lupahistoria.LUPAHISTORIA.KOULUTUSTYYPPI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field16() {
        return Lupahistoria.LUPAHISTORIA.OPPILAITOSTYYPPI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getDiaarinumero();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getYtunnus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getOid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getMaakunta();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getTila();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date value7() {
        return getVoimassaoloalkupvm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date value8() {
        return getVoimassaololoppupvm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date value9() {
        return getPaatospvm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getFilename();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID value11() {
        return getUuid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getAsianumero();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date value13() {
        return getKumottupvm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value14() {
        return getLupaId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value15() {
        return getKoulutustyyppi();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value16() {
        return getOppilaitostyyppi();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord value2(String value) {
        setDiaarinumero(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord value3(String value) {
        setYtunnus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord value4(String value) {
        setOid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord value5(String value) {
        setMaakunta(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord value6(String value) {
        setTila(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord value7(Date value) {
        setVoimassaoloalkupvm(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord value8(Date value) {
        setVoimassaololoppupvm(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord value9(Date value) {
        setPaatospvm(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord value10(String value) {
        setFilename(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord value11(UUID value) {
        setUuid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord value12(String value) {
        setAsianumero(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord value13(Date value) {
        setKumottupvm(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord value14(Long value) {
        setLupaId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord value15(String value) {
        setKoulutustyyppi(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord value16(String value) {
        setOppilaitostyyppi(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LupahistoriaRecord values(Long value1, String value2, String value3, String value4, String value5, String value6, Date value7, Date value8, Date value9, String value10, UUID value11, String value12, Date value13, Long value14, String value15, String value16) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LupahistoriaRecord
     */
    public LupahistoriaRecord() {
        super(Lupahistoria.LUPAHISTORIA);
    }

    /**
     * Create a detached, initialised LupahistoriaRecord
     */
    public LupahistoriaRecord(Long id, String diaarinumero, String ytunnus, String oid, String maakunta, String tila, Date voimassaoloalkupvm, Date voimassaololoppupvm, Date paatospvm, String filename, UUID uuid, String asianumero, Date kumottupvm, Long lupaId, String koulutustyyppi, String oppilaitostyyppi) {
        super(Lupahistoria.LUPAHISTORIA);

        set(0, id);
        set(1, diaarinumero);
        set(2, ytunnus);
        set(3, oid);
        set(4, maakunta);
        set(5, tila);
        set(6, voimassaoloalkupvm);
        set(7, voimassaololoppupvm);
        set(8, paatospvm);
        set(9, filename);
        set(10, uuid);
        set(11, asianumero);
        set(12, kumottupvm);
        set(13, lupaId);
        set(14, koulutustyyppi);
        set(15, oppilaitostyyppi);
    }
}
