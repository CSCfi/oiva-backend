/*
 * This file is generated by jOOQ.
 */
package fi.minedu.oiva.backend.model.jooq;


import fi.minedu.oiva.backend.model.jooq.tables.Asiatilamuutos;
import fi.minedu.oiva.backend.model.jooq.tables.Asiatyyppi;
import fi.minedu.oiva.backend.model.jooq.tables.Esitysmalli;
import fi.minedu.oiva.backend.model.jooq.tables.Kohde;
import fi.minedu.oiva.backend.model.jooq.tables.Liite;
import fi.minedu.oiva.backend.model.jooq.tables.Lupa;
import fi.minedu.oiva.backend.model.jooq.tables.LupaLiite;
import fi.minedu.oiva.backend.model.jooq.tables.Lupahistoria;
import fi.minedu.oiva.backend.model.jooq.tables.Lupatila;
import fi.minedu.oiva.backend.model.jooq.tables.Maarays;
import fi.minedu.oiva.backend.model.jooq.tables.Maaraystyyppi;
import fi.minedu.oiva.backend.model.jooq.tables.Muutos;
import fi.minedu.oiva.backend.model.jooq.tables.MuutosLiite;
import fi.minedu.oiva.backend.model.jooq.tables.Muutospyynto;
import fi.minedu.oiva.backend.model.jooq.tables.MuutospyyntoAsiatilamuutos;
import fi.minedu.oiva.backend.model.jooq.tables.MuutospyyntoLiite;
import fi.minedu.oiva.backend.model.jooq.tables.Paatoskierros;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in 
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>asiatilamuutos</code>.
     */
    public static final Asiatilamuutos ASIATILAMUUTOS = Asiatilamuutos.ASIATILAMUUTOS;

    /**
     * The table <code>asiatyyppi</code>.
     */
    public static final Asiatyyppi ASIATYYPPI = Asiatyyppi.ASIATYYPPI;

    /**
     * The table <code>esitysmalli</code>.
     */
    public static final Esitysmalli ESITYSMALLI = Esitysmalli.ESITYSMALLI;

    /**
     * The table <code>kohde</code>.
     */
    public static final Kohde KOHDE = Kohde.KOHDE;

    /**
     * The table <code>liite</code>.
     */
    public static final Liite LIITE = Liite.LIITE;

    /**
     * The table <code>lupa</code>.
     */
    public static final Lupa LUPA = Lupa.LUPA;

    /**
     * The table <code>lupahistoria</code>.
     */
    public static final Lupahistoria LUPAHISTORIA = Lupahistoria.LUPAHISTORIA;

    /**
     * The table <code>lupa_liite</code>.
     */
    public static final LupaLiite LUPA_LIITE = LupaLiite.LUPA_LIITE;

    /**
     * The table <code>lupatila</code>.
     */
    public static final Lupatila LUPATILA = Lupatila.LUPATILA;

    /**
     * The table <code>maarays</code>.
     */
    public static final Maarays MAARAYS = Maarays.MAARAYS;

    /**
     * The table <code>maaraystyyppi</code>.
     */
    public static final Maaraystyyppi MAARAYSTYYPPI = Maaraystyyppi.MAARAYSTYYPPI;

    /**
     * The table <code>muutos</code>.
     */
    public static final Muutos MUUTOS = Muutos.MUUTOS;

    /**
     * The table <code>muutos_liite</code>.
     */
    public static final MuutosLiite MUUTOS_LIITE = MuutosLiite.MUUTOS_LIITE;

    /**
     * The table <code>muutospyynto</code>.
     */
    public static final Muutospyynto MUUTOSPYYNTO = Muutospyynto.MUUTOSPYYNTO;

    /**
     * The table <code>muutospyynto_asiatilamuutos</code>.
     */
    public static final MuutospyyntoAsiatilamuutos MUUTOSPYYNTO_ASIATILAMUUTOS = MuutospyyntoAsiatilamuutos.MUUTOSPYYNTO_ASIATILAMUUTOS;

    /**
     * The table <code>muutospyynto_liite</code>.
     */
    public static final MuutospyyntoLiite MUUTOSPYYNTO_LIITE = MuutospyyntoLiite.MUUTOSPYYNTO_LIITE;

    /**
     * The table <code>paatoskierros</code>.
     */
    public static final Paatoskierros PAATOSKIERROS = Paatoskierros.PAATOSKIERROS;
}
