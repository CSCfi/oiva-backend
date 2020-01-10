package fi.minedu.oiva.backend.model.jooq;

import com.fasterxml.jackson.databind.JsonNode;
import fi.minedu.oiva.backend.model.entity.TranslatedString;
import org.jooq.Converter;
import org.jooq.Converters;

public class TranslatedStringBinding implements DefaultJsonBindings<TranslatedString> {

    @Override
    @SuppressWarnings("unchecked")
    public Converter<Object, TranslatedString> converter() {
        return Converters.of(
            new PostgresJSONJacksonBinding.JsonNodeConverter(),
            new TranslatedStringConverter());
    }

    public static class TranslatedStringConverter implements Converter<JsonNode, TranslatedString> {

        @Override
        public TranslatedString from(final JsonNode databaseObject) {
            return TranslatedString.of(databaseObject);
        }

        @Override
        public JsonNode to(final TranslatedString userObject) {
            return userObject.toJson();
        }

        @Override
        public Class<JsonNode> fromType() {
            return JsonNode.class;
        }

        @Override
        public Class<TranslatedString> toType() {
            return TranslatedString.class;
        }
    }
}
