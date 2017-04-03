package fi.minedu.oiva.backend.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object to map data from opintopolku organisaatiohenkilo API
 * e.g. /authentication-service/resources/henkilo/{oid}/organisaatiohenkilo
 *
 * {
 *   "id": 382781,
 *   "organisaatioOid": "1.2.246.562.10.78946234170",
 *   "organisaatioHenkiloTyyppi": null,
 *   "tehtavanimike": "Testitapaus",
 *   "passivoitu": false,
 *   "voimassaAlkuPvm": null,
 *   "voimassaLoppuPvm": null
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisaatioInfo {
    String oid;
    String organisaatioHenkiloTyyppi;
    String tehtavanimike;
    String passivoitu;
    String voimassaAlkuPvm;
    String voimassaLoppuPvm;

    public String getOid() {
        return oid;
    }

    @JsonProperty("organisaatioOid")
    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getTehtavanimike() {
        return tehtavanimike;
    }

    public void setTehtavanimike(String tehtavanimike) {
        this.tehtavanimike = tehtavanimike;
    }

    public String getOrganisaatioHenkiloTyyppi() {
        return organisaatioHenkiloTyyppi;
    }

    public void setOrganisaatioHenkiloTyyppi(String organisaatioHenkiloTyyppi) {
        this.organisaatioHenkiloTyyppi = organisaatioHenkiloTyyppi;
    }

    public String getPassivoitu() {
        return passivoitu;
    }

    public void setPassivoitu(String passivoitu) {
        this.passivoitu = passivoitu;
    }

    public String getVoimassaAlkuPvm() {
        return voimassaAlkuPvm;
    }

    public void setVoimassaAlkuPvm(String voimassaAlkuPvm) {
        this.voimassaAlkuPvm = voimassaAlkuPvm;
    }

    public String getVoimassaLoppuPvm() {
        return voimassaLoppuPvm;
    }

    public void setVoimassaLoppuPvm(String voimassaLoppuPvm) {
        this.voimassaLoppuPvm = voimassaLoppuPvm;
    }
}
