package fi.minedu.oiva.backend.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.minedu.oiva.backend.config.OivaMediaTypes;
import fi.minedu.oiva.backend.entity.oiva.Liite;
import fi.minedu.oiva.backend.service.LiiteService;
import fi.minedu.oiva.backend.service.LupaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.util.Arrays;

import static fi.minedu.oiva.backend.util.ControllerUtil.*;

@RestController
@RequestMapping(value = "${api.url.prefix}" + LiiteController.path, produces = {
        MediaType.APPLICATION_JSON_VALUE, OivaMediaTypes.APPLICATION_VND_JSON_VALUE, MediaType.ALL_VALUE
}, consumes = MediaType.ALL_VALUE)
public class LiiteController {
    public static final String path = "/liitteet";
    public static final String rawPath = "/raw";

    private static final ObjectMapper mapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(LiiteController.class);

    @Value("${api.url.prefix}")
    private String apiUrlPrefix;

    @Autowired
    LiiteService attachmentService;

    @Autowired
    LupaService lupaService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void index() {
    }

    @RequestMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, OivaMediaTypes.APPLICATION_VND_JSON_VALUE})
    public HttpEntity<Liite> get(@PathVariable long id) {
        return getOr404_(attachmentService.get(id), item -> item.setLink(apiUrlPrefix + path + rawPath));
    }

    @RequestMapping(value = "/{id}" + rawPath)
    public HttpEntity<Resource> getRawFile(@PathVariable long id) {
        return getOr404(attachmentService.get(id),
                item -> attachmentService.getFileFrom(item)
                        .map(f -> {
                            final HttpHeaders headers = new HttpHeaders();
                            headers.setContentType(MediaType.valueOf(getContentTypeFromAttachment(item)));
                            return new HttpEntity<>(attachmentService.convertToHttpResource(f), headers);
                        })
                        .orElse(notFound()));
    }

    @RequestMapping(value = rawPath + "/{path:.+}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public HttpEntity<Resource> getRawFile(@PathVariable String path) {
        return attachmentService.getFileFor(path)
                .map(f -> new HttpEntity<>(attachmentService.convertToHttpResource(f)))
                .orElse(notFound());
    }

    /**
     * Maps MultipartFile array to collection of JsonNodes while saving the files
     * <p>
     * Successful uploads are returned in "attachments" field, errors are gathered to "errors" field.
     * Those fields do not exist in return JSON if there's no corresponding attachment or error values.
     * </p>
     *
     * Return format:
     * <code>
     *  {
     *    "attachments": [
     *      ...array of {@link Liite} JSON values...
     *    ],
     *    "errors": [
     *      {
     *        "fieldName": "erroneousfile.txt",
     *        "title": "Metadata extraction failed"
     *      }
     *    ]
     *  }
     * </code>
     *
     * @param files
     * @return
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, OivaMediaTypes.APPLICATION_VND_JSON_VALUE})
    public ResponseEntity<?> post(@RequestPart(value = "file", required = true) MultipartFile[] files) throws IOException {

        System.out.println("Testasdsada: " + files.toString());

        Arrays.asList(files).stream()
                .map(f -> {System.out.println("name" + f.getName()); System.out.println("size: " + f.getSize()); return false; } );

        /*
        final String attachmentAsString = "{\"nimi\": \"testi\", \"luoja\": \"jari\"}";
        final Map<String, List<JsonNode>> successesAndErrors = attachmentService.processUpload(
                files, mapper.readValue(attachmentAsString.getBytes(), Liite.class),
                mapper, Long.parseLong("1"), "PAATOSLIITE");

        return new ResponseEntity<>(successesAndErrors,
                (successesAndErrors.containsKey("attachments")) ? HttpStatus.CREATED : HttpStatus.INTERNAL_SERVER_ERROR);
                */

        return new ResponseEntity<>(HttpStatus.CREATED);

    }


    @RequestMapping(value = "/upload-files", headers = "content-type=multipart/*", method = RequestMethod.POST)
    public ResponseEntity<?> upload(@RequestParam(value = "file0", required = true) MultipartFile multipartFile,
                                    @RequestParam(value = "filename0", required = true) String filename,
                                    @RequestParam(value = "category0", required = true) String category,
                                    Integer adminOid) throws IOException {
        //do something with the multipartFile

        System.out.println("name: " + multipartFile.getName());
        System.out.println("size: " + multipartFile.getSize());
        System.out.println("bin: " + multipartFile.getBytes().toString());
        System.out.println("filename: " + filename);
        System.out.println("category: " + category);


        final String attachmentAsString = "{\"nimi\": \"testi\", \"luoja\": \"jari\"}";
        final JsonNode successesAndErrors = attachmentService.processOneUpload(
                multipartFile, mapper.readValue(attachmentAsString.getBytes(), Liite.class),
                mapper, Long.parseLong("1"), "PAATOSLIITE");

        return new ResponseEntity<>(successesAndErrors,
                (successesAndErrors.asBoolean()) ? HttpStatus.CREATED : HttpStatus.INTERNAL_SERVER_ERROR);


    }

    /**
     * Get content type either from stored file metadata or 'guess' it
     * from the file extension.
     * @param item Attachment under scrutiny
     * @return content type as string
     */
    private String getContentTypeFromAttachment(Liite item) {
        return item.getMetadataOpt().map(md -> {
            JsonNode contentType = md.findValue("Content-Type");
            return contentType != null ? contentType.asText() : null;
        }).orElse(getFallback(item));
    }

    private String getFallback(Liite item) {
        return MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(item.getNimi());
    }

    public static String readFile(String filename) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
