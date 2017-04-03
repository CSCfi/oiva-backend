/*
 * This file is generated by jOOQ.
*/
package fi.minedu.oiva.backend.jooq.tables.pojos;


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
public class PaatoskierrosKohdeLink implements Serializable {

    private static final long serialVersionUID = -401313522;

    private Long    kohdeId;
    private Long    paatoskierrosId;
    private Boolean pakollisuus;

    public PaatoskierrosKohdeLink() {}

    public PaatoskierrosKohdeLink(PaatoskierrosKohdeLink value) {
        this.kohdeId = value.kohdeId;
        this.paatoskierrosId = value.paatoskierrosId;
        this.pakollisuus = value.pakollisuus;
    }

    public PaatoskierrosKohdeLink(
        Long    kohdeId,
        Long    paatoskierrosId,
        Boolean pakollisuus
    ) {
        this.kohdeId = kohdeId;
        this.paatoskierrosId = paatoskierrosId;
        this.pakollisuus = pakollisuus;
    }

    @NotNull
    public Long getKohdeId() {
        return this.kohdeId;
    }

    public void setKohdeId(Long kohdeId) {
        this.kohdeId = kohdeId;
    }

    @NotNull
    public Long getPaatoskierrosId() {
        return this.paatoskierrosId;
    }

    public void setPaatoskierrosId(Long paatoskierrosId) {
        this.paatoskierrosId = paatoskierrosId;
    }

    public Boolean getPakollisuus() {
        return this.pakollisuus;
    }

    public void setPakollisuus(Boolean pakollisuus) {
        this.pakollisuus = pakollisuus;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PaatoskierrosKohdeLink (");

        sb.append(kohdeId);
        sb.append(", ").append(paatoskierrosId);
        sb.append(", ").append(pakollisuus);

        sb.append(")");
        return sb.toString();
    }
}
