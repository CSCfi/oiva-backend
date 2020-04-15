package fi.minedu.oiva.backend.model.entity.oiva

import java.util.{Collection, Optional}

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.model.entity.opintopolku.Organisaatio
import fi.minedu.oiva.backend.model.jooq.tables._
import org.apache.commons.lang3.RegExUtils

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Lupa(
    var paatoskierros: Paatoskierros,
    var asiatyyppi: Asiatyyppi,
    var lupatila: Lupatila,
    var jarjestaja: Organisaatio,
    var maaraykset: Collection[Maarays]) extends pojos.Lupa {

    def this() = this(null, null, null, null, null)

    // exclude from json
    @JsonIgnore override def getId = super.getId
    @JsonIgnore override def getPaatoskierrosId = super.getPaatoskierrosId
    @JsonIgnore override def getAsiatyyppiId = super.getAsiatyyppiId
    @JsonIgnore override def getLupatilaId = super.getLupatilaId
    @JsonIgnore override def getLuoja = super.getLuoja
    @JsonIgnore override def getLuontipvm = super.getLuontipvm
    @JsonIgnore override def getPaivittaja = super.getPaivittaja
    @JsonIgnore override def getPaivityspvm = super.getPaivityspvm

    def getPaatoskierros = paatoskierros
    def setPaatoskierros(paatoskierros: Paatoskierros): Unit = this.paatoskierros = paatoskierros

    def getAsiatyyppi = asiatyyppi
    def setAsiatyyppi(asiatyyppi: Asiatyyppi): Unit = this.asiatyyppi = asiatyyppi

    def getLupatila = lupatila
    def setLupatila(lupatila: Lupatila): Unit = this.lupatila = lupatila

    def getJarjestaja = jarjestaja
    def setJarjestaja(jarjestaja: Organisaatio): Unit = this.jarjestaja = jarjestaja
    @JsonIgnore def getJarjestajaOpt: java.util.Optional[Organisaatio] = Optional.ofNullable(jarjestaja)

    def getMaaraykset = maaraykset
    def setMaaraykset(maaraykset: Collection[Maarays]): Unit = this.maaraykset = maaraykset

    @JsonIgnore def getUUIDValue = getUuid.toString
    @JsonIgnore def getPDFFileName = "lupa-" + RegExUtils.replaceAll(Option(getAsianumero).getOrElse(getDiaarinumero), "/", "-") + ".pdf"
}