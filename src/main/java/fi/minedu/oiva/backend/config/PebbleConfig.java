package fi.minedu.oiva.backend.config;

import fi.minedu.oiva.backend.template.extension.OivaTemplateExtension;
import fi.minedu.oiva.backend.template.extension.OivaTemplateLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mitchellbosecke.pebble.PebbleEngine;

@Configuration
public class PebbleConfig {

    @Value("${templates.base.path}")
    private String templateBasePath;

    @Bean
    public PebbleEngine defaultEngine() {
        final OivaTemplateLoader oivaLoader = new OivaTemplateLoader(this.templateBasePath);
        final OivaTemplateExtension oivaExtension = new OivaTemplateExtension();

        return new PebbleEngine.Builder()
            .loader(oivaLoader)
            .extension(oivaExtension)
            .autoEscaping(false)
            .cacheActive(false)
            .build();
    }

    public String getTemplateBasePath() {
        return templateBasePath;
    }
}