package fi.minedu.oiva.backend.model.entity.opintopolku

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.model.entity.TranslatedString

import scala.beans.BeanProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class Jarjestaja(
    @BeanProperty var nimi: TranslatedString,
    @BeanProperty var oid: String,
    @BeanProperty var ytunnus: String) {

    def this() = this(null, null, null)
}

object Jarjestaja {
    def apply(org: Organisaatio) = new Jarjestaja(org.nimi, org.oid, org.ytunnus)
}
