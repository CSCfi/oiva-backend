package fi.minedu.oiva.backend.core.config;

import org.apache.tika.parser.AutoDetectParser;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AutoDetectParserConfig {

    @Bean
    public AutoDetectParser autoDetectParser() {
        return new AutoDetectParser();
    }
}
