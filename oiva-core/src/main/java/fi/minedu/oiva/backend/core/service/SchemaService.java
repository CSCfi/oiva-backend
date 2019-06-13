package fi.minedu.oiva.backend.core.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.customProperties.HyperSchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.google.common.reflect.ClassPath;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SchemaService {

    private final Logger logger = LoggerFactory.getLogger(SchemaService.class);

    private static final String schemaBasePackagePath = "fi.minedu.oiva.backend.entity.";
    private static final String oivaEntityPackage = schemaBasePackagePath + "oiva";
    private static final String opintopolkuEntityPackage = schemaBasePackagePath + "opintopolku";
    private static final String exportEntityPackage = schemaBasePackagePath + "export";

    public List<String> getOivaSchemaClasses() {
        return getSchemaClassNames(oivaEntityPackage);
    }

    public Optional<JsonSchema> getOivaSchema(final String entityClass) {
        return getSchema(oivaEntityPackage, entityClass);
    }

    public List<String> getOpintopolkuSchemaClasses() {
        return getSchemaClassNames(opintopolkuEntityPackage);
    }

    public Optional<JsonSchema> getOpintopolkuSchema(final String entityClass) {
        return getSchema(opintopolkuEntityPackage, entityClass);
    }

    public List<String> getExportSchemaClasses() {
        return getSchemaClassNames(exportEntityPackage);
    }

    public Optional<JsonSchema> getExportSchema(final String entityClass) {
        return getSchema(exportEntityPackage, entityClass);
    }

    protected Optional<JsonSchema> getSchema(final String packageName, final String entityClass) {
        return getSchema(packageName + "." + entityClass);
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

    protected List<String> getSchemaClassNames(final String packageName) {
        try {
            return ClassPath.from(this.getClass().getClassLoader()).getTopLevelClasses(packageName).stream()
                .map(ClassPath.ClassInfo::getSimpleName)
                .filter(className -> !StringUtils.equals(className, "package"))
                .collect(Collectors.toList());
        } catch (IOException ioe) {
            logger.error("Failed to list schema classes", ioe);
            return Collections.emptyList();
        }
    }
}
