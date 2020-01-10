package fi.minedu.oiva.backend.model.entity.opintopolku

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonInclude}

import scala.beans.BeanProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class Kunta(
    @BeanProperty var koodiArvo: String,
    @BeanProperty var metadata: Array[Metadata],
    @BeanProperty var tila: String,
    @BeanProperty var voimassaAlkuPvm: String,
    @BeanProperty var voimassaLoppuPvm: String,
    @BeanProperty var jarjestaja: Array[Jarjestaja]) {

    def this() = this(null, null, null, null, null, null)
}
