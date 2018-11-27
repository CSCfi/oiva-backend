package fi.minedu.oiva.backend.entity.oiva

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Lupahistoria(var lupahistoria: Lupahistoria) extends fi.minedu.oiva.backend.jooq.tables.pojos.Lupahistoria  {

  def this() = this(null)

  // exclude from json
  @JsonIgnore override def getId = super.getId

  def getEsitysmalli = lupahistoria
  def setEsitysmalli(esitysmalli: Esitysmalli): Unit = this.lupahistoria = lupahistoria
}
