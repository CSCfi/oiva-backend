package fi.minedu.oiva.backend.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fi.minedu.oiva.backend.core.config.FileStorageConfig;
import fi.minedu.oiva.backend.model.entity.oiva.Liite;
import fi.minedu.oiva.backend.model.jooq.tables.pojos.LupaLiite;
import fi.minedu.oiva.backend.model.jooq.tables.records.LiiteRecord;
import fi.minedu.oiva.backend.model.jooq.tables.records.LupaLiiteRecord;
import fi.minedu.oiva.backend.model.util.CollectionUtils;
import fi.minedu.oiva.backend.core.util.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fi.minedu.oiva.backend.model.jooq.Tables.LIITE;
import static fi.minedu.oiva.backend.model.jooq.Tables.LUPA_LIITE;
import static fi.minedu.oiva.backend.core.util.FileUtils.PathBuilder.pathify;
import static fi.minedu.oiva.backend.core.util.FileUtils.getFileOptFromPath;

@Service
public class LiiteService {
    private final Logger logger = LoggerFactory.getLogger(LiiteService.class);

    private FileStorageConfig fileStorageConfig;
    private DSLContext dsl;
    private AutoDetectParser parser;
    private AuthService authService;

    @Autowired
    public LiiteService(FileStorageConfig fileStorageConfig, DSLContext dsl, AutoDetectParser parser,
                        AuthService authService) {
        this.fileStorageConfig = fileStorageConfig;
        this.dsl = dsl;
        this.parser = parser;
        this.authService = authService;
    }

    public Optional<Liite> get(Long id) {
        return Optional.ofNullable(dsl.select(LIITE.fields())
                .from(LIITE)
                .where(LIITE.ID.eq(id))
                .fetchOneInto(Liite.class));
    }

    public Optional<Liite> getByUuid(UUID uuid) {
        return Optional.ofNullable(dsl.select(LIITE.fields())
                .from(LIITE)
                .where(LIITE.UUID.eq(uuid))
                .fetchOneInto(Liite.class));
    }

    public Optional<File> getFileFrom(Liite a) {
        return getFileOptFromPath(getAbsolutePath(a));
    }

    public Resource convertToHttpResource(File f) {
        return new FileSystemResource(f);
    }

    /**
     * Diaarinumeros in filename should be in the form of 11-530-2011
     *
     * @param fileName Some file with diaari in it's name
     * @return Optinal diaarinumero
     */
    public Optional<String> getDiaariFromFilename(String fileName) {
        Matcher matches = Pattern.compile("(?<diaari>[0-9]+-[0-9]+-[0-9]+)-(.*)").matcher(fileName);
        if (matches.find()) {
            String[] inversedDiaariParts = matches.group("diaari").split("-");
            ArrayUtils.reverse(inversedDiaariParts);
            inversedDiaariParts[0] = StringUtils.stripStart(inversedDiaariParts[0], "0");
            return Optional.of(CollectionUtils.mkString(inversedDiaariParts, "-"));
        } else {
            return Optional.empty();
        }
    }

    public Optional<File> getFileFor(String path) {
        return getFileOptFromPath(pathify(fileStorageConfig.getLiitteetBasePath(), path));
    }

    public void update(Liite liite) {
        getByUuid(liite.getUuid()).ifPresent(l -> {
            liite.setId(l.getId());
            liite.setPaivittaja(authService.getUsername());
            liite.setPaivityspvm(Timestamp.from(Instant.now()));
            final LiiteRecord liiteRecord = dsl.newRecord(LIITE, liite);
            dsl.executeUpdate(liiteRecord);
        });
    }

    public Optional<Liite> save(MultipartFile file, Liite liite) {
        Optional<Path> pathToFile = createFile(file, liite);
        return pathToFile.map(path -> createDbRecord(liite)
                .map(newId -> {
                    liite.setId(newId);
                    return liite;
                }).orElseGet(() -> {
                    //if db insertion fails, delete file and it's dirs too
                    return deleteFile(liite, path).orElse(null);
                }));
    }

    public Map<String, List<JsonNode>> processUpload(final MultipartFile[] files, final Liite tplLiite,
                                                     final ObjectMapper mapper, final Long lupaId, final String type) {
        return Arrays.stream(files)
                .map(f -> processUpload2Json(f, tplLiite, mapper, lupaId, type))
                .collect(Collectors.groupingBy(x -> (x.has("id")) ? "attachments" : "errors"));
    }

    public JsonNode processOneUpload(final MultipartFile file, final Liite tplLiite, final ObjectMapper mapper,
                                     final Long lupaId, final String type) {
        return processUpload2Json(file, tplLiite, mapper, lupaId, type);

    }

    public void delete(Liite liite) {
        getByUuid(liite.getUuid()).ifPresent(l -> {
            dsl.delete(LIITE).where(LIITE.UUID.eq(liite.getUuid())).execute();
            getFileFrom(l).ifPresent(file -> {
                try {
                    Files.deleteIfExists(file.toPath());
                    Files.deleteIfExists(file.getParentFile().toPath());
                } catch (IOException e) {
                    logger.error("Could not delete file", e);
                }
            });
        });
    }

    private Optional<Metadata> extractMetadataFromInputStream(InputStream inputStream) {
        return Optional.ofNullable(inputStream).map(in -> {
                    BodyContentHandler handler = new BodyContentHandler(10000000);
                    Metadata metadata = new Metadata();
                    try {
                        parser.parse(in, handler, metadata, new ParseContext());
                        return metadata;
                    } catch (IOException | SAXException | TikaException e) {
                        logger.error(e.getMessage());
                        return null;
                    }
                }
        );
    }

    private Optional<Path> createFile(MultipartFile file, Liite liite) {
        String newPath = FileUtils.generateUniqueFilePath();
        liite.setPolku(pathify(newPath, file.getOriginalFilename()));
        Path pathToFile = Paths.get(getAbsolutePath(liite));
        try {
            FileUtils.makeDir(pathify(fileStorageConfig.getLiitteetBasePath(), newPath));
            Files.copy(file.getInputStream(), pathToFile);
            liite.setKoko(Files.size(pathToFile));
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            return Optional.empty();
        }
        return Optional.of(pathToFile);
    }

    private JsonNode processUpload2Json(final MultipartFile file, Liite tplLiite, ObjectMapper mapper,
                                        Long lupaId, String isPaatos) {
        final Optional<InputStream> is = getIs(file);
        if (!is.isPresent()) {
            return error(file.getOriginalFilename(), "Could not read InputStream");
        }
        return (JsonNode) extractMetadataFromInputStream(is.get()).map(
                metadata -> {
                    final Liite liite = new Liite();
                    BeanUtils.copyProperties(tplLiite, liite);
                    liite.setNimi(org.springframework.util.StringUtils.trimAllWhitespace(file.getOriginalFilename()));
                    Map<String, String> metadataMap = metadata2Map(metadata);
                    liite.setMeta(mapper.valueToTree(metadataMap));

                    return saveLiite(file, liite, lupaId)
                            .map(mapper::valueToTree)
                            .orElse(error(liite.getNimi(), "File save failed"));

                }
        ).orElse(error(file.getOriginalFilename(), "Metadata extraction failed"));
    }

    private Optional<Liite> saveLiite(MultipartFile file, Liite liite, Long lupaId) {
        final Optional<Path> pathToFile = createFile(file, liite);
        if (!pathToFile.isPresent()) {
            return Optional.empty();
        }
        return createDbRecord(liite).map(newId -> {
            liite.setId(newId);
            // linkitetään hakemukseen tai päätökseen
            createLupaLinkDbRecord(newId, lupaId);
            return Optional.of(liite);
        }).orElseGet(() -> deleteFile(liite, pathToFile.get()));
    }

    private Optional<Liite> deleteFile(Liite liite, Path pathToFile) {
        //if db insertion fails, delete file and it's dirs too
        try {
            Files.delete(pathToFile);
            FileUtils.deleteDirs(fileStorageConfig.getLiitteetBasePath(), liite.getPolku());
        } catch (IOException e) {
            logger.error("Could not delete file!", e);
        }
        return Optional.empty();
    }

    private Optional<Long> createDbRecord(Liite liite) {
        liite.setLuoja(authService.getUsername());
        liite.setLuontipvm(Timestamp.from(Instant.now()));
        LiiteRecord rcrd = dsl.newRecord(LIITE, liite);
        rcrd.store();
        return Optional.ofNullable(rcrd.getId());
    }

    private void createLupaLinkDbRecord(Long id, Long lupaId) {
        LupaLiite lupaLiite = new LupaLiite();
        lupaLiite.setLiiteId(id);
        lupaLiite.setLupaId(lupaId);
        LupaLiiteRecord rcrd = dsl.newRecord(LUPA_LIITE, lupaLiite);
        rcrd.store();
        rcrd.getId();
    }

    private Optional<InputStream> getIs(MultipartFile mpFile) {
        try {
            return Optional.of(mpFile.getInputStream());
        } catch (IOException e) {
            logger.error("Could not get InputStream!", e);
            return Optional.empty();
        }
    }

    private String getAbsolutePath(Liite a) {
        return pathify(fileStorageConfig.getLiitteetBasePath(), a.getPolku());
    }

    private JsonNode error(String fileName, String msg) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("fileName", fileName);
        node.put("title", msg);
        return node;
    }

    private Map<String, String> metadata2Map(Metadata metadata) {
        Map<String, String> map = new HashMap<>();
        metadata.remove("X-Parsed-By"); //remove unnecessary value
        String[] metadataNames = metadata.names();
        for (String name : metadataNames) {
            map.put(name, metadata.get(name));
        }
        return map;
    }
}
