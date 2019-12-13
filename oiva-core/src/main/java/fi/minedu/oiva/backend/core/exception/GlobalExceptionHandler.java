package fi.minedu.oiva.backend.core.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ValidationException;

@ControllerAdvice
class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public JsonNode handleNoTFound() {
        final ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("status", HttpStatus.NOT_FOUND.value());
        node.put("title", HttpStatus.NOT_FOUND.getReasonPhrase());
        return node;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public JsonNode handleNoAccess() {
        final ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("status", HttpStatus.FORBIDDEN.value());
        node.put("title", HttpStatus.FORBIDDEN.getReasonPhrase());
        return node;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public JsonNode handleInvalid() {
        final ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("status", HttpStatus.BAD_REQUEST.value());
        node.put("title", HttpStatus.BAD_REQUEST.getReasonPhrase());
        return node;
    }

}
