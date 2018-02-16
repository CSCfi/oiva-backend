package fi.minedu.oiva.backend.entity

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Muutospyynto(
                    var muutospyynto: Muutospyynto) extends fi.minedu.oiva.backend.jooq.tables.pojos.Muutospyynto  {

  def this() = this(null)

  // exclude from json
  @JsonIgnore override def getId = super.getId

}
