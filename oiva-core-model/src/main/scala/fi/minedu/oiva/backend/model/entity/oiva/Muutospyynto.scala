package fi.minedu.oiva.backend.model.entity.oiva

import java.{lang, util}
import java.util.{Collection, Optional}

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.model.entity.opintopolku.Organisaatio
import fi.minedu.oiva.backend.model.jooq.tables._
import org.apache.commons.lang3.StringUtils

import scala.beans.BeanProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Muutospyynto(
                    var diaarinumero: String,
                    var jarjestaja_oid: String,
                    var lupa_uuid: String,
                    var muutokset: util.Collection[Muutos],
                    var liitteet: util.Collection[Liite],
                    var paatoskierros: Paatoskierros,
                    var jarjestaja: Organisaatio,
                    var muutospyynto: Muutospyynto,
                    @BeanProperty
                    var tilamuutokset: util.Collection[Tilamuutos]) extends pojos.Muutospyynto  {

  def this() = this(null,null,null,null,null,null,null,null,null)

  // exclude from json
  @JsonIgnore override def getId: lang.Long = super.getId

  def getJarjestajaOid: String = jarjestaja_oid
  def setJarjestajaOid(jarjestajaOid: String): Unit = this.jarjestaja_oid = jarjestajaOid

  def getLupaUuid: String = lupa_uuid
  def setLupaUuid(lupaUuid: String): Unit = this.lupa_uuid = lupaUuid

  def getDiaarinumero: String = diaarinumero
  def setDiaarinumero(diaarinumero: String): Unit = this.diaarinumero = diaarinumero

  def getMuutokset: util.Collection[Muutos] = muutokset
  def setMuutokset(muutokset: util.Collection[Muutos]): Unit = this.muutokset = muutokset

  def getLiitteet: util.Collection[Liite] = liitteet
  def setLiitteet(liitteet: util.Collection[Liite]): Unit = this.liitteet = liitteet

  def getPaatoskierros: Paatoskierros = paatoskierros
  def setPaatoskierros(paatoskierros: Paatoskierros): Unit = this.paatoskierros = paatoskierros

  def getJarjestaja: Organisaatio = jarjestaja
  def setJarjestaja(jarjestaja: Organisaatio): Unit = this.jarjestaja = jarjestaja
  @JsonIgnore def getJarjestajaOpt: java.util.Optional[Organisaatio] = Optional.ofNullable(jarjestaja)
  @JsonIgnore def getPDFFileName: String = "muutospyynto-" + StringUtils.replaceAll(getDiaarinumero, "/", "-") + ".pdf"
}
