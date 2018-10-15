package fi.minedu.oiva.backend.entity.oiva

import java.util.{Collection, Optional}

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Muutospyynto(
    var diaarinumero: String,
    var jarjestaja_oid: String,
    var lupa_uuid: String,
    var muutokset: Collection[Muutos],
    var paatoskierros: Paatoskierros,
    var jarjestaja: Organisaatio,
    var muutospyynto: Muutospyynto) extends fi.minedu.oiva.backend.jooq.tables.pojos.Muutospyynto  {

  def this() = this(null,null,null,null,null,null,null)

  // exclude from json
  @JsonIgnore override def getId = super.getId

  def getJarjestajaOid = jarjestaja_oid
  def setJarjestajaOid(jarjestajaOid: String): Unit = this.jarjestaja_oid = jarjestajaOid

  def getLupaUuid = lupa_uuid
  def setLupaUuid(lupaUuid: String): Unit = this.lupa_uuid = lupaUuid

  def getDiaarinumero = diaarinumero
  def setDiaarinumero(diaarinumero: String): Unit = this.diaarinumero = diaarinumero

  def getMuutokset = muutokset
  def setMuutokset(muutokset: Collection[Muutos]): Unit = this.muutokset = muutokset

  def getPaatoskierros = paatoskierros
  def setPaatoskierros(paatoskierros: Paatoskierros): Unit = this.paatoskierros = paatoskierros

  def getJarjestaja = jarjestaja
  def setJarjestaja(jarjestaja: Organisaatio): Unit = this.jarjestaja = jarjestaja
  @JsonIgnore def getJarjestajaOpt: java.util.Optional[Organisaatio] = Optional.ofNullable(jarjestaja)

}
