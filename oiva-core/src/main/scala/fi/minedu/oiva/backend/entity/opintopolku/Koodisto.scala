package fi.minedu.oiva.backend.entity.opintopolku

import java.time.LocalDate
import java.time.format.DateTimeParseException

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.entity.TranslatedString

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class Koodisto(
    @BeanProperty var koodistoUri: String,
    @BeanProperty var versio: Integer,
    @BeanProperty var metadata: Array[Metadata],
    @BeanProperty var voimassaAlkuPvm: String,
    @BeanProperty var voimassaLoppuPvm: String) {

    def this() = this(null, null, null, null, null)

    @JsonIgnore def getNimi = TranslatedString.ofNimi(this)
    @JsonIgnore def getKuvaus = TranslatedString.ofKuvaus(this)
    @JsonIgnore def getMetadataList: java.util.List[Metadata] = if(null == metadata) null else metadata.toList.asJava
    @JsonIgnore def is(koodistoValue: String) = koodistoValue == koodistoUri

    @JsonIgnore def isValidDate = voimassaLoppuPvm == null ||
        (try { LocalDate.parse(voimassaLoppuPvm).isAfter(LocalDate.now()) } catch { case e: DateTimeParseException => true })
}

object Koodisto {
    @JsonIgnore def notFound(koodisto: String, koodistoVersio: Integer = null) = Koodisto(koodisto, koodistoVersio, Array(
        Metadata("fi", s"Koodistoa $koodisto ei löydy", "",null,null),
        Metadata("sv", s"Koodistoa $koodisto hittades inte", "",null,null)), null, null)
}