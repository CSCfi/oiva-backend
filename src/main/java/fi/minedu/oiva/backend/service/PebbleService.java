package fi.minedu.oiva.backend.service;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import fi.minedu.oiva.backend.config.PebbleConfig;
import fi.minedu.oiva.backend.entity.Lupa;
import fi.minedu.oiva.backend.entity.LupatilaValue;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static fi.minedu.oiva.backend.entity.OivaTemplates.*;

@Service
public class PebbleService {

    private static final Logger logger = LoggerFactory.getLogger(PebbleService.class);

    @Autowired
    private PebbleConfig pebble;

    @OivaAccess_Public
    public Optional<ByteArrayResource> getResource(final String path) {
        final String resourcePath = pebble.getTemplateBasePath() + "/" + path;
        try {
            return Optional.ofNullable(new ByteArrayResource(Files.readAllBytes(Paths.get(resourcePath))));
        } catch (IOException ioe) {
            logger.error("No such pebble resource with path: " + resourcePath, ioe);
            return Optional.empty();
        }
    }

    @OivaAccess_Public
    public Optional<String> toHTML(final Lupa lupa, final RenderOptions options) {

        // Tulostetaan lupatilan mukainen esitysmalli
        // HUOM! Toistaiseksi käytössä vain yksi malli, joka on paatos tilasta riippumatta

        final LupatilaValue lupaTila = lupa.lupatila().getTunniste();
        if(lupaTila == LupatilaValue.LUONNOS) options.setTemplateName("paatos/base");
        if(lupaTila == LupatilaValue.VALMIS) options.setTemplateName("paatos/base");
        if(lupaTila == LupatilaValue.PASSIVOITU) options.setTemplateName("paatos/base");
        if(lupaTila == LupatilaValue.HYLATTY) options.setTemplateName("paatos/base");

        return generateHtml(lupa, options);
    }

    private Optional<String> generateHtml(final Lupa lupa, final RenderOptions options) {

        final String contextPath = "paatoskierros2017"; // TODDO = lupa.paatoskierros().esitysmalli().getTemplatepath();
        final String templateName = "/" + StringUtils.removeStart(options.getTemplateName(), "/");

        logger.debug("Using base path: {}", pebble.getTemplateBasePath());
        logger.debug("Using context path: {}", contextPath);
        logger.debug("Using template: {}", templateName);

        final String templatePath = contextPath + templateName;
        final Map<String, Object> context = defaultContext(options, contextPath, templatePath);
        context.put("lupa", nonNullVersion(lupa));
        context.put("jarjestaja", lupa.jarjestaja());

        return writeHTML(templatePath, context);
    }

    protected Optional<String> writeHTML(final String templatePath, final Map<String, Object> context) {
        try {
            final PebbleEngine engine = pebble.defaultEngine();
            final PebbleTemplate pebbleTemplate = engine.getTemplate(templatePath);
            final Writer writer = new StringWriter();
            pebbleTemplate.evaluate(writer, context);
            return Optional.ofNullable(writer.toString());

        } catch (PebbleException | IOException e) {
            logger.error("Template error", e);
            return Optional.empty();
        }
    }

    private Map<String, Object> defaultContext(final RenderOptions options, final String contextPath, final String template) {
        final Map<String, Object> context = new HashMap<>();
        context.put("baseUri", options.isType(RenderOutput.web) ? "/api/pebble/resources" : pebble.getTemplateBasePath());
        context.put("contextPath", contextPath);
        context.put("defaultPath", "default");
        context.put("template", template);
        context.put("output", options.getOutput().name());
        context.put("language", options.getLanguage().name());
        context.put("state", options.getState().name());
        context.put("debug", options.isDebugMode());
        return context;
    }

    private Lupa nonNullVersion(final Lupa lupa) {
        if(null == lupa.getMeta()) lupa.setMeta(JsonNodeFactory.instance.nullNode());
        if(null == lupa.getMaaraykset()) lupa.setMaaraykset(Collections.emptyList());
        return lupa;
    }
}