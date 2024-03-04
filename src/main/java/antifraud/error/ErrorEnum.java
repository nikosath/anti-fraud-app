package antifraud.error;

import org.springframework.http.HttpStatus;


public enum ErrorEnum {
    ENTITY_ALREADY_EXISTS, ENTITY_NOT_FOUND, INVALID_ARGUMENT, STATE_ALREADY_EXISTS;

    public static HttpStatus toHttpStatus(ErrorEnum error) {
        return switch (error) {
            case ENTITY_ALREADY_EXISTS, STATE_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case ENTITY_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
        };
    }
}
