package fi.minedu.oiva.backend.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.customProperties.HyperSchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JsonSchemaService {
    private static final String CLASS_PREFIX = "fi.minedu.oiva.backend.entity.";

    private final Logger logger = LoggerFactory.getLogger(JsonSchemaService.class);

    public Optional<JsonSchema> getSchema(String entityClass) {
        try {
            return getSchema(Class.forName(CLASS_PREFIX + entityClass));
        } catch (ClassNotFoundException e) {
            if (logger.isDebugEnabled()) e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<JsonSchema> getSchema(Class klass) {
        ObjectMapper m = new ObjectMapper();
        SchemaFactoryWrapper visitor = new HyperSchemaFactoryWrapper();
        try {
            m.acceptJsonFormatVisitor(m.constructType(klass), visitor);
            return Optional.ofNullable(visitor.finalSchema());
        } catch (JsonMappingException e) {
            if (logger.isDebugEnabled()) e.printStackTrace();
            return Optional.empty();
        }
    }
}
