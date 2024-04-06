package antifraud.common;

import antifraud.error.ErrorEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class WebUtils {
    public static <R> ResponseEntity<R> errorToResponseEntity(ErrorEnum error) {
        return ResponseEntity.status(toHttpStatus(error)).build();
    }

    public static HttpStatus toHttpStatus(ErrorEnum error) {
        return switch (error) {
            case ENTITY_ALREADY_EXISTS, STATE_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case ENTITY_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
            case MULTIPLE_ENTITIES_FOUND -> HttpStatus.INTERNAL_SERVER_ERROR;
            case UNPROCESSABLE_ENTITY -> HttpStatus.UNPROCESSABLE_ENTITY;
        };
    }
}
