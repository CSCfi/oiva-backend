package fi.minedu.oiva.backend.model.jooq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.Converter;
import org.jooq.JSONB;

import java.io.IOException;

public class PostgresJSONJacksonBinding implements DefaultJsonBindings<JsonNode> {

    @Override
    public Converter<JSONB, JsonNode> converter() {
        return new JsonNodeConverter();
    }

    public static class JsonNodeConverter implements Converter<JSONB, JsonNode> {
        private static final ObjectMapper mapper = new ObjectMapper();

        @Override
        public JsonNode from(final JSONB source) {
            try {
                return source == null ? mapper.createObjectNode() : mapper.readTree(source.toString());
            } catch (IOException e) {
                return mapper.createObjectNode();
            }
        }

        @Override
        public JSONB to(final JsonNode source) {
            try {
                return source == null || source.isNull() ? null : JSONB.valueOf(mapper.writeValueAsString(source));
            } catch (JsonProcessingException e) {
                return null;
            }
        }

        @Override
        public Class<JSONB> fromType() {
            return JSONB.class;
        }

        @Override
        public Class<JsonNode> toType() {
            return JsonNode.class;
        }
    }
}