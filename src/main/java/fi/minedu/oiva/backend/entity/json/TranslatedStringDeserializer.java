package fi.minedu.oiva.backend.entity.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import fi.minedu.oiva.backend.entity.TranslatedString;

import java.io.IOException;

public class TranslatedStringDeserializer extends JsonDeserializer<TranslatedString> {
    @Override
    public TranslatedString deserialize(JsonParser parser, DeserializationContext context)
        throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        return TranslatedString.of(node);
    }
}
