package fi.minedu.oiva.backend.model.entity.opintopolku

import java.time.LocalDate
import java.time.format.DateTimeParseException

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.model.entity.TranslatedString

import scala.collection.JavaConverters._
import scala.beans.BeanProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class KoodistoKoodi(
    @BeanProperty var koodiArvo: String,
    @BeanProperty var koodisto: KoodistoKoodi.KoodistoTiedot,
    @BeanProperty var versio: Integer,
    @BeanProperty var metadata: Array[Metadata],
    @BeanProperty var voimassaAlkuPvm: String,
    @BeanProperty var voimassaLoppuPvm: String) {

    def this() = this(null, null, null, null, null, null)

    @JsonIgnore def getNimi = TranslatedString.ofNimi(this)
    @JsonIgnore def getKuvaus = TranslatedString.ofKuvaus(this)
    @JsonIgnore def getMetadataList: java.util.List[Metadata] = if(null == metadata) null else metadata.toList.asJava
    @JsonIgnore def getKoodistoUri = if(null == koodisto) null else koodisto.koodistoUri
    @JsonIgnore def isKoodisto(koodistoValue: String) = koodistoValue == getKoodistoUri
    @JsonIgnore def isKoodi(koodiUri: String = ""): Boolean = koodiUri.split("_") match {
        case Array(koodistoUri, koodiArvo) => isKoodi(koodistoUri, koodiArvo)
        case _ => false
    }
    @JsonIgnore def isKoodi(koodistoValue: String, koodiValue: String): Boolean = koodistoValue == getKoodistoUri && koodiValue == getKoodiArvo
    @JsonIgnore def koodiUri = if(null != getKoodistoUri && null != koodiArvo) getKoodistoUri + "_" + koodiArvo else null

    @JsonIgnore def isValidDate = voimassaLoppuPvm == null ||
        (try { LocalDate.parse(voimassaLoppuPvm).isAfter(LocalDate.now()) } catch { case e: DateTimeParseException => true })

    override def toString: String = koodiArvo + " " + getNimi.getFirstOfOrEmpty("fi", "sv")

}

object KoodistoKoodi {
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(Include.NON_NULL)
    case class KoodistoTiedot(@BeanProperty var koodistoUri: String) {
        def this() = this(null)
    }
    @JsonIgnore def notFound(koodi: String) = KoodistoKoodi(koodi, null, null, Array(
        Metadata("fi", s"Koodia $koodi ei löydy", "",null,null,null,null),
        Metadata("sv", s"Koda $koodi hittades inte", "",null,null,null,null)), null, null)
}
