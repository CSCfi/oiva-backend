package fi.minedu.oiva.backend.entity

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import org.apache.commons.lang3.StringUtils

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Kohde() extends fi.minedu.oiva.backend.jooq.tables.pojos.Kohde  {

    // exclude from json
    @JsonIgnore override def getLuoja = super.getLuoja
    @JsonIgnore override def getLuontipvm = super.getLuontipvm
    @JsonIgnore override def getPaivittaja = super.getPaivittaja
    @JsonIgnore override def getPaivityspvm = super.getPaivityspvm

    @JsonIgnore def isTunniste(tunniste: String) = StringUtils.equalsIgnoreCase(getTunniste, tunniste)
}
