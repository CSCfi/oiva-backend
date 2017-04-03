package fi.minedu.oiva.backend.entity

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.entity.opintopolku.Koodisto
import org.apache.commons.lang3.StringUtils

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Maarays(
    var kohde: Kohde,
    var maaraystyyppi: Maaraystyyppi,
    var tekstityyppi: Tekstityyppi,
    var koodi: Koodisto) extends fi.minedu.oiva.backend.jooq.tables.pojos.Maarays  {

    def this() = this(null, null, null, null)
    @JsonIgnore override def getKohdeId = super.getKohdeId
    @JsonIgnore override def getMaaraystyyppiId = super.getMaaraystyyppiId
    @JsonIgnore override def getTekstityyppiId = super.getTekstityyppiId

    def getKohde = kohde
    def setKohde(kohde: Kohde): Unit = this.kohde = kohde
    def kohdeValue = if(null != kohde) kohde.getTunniste else null
    def isKohde(kohde: String) = null != kohde && StringUtils.equalsIgnoreCase(kohdeValue, kohde)

    def getMaaraystyyppi = maaraystyyppi
    def setMaaraystyyppi(maaraystyyppi: Maaraystyyppi): Unit = this.maaraystyyppi = maaraystyyppi
    def maaraystyyppiValue = if(null != maaraystyyppi) maaraystyyppi.getTunniste else null
    def isMaaraystyyppi(tyyppi: MaaraystyyppiValue) = maaraystyyppiValue == tyyppi

    def getTekstityyppi = tekstityyppi
    def setTekstityyppi(tekstityyppi: Tekstityyppi): Unit = this.tekstityyppi = tekstityyppi
    def isTekstityyppi = if(null != tekstityyppi) tekstityyppi.getTunniste else null

    def getKoodi = koodi
    def setKoodi(koodi: Koodisto) = this.koodi = koodi
}
