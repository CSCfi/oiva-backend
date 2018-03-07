package fi.minedu.oiva.backend.entity

import java.util.{Collection, Optional}

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.entity.opintopolku.Organisaatio

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Muutospyynto(
                    var diaarinumero: String,
                    var jarjestaja_oid: String,
                    var muutosperustelu: Muutosperustelu,
                    var muutokset: Collection[Muutos],
                    var paatoskierros: Paatoskierros,
                    var jarjestaja: Organisaatio,
                    var muutospyynto: Muutospyynto) extends fi.minedu.oiva.backend.jooq.tables.pojos.Muutospyynto  {

  def this() = this(null,null,null,null,null,null,null)

  def getJarjestajaOid = jarjestaja_oid
  def setJarjestajaOid(jarjestajaOid: String): Unit = this.jarjestaja_oid = jarjestajaOid

  def getDiaarinumero = diaarinumero
  def setDiaarinumero(diaarinumero: String): Unit = this.diaarinumero = diaarinumero

  def getMuutosperustelu = muutosperustelu
  def setMuutosperustelu(muutosperustelu: Muutosperustelu): Unit = this.muutosperustelu = muutosperustelu

  def getMuutokset = muutokset
  def setMuutokset(muutokset: Collection[Muutos]): Unit = this.muutokset = muutokset

  def getPaatoskierros = paatoskierros
  def setPaatoskierros(paatoskierros: Paatoskierros): Unit = this.paatoskierros = paatoskierros

  def getJarjestaja = jarjestaja
  def setJarjestaja(jarjestaja: Organisaatio): Unit = this.jarjestaja = jarjestaja
  @JsonIgnore def getJarjestajaOpt: java.util.Optional[Organisaatio] = Optional.ofNullable(jarjestaja)

}
