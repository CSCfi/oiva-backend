package fi.minedu.oiva.backend.entity.export

import java.util.Collection

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class KoulutusLupa(
    var koulutukset: Collection[String]) extends fi.minedu.oiva.backend.jooq.tables.pojos.Lupa {

    def this() = this(null)

    // exclude from json
    @JsonIgnore override def getId = super.getId
    @JsonIgnore override def getEdellinenLupaId = super.getEdellinenLupaId
    @JsonIgnore override def getPaatoskierrosId = super.getPaatoskierrosId
    @JsonIgnore override def getLupatilaId = super.getLupatilaId
    @JsonIgnore override def getAsiatyyppiId = super.getAsiatyyppiId
    @JsonIgnore override def getPaatospvm = super.getPaatospvm
    @JsonIgnore override def getMeta = super.getMeta
    @JsonIgnore override def getMaksu = super.getMaksu
    @JsonIgnore override def getLuoja = super.getLuoja
    @JsonIgnore override def getLuontipvm = super.getLuontipvm
    @JsonIgnore override def getPaivittaja = super.getPaivittaja
    @JsonIgnore override def getPaivityspvm = super.getPaivityspvm

    def getKoulutukset = koulutukset
    def setKoulutukset(koulutukset: Collection[String]): Unit = this.koulutukset = koulutukset
}
