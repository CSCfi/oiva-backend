package fi.minedu.oiva.backend.jooq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.Converter;

import java.io.IOException;

public class PostgresJSONJacksonBinding implements DefaultJsonBindings<JsonNode> {

    @Override
    public Converter<Object, JsonNode> converter() {
        return new JsonNodeConverter();
    }

    public static class JsonNodeConverter implements Converter<Object, JsonNode> {
        private static final ObjectMapper mapper = new ObjectMapper();

        @Override
        public JsonNode from(final Object source) {
            try {
                return source == null ? mapper.createObjectNode() : mapper.readTree((String) source);
            } catch (IOException e) {
                return mapper.createObjectNode();
            }
        }

        @Override
        public Object to(final JsonNode source) {
            try {
                return source == null || source.isNull() ? null : mapper.writeValueAsString(source);
            } catch (JsonProcessingException e) {
                return null;
            }
        }

        @Override
        public Class<Object> fromType() {
            return Object.class;
        }

        @Override
        public Class<JsonNode> toType() {
            return JsonNode.class;
        }
    }
}