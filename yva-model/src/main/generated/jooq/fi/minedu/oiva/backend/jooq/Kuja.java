/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.jooq;


import fi.minedu.oiva.backend.jooq.tables.Asiatyyppi;
import fi.minedu.oiva.backend.jooq.tables.Esitysmalli;
import fi.minedu.oiva.backend.jooq.tables.Kohde;
import fi.minedu.oiva.backend.jooq.tables.Liite;
import fi.minedu.oiva.backend.jooq.tables.Lupa;
import fi.minedu.oiva.backend.jooq.tables.LupaLiite;
import fi.minedu.oiva.backend.jooq.tables.Lupahistoria;
import fi.minedu.oiva.backend.jooq.tables.Lupatila;
import fi.minedu.oiva.backend.jooq.tables.Maarays;
import fi.minedu.oiva.backend.jooq.tables.Maaraystyyppi;
import fi.minedu.oiva.backend.jooq.tables.Muutos;
import fi.minedu.oiva.backend.jooq.tables.Muutosliite;
import fi.minedu.oiva.backend.jooq.tables.Muutospyynto;
import fi.minedu.oiva.backend.jooq.tables.Paatoskierros;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


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
public class Kuja extends SchemaImpl {

    private static final long serialVersionUID = -1297132248;

    /**
     * The reference instance of <code>kuja</code>
     */
    public static final Kuja KUJA = new Kuja();

    /**
     * The table <code>kuja.asiatyyppi</code>.
     */
    public final Asiatyyppi ASIATYYPPI = fi.minedu.oiva.backend.jooq.tables.Asiatyyppi.ASIATYYPPI;

    /**
     * The table <code>kuja.esitysmalli</code>.
     */
    public final Esitysmalli ESITYSMALLI = fi.minedu.oiva.backend.jooq.tables.Esitysmalli.ESITYSMALLI;

    /**
     * The table <code>kuja.kohde</code>.
     */
    public final Kohde KOHDE = fi.minedu.oiva.backend.jooq.tables.Kohde.KOHDE;

    /**
     * The table <code>kuja.liite</code>.
     */
    public final Liite LIITE = fi.minedu.oiva.backend.jooq.tables.Liite.LIITE;

    /**
     * The table <code>kuja.lupa</code>.
     */
    public final Lupa LUPA = fi.minedu.oiva.backend.jooq.tables.Lupa.LUPA;

    /**
     * The table <code>kuja.lupa_liite</code>.
     */
    public final LupaLiite LUPA_LIITE = fi.minedu.oiva.backend.jooq.tables.LupaLiite.LUPA_LIITE;

    /**
     * The table <code>kuja.lupahistoria</code>.
     */
    public final Lupahistoria LUPAHISTORIA = fi.minedu.oiva.backend.jooq.tables.Lupahistoria.LUPAHISTORIA;

    /**
     * The table <code>kuja.lupatila</code>.
     */
    public final Lupatila LUPATILA = fi.minedu.oiva.backend.jooq.tables.Lupatila.LUPATILA;

    /**
     * The table <code>kuja.maarays</code>.
     */
    public final Maarays MAARAYS = fi.minedu.oiva.backend.jooq.tables.Maarays.MAARAYS;

    /**
     * The table <code>kuja.maaraystyyppi</code>.
     */
    public final Maaraystyyppi MAARAYSTYYPPI = fi.minedu.oiva.backend.jooq.tables.Maaraystyyppi.MAARAYSTYYPPI;

    /**
     * The table <code>kuja.muutos</code>.
     */
    public final Muutos MUUTOS = fi.minedu.oiva.backend.jooq.tables.Muutos.MUUTOS;

    /**
     * The table <code>kuja.muutosliite</code>.
     */
    public final Muutosliite MUUTOSLIITE = fi.minedu.oiva.backend.jooq.tables.Muutosliite.MUUTOSLIITE;

    /**
     * The table <code>kuja.muutospyynto</code>.
     */
    public final Muutospyynto MUUTOSPYYNTO = fi.minedu.oiva.backend.jooq.tables.Muutospyynto.MUUTOSPYYNTO;

    /**
     * The table <code>kuja.paatoskierros</code>.
     */
    public final Paatoskierros PAATOSKIERROS = fi.minedu.oiva.backend.jooq.tables.Paatoskierros.PAATOSKIERROS;

    /**
     * No further instances allowed
     */
    private Kuja() {
        super("kuja", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        List result = new ArrayList();
        result.addAll(getSequences0());
        return result;
    }

    private final List<Sequence<?>> getSequences0() {
        return Arrays.<Sequence<?>>asList(
            Sequences.ASIATYYPPI_ID_SEQ,
            Sequences.DIAARINUMERO_SEQ,
            Sequences.ESITYSMALLI_ID_SEQ,
            Sequences.KOHDE_ID_SEQ,
            Sequences.LIITE_ID_SEQ,
            Sequences.LUPAHISTORIA_ID_SEQ,
            Sequences.LUPA_ID_SEQ,
            Sequences.LUPA_LIITE_ID_SEQ,
            Sequences.LUPATILA_ID_SEQ,
            Sequences.MAARAYS_ID_SEQ,
            Sequences.MAARAYSTYYPPI_ID_SEQ,
            Sequences.MUUTOS_ID_SEQ,
            Sequences.MUUTOSLIITE_ID_SEQ,
            Sequences.MUUTOSPYYNTO_ID_SEQ,
            Sequences.PAATOSKIERROS_ID_SEQ);
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Asiatyyppi.ASIATYYPPI,
            Esitysmalli.ESITYSMALLI,
            Kohde.KOHDE,
            Liite.LIITE,
            Lupa.LUPA,
            LupaLiite.LUPA_LIITE,
            Lupahistoria.LUPAHISTORIA,
            Lupatila.LUPATILA,
            Maarays.MAARAYS,
            Maaraystyyppi.MAARAYSTYYPPI,
            Muutos.MUUTOS,
            Muutosliite.MUUTOSLIITE,
            Muutospyynto.MUUTOSPYYNTO,
            Paatoskierros.PAATOSKIERROS);
    }
}
