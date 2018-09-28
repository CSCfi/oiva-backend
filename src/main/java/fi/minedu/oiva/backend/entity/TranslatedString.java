package fi.minedu.oiva.backend.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.minedu.oiva.backend.entity.json.TranslatedStringDeserializer;
import fi.minedu.oiva.backend.entity.json.TranslatedStringSerializer;
import fi.minedu.oiva.backend.entity.opintopolku.Koodisto;
import fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi;
import fi.minedu.oiva.backend.entity.json.ObjectMapperSingleton;
import fi.minedu.oiva.backend.entity.opintopolku.Metadata;
import fi.minedu.oiva.backend.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.jooq.lambda.Seq.seq;
import static org.jooq.lambda.tuple.Tuple.tuple;

@JsonDeserialize(using = TranslatedStringDeserializer.class)
@JsonSerialize(using = TranslatedStringSerializer.class)
public class TranslatedString implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(TranslatedString.class);
    private Map<String, String> values;

    private TranslatedString(final Map<String, String> values) {
        this.values = values;
    }

    public Optional<String> get(final String lang) {
        return Optional.ofNullable(values.get(lang));
    }

    public Optional<String> getOrFirst(final String lang) {
        return this.values.containsKey(lang) ? get(lang) : getFirst();
    }

    public Optional<String> getFirst() {
        return (values.isEmpty()) ? Optional.empty() : values.entrySet().stream().map(Map.Entry::getValue).findFirst();
    }

    public Stream<Map.Entry<String, String>> stream() {
        return values.entrySet().stream();
    }

    public static TranslatedString of(final JsonNode node, final String fieldName) {
        final String targetFieldName = fieldName + "_";
        final Map<String, String> content = new HashMap<>();
        final Iterator<Map.Entry<String,JsonNode>> iterator = node.fields();
        while(iterator.hasNext()) {
            final Map.Entry<String, JsonNode> entry = iterator.next();
            if(StringUtils.startsWith(entry.getKey(), targetFieldName)) {
                final String lang = StringUtils.removeStart(entry.getKey(), targetFieldName);
                content.put(lang, entry.getValue().asText(""));
            }
        }
        return new TranslatedString(content);
    }

    @SuppressWarnings("unchecked")
    public static TranslatedString of(final JsonNode node) {
        try {
            return new TranslatedString(ObjectMapperSingleton.mapper.treeToValue(node, Map.class));
        } catch (IOException ioe) {
            logger.error("Failed to create TranslatedString", ioe);
            return null;
        }
    }

    public static TranslatedString empty() {
        return new TranslatedString(new HashMap<>());
    }

    public static TranslatedString of(final Map<String, String> map) {
        return new TranslatedString(map);
    }

    public static TranslatedString of(final Seq<Tuple2<String, String>> seqOfTuples) {
        return new TranslatedString(seqOfTuples.collect(CollectionUtils.tuples2Map()));
    }

    public static TranslatedString ofNimi(final Koodisto koodisto) {
        return ofNimi(koodisto.getMetadataList());
    }

    public static TranslatedString ofKuvaus(final Koodisto koodisto) {
        return ofKuvaus(koodisto.getMetadataList());
    }

    public static TranslatedString ofNimi(final KoodistoKoodi koodi) {
        return ofNimi(koodi.getMetadataList());
    }

    public static TranslatedString ofKuvaus(final KoodistoKoodi koodi) {
        return ofKuvaus(koodi.getMetadataList());
    }

    protected static TranslatedString ofNimi(final List<Metadata> metadata) {
        return of(seq(metadata).map(m -> tuple(m.kieli().toLowerCase(), m.nimi())));
    }

    protected static TranslatedString ofKuvaus(final List<Metadata> metadata) {
        return of(seq(metadata).map(m -> tuple(m.kieli().toLowerCase(), null != m.kuvaus() ? m.kuvaus() : "")));
    }

    public JsonNode toJson() {
        return ObjectMapperSingleton.mapper.valueToTree(this.values);
    }

    public boolean hasContent() {
        if(null != this.values) {
            for(Map.Entry<String, String> e : this.values.entrySet()) {
                if(StringUtils.isNotBlank(e.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }
}
