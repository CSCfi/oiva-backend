package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.config.FileStorageConfig;
import fi.minedu.oiva.backend.entity.Lupa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Autowired
    private FileStorageConfig fileStorage;

    public Optional<File> createLupaFile(final Lupa lupa) {
        if(null != lupa) {
            final String filePath = getLupaFilePath(lupa).get();
            final File file = new File(filePath);
            if(file.getParentFile().mkdirs()) {
                return Optional.ofNullable(file);
            } else logger.error("Failed to create file path: " + filePath);
        } return Optional.empty();
    }

    public Optional<String> getLupaFilePath(final Lupa lupa) {
        return null != lupa ? Optional.ofNullable(fileStorage.getFileStorageBasePath() + "/" + lupa.getFilePath() + "/" + lupa.getFileName()) : Optional.empty();
    }
}