package fi.minedu.oiva.backend.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStorageConfig {

    @Value("${file.storage.base.path}")
    private String fileStorageBasePath;

    public String getFileStorageBasePath() {
        return fileStorageBasePath;
    }

    public String getLuvatBasePath() {
        return getFileStorageBasePath() + "/luvat";
    }

    public String getHakemuksetBasePath() {
        return getFileStorageBasePath() + "/hakemukset";
    }

    public String getLiitteetBasePath() {
        return getFileStorageBasePath() + "/liitteet";
    }
}