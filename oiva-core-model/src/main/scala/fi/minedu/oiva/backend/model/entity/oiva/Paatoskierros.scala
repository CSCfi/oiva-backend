package fi.minedu.oiva.backend.model.entity.oiva

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.model.jooq.tables._

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Paatoskierros(
    var esitysmalli: pojos.Esitysmalli) extends pojos.Paatoskierros  {

  def this() = this(null)

  // exclude from json
  @JsonIgnore override def getId = super.getId

  @JsonIgnore override def getEsitysmalliId = super.getEsitysmalliId

  @JsonIgnore override def getLuoja = super.getLuoja

  @JsonIgnore override def getLuontipvm = super.getLuontipvm

  @JsonIgnore override def getPaivittaja = super.getPaivittaja

  @JsonIgnore override def getPaivityspvm = super.getPaivityspvm

  def getEsitysmalli = esitysmalli

  def setEsitysmalli(esitysmalli: pojos.Esitysmalli): Unit = this.esitysmalli = esitysmalli
}
