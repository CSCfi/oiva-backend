package fi.minedu.oiva.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStorageConfig {

    @Value("${file.storage.base.path}")
    private String fileStorageBasePath;

    public String getFileStorageBasePath() {
        return fileStorageBasePath;
    }
}