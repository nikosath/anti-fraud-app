package antifraud.error;

import org.springframework.http.HttpStatus;


public enum Error {
    ENTITY_EXISTS, ENTITY_NOT_FOUND;

    public static HttpStatus toHttpStatus(Error error) {
        return switch (error) {
            case ENTITY_EXISTS -> HttpStatus.CONFLICT;
            case ENTITY_NOT_FOUND -> HttpStatus.NOT_FOUND;
        };
    }
}
