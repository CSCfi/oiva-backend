package fi.minedu.oiva.backend.entity

import java.util.Collection
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Lupa(
    var paatoskierros: Paatoskierros,
    var asiatyyppi: Asiatyyppi,
    var lupatila: Lupatila,
    var jarjestaja: Organisaatio,
    var maaraykset: Collection[Maarays]) extends fi.minedu.oiva.backend.jooq.tables.pojos.Lupa {

    def this() = this(null, null, null, null, null)
    @JsonIgnore override def getPaatoskierrosId = super.getPaatoskierrosId
    @JsonIgnore override def getAsiatyyppiId = super.getAsiatyyppiId
    @JsonIgnore override def getLupatilaId = super.getLupatilaId

    def getPaatoskierros = paatoskierros
    def setPaatoskierros(paatoskierros: Paatoskierros): Unit = this.paatoskierros = paatoskierros

    def getAsiatyyppi = asiatyyppi
    def setAsiatyyppi(asiatyyppi: Asiatyyppi): Unit = this.asiatyyppi = asiatyyppi

    def getLupatila = lupatila
    def setLupatila(lupatila: Lupatila): Unit = this.lupatila = lupatila

    def getJarjestaja = jarjestaja
    def setJarjestaja(jarjestaja: Organisaatio): Unit = this.jarjestaja = jarjestaja

    def getMaaraykset = maaraykset
    def setMaaraykset(maaraykset: Collection[Maarays]): Unit = this.maaraykset = maaraykset
}