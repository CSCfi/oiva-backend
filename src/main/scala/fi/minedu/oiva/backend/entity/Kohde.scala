package fi.minedu.oiva.backend.entity

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonInclude}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Kohde() extends fi.minedu.oiva.backend.jooq.tables.pojos.Kohde  {
}
