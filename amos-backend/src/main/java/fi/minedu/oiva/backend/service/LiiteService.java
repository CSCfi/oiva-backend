package fi.minedu.oiva.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fi.minedu.oiva.backend.entity.oiva.Liite;
import fi.minedu.oiva.backend.jooq.tables.records.LiiteRecord;
import fi.minedu.oiva.backend.jooq.tables.records.LupaLiiteRecord;
import fi.minedu.oiva.backend.jooq.tables.pojos.LupaLiite;
import fi.minedu.oiva.backend.util.CollectionUtils;
import fi.minedu.oiva.backend.util.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fi.minedu.oiva.backend.jooq.Tables.LIITE;
import static fi.minedu.oiva.backend.jooq.Tables.LUPA_LIITE;
import static fi.minedu.oiva.backend.util.FileUtils.PathBuilder.pathify;
import static fi.minedu.oiva.backend.util.FileUtils.getFileOptFromPath;

@Service
public class LiiteService {
    private final Logger logger = LoggerFactory.getLogger(LiiteService.class);

    @Autowired
    DSLContext dsl;

    @Value("${attachments.upload.path}")
    private String attachmentsUploadPath;

    @Autowired
    AutoDetectParser parser;

    public Optional<Liite> get(long id) {
        return Optional.ofNullable(dsl.select(LIITE.fields())
                .from(LIITE)
                .where(LIITE.ID.eq(id))
                .fetchOneInto(Liite.class));
    }

    public Optional<File> getFileFrom(Liite a) {
        return getFileOptFromPath(getAbsolutePath(a));
    }

    public Optional<Metadata> extractMetadataFromInputStream(Optional<InputStream> inputStreamOpt) {
        return inputStreamOpt.map(inputStream -> {
                    BodyContentHandler handler = new BodyContentHandler(10000000);
                    Metadata metadata = new Metadata();
                    try {
                        parser.parse(inputStream, handler, metadata, new ParseContext());
                        return metadata;
                    } catch (IOException | SAXException | TikaException e) {
                        logger.error(e.getMessage());
                        return null;
                    }
                }
        );
    }

    public Resource convertToHttpResource(File f) {
        return new FileSystemResource(f);
    }

    /**
     * Diaarinumeros in filename should be in the form of 11-530-2011
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
        return getFileOptFromPath(pathify(attachmentsUploadPath, path));
    }

    public Optional<Liite> save(InputStream is, Liite liite) {
        String newPath = FileUtils.generateUniqueFilePath();
        liite.setPolku(newPath);
        Path pathToFile = Paths.get(getAbsolutePath(liite));

        try {
            FileUtils.makeDir(pathify(attachmentsUploadPath, liite.getPolku()));
            Files.copy(is, pathToFile);
            liite.setKoko(Files.size(pathToFile));
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            return Optional.<Liite>empty();
        }

        return createDbRecord(liite).map(newId -> {
            liite.setId(newId);
            return Optional.of(liite);
        }).orElseGet(() -> {
            //if db insertion fails, delete file and it's dirs too
            try {
                Files.delete(pathToFile);
                FileUtils.deleteDirs(attachmentsUploadPath, liite.getPolku());
            } catch (IOException e) {
                if (logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
            }
            return Optional.<Liite>empty();
        });
    }

    public Map<String, List<JsonNode>> processUpload(final MultipartFile[] files, final Liite tplLiite, final ObjectMapper mapper, final Long lupaId, final String type) {
        return Arrays.asList(files).stream()
                .map(f -> this.processUpload2Json(f, tplLiite, mapper, lupaId, type))
                .collect(Collectors.groupingBy(x -> (x.has("id")) ? "attachments" : "errors"));
    }

    public JsonNode processOneUpload(final MultipartFile file, final Liite tplLiite, final ObjectMapper mapper, final Long lupaId, final String type) {
        return this.processUpload2Json(file, tplLiite, mapper, lupaId, type);

    }

    private JsonNode processUpload2Json(final MultipartFile file, Liite tplLiite, ObjectMapper mapper, Long lupaId, String isPaatos) {
        return this.extractMetadataFromInputStream(getIs(file)).map(
                metadata -> {
                    try {
                        final Liite liite = new Liite();
                        BeanUtils.copyProperties(tplLiite, liite);
                        liite.setNimi(org.springframework.util.StringUtils.trimAllWhitespace(file.getOriginalFilename()));
                        Map<String, String> metadataMap = this.metadata2Map(metadata);
                        liite.setMeta(mapper.valueToTree(metadataMap));

                        return this
                                .saveLiite(file.getInputStream(), liite, lupaId)
                                .map(mapper::<JsonNode>valueToTree)
                                .orElse(error(liite.getNimi(), "File save failed"));
                    } catch (IOException e) {
                        if (logger.isDebugEnabled()) {
                            e.printStackTrace();
                        }
                        return error(file.getOriginalFilename(), e.getMessage());
                    }
                }
        ).orElse(error(file.getOriginalFilename(), "Metadata extraction failed"));
    }

    private Optional<Liite> saveLiite(InputStream is, Liite liite, Long lupaId) {
        String newPath = FileUtils.generateUniqueFilePath();
        liite.setPolku(newPath);
        Path pathToFile = Paths.get(getAbsolutePath(liite));

        try {
            FileUtils.makeDir(pathify(attachmentsUploadPath, liite.getPolku()));
            Files.copy(is, pathToFile);
            liite.setKoko(Files.size(pathToFile));
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            return Optional.<Liite>empty();
        }

        return createDbRecord(liite).map(newId -> {
            liite.setId(newId);

            // linkitetään hakemukseen tai päätökseen
            createLupaLinkDbRecord(newId, lupaId);


            return Optional.of(liite);
        }).orElseGet(() -> {
            //if db insertion fails, delete file and it's dirs too
            try {
                Files.delete(pathToFile);
                FileUtils.deleteDirs(attachmentsUploadPath, liite.getPolku());
            } catch (IOException e) {
                if (logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
            }
            return Optional.<Liite>empty();
        });
    }

    private Optional<Long> createDbRecord(Liite liite) {
        LiiteRecord rcrd = dsl.newRecord(LIITE, liite);
        rcrd.store();
        return Optional.ofNullable(rcrd.getId());
    }

    private Optional<Long> createLupaLinkDbRecord(Long id, Long lupaId) {
        LupaLiite lupaLiite = new LupaLiite();
        lupaLiite.setLiiteId(id);
        lupaLiite.setLupaId(lupaId);
        LupaLiiteRecord rcrd = dsl.newRecord(LUPA_LIITE, lupaLiite);
        rcrd.store();
        return Optional.ofNullable(rcrd.getId());
    }

    private Optional<InputStream> getIs(MultipartFile mpFile) {
        try {
            return Optional.of(mpFile.getInputStream());

        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
                logger.debug(e.getMessage());
            }
            return Optional.empty();
        }
    }

    private String getAbsolutePath(Liite a) {
        return pathify(attachmentsUploadPath, a.getPolku(), a.getNimi());
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
