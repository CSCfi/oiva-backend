package fi.minedu.oiva.backend.entity

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi
import org.apache.commons.lang3.StringUtils

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Maarays(
    var kohde: Kohde,
    var maaraystyyppi: Maaraystyyppi,
    var koodi: KoodistoKoodi,
    var ylaKoodit: Array[KoodistoKoodi]) extends fi.minedu.oiva.backend.jooq.tables.pojos.Maarays  {

    def this() = this(null, null, null, Array())
    @JsonIgnore override def getKohdeId = super.getKohdeId
    @JsonIgnore override def getMaaraystyyppiId = super.getMaaraystyyppiId

    @JsonIgnore def hasKoodistoKoodiBind = StringUtils.isNotBlank(this.getKoodisto) && StringUtils.isNotBlank(this.getKoodiarvo)

    def getKohde = kohde
    def setKohde(kohde: Kohde): Unit = this.kohde = kohde
    def kohdeValue = if(null != kohde) kohde.getTunniste else null
    def isKohde(kohde: String) = null != kohde && StringUtils.equalsIgnoreCase(kohdeValue, kohde)

    def isKoodisto(koodisto: String) = null != getKoodisto && StringUtils.equalsIgnoreCase(getKoodisto, koodisto)

    def getMaaraystyyppi = maaraystyyppi
    def setMaaraystyyppi(maaraystyyppi: Maaraystyyppi): Unit = this.maaraystyyppi = maaraystyyppi
    def maaraystyyppiValue = if(null != maaraystyyppi) maaraystyyppi.getTunniste else null
    def isMaaraystyyppi(tyyppi: String): Boolean = isMaaraystyyppi(MaaraystyyppiValue.valueOf(StringUtils.upperCase(tyyppi)))
    def isMaaraystyyppi(tyyppi: MaaraystyyppiValue) = maaraystyyppiValue == tyyppi
    def tyyppi = if(null != maaraystyyppiValue) StringUtils.lowerCase(maaraystyyppiValue.name()) else ""

    def getKoodi = koodi
    def setKoodi(koodi: KoodistoKoodi) = this.koodi = koodi

    def getYlaKoodit = ylaKoodit
    def addYlaKoodi(koodi: KoodistoKoodi) { this.ylaKoodit = this.ylaKoodit :+ koodi }
    def hasYlaKoodi(koodiUri: String = ""): Boolean = koodiUri.split("_") match {
        case Array(koodisto, koodiArvo) => hasYlaKoodi(koodisto, koodiArvo)
        case _ => false
    }
    def hasYlaKoodi(koodisto: String, koodiArvo: String) = ylaKoodit.exists(koodi => koodi.isKoodisto(koodisto) && koodi.koodiArvo == koodiArvo)
}