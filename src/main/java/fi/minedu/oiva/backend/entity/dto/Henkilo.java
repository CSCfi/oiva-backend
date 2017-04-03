package fi.minedu.oiva.backend.entity.dto;

import java.util.List;

public class Henkilo implements java.io.Serializable {
    public String username;

    public String etunimi;

    public String sukunimi;

    public String oid;

    public String organization;

    public List<String> roles;

    public List<OrganisaatioInfo> organisaatioInfo;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEtunimi() {
        return etunimi;
    }

    public void setEtunimi(String etunimi) {
        this.etunimi = etunimi;
    }

    public String getSukunimi() {
        return sukunimi;
    }

    public void setSukunimi(String sukunimi) {
        this.sukunimi = sukunimi;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<OrganisaatioInfo> getOrganisaatioInfo() {
        return organisaatioInfo;
    }

    public void setOrganisaatioInfo(List<OrganisaatioInfo> organisaatioInfo) {
        this.organisaatioInfo = organisaatioInfo;
    }
}
