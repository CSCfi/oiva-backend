package fi.minedu.oiva.backend.entity

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Paatoskierros(
    var esitysmalli: Esitysmalli) extends fi.minedu.oiva.backend.jooq.tables.pojos.Paatoskierros  {

    def this() = this(null)
    @JsonIgnore override def getEsitysmalliId = super.getEsitysmalliId

    def getEsitysmalli = esitysmalli
    def setEsitysmalli(esitysmalli: Esitysmalli): Unit = this.esitysmalli = esitysmalli
}
