package antifraud.error;

public class FailedPreconditionException extends RuntimeException {
    public FailedPreconditionException(ErrorEnum errorEnum) {
        super(errorEnum.name());
    }
}
