package fi.minedu.oiva.backend.model.entity.oiva

import java.lang
import java.sql.Timestamp

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.model.jooq.tables._

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Asiatilamuutos(
                      var alkutila: String,
                      var lopputila: String,
                      var muutosaika: Timestamp,
                      var kayttajatunnus: String) extends pojos.Asiatilamuutos {

  def this() = this(null, null, null, null)

  def this(alkutila: String, lopputila: String, kayttajatunnus: String) {
    this(alkutila, lopputila, new Timestamp(System.currentTimeMillis()), kayttajatunnus)
  }

  // exclude from json
  @JsonIgnore override def getId: lang.Long = super.getId
}
