/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.model.jooq.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;


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
public class MuutospyyntoTilamuutos implements Serializable {

    private static final long serialVersionUID = -1609916974;

    private Long muutospyyntoId;
    private Long tilamuutosId;

    public MuutospyyntoTilamuutos() {}

    public MuutospyyntoTilamuutos(MuutospyyntoTilamuutos value) {
        this.muutospyyntoId = value.muutospyyntoId;
        this.tilamuutosId = value.tilamuutosId;
    }

    public MuutospyyntoTilamuutos(
        Long muutospyyntoId,
        Long tilamuutosId
    ) {
        this.muutospyyntoId = muutospyyntoId;
        this.tilamuutosId = tilamuutosId;
    }

    @NotNull
    public Long getMuutospyyntoId() {
        return this.muutospyyntoId;
    }

    public void setMuutospyyntoId(Long muutospyyntoId) {
        this.muutospyyntoId = muutospyyntoId;
    }

    @NotNull
    public Long getTilamuutosId() {
        return this.tilamuutosId;
    }

    public void setTilamuutosId(Long tilamuutosId) {
        this.tilamuutosId = tilamuutosId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MuutospyyntoTilamuutos (");

        sb.append(muutospyyntoId);
        sb.append(", ").append(tilamuutosId);

        sb.append(")");
        return sb.toString();
    }
}
