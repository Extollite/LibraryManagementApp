package pl.rjsk.librarymanagement.exception.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.rjsk.librarymanagement.exception.IncorrectDataException;
import pl.rjsk.librarymanagement.exception.MessageError;
import pl.rjsk.librarymanagement.exception.ResourceNotFoundException;

import java.time.ZonedDateTime;

@ControllerAdvice
public class LibraryExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = IncorrectDataException.class)
    protected ResponseEntity<Object> incorrectData(IncorrectDataException ex, WebRequest request) {

        MessageError error = new MessageError();
        error.setTimestamp(ZonedDateTime.now());
        error.setException(ex.getClass().getSimpleName());
        error.setMessage(ex.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.value());

        logger.error("Exception caught: ", ex);

        return super.handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    protected ResponseEntity<Object> resourceNotFound(ResourceNotFoundException ex, WebRequest request) {

        MessageError error = new MessageError();
        error.setTimestamp(ZonedDateTime.now());
        error.setException(ex.getClass().getSimpleName());
        error.setMessage(ex.getMessage());
        error.setStatus(HttpStatus.NOT_FOUND.value());

        logger.error("Exception caught: ", ex);

        return super.handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {

        MessageError error = new MessageError();
        error.setTimestamp(ZonedDateTime.now());
        error.setException(exception.getClass().getSimpleName());
        error.setMessage(exception.getMessage());
        error.setStatus(status.value());

        logger.error("Unknown exception caught: ", exception);

        return super.handleExceptionInternal(exception, error, headers, status, request);
    }
}
