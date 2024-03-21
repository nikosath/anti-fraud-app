package antifraud.transactionvalidation.service;

import antifraud.common.datastore.ConfigEntity;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static antifraud.transactionvalidation.service.TransactionValidationConfig.PropertyNames.AMOUNT_LIMIT_FOR_ALLOWED;
import static antifraud.transactionvalidation.service.TransactionValidationConfig.PropertyNames.AMOUNT_LIMIT_FOR_MANUAL_PROCESSING;

public record TransactionValidationConfig(long amountLimitForAllowed, long amountLimitForManualProcessing) {
    public static TransactionValidationConfig from(Collection<ConfigEntity> entities) {
        Map<String, String> propertyNameToValue = entities.stream().collect(
                Collectors.toMap(entity -> entity.getPropertyName(), entity -> entity.getPropertyValue()));

        return new TransactionValidationConfig(
                Long.parseLong(propertyNameToValue.get(AMOUNT_LIMIT_FOR_ALLOWED)),
                Long.parseLong(propertyNameToValue.get(AMOUNT_LIMIT_FOR_MANUAL_PROCESSING))
        );

    }

    public static class PropertyNames {
        public static final String AMOUNT_LIMIT_FOR_ALLOWED = "AMOUNT_LIMIT_FOR_ALLOWED";
        public static final String AMOUNT_LIMIT_FOR_MANUAL_PROCESSING = "AMOUNT_LIMIT_FOR_MANUAL_PROCESSING";
    }
}
