package fi.minedu.oiva.backend.core.config;

import org.apache.tika.parser.AutoDetectParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TikaConfig {

    @Bean
    public AutoDetectParser getTikaAutoDetect() {
        return new AutoDetectParser();
    }
}
