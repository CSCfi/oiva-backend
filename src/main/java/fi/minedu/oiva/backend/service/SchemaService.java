package fi.minedu.oiva.backend.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.customProperties.HyperSchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SchemaService {

    private final Logger logger = LoggerFactory.getLogger(SchemaService.class);

    private static final String CLASS_PREFIX = "fi.minedu.oiva.backend.entity.";
    private static final String OPINTOPOLKU_CLASS_PREFIX = CLASS_PREFIX + "opintopolku.";

    @OivaAccess_Public
    @Cacheable(value = {"SchemaService:getOivaSchema"})
    public Optional<JsonSchema> getOivaSchema(final String entityClass) {
        return getSchema(CLASS_PREFIX + entityClass);
    }

    @OivaAccess_Public
    @Cacheable(value = {"SchemaService:getOpintopolkuSchema"})
    public Optional<JsonSchema> getOpintopolkuSchema(final String entityClass) {
        return getSchema(OPINTOPOLKU_CLASS_PREFIX + entityClass);
    }

    protected Optional<JsonSchema> getSchema(final String entityClass) {
        try {
            return getSchema(Class.forName(entityClass));

        } catch (ClassNotFoundException cnfe) {
            if (logger.isDebugEnabled()) {
                logger.warn("Schema generation error", cnfe);
            };
            return Optional.empty();
        }
    }

    protected Optional<JsonSchema> getSchema(final Class clazz) {
        final ObjectMapper mapper = new ObjectMapper();
        final SchemaFactoryWrapper visitor = new HyperSchemaFactoryWrapper();
        try {
            mapper.acceptJsonFormatVisitor(mapper.constructType(clazz), visitor);
            return Optional.ofNullable(visitor.finalSchema());

        } catch (JsonMappingException jme) {
            if (logger.isDebugEnabled()) {
                logger.warn("Mapping error", jme);
            };
            return Optional.empty();
        }
    }
}
