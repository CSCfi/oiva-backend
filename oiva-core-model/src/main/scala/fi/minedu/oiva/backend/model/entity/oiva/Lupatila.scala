package fi.minedu.oiva.backend.model.entity.oiva

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import fi.minedu.oiva.backend.model.jooq.tables._

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Lupatila() extends pojos.Lupatila  {

    // exclude from json
    @JsonIgnore override def getId = super.getId
}
