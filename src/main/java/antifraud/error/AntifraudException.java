package antifraud.error;

public class AntifraudException extends RuntimeException {
    public AntifraudException(ErrorEnum errorEnum) {
        super(errorEnum.name());
    }
}
