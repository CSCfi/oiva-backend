package fi.minedu.oiva.backend.model.entity.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import fi.minedu.oiva.backend.model.entity.TranslatedString;

import java.io.IOException;

public class TranslatedStringSerializer extends JsonSerializer<TranslatedString> {

    @Override
    public void serialize(final TranslatedString value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        value.stream().forEach(e -> {
            try {
                jgen.writeStringField(e.getKey(), e.getValue());
            } catch (IOException ignore) {}
        });
        jgen.writeEndObject();
    }

    @Override
    public void serializeWithType(TranslatedString value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        typeSer.writeTypePrefixForObject(value, gen);
        value.stream().forEach(e -> {
            try {
                gen.writeStringField(e.getKey(), e.getValue());
            } catch (IOException ignore) {}
        });
        typeSer.writeTypeSuffixForObject(value, gen);
    }
}
