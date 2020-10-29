package fi.minedu.oiva.backend.model.entity.oiva

import java.lang
import java.sql.Timestamp
import java.util.Optional

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.databind.node.ObjectNode
import fi.minedu.oiva.backend.model.jooq.tables._
import fi.minedu.oiva.backend.model.util.ControllerUtil.buildURI


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class Liite() extends pojos.Liite  {

    // exclude from json
    @JsonIgnore override def getId: lang.Long = super.getId
    @JsonIgnore override def getPaivittaja: String = super.getPaivittaja
    @JsonIgnore override def getPaivityspvm: Timestamp = super.getPaivityspvm

    private var links: JsonNode = _
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

    def isRemoved: Boolean = this.removed
    def setRemoved(removed: Boolean): Unit = this.removed = removed

    @JsonIgnore def getMetadataOpt: Optional[JsonNode] = {
        Optional.ofNullable(getMeta)
    }


}
