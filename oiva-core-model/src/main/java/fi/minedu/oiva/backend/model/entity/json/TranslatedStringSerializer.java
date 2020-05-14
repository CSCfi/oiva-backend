package fi.minedu.oiva.backend.model.entity.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import fi.minedu.oiva.backend.model.entity.TranslatedString;

import java.io.IOException;

public class TranslatedStringSerializer extends JsonSerializer<TranslatedString> {

    @Override
    public void serialize(final TranslatedString value, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException {
        jgen.writeStartObject();
        serialize(value, jgen);
        jgen.writeEndObject();
    }

    @Override
    public void serializeWithType(TranslatedString value, JsonGenerator gen, SerializerProvider provider,
                                  TypeSerializer typeSer) throws IOException {
        final WritableTypeId typeId = typeSer.writeTypePrefix(gen, typeSer.typeId(value, JsonToken.START_OBJECT));
        serialize(value, gen);
        typeSer.writeTypeSuffix(gen, typeId);
    }

    private void serialize(TranslatedString value, JsonGenerator jgen) {
        value.stream().forEach(e -> {
            try {
                jgen.writeStringField(e.getKey(), e.getValue());
            } catch (IOException ignore) {
            }
        });
    }
}
