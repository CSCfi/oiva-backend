package fi.minedu.oiva.backend.model.entity.opintopolku

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonInclude, JsonIgnore, JsonIgnoreProperties}
import scala.beans.BeanProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class Maakunta(
    @BeanProperty var koodiArvo: String,
    @BeanProperty var kunta: Array[Kunta],
    @BeanProperty var metadata: Array[Metadata],
    @BeanProperty var tila: String,
    @BeanProperty var voimassaAlkuPvm: String,
    @BeanProperty var voimassaLoppuPvm: String) {

    def this() = this(null, null, null, null, null, null)
    @JsonIgnore def getKunnat: Array[Kunta] = if(null == kunta) Array.empty else kunta
}
