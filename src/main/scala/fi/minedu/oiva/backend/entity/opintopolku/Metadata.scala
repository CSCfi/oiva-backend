package fi.minedu.oiva.backend.entity.opintopolku

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonInclude, JsonIgnoreProperties}

import scala.beans.BeanProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class Metadata(
    @BeanProperty var kieli: String,
    @BeanProperty var nimi: String) {

    def this() = this(null, null)
}
