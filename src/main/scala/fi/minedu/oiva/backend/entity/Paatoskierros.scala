package fi.minedu.oiva.backend.entity

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Paatoskierros(
    var esitysmalli: Esitysmalli) extends fi.minedu.oiva.backend.jooq.tables.pojos.Paatoskierros  {

    def this() = this(null)

    // exclude from json
    @JsonIgnore override def getId = super.getId
    @JsonIgnore override def getEsitysmalliId = super.getEsitysmalliId
    @JsonIgnore override def getLuoja = super.getLuoja
    @JsonIgnore override def getLuontipvm = super.getLuontipvm
    @JsonIgnore override def getPaivittaja = super.getPaivittaja
    @JsonIgnore override def getPaivityspvm = super.getPaivityspvm

    def getEsitysmalli = esitysmalli
    def setEsitysmalli(esitysmalli: Esitysmalli): Unit = this.esitysmalli = esitysmalli
}
