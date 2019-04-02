package fi.minedu.oiva.backend.entity.opintopolku

import java.util.Optional

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.entity.TranslatedString
import java.util.Collection

import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio.Yhteystieto

import scala.collection.JavaConversions._
import scala.beans.BeanProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class Organisaatio(
    @BeanProperty var oid: String,
    @BeanProperty var ytunnus: String,
    @BeanProperty var parentOid: String,
    @BeanProperty var parentOidPath: String,
    @BeanProperty var kotipaikkaUri: String,
    @BeanProperty var nimi: TranslatedString,
    @BeanProperty var postiosoite: Organisaatio.Osoite,
    @BeanProperty var kayntiosoite: Organisaatio.Osoite,
    @BeanProperty var yhteystiedot: Collection[Organisaatio.Yhteystieto],
    @BeanProperty var kuntaKoodi: KoodistoKoodi,
    @BeanProperty var maakuntaKoodi: KoodistoKoodi) {

    def this() = this(null, null, null, null, null, null, null, null, null, null, null)
    @JsonIgnore def kuntaKoodiArvo = if(null != kotipaikkaUri && kotipaikkaUri.startsWith("kunta_")) kotipaikkaUri.substring(6) else null
    @JsonIgnore def isKunta(k: Kunta) = isKuntaKoodi(k.koodiArvo)
    @JsonIgnore def isKuntaKoodi(k: String) = kuntaKoodiArvo == k && null != k
    @JsonIgnore def getKuntaKoodiOpt: java.util.Optional[KoodistoKoodi] = Optional.ofNullable(kuntaKoodi)
    @JsonIgnore def getMaakuntaKoodiOpt: java.util.Optional[KoodistoKoodi] = Optional.ofNullable(maakuntaKoodi)

    @JsonIgnore
    def getMappedYhteystieto: Yhteystieto = {
        val yhteystieto = Yhteystieto(null, null, null)
        this.yhteystiedot.foreach(y => {
            if (y.email != null) yhteystieto.email = y.email
            if (y.numero != null) yhteystieto.numero = y.numero
            if (y.www != null) yhteystieto.www = y.www
        })
        yhteystieto
    }
}

object Organisaatio {
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(Include.NON_NULL)
    case class Osoite(
        @BeanProperty var osoiteTyyppi: String,
        @BeanProperty var osoite: String,
        @BeanProperty var postitoimipaikka: String,
        @BeanProperty var postinumeroUri: String) {

        def this() = this(null, null, null, null)
        @JsonIgnore def postiKoodi = if(null != postinumeroUri && postinumeroUri.startsWith("posti_")) postinumeroUri.substring(6) else null
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(Include.NON_NULL)
    case class Yhteystieto(
        @BeanProperty var numero: String,
        @BeanProperty var email: String,
        @BeanProperty var www: String) {

        def this() = this(null, null, null)
    }
    def apply(oid: String, ytunnus: String) = new Organisaatio(oid, ytunnus, null, null, null, null, null, null, null, null, null)
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class OrganisaatioHits(
    @BeanProperty var numHits: Int,
    @BeanProperty var organisaatiot: Array[Organisaatio]) {

    def this() = this(0, null)
}
