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
    var koodi: KoodistoKoodi) extends fi.minedu.oiva.backend.jooq.tables.pojos.Maarays  {

    def this() = this(null, null, null)
    @JsonIgnore override def getKohdeId = super.getKohdeId
    @JsonIgnore override def getMaaraystyyppiId = super.getMaaraystyyppiId

    @JsonIgnore def hasKoodistoKoodiAssociation = StringUtils.isNotBlank(this.getKoodisto) && StringUtils.isNotBlank(this.getKoodiarvo)

    def getKohde = kohde
    def setKohde(kohde: Kohde): Unit = this.kohde = kohde
    def kohdeValue = if(null != kohde) kohde.getTunniste else null
    def isKohde(kohde: String) = null != kohde && StringUtils.equalsIgnoreCase(kohdeValue, kohde)

    def getMaaraystyyppi = maaraystyyppi
    def setMaaraystyyppi(maaraystyyppi: Maaraystyyppi): Unit = this.maaraystyyppi = maaraystyyppi
    def maaraystyyppiValue = if(null != maaraystyyppi) maaraystyyppi.getTunniste else null
    def isMaaraystyyppi(tyyppi: MaaraystyyppiValue) = maaraystyyppiValue == tyyppi

    def getKoodi = koodi
    def setKoodi(koodi: KoodistoKoodi) = this.koodi = koodi
}
