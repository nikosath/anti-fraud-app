package antifraud.transactionvalidation.service;

public record TransactionValidationConfig(long amountLimitForAllowed, long amountLimitForManualProcessing) {

    public static class PropertyNames {
        public static final String AMOUNT_LIMIT_FOR_ALLOWED = "AMOUNT_LIMIT_FOR_ALLOWED";
        public static final String AMOUNT_LIMIT_FOR_MANUAL_PROCESSING = "AMOUNT_LIMIT_FOR_MANUAL_PROCESSING";
    }
}
