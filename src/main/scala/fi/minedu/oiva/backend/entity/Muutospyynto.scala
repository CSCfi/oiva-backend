package fi.minedu.oiva.backend.entity

import java.util.Collection

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Muutospyynto(
                    var diaarinumero: String,
                    var muutosperustelu: Muutosperustelu,
                    var muutokset: Collection[Muutos],
                    var muutospyynto: Muutospyynto) extends fi.minedu.oiva.backend.jooq.tables.pojos.Muutospyynto  {

  def this() = this(null,null,null,null)

  // exclude from json
  @JsonIgnore override def getId = super.getId


  def getDiaarinumero = diaarinumero
  def setDiaarinumero(diaarinumero: String): Unit = this.diaarinumero = diaarinumero


  def getMuutosperustelu = muutosperustelu
  def setMuutosperustelu(muutosperustelu: Muutosperustelu): Unit = this.muutosperustelu = muutosperustelu

  def getMuutokset = muutokset
  def setMuutokset(muutokset: Collection[Muutos]): Unit = this.muutokset = muutokset

}
