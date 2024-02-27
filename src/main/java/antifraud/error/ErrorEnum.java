package antifraud.error;

import org.springframework.http.HttpStatus;


public enum ErrorEnum {
    ENTITY_ALREADY_EXISTS, ENTITY_NOT_FOUND;

    public static HttpStatus toHttpStatus(ErrorEnum error) {
        return switch (error) {
            case ENTITY_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case ENTITY_NOT_FOUND -> HttpStatus.NOT_FOUND;
        };
    }
}
