package fi.minedu.oiva.backend.model.entity.opintopolku

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonInclude, JsonIgnoreProperties}

import scala.beans.BeanProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class Metadata(
    @BeanProperty var kieli: String,
    @BeanProperty var nimi: String,
    @BeanProperty var kuvaus: String,
    @BeanProperty var kasite: String,
    @BeanProperty var huomioitavaKoodi: String,
    @BeanProperty var sisaltaaMerkityksen: String,
    @BeanProperty var kayttoohje: String) {

    def this() = this(null, null, null, null, null, null, null)
}
