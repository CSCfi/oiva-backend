package fi.minedu.oiva.backend.entity

import java.util.{ArrayList, Collection}

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi
import org.apache.commons.lang3.StringUtils

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Muutos(    var kohde: Kohde,
                 var koodi: KoodistoKoodi,
                 var ylaKoodit: Array[KoodistoKoodi],
                 var maaraystyyppi: Maaraystyyppi,
                 var aliMaaraykset: Collection[Muutos],
                 var muutos: Muutos) extends fi.minedu.oiva.backend.jooq.tables.pojos.Muutos  {

  def this() = this(null,null,null,null,null,null)

  def getKohde = kohde
  def setKohde(kohde: Kohde): Unit = this.kohde = kohde
  @JsonIgnore def kohdeValue = if(null != kohde) kohde.getTunniste else null
  @JsonIgnore def isKohde(kohde: String) = null != kohde && StringUtils.equalsIgnoreCase(kohdeValue, kohde)

  @JsonIgnore def isKoodisto(koodisto: String) = null != getKoodisto && StringUtils.equalsIgnoreCase(getKoodisto, koodisto)
  @JsonIgnore def isKoodiArvo(koodiArvo: String) = null != getKoodiarvo && StringUtils.equalsIgnoreCase(getKoodiarvo, koodiArvo)
  @JsonIgnore def isKoodi(koodisto: String, koodiArvo: String) = isKoodisto(koodisto) && isKoodiArvo(koodiArvo)

  @JsonIgnore def hasKoodistoAndKoodiArvo = StringUtils.isNotBlank(this.getKoodisto) && StringUtils.isNotBlank(this.getKoodiarvo)

  def getKoodi = koodi
  def setKoodi(koodi: KoodistoKoodi) = this.koodi = koodi
  def getYlaKoodit = ylaKoodit
  @JsonIgnore def addYlaKoodi(koodi: KoodistoKoodi): Unit =
    if(null == this.ylaKoodit) this.ylaKoodit = Array(koodi)
    else this.ylaKoodit = this.ylaKoodit :+ koodi
  @JsonIgnore def hasYlaKoodi(koodiUri: String = ""): Boolean = null != ylaKoodit && ylaKoodit.exists(ylakoodi => ylakoodi.isKoodi(koodiUri))

  def getMaaraystyyppi = maaraystyyppi
  def setMaaraystyyppi(maaraystyyppi: Maaraystyyppi): Unit = this.maaraystyyppi = maaraystyyppi
  @JsonIgnore def maaraystyyppiValue = if(null != maaraystyyppi) maaraystyyppi.getTunniste else null
  @JsonIgnore def isMaaraystyyppi(tyyppi: String): Boolean = isMaaraystyyppi(MaaraystyyppiValue.valueOf(StringUtils.upperCase(tyyppi)))
  @JsonIgnore def isMaaraystyyppi(tyyppi: MaaraystyyppiValue) = maaraystyyppiValue == tyyppi
  @JsonIgnore def tyyppi = if(null != maaraystyyppiValue) StringUtils.lowerCase(maaraystyyppiValue.name()) else ""

  def getAliMaaraykset = aliMaaraykset
  @JsonIgnore def addAliMaarays(muutos: Muutos): Unit = if(null != muutos && getId != muutos.getId) {
    if (null == this.aliMaaraykset) this.aliMaaraykset = new ArrayList()
    this.aliMaaraykset.add(muutos)
  }
  @JsonIgnore def hasAliMaarays = null != aliMaaraykset && !aliMaaraykset.isEmpty
  @JsonIgnore def isYlinMaarays = getParentId == null
  @JsonIgnore def isParentOf(maarays: Maarays) = if(null != maarays) getId == maarays.getParentId else false

  // exclude from json
  @JsonIgnore override def getId = super.getId

}
