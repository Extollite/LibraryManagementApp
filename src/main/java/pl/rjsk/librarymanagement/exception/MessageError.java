package pl.rjsk.librarymanagement.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
public class MessageError {

    private ZonedDateTime timestamp;
    private int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    private String exception = "";
    private String message = "";
}
