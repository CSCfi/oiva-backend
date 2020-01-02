package fi.minedu.oiva.backend.core.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ValidationException;

@ControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleNoTFound() {
        return createEntity(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleNoAccess() {
        return createEntity(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleInvalid() {
        return createEntity(HttpStatus.BAD_REQUEST);
    }

    private static ResponseEntity<Object> createEntity(HttpStatus status) {
        final ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("status", status.value());
        node.put("title", status.getReasonPhrase());
        return new ResponseEntity<>(node.toString(), status);
    }
}
