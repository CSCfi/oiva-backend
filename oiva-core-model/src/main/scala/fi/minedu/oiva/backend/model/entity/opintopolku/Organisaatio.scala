package fi.minedu.oiva.backend.model.entity.opintopolku

import java.util
import java.util.Optional

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.model.entity.TranslatedString
import fi.minedu.oiva.backend.model.entity.opintopolku.Organisaatio.Yhteystieto

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class Organisaatio(
                         @BeanProperty var oid: String,
                         @BeanProperty var status: String,
                         @BeanProperty var ytunnus: String,
                         @BeanProperty var parentOid: String,
                         @BeanProperty var parentOidPath: String,
                         @BeanProperty var kotipaikkaUri: String,
                         @BeanProperty var nimi: TranslatedString,
                         @BeanProperty var oppilaitostyyppi: String,
                         @BeanProperty var postiosoite: Organisaatio.Osoite,
                         @BeanProperty var kayntiosoite: Organisaatio.Osoite,
                         @BeanProperty var yhteystiedot: util.Collection[Organisaatio.Yhteystieto],
                         @BeanProperty var kuntaKoodi: KoodistoKoodi,
                         @BeanProperty var maakuntaKoodi: KoodistoKoodi,
                         @BeanProperty var muutKotipaikatUris: util.Collection[String],
                         @BeanProperty var muutKuntaKoodit: util.Collection[KoodistoKoodi],
                         @BeanProperty var children: util.Collection[Organisaatio]) {

  def this() = this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)

  @JsonIgnore def kuntaKoodiArvo: String = parseKuntaKoodi(kotipaikkaUri)

  def parseKuntaKoodi(kuntaUri: String): String = {
    if (null != kuntaUri && kuntaUri.startsWith("kunta_")) kuntaUri.substring(6) else null
  }

  @JsonIgnore def addMuutKuntaKoodi(koodistoKoodi: KoodistoKoodi): Unit = {
    if (muutKuntaKoodit == null) muutKuntaKoodit = new util.ArrayList[KoodistoKoodi]
    muutKuntaKoodit.add(koodistoKoodi)
  }

  @JsonIgnore def getAllKuntaKoodis: util.Collection[KoodistoKoodi] = {
    val all = new util.ArrayList[KoodistoKoodi]()
    all.add(kuntaKoodi)
    if (muutKuntaKoodit != null) all.addAll(muutKuntaKoodit)
    all
  }

  @JsonIgnore def isKunta(k: Kunta): Boolean = isKuntaKoodi(k.koodiArvo)

  @JsonIgnore def isKuntaKoodi(k: String): Boolean = kuntaKoodiArvo == k && null != k

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

    @JsonIgnore def postiKoodi: String = if (null != postinumeroUri && postinumeroUri.startsWith("posti_")) postinumeroUri.substring(6) else null
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonInclude(Include.NON_NULL)
  case class Yhteystieto(
                          @BeanProperty var numero: String,
                          @BeanProperty var email: String,
                          @BeanProperty var www: String) {

    def this() = this(null, null, null)
  }

  def apply(oid: String, ytunnus: String) = new Organisaatio(oid, ytunnus, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class OrganisaatioHits(
                             @BeanProperty var numHits: Int,
                             @BeanProperty var organisaatiot: Array[Organisaatio]) {

  def this() = this(0, null)
}
