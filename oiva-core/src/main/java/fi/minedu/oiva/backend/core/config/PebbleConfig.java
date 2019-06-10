package fi.minedu.oiva.backend.core.config;

import fi.minedu.oiva.backend.core.extension.OivaTemplateExtension;
import fi.minedu.oiva.backend.core.extension.OivaTemplateLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mitchellbosecke.pebble.PebbleEngine;

@Configuration
public abstract class PebbleConfig {

    @Value("${templates.base.path}")
    private String templateBasePath;

    public abstract OivaTemplateExtension getTemplateExtension();

    @Bean
    public PebbleEngine defaultEngine() {
        return new PebbleEngine.Builder()
            .loader(new OivaTemplateLoader(this.templateBasePath))
            .extension(getTemplateExtension())
            .autoEscaping(false)
            .cacheActive(false)
            .build();
    }

    public String getTemplateBasePath() {
        return templateBasePath;
    }
}