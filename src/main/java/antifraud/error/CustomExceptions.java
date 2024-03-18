package antifraud.error;

public class CustomExceptions {
    public static class FailedPreconditionException extends RuntimeException {
        public  FailedPreconditionException(String description) {
            super(description);
        }
    }
    public static class FailedPostconditionException extends RuntimeException {
        public  FailedPostconditionException(String description) {
            super(description);
        }
    }
}
