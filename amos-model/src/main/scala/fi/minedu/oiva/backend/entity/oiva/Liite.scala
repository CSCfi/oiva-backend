package fi.minedu.oiva.backend.entity.oiva


import java.io.File
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
    @JsonIgnore override def getId = super.getId
    @JsonIgnore override def getLuoja = super.getLuoja
    @JsonIgnore override def getLuontipvm = super.getLuontipvm
    @JsonIgnore override def getPaivittaja = super.getPaivittaja
    @JsonIgnore override def getPaivityspvm = super.getPaivityspvm

    private var links: JsonNode = null

    def getLinks: JsonNode = {
        return links
    }

    def setLink(urlPrefix: String): Liite = {
        val mapper: ObjectMapper = new ObjectMapper
        val outerNode: ObjectNode = mapper.createObjectNode
        outerNode.put("rawFile", buildURI(urlPrefix, getPolku.replace(File.separatorChar, '/') + getNimi).toString)
        links = outerNode
        return this
    }

    @JsonIgnore def getMetadataOpt: Optional[JsonNode] = {
        return Optional.ofNullable(getMeta)
    }


}
