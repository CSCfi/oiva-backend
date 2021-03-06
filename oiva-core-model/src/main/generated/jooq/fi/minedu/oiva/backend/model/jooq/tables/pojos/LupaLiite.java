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
public class LupaLiite implements Serializable {

    private static final long serialVersionUID = 1637254895;

    private Long id;
    private Long lupaId;
    private Long liiteId;

    public LupaLiite() {}

    public LupaLiite(LupaLiite value) {
        this.id = value.id;
        this.lupaId = value.lupaId;
        this.liiteId = value.liiteId;
    }

    public LupaLiite(
        Long id,
        Long lupaId,
        Long liiteId
    ) {
        this.id = id;
        this.lupaId = lupaId;
        this.liiteId = liiteId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public Long getLupaId() {
        return this.lupaId;
    }

    public void setLupaId(Long lupaId) {
        this.lupaId = lupaId;
    }

    @NotNull
    public Long getLiiteId() {
        return this.liiteId;
    }

    public void setLiiteId(Long liiteId) {
        this.liiteId = liiteId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("LupaLiite (");

        sb.append(id);
        sb.append(", ").append(lupaId);
        sb.append(", ").append(liiteId);

        sb.append(")");
        return sb.toString();
    }
}
