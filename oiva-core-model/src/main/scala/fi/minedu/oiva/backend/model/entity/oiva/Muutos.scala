package fi.minedu.oiva.backend.model.entity.oiva

import java.util

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.model.jooq.tables._
import fi.minedu.oiva.backend.model.entity.MaaraystyyppiValue
import fi.minedu.oiva.backend.model.entity.opintopolku.KoodistoKoodi
import org.apache.commons.lang3.StringUtils

import scala.beans.BeanProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Muutos(@BeanProperty var kohde: pojos.Kohde,
             @BeanProperty var koodi: KoodistoKoodi,
             var ylaKoodit: Array[KoodistoKoodi],
             @BeanProperty var maaraystyyppi: pojos.Maaraystyyppi,
             @BeanProperty var aliMaaraykset: util.Collection[Muutos],
             @BeanProperty var liitteet: util.Collection[Liite],
             var muutos: Muutos,
             var maarays: Maarays,
             @BeanProperty var parent: String,
             @BeanProperty var maaraysUuid: String,
             @BeanProperty var generatedId: String) extends pojos.Muutos {

  def this() = this(null, null, null, null, null, null, null, null, null, null, null)

  @JsonIgnore def kohdeValue = if (null != kohde) kohde.getTunniste else null

  @JsonIgnore def isKohde(kohde: String) = null != kohde && StringUtils.equalsIgnoreCase(kohdeValue, kohde)

  @JsonIgnore def isKoodisto(koodisto: String) = null != getKoodisto && StringUtils.equalsIgnoreCase(getKoodisto, koodisto)

  @JsonIgnore def isKoodiArvo(koodiArvo: String) = null != getKoodiarvo && StringUtils.equalsIgnoreCase(getKoodiarvo, koodiArvo)

  @JsonIgnore def isKoodi(koodisto: String, koodiArvo: String) = isKoodisto(koodisto) && isKoodiArvo(koodiArvo)

  @JsonIgnore def hasKoodistoAndKoodiArvo = StringUtils.isNotBlank(this.getKoodisto) && StringUtils.isNotBlank(this.getKoodiarvo)

  @JsonIgnore def hasKoodiKasite(kasite: String): Boolean = Option.apply(this.koodi).map(k => k.metadata)
    .getOrElse(Array.empty).toStream.exists(m => Option.apply(m.getKasite).getOrElse("").startsWith(kasite))


  def getYlaKoodit = ylaKoodit

  @JsonIgnore def addYlaKoodi(koodi: KoodistoKoodi): Unit =
    if (null == this.ylaKoodit) this.ylaKoodit = Array(koodi)
    else this.ylaKoodit = this.ylaKoodit :+ koodi

  @JsonIgnore def hasYlaKoodi(koodiUri: String = ""): Boolean = null != ylaKoodit && ylaKoodit.exists(ylakoodi => ylakoodi.isKoodi(koodiUri))

  @JsonIgnore def maaraystyyppiValue = if (null != maaraystyyppi) maaraystyyppi.getTunniste else null

  @JsonIgnore def isMaaraystyyppi(tyyppi: String): Boolean = isMaaraystyyppi(MaaraystyyppiValue.valueOf(StringUtils.upperCase(tyyppi)))

  @JsonIgnore def isMaaraystyyppi(tyyppi: MaaraystyyppiValue) = maaraystyyppiValue == tyyppi

  @JsonIgnore def tyyppi = if (null != maaraystyyppiValue) StringUtils.lowerCase(maaraystyyppiValue.name()) else ""

  @JsonIgnore def addAliMaarays(muutos: Muutos): Unit = if (null != muutos && getId != muutos.getId) {
    if (null == this.aliMaaraykset) this.aliMaaraykset = new util.ArrayList()
    this.aliMaaraykset.add(muutos)
  }

  @JsonIgnore def hasAliMaarays = null != aliMaaraykset && !aliMaaraykset.isEmpty

  @JsonIgnore def isYlinMaarays = getParentId == null

  @JsonIgnore def isParentOf(maarays: Maarays) = if (null != maarays) getId == maarays.getParentId else false

  // exclude from json
  @JsonIgnore override def getId = super.getId

}
