package fi.minedu.oiva.backend.core.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import fi.minedu.oiva.backend.core.config.OivaMediaTypes;
import fi.minedu.oiva.backend.core.exception.ResourceNotFoundException;
import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Esittelija;
import fi.minedu.oiva.backend.model.entity.oiva.Liite;
import fi.minedu.oiva.backend.core.service.LiiteService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.activation.MimetypesFileTypeMap;

import java.util.Optional;
import java.util.UUID;

import static fi.minedu.oiva.backend.model.util.ControllerUtil.getOr404;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.getOr404_;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.notFound;

@RestController
@RequestMapping(value = "${api.url.prefix}" + LiiteController.path, produces = {
        MediaType.APPLICATION_JSON_VALUE, OivaMediaTypes.APPLICATION_VND_JSON_VALUE, MediaType.ALL_VALUE
}, consumes = MediaType.ALL_VALUE)
public class LiiteController {
    public static final String path = "/liitteet";
    private static final String rawPath = "/raw";

    private final Logger logger = LoggerFactory.getLogger(LiiteController.class);

    @Value("${api.url.prefix}")
    private String apiUrlPrefix;

    private LiiteService liiteService;

    @Autowired
    public LiiteController(LiiteService liiteService) {
        this.liiteService = liiteService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void index() {
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, OivaMediaTypes.APPLICATION_VND_JSON_VALUE})
    public HttpEntity<Liite> get(@PathVariable String uuid) {
        return getOr404_(liiteService.getByUuid(UUID.fromString(uuid)), item -> item.setLink(apiUrlPrefix + path + "/" + uuid + rawPath));
    }

    @RequestMapping(value = "/{uuid}" + rawPath,  method = RequestMethod.GET)
    public HttpEntity<Resource> getRawFile(@PathVariable String uuid) {
        return getOr404(liiteService.getByUuid(UUID.fromString(uuid)),
                item -> liiteService.getFileFrom(item)
                        .map(f -> {
                            final HttpHeaders headers = new HttpHeaders();
                            headers.setContentType(MediaType.valueOf(getContentTypeFromAttachment(item)));
                            headers.setContentDispositionFormData("attachment", f.getName());
                            return new HttpEntity<>(liiteService.convertToHttpResource(f), headers);
                        })
                        .orElse(notFound()));
    }

    @OivaAccess_Esittelija
    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    @ApiOperation(notes = "Poistaa liitteen uuid:n perusteella", value = "")
    public void deleteFile(@PathVariable String uuid) {
        Optional<Liite> liite = liiteService.getByUuid(UUID.fromString(uuid));
        if (!liite.isPresent()) {
            throw new ResourceNotFoundException("Liite not found with uuid " + uuid);
        }
        else {
            liiteService.delete(liite.get());
        }
    }

    /**
     * Get content type either from stored file metadata or 'guess' it
     * from the file extension.
     *
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
        return MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(item.getPolku());
    }

}
