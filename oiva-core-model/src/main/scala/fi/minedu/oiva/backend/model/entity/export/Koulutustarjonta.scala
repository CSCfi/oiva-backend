package fi.minedu.oiva.backend.model.entity.export

import java.util
import java.util.Collection

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.model.entity.oiva.Maarays
import fi.minedu.oiva.backend.model.jooq.tables.pojos.Lupa

import scala.beans.BeanProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class Koulutustarjonta(
    @BeanProperty var koulutukset: Collection[KoulutustarjontaKoulutus],
    @BeanProperty var opetuskielet: Collection[String]) extends Lupa {

    def this() = this(null, null)

    // exclude from json
    @JsonIgnore override def getId = super.getId
    @JsonIgnore override def getEdellinenLupaId = super.getEdellinenLupaId
    @JsonIgnore override def getPaatoskierrosId = super.getPaatoskierrosId
    @JsonIgnore override def getLupatilaId = super.getLupatilaId
    @JsonIgnore override def getAsiatyyppiId = super.getAsiatyyppiId
    @JsonIgnore override def getMeta = super.getMeta
    @JsonIgnore override def getMaksu = super.getMaksu
    @JsonIgnore override def getLuoja = super.getLuoja
    @JsonIgnore override def getLuontipvm = super.getLuontipvm
    @JsonIgnore override def getPaivittaja = super.getPaivittaja
    @JsonIgnore override def getPaivityspvm = super.getPaivityspvm

    @JsonIgnore def addKoulutus(koulutus: KoulutustarjontaKoulutus) = {
        if(null == koulutukset) setKoulutukset(new util.ArrayList())
        koulutukset.add(koulutus)
    }

    @JsonIgnore def addOpetuskieli(kieli: String) = {
        if(null == opetuskielet) setOpetuskielet(new util.ArrayList())
        opetuskielet.add(kieli)
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class KoulutustarjontaKoulutus(
    @JsonIgnore var maaraysId: Long,
    @BeanProperty var koulutus: String,
    @BeanProperty var koulutusala: String,
    @BeanProperty var kielet: Collection[String],
    @BeanProperty var rajoitteet: Collection[String]) {

    def this(maarays: Maarays) = this(maarays.getId, maarays.getKoodiarvo, null, null, null)

    @JsonIgnore def getMaaraysId = maaraysId

    @JsonIgnore def addKieli(kieli: String) = {
        if(null == kielet) setKielet(new util.ArrayList())
        kielet.add(kieli)
    }

    @JsonIgnore def addRajoitus(koodiUri: String) = {
        if(null == rajoitteet) setRajoitteet(new util.ArrayList())
        rajoitteet.add(koodiUri)
    }
}