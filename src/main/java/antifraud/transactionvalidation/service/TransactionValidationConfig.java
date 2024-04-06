package antifraud.transactionvalidation.service;

import antifraud.common.datastore.ConfigEntity;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static antifraud.transactionvalidation.service.TransactionValidationConfig.PropertyNames.AMOUNT_LIMIT_FOR_ALLOWED;
import static antifraud.transactionvalidation.service.TransactionValidationConfig.PropertyNames.AMOUNT_LIMIT_FOR_MANUAL_PROCESSING;

public record TransactionValidationConfig(long amountLimitForAllowed, long amountLimitForManualProcessing) {

    public static class PropertyNames {
        public static final String AMOUNT_LIMIT_FOR_ALLOWED = "AMOUNT_LIMIT_FOR_ALLOWED";
        public static final String AMOUNT_LIMIT_FOR_MANUAL_PROCESSING = "AMOUNT_LIMIT_FOR_MANUAL_PROCESSING";
    }
}
