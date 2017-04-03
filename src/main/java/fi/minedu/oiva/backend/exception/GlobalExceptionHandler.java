package fi.minedu.oiva.backend.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public JsonNode handleNoTFound() {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("status", HttpStatus.NOT_FOUND.value());
        node.put("title", HttpStatus.NOT_FOUND.getReasonPhrase());
        return node;
    }

}
