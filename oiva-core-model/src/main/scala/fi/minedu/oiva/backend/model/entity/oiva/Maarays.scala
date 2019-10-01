package fi.minedu.oiva.backend.model.entity.oiva

import java.util.{ArrayList, Collection}

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.model.entity.MaaraystyyppiValue
import fi.minedu.oiva.backend.model.entity.opintopolku.{KoodistoKoodi, Organisaatio}
import fi.minedu.oiva.backend.model.jooq.tables._
import org.apache.commons.lang3.StringUtils

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Maarays(
               var kohde: pojos.Kohde,
               var maaraystyyppi: pojos.Maaraystyyppi,
               var koodi: KoodistoKoodi,
               var ylaKoodit: Array[KoodistoKoodi],
               var organisaatio: Organisaatio,
               var aliMaaraykset: Collection[Maarays]) extends pojos.Maarays {

  def this() = this(null, null, null, null, null, null)

  // exclude from json
  @JsonIgnore override def getId = super.getId

  @JsonIgnore override def getKohdeId = super.getKohdeId

  @JsonIgnore override def getMaaraystyyppiId = super.getMaaraystyyppiId

  @JsonIgnore override def getParentId = super.getParentId

  @JsonIgnore override def getLuoja = super.getLuoja

  @JsonIgnore override def getLuontipvm = super.getLuontipvm

  @JsonIgnore override def getPaivittaja = super.getPaivittaja

  @JsonIgnore override def getPaivityspvm = super.getPaivityspvm

  @JsonIgnore def hasKoodistoAndKoodiArvo = StringUtils.isNotBlank(this.getKoodisto) && StringUtils.isNotBlank(this.getKoodiarvo)

  def getKohde = kohde

  def setKohde(kohde: pojos.Kohde): Unit = this.kohde = kohde

  @JsonIgnore def kohdeValue = if (null != kohde) kohde.getTunniste else null

  @JsonIgnore def isKohde(kohde: String) = null != kohde && StringUtils.equalsIgnoreCase(kohdeValue, kohde)

  @JsonIgnore def hasMetaValue(key: String, value: String) = null != getMeta && getMeta.has(key) && getMeta.get(key).asText("").equals(value)

  @JsonIgnore def isKoodisto(koodisto: String) = null != getKoodisto && StringUtils.equalsIgnoreCase(getKoodisto, koodisto)

  @JsonIgnore def isKoodiArvo(koodiArvo: String) = null != getKoodiarvo && StringUtils.equalsIgnoreCase(getKoodiarvo, koodiArvo)

  @JsonIgnore def isKoodi(koodisto: String, koodiArvo: String) = isKoodisto(koodisto) && isKoodiArvo(koodiArvo)

  @JsonIgnore def koodiUri = if (null != getKoodisto && null != getKoodiarvo) getKoodisto + "_" + getKoodiarvo else ""

  def getMaaraystyyppi = maaraystyyppi

  def setMaaraystyyppi(maaraystyyppi: pojos.Maaraystyyppi): Unit = this.maaraystyyppi = maaraystyyppi

  @JsonIgnore def maaraystyyppiValue = if (null != maaraystyyppi) maaraystyyppi.getTunniste else null

  @JsonIgnore def isMaaraystyyppi(tyyppi: String): Boolean = isMaaraystyyppi(MaaraystyyppiValue.valueOf(StringUtils.upperCase(tyyppi)))

  @JsonIgnore def isMaaraystyyppi(tyyppi: MaaraystyyppiValue) = maaraystyyppiValue == tyyppi

  @JsonIgnore def tyyppi = if (null != maaraystyyppiValue) StringUtils.lowerCase(maaraystyyppiValue.name()) else ""

  def getKoodi = koodi

  def setKoodi(koodi: KoodistoKoodi) = this.koodi = koodi

  def getYlaKoodit = ylaKoodit

  @JsonIgnore def addYlaKoodi(koodi: KoodistoKoodi): Unit =
    if (null == this.ylaKoodit) this.ylaKoodit = Array(koodi)
    else this.ylaKoodit = this.ylaKoodit :+ koodi

  @JsonIgnore def hasYlaKoodi(koodiUri: String = ""): Boolean = null != ylaKoodit && ylaKoodit.exists(ylakoodi => ylakoodi.isKoodi(koodiUri))

  def setOrganisaatio(organisaatio: Organisaatio) = this.organisaatio = organisaatio

  def getOrganisaatio = organisaatio

  def getAliMaaraykset = aliMaaraykset

  @JsonIgnore def addAliMaarays(maarays: Maarays): Unit = if (null != maarays && getId != maarays.getId) {
    if (null == this.aliMaaraykset) this.aliMaaraykset = new ArrayList()
    this.aliMaaraykset.add(maarays)
  }

  @JsonIgnore def hasAliMaarays = null != aliMaaraykset && !aliMaaraykset.isEmpty

  @JsonIgnore def isYlinMaarays = getParentId == null

  @JsonIgnore def isParentOf(maarays: Maarays) = if (null != maarays) getId == maarays.getParentId else false
}