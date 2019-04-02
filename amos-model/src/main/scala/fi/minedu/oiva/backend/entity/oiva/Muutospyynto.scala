package fi.minedu.oiva.backend.entity.oiva

import java.lang
import java.util.{Collection, Optional}

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio
import org.apache.commons.lang3.StringUtils

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Muutospyynto(
                    var diaarinumero: String,
                    var jarjestaja_oid: String,
                    var lupa_uuid: String,
                    var muutokset: Collection[Muutos],
                    var liitteet: Collection[Liite],
                    var paatoskierros: Paatoskierros,
                    var jarjestaja: Organisaatio,
                    var muutospyynto: Muutospyynto) extends fi.minedu.oiva.backend.jooq.tables.pojos.Muutospyynto  {

  def this() = this(null,null,null,null,null,null,null,null)

  // exclude from json
  @JsonIgnore override def getId: lang.Long = super.getId

  def getJarjestajaOid: String = jarjestaja_oid
  def setJarjestajaOid(jarjestajaOid: String): Unit = this.jarjestaja_oid = jarjestajaOid

  def getLupaUuid: String = lupa_uuid
  def setLupaUuid(lupaUuid: String): Unit = this.lupa_uuid = lupaUuid

  def getDiaarinumero: String = diaarinumero
  def setDiaarinumero(diaarinumero: String): Unit = this.diaarinumero = diaarinumero

  def getMuutokset: Collection[Muutos] = muutokset
  def setMuutokset(muutokset: Collection[Muutos]): Unit = this.muutokset = muutokset

  def getLiitteet: Collection[Liite] = liitteet
  def setLiitteet(liitteet: Collection[Liite]): Unit = this.liitteet = liitteet

  def getPaatoskierros: Paatoskierros = paatoskierros
  def setPaatoskierros(paatoskierros: Paatoskierros): Unit = this.paatoskierros = paatoskierros

  def getJarjestaja: Organisaatio = jarjestaja
  def setJarjestaja(jarjestaja: Organisaatio): Unit = this.jarjestaja = jarjestaja
  @JsonIgnore def getJarjestajaOpt: java.util.Optional[Organisaatio] = Optional.ofNullable(jarjestaja)
  @JsonIgnore def getPDFFileName = "muutospyynto-" + StringUtils.replaceAll(getDiaarinumero, "/", "-") + ".pdf"
}
