package fi.minedu.oiva.backend.entity.oiva


import java.lang
import java.sql.Timestamp
import java.util.Optional

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.databind.node.ObjectNode
import fi.minedu.oiva.backend.util.ControllerUtil.buildURI


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Liite() extends fi.minedu.oiva.backend.jooq.tables.pojos.Liite  {

    // exclude from json
    @JsonIgnore override def getId: lang.Long = super.getId
    @JsonIgnore override def getLuoja: String = super.getLuoja
    @JsonIgnore override def getLuontipvm: Timestamp = super.getLuontipvm
    @JsonIgnore override def getPaivittaja: String = super.getPaivittaja
    @JsonIgnore override def getPaivityspvm: Timestamp = super.getPaivityspvm

    private var links: JsonNode = _
    /**
      * Unique name part for mapping multipart form files to Json data.
      */
    private var tiedostoId: String = ""
    private var removed: Boolean = false

    def getLinks: JsonNode = {
        links
    }

    def setLink(urlPrefix: String): Liite = {
        val mapper: ObjectMapper = new ObjectMapper
        val outerNode: ObjectNode = mapper.createObjectNode
        outerNode.put("rawFile", buildURI(urlPrefix).toString)
        links = outerNode
        this
    }

    def getTiedostoId: String = this.tiedostoId
    def setTiedostoId(tiedostoId: String): Unit = this.tiedostoId = tiedostoId
    def isRemoved: Boolean = this.removed
    def setRemoved(removed: Boolean): Unit = this.removed = removed

    @JsonIgnore def getMetadataOpt: Optional[JsonNode] = {
        Optional.ofNullable(getMeta)
    }


}
