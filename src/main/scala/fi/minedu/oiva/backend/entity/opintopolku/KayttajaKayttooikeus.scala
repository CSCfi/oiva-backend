package fi.minedu.oiva.backend.entity.opintopolku

import java.util.Collection
import java.util.Optional

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.security.annotations.OivaAccess
import org.apache.commons.lang3.StringUtils

import scala.collection.JavaConversions._
import scala.beans.BeanProperty

/*
 * OIVA_APP
 * OIVA_APP_YLLAPITAJA
 * OIVA_APP_ESITTELIJA
 * OIVA_APP_KAYTTAJA
 * OIVA_APP_KAYTTAJA_111.222.333
*/

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class KayttajaKayttooikeus(
    @BeanProperty var oidHenkilo: String,
    @BeanProperty var organisaatiot: Collection[OrganisaatioKayttooikeus]) {

    def this() = this(null, null)

    @JsonIgnore def getOrganisaatioOids: Optional[java.util.List[String]] =
        if(null == organisaatiot) Optional.empty()
        else Optional.ofNullable(organisaatiot.toList.map(_.organisaatioOid))

    @JsonIgnore def getOivaOikeudet: Optional[java.util.List[String]] =
        if(null == organisaatiot) Optional.empty()
        else organisaatiot.toList.map(_.getOivaOikeudet).flatten.distinct match {
            case Nil => Optional.empty()
            case list => Optional.of(OivaAccess.Role_Application :: list)
        }
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class OrganisaatioKayttooikeus(
   @BeanProperty var organisaatioOid: String,
   @BeanProperty var kayttooikeudet: Collection[Kayttooikeus]) {

    def this() = this(null, null)

    @JsonIgnore def getOivaOikeudet = if(null == kayttooikeudet) Nil else {
        val oikeudet = kayttooikeudet.filter(_.isOivaOikeus).map(_.oivaOikeus)
        oikeudet ++ (if(StringUtils.isNotBlank(organisaatioOid)) oikeudet.map(_ + "_" + organisaatioOid) else Nil)
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class Kayttooikeus(
    @BeanProperty var palvelu: String,
    @BeanProperty var oikeus: String) {

    def this() = this(null, null)

    @JsonIgnore def isOivaOikeus = palvelu == OivaAccess.Context_Oiva && StringUtils.isNotBlank(oivaOikeus)
    @JsonIgnore def oivaOikeus = oikeus match {
        case OivaAccess.Context_Yllapitaja => OivaAccess.Role_Yllapitaja
        case OivaAccess.Context_Esittelija => OivaAccess.Role_Esittelija
        case OivaAccess.Context_Kayttaja => OivaAccess.Role_Kayttaja
        case _ => ""
    }
}
